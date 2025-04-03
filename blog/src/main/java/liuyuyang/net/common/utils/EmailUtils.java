package liuyuyang.net.common.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Component
public class EmailUtils {
    @Resource
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Async
    public void send(String to, String subject, String template) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // 发送邮件
            helper.setFrom(new InternetAddress(from, "你有新的消息~")); // 发送者
            helper.setTo((to == null || to.isEmpty()) ? from : to); // 接收者
            helper.setSubject(subject); // 邮件标题
            helper.setText(template, true); // 第二个参数为 true 表示发送 HTML 格式
            mailSender.send(message);
        } catch (Exception e) {
            // todo 引入更可靠的日志框架
            System.out.println("邮件发送失败！");
        }
    }
}
