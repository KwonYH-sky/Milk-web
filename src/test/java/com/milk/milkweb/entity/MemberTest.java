package com.milk.milkweb.entity;

import com.milk.milkweb.constant.Role;
import com.milk.milkweb.dto.MemberFormDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


class MemberTest {

	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void beforeEach() {
		passwordEncoder = new BCryptPasswordEncoder();
	}

	@Test
	@DisplayName("멤버 생성 확인")
	void createMember() {
		// given
		MemberFormDto memberFormDto = MemberFormDto.builder()
				.email("test@test")
				.name("김우유")
				.password("1234")
				.build();

		// when
		Member member = Member.createMember(memberFormDto, passwordEncoder);

		// then
		Assertions.assertThat(member.getName()).isEqualTo("김우유");
		Assertions.assertThat(member.getEmail()).isEqualTo("test@test");
		Assertions.assertThat(passwordEncoder.matches("1234", member.getPassword())).isTrue();
		Assertions.assertThat(member.getRole()).isEqualTo(Role.USER);
	}

	@Test
	@DisplayName("이름 변경 테스트")
	void updateName() {
		// given
		Member member = Member.builder()
				.name("김우유")
				.email("test@test")
				.password("1234")
				.role(Role.ADMIN)
				.build();

		// when
		member.updateName("우유");

		// then
		Assertions.assertThat(member.getName()).isEqualTo("우유");
	}

	@Test
	@DisplayName("비밀 번호 변경 테스트")
	void updatePassword() {
		// given
		Member member = Member.builder()
				.name("김우유")
				.email("test@test")
				.password("1234")
				.role(Role.ADMIN)
				.build();

		// when
		member.updatePassword("5678", passwordEncoder);

		// then
		Assertions.assertThat(passwordEncoder.matches("5678", member.getPassword())).isTrue();
	}

	@Test
	@DisplayName("User로 전환 테스트")
	void toUSER() {
		// given
		Member member = Member.builder()
				.name("김우유")
				.email("test@test")
				.password("1234")
				.role(Role.ADMIN)
				.build();

		// when
		member.toUSER();

		// then
		Assertions.assertThat(member.getRole()).isEqualTo(Role.USER);
	}
}