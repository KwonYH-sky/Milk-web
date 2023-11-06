package com.milk.milkweb.service;


import com.milk.milkweb.constant.Role;
import com.milk.milkweb.dto.CustomUserDetails;
import com.milk.milkweb.entity.Member;
import com.milk.milkweb.exception.AlreadyRegisterException;
import com.milk.milkweb.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

	private Member member;
	private PasswordEncoder passwordEncoder;

	@Mock
	MemberRepository memberRepository;

	@InjectMocks
	MemberService memberService;

	@BeforeEach
	void setUp() {
		passwordEncoder = new BCryptPasswordEncoder();

		member = Member.builder()
				.id(1L)
				.name("김우유")
				.email("test@test")
				.password(passwordEncoder.encode("1234"))
				.role(Role.ADMIN)
				.build();
	}

	@Test
	@DisplayName("회원가입 테스트")
	void registerTest() {
		// given
		given(memberRepository.save(any())).willReturn(member);

		// when
		Member savedMember = memberService.saveMember(member);

		// then
		Assertions.assertThat(savedMember.getName()).isEqualTo(member.getName());
		Assertions.assertThat(savedMember.getEmail()).isEqualTo(member.getEmail());
	}


	@Test
	@DisplayName("이미 존재하는 이름 회원가입 테스트")
	void validateNameRegisterTest() {
		// given
		given(memberRepository.existsByName(member.getName())).willReturn(true);

		// when, then
		Assertions.assertThatThrownBy(() -> memberService.saveMember(member)).as("이름 일치 여부").isInstanceOf(AlreadyRegisterException.class);
	}

	@Test
	@DisplayName("이미 존재하는 이메일 회원가입 테스트")
	void validateEmailRegisterTest() {
		// given
		given(memberRepository.findByEmail(member.getEmail())).willReturn(member);

		// when, then
		Assertions.assertThatThrownBy(() -> memberService.saveMember(member)).as("이메일 일치 여부").isInstanceOf(AlreadyRegisterException.class);
	}
	
	@Test
	@DisplayName("이름 변경 성공 테스트")
	void updateNameTest() {
		// given
		String newName = "흰우유";
		given(memberRepository.existsByName(any())).willReturn(false);
		given(memberRepository.findByEmail(member.getEmail())).willReturn(member);

		// when
		memberService.updateName(member.getEmail(), newName);
		
		// then
		Assertions.assertThat(member.getName()).as("이름 일치").isEqualTo(newName);
	}

	@Test
	@DisplayName("이름 변경 실패 테스트")
	void failUpdateNameTest() {
		// given
		String newName = "흰우유";
		given(memberRepository.existsByName(any())).willReturn(true);

		// when, then
		Assertions.assertThatThrownBy(() -> memberService.updateName(member.getEmail(), newName)).isInstanceOf(AlreadyRegisterException.class);
	}

	@Test
	@DisplayName("비밀번호 검증 테스트")
	void validatePasswordTest() {
		// given
		CustomUserDetails userDetails = new CustomUserDetails(member);

		// when
		Boolean validatePwd = memberService.validatePassword(userDetails, "1234", passwordEncoder);
		Boolean failValidatePwd = memberService.validatePassword(userDetails, "4321", passwordEncoder);
		// then
		Assertions.assertThat(validatePwd).as("비밀번호 일치").isTrue();
		Assertions.assertThat(failValidatePwd).as("비밀번호 불일치").isFalse();
	}
	
	@Test
	@DisplayName("비밀번호 변경 테스트")
	void updatePasswordTest() {
		// given
		Member socialMember = Member.builder()
				.id(2L)
				.email("test22@test.test")
				.password(passwordEncoder.encode("1234"))
				.role(Role.SOCIAL)
				.build();
		String newPwd = "1q2w3e4r";
		given(memberRepository.findByEmail(socialMember.getEmail())).willReturn(socialMember);

		// when
		memberService.updatePassword(socialMember.getEmail(), newPwd, passwordEncoder);

		// then
		Assertions.assertThat(passwordEncoder.matches(newPwd, socialMember.getPassword())).as("비밀 번호 일치?").isTrue();
		Assertions.assertThat(socialMember.getRole()).as("User로 전환?").isEqualTo(Role.USER);
	}

	@Test
	@DisplayName("loadUserByUsername 테스트")
	void loadUserByUsernameTest() {
		// given
		given(memberRepository.findByEmail(member.getEmail())).willReturn(member);
		
		// then
		CustomUserDetails userDetails = (CustomUserDetails) memberService.loadUserByUsername(member.getEmail());
		
		// then
		Assertions.assertThat(userDetails.getName()).as("이메일 체크").isEqualTo(member.getEmail());
		Assertions.assertThat(userDetails.getUsername()).as("이름 체크").isEqualTo(member.getName());
		Assertions.assertThat(userDetails.getAuthorities().stream().anyMatch(authority -> 
				authority.getAuthority().equals(member.getRoleKey()))).as("권한 체크").isTrue();
	}
}
