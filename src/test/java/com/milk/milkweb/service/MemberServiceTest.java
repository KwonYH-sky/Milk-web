package com.milk.milkweb.service;


import com.milk.milkweb.constant.Role;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

	private Member member;

	@Mock
	MemberRepository memberRepository;

	@InjectMocks
	MemberService memberService;

	@BeforeEach
	void setUp() {
		member = Member.builder()
				.id(1L)
				.name("김우유")
				.email("test@test")
				.password("1234")
				.role(Role.ADMIN)
				.build();
	}

	@Test
	@DisplayName("회원가입 테스트")
	void registerMemberService() {
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
	void validateNameRegister() {
		// given
		given(memberRepository.existsByName(member.getName())).willReturn(true);

		// when, then
		Assertions.assertThatThrownBy(() -> memberService.saveMember(member)).as("이름 일치 여부").isInstanceOf(AlreadyRegisterException.class);
	}

	@Test
	@DisplayName("이미 존재하는 이메일 회원가입 테스트")
	void validateEmailRegister() {
		// given
		given(memberRepository.findByEmail(member.getEmail())).willReturn(member);

		// when, then
		Assertions.assertThatThrownBy(() -> memberService.saveMember(member)).as("이메일 일치 여부").isInstanceOf(AlreadyRegisterException.class);
	}
}
