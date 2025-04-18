package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import liuyuyang.net.web.mapper.LinkMapper;
import liuyuyang.net.web.mapper.LinkTypeMapper;
import liuyuyang.net.model.Link;
import liuyuyang.net.model.LinkType;
import liuyuyang.net.model.Rss;
import liuyuyang.net.web.service.RssService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RssServiceImpl implements RssService {
    @Resource
    private LinkMapper linkMapper;
    @Resource
    private LinkTypeMapper linkTypeMapper;

    @Override
    public List<Rss> list() {
        List<Rss> rssList = new ArrayList<>();

        // 目标网站列表
        List<Link> linkList = linkMapper.selectList(null);
        List<String> feedUrls = linkList.stream().map(Link::getRss).collect(Collectors.toList());

        for (String feedUrl : feedUrls) {
            if (feedUrl == null) continue;

            QueryWrapper<Link> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("rss", feedUrl);
            Link link = linkMapper.selectOne(queryWrapper);

            try {
                // 创建一个URL对象
                URL url = new URL(feedUrl);
                // 为URL创建一个XmlReader
                XmlReader xmlReader = new XmlReader(url);
                // 创建一个SyndFeedInput对象
                SyndFeedInput input = new SyndFeedInput();
                // 从XmlReader构建SyndFeed对象
                SyndFeed feed = input.build(xmlReader);

                // 遍历提要中的条目
                for (SyndEntry data : feed.getEntries()) {
                    LinkType lt = linkTypeMapper.selectById(link.getTypeId());

                    Rss rss = new Rss();
                    rss.setImage(link.getImage());
                    rss.setEmail(link.getEmail());
                    rss.setType(lt.getName());
                    rss.setAuthor(data.getAuthor());
                    rss.setTitle(data.getTitle());
                    if (data.getDescription() != null) {
                        rss.setDescription(data.getDescription().getValue());
                    } else {
                        rss.setDescription("");
                    }

                    rss.setUrl(data.getLink());
                    rss.setCreateTime(String.valueOf(data.getPublishedDate().getTime()));
                    rssList.add(rss);
                }
            } catch (Exception e) {
                System.err.println("解析失败: " + feedUrl);
            }
        }

        // 对rssList进行排序，根据createTime降序排序
        Collections.sort(rssList, new Comparator<Rss>() {
            @Override
            public int compare(Rss o1, Rss o2) {
                long time1 = Long.parseLong(o1.getCreateTime());
                long time2 = Long.parseLong(o2.getCreateTime());
                // 降序排序
                return Long.compare(time2, time1);
            }
        });

        return rssList;
    }
}