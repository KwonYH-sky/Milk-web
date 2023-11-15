package com.milk.milkweb.service;

import com.milk.milkweb.dto.MailDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

	@Mock
	private JavaMailSender javaMailSender;

	@InjectMocks
	private EmailService emailService;

	@Test
	@DisplayName("이메일 보내기 테스트")
	void sendMailTest() {
		// given
		MailDto mailDto = MailDto.toTempPwdMailDto("test@test.com", "1234");
		SimpleMailMessage expectedMessage = new SimpleMailMessage();
		expectedMessage.setTo(mailDto.getAddress());
		expectedMessage.setSubject(mailDto.getTitle());
		expectedMessage.setText(mailDto.getMessage());
		expectedMessage.setFrom("yhoung11@gmail.com");
		expectedMessage.setReplyTo("yhoung11@gmail.com");

		// when
		emailService.sendMail(mailDto);

		// then
		verify(javaMailSender, times(1)).send(expectedMessage);
	}

	@Test
	@DisplayName("임시 비밀번호 테스트")
	void getTempPasswordTest() {
		// when
		String tempPwd = emailService.getTempPassword();

		// then
		assertThat(tempPwd).as("존재 여부")
				.isNotNull()
				.isNotEmpty();
		assertThat(tempPwd.length()).as("비밀번호 길이").isEqualTo(10);
	}
}