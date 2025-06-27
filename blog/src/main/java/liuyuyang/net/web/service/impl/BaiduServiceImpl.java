package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import liuyuyang.net.common.execption.CustomException;
import liuyuyang.net.common.properties.BaiduProperties;
import liuyuyang.net.model.Baidu;
import liuyuyang.net.web.mapper.BaiduMapper;
import liuyuyang.net.web.service.BaiduService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@Transactional
public class BaiduServiceImpl implements BaiduService {
    @Resource
    private WebClient webClient;
    @Resource
    private BaiduProperties baiduProperties;
    @Resource
    private BaiduMapper baiduMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String BASE_API_URL = "https://openapi.baidu.com/rest/2.0/tongji/report/getData";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public Baidu refreshAccessToken() {
        try {
            // 获取当前refresh token
            String refreshToken = getCurrentRefreshToken();

            if (!StringUtils.hasText(refreshToken)) throw new CustomException("未找到有效的refresh token");

            // 构建刷新token的URL
            String url = String.format(
                    "http://openapi.baidu.com/oauth/2.0/token?grant_type=refresh_token&refresh_token=%s&client_id=%s&client_secret=%s",
                    refreshToken,
                    baiduProperties.getClientKey(),
                    baiduProperties.getClientSecret()
            );

            log.info("开始刷新百度统计access token");

            // 调用百度API刷新token
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (!StringUtils.hasText(response)) throw new CustomException("刷新token失败，响应为空");

            // 解析响应数据
            JsonNode jsonNode = objectMapper.readTree(response);

            // 检查是否有错误
            if (jsonNode.has("error")) throw new CustomException("刷新token失败: " + jsonNode.get("error_description").asText());

            // 提取token信息
            String newAccessToken = jsonNode.get("access_token").asText();
            String newRefreshToken = jsonNode.get("refresh_token").asText();
            int expiresIn = jsonNode.get("expires_in").asInt();

            // 计算过期时间
            LocalDateTime expiresTime = LocalDateTime.now().plusSeconds(expiresIn);

            // 保存到数据库
            Baidu baidu = new Baidu();
            baidu.setAccessToken(newAccessToken);
            baidu.setRefreshToken(newRefreshToken);
            baidu.setExpiresTime(expiresTime);
            baidu.setCreateTime(LocalDateTime.now());

            // 先删除旧记录，再插入新记录
            baiduMapper.delete(new QueryWrapper<>());
            baiduMapper.insert(baidu);

            log.info("百度统计access token刷新成功，过期时间: {}", expiresTime);
            return baidu;

        } catch (Exception e) {
            log.error("刷新百度统计access token失败", e);
            return null;
        }
    }

    // 获取当前有效的access token
    @Override
    public String getValidAccessToken() {
        // 检查是否需要刷新
        if (isTokenNeedRefresh()) {
            log.info("token即将过期，开始自动刷新");
            refreshAccessToken();
        }

        // 从数据库获取最新的token
        List<Baidu> list = baiduMapper.selectList(new QueryWrapper<Baidu>().orderByDesc("create_time").last("limit 1"));
        if (list.isEmpty()) {
            log.warn("数据库中未找到有效的access token");
            return null;
        }

        Baidu baidu = list.get(0);
        if (baidu.getExpiresTime().isBefore(LocalDateTime.now())) {
            log.warn("access token已过期，需要刷新");
            Baidu newBaidu = refreshAccessToken();
            return newBaidu != null ? newBaidu.getAccessToken() : null;
        }

        return baidu.getAccessToken();
    }

    // 检查token是否即将过期（提前1天刷新）
    @Override
    public boolean isTokenNeedRefresh() {
        List<Baidu> list = baiduMapper.selectList(new QueryWrapper<Baidu>().orderByDesc("create_time").last("limit 1"));
        if (list.isEmpty()) {
            return true; // 没有token时需要刷新
        }

        Baidu baidu = list.get(0);
        // 提前1天刷新token
        return baidu.getExpiresTime().isBefore(LocalDateTime.now().plusDays(1));
    }

    // 获取当前的refresh token
    private String getCurrentRefreshToken() {
        // 先从数据库获取
        List<Baidu> list = baiduMapper.selectList(new QueryWrapper<Baidu>().orderByDesc("create_time").last("limit 1"));
        if (!list.isEmpty() && StringUtils.hasText(list.get(0).getRefreshToken())) {
            return list.get(0).getRefreshToken();
        }

        // 如果数据库没有，使用配置文件中的初始refresh token
        return baiduProperties.getRefreshToken();
    }

