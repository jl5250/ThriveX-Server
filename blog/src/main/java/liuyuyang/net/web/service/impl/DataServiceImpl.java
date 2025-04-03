package liuyuyang.net.web.service.impl;

import liuyuyang.net.common.execption.CustomException;
import liuyuyang.net.web.service.DataService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

@Service
public class DataServiceImpl implements DataService {
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    @Override
    public void Backup() {
        // 获取当前日期和时间
        LocalDateTime currentDateTime = LocalDateTime.now();

        // 获取当前的年、月、日、时、分
        int year = currentDateTime.getYear();
        int month = currentDateTime.getMonthValue();
        int day = currentDateTime.getDayOfMonth();
        int hour = currentDateTime.getHour();
        int minute = currentDateTime.getMinute();

        String host = url.substring(url.indexOf("//") + 2, url.lastIndexOf(":"));
        String port = "3306";
        String database = url.substring(url.lastIndexOf("/") + 1, url.contains("?") ? url.indexOf("?") : url.length());
        String backupPath = String.format(
                "./Backup_%s_%s.sql",
                database, year + "" + month + day + hour + minute
        );

        backupDatabase(host, port, username, password, database, backupPath);
    }

    // 备份指定数据
    public static void backupDatabase(String host, String port, String user, String password, String database, String backupPath) {
        String command = String.format(
                "mysqldump -h%s -P%s -u%s -p%s %s -r %s",
                host, port, user, password, database, backupPath
        );

        Process process;

        try {
            process = Runtime.getRuntime().exec(command);
            int processComplete = process.waitFor();

            if (processComplete != 0) {
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    StringBuilder errorMessage = new StringBuilder();
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        errorMessage.append(line).append(System.lineSeparator());
                    }
                    throw new CustomException(400, "备份失败：" + errorMessage.toString());
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
