package com.milk.milkweb.service;

import com.milk.milkweb.dto.MailDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

	private final JavaMailSender javaMailSender;

	public void sendMail(MailDto mailDto) {
		log.info("EmailService sendMail()");
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(mailDto.getAddress());
		mailMessage.setSubject(mailDto.getTitle());
		mailMessage.setText(mailDto.getMessage());
		mailMessage.setFrom("yhoung11@gmail.com");
		mailMessage.setReplyTo("yhoung11@gmail.com");
		javaMailSender.send(mailMessage);
	}

	public String getTempPassword() {
		UUID uuid = UUID.randomUUID();
		String password = uuid.toString().replace("-", "").substring(10);

		return password;
	}

}
