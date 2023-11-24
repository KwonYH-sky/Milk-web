package com.milk.milkweb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.milk.milkweb.config.OAuth2Config;
import com.milk.milkweb.config.SecurityConfig;
import com.milk.milkweb.constant.Role;
import com.milk.milkweb.dto.*;
import com.milk.milkweb.entity.Member;
import com.milk.milkweb.service.CustomOAuth2UserService;
import com.milk.milkweb.service.EmailService;
import com.milk.milkweb.service.MemberService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import({SecurityConfig.class, OAuth2Config.class})
@WebMvcTest(MemberController.class)
public class MemberControllerTest {

	private CustomUserDetails mockUser;

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private MemberService memberService;
	@MockBean
	private CustomOAuth2UserService customOAuth2UserService;
	@MockBean
	private EmailService emailService;
	@MockBean
	PasswordEncoder passwordEncoder;

	@BeforeEach
	void setUp() {
		mockUser = new CustomUserDetails(Member.builder()
				.id(1L)
				.name("김우유")
				.email("test@test")
				.password("1234")
				.role(Role.ADMIN)
				.build());
	}

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
	@DisplayName("회원 가입 성공 테스트")
	void registerMemberSuccessTest() throws Exception {
		// given
		MemberFormDto memberFormDto = MemberFormDto.builder()
				.email("test@test.com")
				.name("tester")
				.password("12345678")
				.build();

		// when, then
		mockMvc.perform(post("/member/register").with(csrf())
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.content(objectMapper.writeValueAsString(memberFormDto)))
				.andExpect(status().is2xxSuccessful());
	}

	@Test
	@DisplayName("임시 비밀번호 이메일 전송 요청 테스트")
	void sendTempPwdMailTest() throws Exception {
		// given
		MailPwdSendDto mailPwdSendDto = MailPwdSendDto.builder()
				.email("test@test")
				.build();

		given(emailService.getTempPassword()).willReturn("temp1234");

		// when, then
		mockMvc.perform(post("/member/find-pwd/send-mail").with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(mailPwdSendDto)))
				.andExpect(status().isOk());

		verify(memberService, times(1))
				.updatePassword(eq(mailPwdSendDto.getEmail()), eq("temp1234"), any());
		verify(emailService, times(1)).sendMail(any(MailDto.class));
	}

	@Test
	@DisplayName("MyPage 요청 테스트")
	void toMyPageTest() throws Exception {
		// when, then
		mockMvc.perform(get("/member/mypage").with(user(mockUser)))
				.andExpect(status().isOk())
				.andExpect(view().name("member/memberMyPage"))
				.andExpect(model().attributeExists("MemberName"));
	}

	@Test
	@DisplayName("이름 변경 요청 테스트")
	void changeNameTest() throws Exception {
		// given
		MyPageNameDto myPageNameDto = MyPageNameDto.builder()
				.name("흰우유")
				.build();

		// when, then
		mockMvc.perform(post("/member/mypage/change-name")
						.with(csrf())
						.with(user(mockUser))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(myPageNameDto)))
				.andExpect(status().isOk());

		verify(memberService, times(1)).updateName(eq(mockUser.getName()), eq(myPageNameDto.getName()));
	}

	@Test
	@DisplayName("비밀번호 수정 GET 요청 테스트")
	void toModifyPwdTest() throws Exception {
		// when, then
		mockMvc.perform(get("/member/mypage/modify-info").with(user(mockUser)))
				.andExpect(status().isOk())
				.andExpect(view().name("member/memberModifyPwd"))
				.andExpect(model().attributeExists("isSocial"))
				.andExpect(model().attribute("isSocial", false));
	}

	@Test
	@WithMockUser(username = "test@test.com", password = "1234")
	@DisplayName("로그인 성공 테스트")
	public void loginSuccessTest() throws Exception {
		// given
		String email = "test@test.com";
		String password = "1234";

		// when, then
		mockMvc.perform(formLogin()
						.userParameter("email")
						.loginProcessingUrl("/member/login")
						.user(email).password(password))
				.andExpect(SecurityMockMvcResultMatchers.authenticated());
	}

	@Test
	@DisplayName("로그인 실패 테스트")
	public void loginFailTest() throws Exception{
		// given
		String email = "test@test.com";
		String wrongPassword = "wrongPassword";

		// when, then
		mockMvc.perform(formLogin()
						.userParameter("email")
						.loginProcessingUrl("/member/login")
						.user(email).password(wrongPassword))
				.andExpect(SecurityMockMvcResultMatchers.unauthenticated());
	}
}
