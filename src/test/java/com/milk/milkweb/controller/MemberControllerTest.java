package com.milk.milkweb.controller;

import com.milk.milkweb.dto.MemberFormDto;
import com.milk.milkweb.entity.Member;
import com.milk.milkweb.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class MemberControllerTest {

	@Autowired
	private MemberService memberService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	PasswordEncoder passwordEncoder;

	public Member createMember(String email, String password) {
		MemberFormDto memberFormDto = new MemberFormDto();
		memberFormDto.setName("김우유");
		memberFormDto.setEmail(email);
		memberFormDto.setPassword(password);
		Member member = Member.createMember(memberFormDto, passwordEncoder);
		return memberService.saveMember(member);
	}

	@Test
	@DisplayName("로그인 성공 테스트")
	public void loginSuccessTest() throws Exception {
		String email = "test@email.com";
		String password = "1234";
		this.createMember(email, password);
		mockMvc.perform(formLogin()
						.userParameter("email")
						.loginProcessingUrl("/member/login")
						.user(email).password(password))
				.andExpect(SecurityMockMvcResultMatchers.authenticated());
	}

	@Test
	@DisplayName("로그인 실패 테스트")
	public void loginFailTest() throws Exception{
		String email = "test@email.com";
		String password = "1234";
		this.createMember(email, password);
		mockMvc.perform(formLogin()
						.userParameter("email")
						.loginProcessingUrl("/member/login")
						.user(email).password("wrongPassword"))
				.andExpect(SecurityMockMvcResultMatchers.unauthenticated());
	}
}
