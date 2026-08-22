package com.livedesk.events.email;


import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final ResourceLoader resourceLoader;

    public EmailServiceImpl(
            JavaMailSender mailSender,
            ResourceLoader resourceLoader
    ) {
        this.mailSender = mailSender;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void sendEscalationSummary(
            String recipientEmail,
            int queuedEscalatedCount,
            int unansweredEscalatedCount
    ) {

        try {
            String emailContent = buildEmailContent(
                    queuedEscalatedCount,
                    unansweredEscalatedCount
            );

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    false,
                    StandardCharsets.UTF_8.name()
            );

            helper.setTo(recipientEmail);
            helper.setSubject("LiveDesk — SLA Escalation Summary");
            helper.setText(emailContent, true);

            mailSender.send(message);

        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            throw new EmailSendingException(
                    "Failed to send escalation summary email", e
            );
        }
    }

    private String loadTemplate() throws IOException {

        Resource resource = resourceLoader.getResource(
                "classpath:templates/email/ticket-escalation-summary.html"
        );

        return resource.getContentAsString(StandardCharsets.UTF_8);
    }

    private String buildEmailContent(
            int queuedEscalatedCount,
            int unansweredEscalatedCount
    ) throws IOException {

        int totalEscalatedCount =
                queuedEscalatedCount + unansweredEscalatedCount;

        return loadTemplate()
                .replace(
                        "{{queuedEscalatedCount}}",
                        String.valueOf(queuedEscalatedCount)
                )
                .replace(
                        "{{unansweredEscalatedCount}}",
                        String.valueOf(unansweredEscalatedCount)
                )
                .replace(
                        "{{totalEscalatedCount}}",
                        String.valueOf(totalEscalatedCount)
                );
    }
}