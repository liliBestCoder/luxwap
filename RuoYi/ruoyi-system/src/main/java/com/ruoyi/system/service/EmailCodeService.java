package com.ruoyi.system.service;

import com.ruoyi.system.domain.EmailVerification;
import com.ruoyi.system.mapper.EmailVerificationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;

@Service
public class EmailCodeService implements InitializingBean, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(EmailCodeService.class);

    @Autowired
    private EmailVerificationMapper mapper;

    private static final long EXPIRE_MINUTES = 10; // 5分钟有效

    @Autowired
    private JavaMailSender mailSender;

    private ThreadPoolTaskExecutor executor;


    @Override
    public void afterPropertiesSet() throws Exception {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(10000);
        executor.initialize();
        this.executor = executor;
    }

    @Override
    public void destroy() throws Exception {
        if(executor != null){
            executor.shutdown();
        }
    }

    public void sendMail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("1871814749@qq.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    private String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    public void sendCode(String email) {
        String code = generateCode();
        Date expireAt = Date.from(LocalDateTime.now().plusMinutes(EXPIRE_MINUTES).atZone(ZoneId.systemDefault()).toInstant());

        EmailVerification ev = new EmailVerification();
        ev.setEmail(email);
        ev.setCode(code);
        ev.setExpireAt(expireAt);

        mapper.insert(ev);

        String content = "您的验证码为：" + code + "，有效期5分钟，请勿泄露。";
        executor.execute(() -> {
            try {
                sendMail(email, "验证码邮件", content);
            } catch (Exception e) {
                log.error("send mail error, email = {}, code = {}", email, code, e);
            }
        });
    }

    public boolean verifyCode(String email, String code) {
        EmailVerification ev = mapper.getLatestValidCodeByEmail(email);
        return ev != null && ev.getCode().equals(code);
    }
}