    // 定时任务：每天凌晨2点检查并刷新token
    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduledRefreshToken() {
        log.info("开始执行定时刷新百度统计token任务");
        if (isTokenNeedRefresh()) {
            refreshAccessToken();
        } else {
            log.info("token仍然有效，无需刷新");
        }
    }

    // 初始化token（首次运行时调用）
    public void initializeToken() {
        List<Baidu> list = baiduMapper.selectList(new QueryWrapper<>());
        if (list.isEmpty()) {
            log.info("首次运行，初始化百度统计token");
            refreshAccessToken();
        }
    }

    /**
     * 统一的百度统计API调用方法
     * @param metrics 指标参数
     * @param method 方法参数
     * @param additionalParams 额外参数
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param apiName API名称（用于日志）
     * @return JsonNode结果
     */
    private JsonNode callBaiduStatisticsApi(String metrics, String method, String additionalParams, 
                                          String startDate, String endDate, String apiName) {
        String accessToken = getValidAccessToken();
        if (accessToken == null) {
            throw new CustomException("无有效的access token");
        }

        // 处理日期参数
        String[] dates = processDateParams(startDate, endDate);
        String processedStartDate = dates[0];
        String processedEndDate = dates[1];

        try {
            // 构建URL
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(BASE_API_URL)
                    .append("?access_token=").append(accessToken)
                    .append("&site_id=").append(baiduProperties.getSiteId())
                    .append("&start_date=").append(processedStartDate)
                    .append("&end_date=").append(processedEndDate)
                    .append("&metrics=").append(metrics)
                    .append("&method=").append(method);

            // 添加额外参数
            if (StringUtils.hasText(additionalParams)) {
                urlBuilder.append("&").append(additionalParams);
            }

            String url = urlBuilder.toString();
            log.info("调用{}API，URL: {}", apiName, url);

            // 发起请求
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response != null) {
                JsonNode jsonNode = objectMapper.readTree(response);

                // 检查是否有错误
                if (jsonNode.has("error_code")) {
                    String errorMsg = jsonNode.get("error_msg").asText();
                    log.error("{}API调用失败: {}", apiName, errorMsg);
                    throw new CustomException("获取数据失败: " + errorMsg);
                }
                
                log.info("{}API调用成功", apiName);
                return jsonNode;
            }
        } catch (Exception e) {
            log.error("调用{}API失败", apiName, e);
            throw new CustomException("调用" + apiName + "API失败: " + e.getMessage());
        }

        return null;
    }

    /**
     * 处理日期参数
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 处理后的日期数组 [startDate, endDate]
     */
    private String[] processDateParams(String startDate, String endDate) {
        String today = LocalDateTime.now().format(DATE_FORMAT);
        
        if (!StringUtils.hasText(startDate)) {
            startDate = today;
        }
        if (!StringUtils.hasText(endDate)) {
            endDate = today;
        }
        
        return new String[]{startDate, endDate};
    }

    @Override
    public JsonNode getStatisData(String startDate, String endDate) {
        return callBaiduStatisticsApi(
                "pv_count,ip_count", 
                "overview/getTimeTrendRpt", 
                null,
                startDate, 
                endDate, 
                "基础统计数据"
        );
    }

    @Override
    public JsonNode getOverviewTimeTrend(String startDate, String endDate) {
        return callBaiduStatisticsApi(
                "pv_count,ip_count,bounce_ratio,avg_visit_time",
                "overview/getTimeTrendRpt",
                null,
                startDate,
                endDate,
                "概览时间趋势报表"
        );
    }

    @Override
    public JsonNode getNewVisitorTrend(String startDate, String endDate) {
        return callBaiduStatisticsApi(
                "new_visitor_count,new_visitor_ratio",
                "trend/time/a",
                "gran=day&area=",
                startDate,
                endDate,
                "新访客趋势报表"
        );
    }

    @Override
    public JsonNode getBasicOverviewTrend(String startDate, String endDate) {
        return callBaiduStatisticsApi(
                "pv_count,ip_count",
                "overview/getTimeTrendRpt",
                null,
                startDate,
                endDate,
                "基础概览时间趋势报表"
        );
    }
}