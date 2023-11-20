package com.milk.milkweb.controller;

import com.milk.milkweb.config.OAuth2Config;
import com.milk.milkweb.config.SecurityConfig;
import com.milk.milkweb.dto.MemberFormDto;
import com.milk.milkweb.service.CustomOAuth2UserService;
import com.milk.milkweb.service.EmailService;
import com.milk.milkweb.service.MemberService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({SecurityConfig.class, OAuth2Config.class})
@WebMvcTest(MemberController.class)
public class MemberControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MemberService memberService;
	@MockBean
	private CustomOAuth2UserService customOAuth2UserService;
	@MockBean
	private EmailService emailService;
	@MockBean
	PasswordEncoder passwordEncoder;

	@Test
	@WithAnonymousUser
	@DisplayName("회원가입 폼 GET 요청 테스트")
	void entryMemberFormTest() throws Exception {
		// given, when, then
		mockMvc.perform(get("/member/register").with(csrf()))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("memberFormDto"))
				.andExpect(model().attribute("memberFormDto", Matchers.instanceOf(MemberFormDto.class)));
	}

	@Test
	@WithMockUser(username = "test@test.com", password = "1234")
	@DisplayName("로그인 성공 테스트")
	public void loginSuccessTest() throws Exception {
		String email = "test@test.com";
		String password = "1234";
		mockMvc.perform(formLogin()
						.userParameter("email")
						.loginProcessingUrl("/member/login")
						.user(email).password(password))
				.andExpect(SecurityMockMvcResultMatchers.authenticated());
	}

	@Test
	@DisplayName("로그인 실패 테스트")
	public void loginFailTest() throws Exception{
		String email = "test@test.com";
		String wrongPassword = "wrongPassword";
		mockMvc.perform(formLogin()
						.userParameter("email")
						.loginProcessingUrl("/member/login")
						.user(email).password(wrongPassword))
				.andExpect(SecurityMockMvcResultMatchers.unauthenticated());
	}
}
