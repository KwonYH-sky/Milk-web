package com.milk.milkweb.service;

import com.milk.milkweb.dto.MemberFromDto;
import com.milk.milkweb.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class MemberServiceTest {

	@Autowired
	MemberService memberService;

	@Autowired
	PasswordEncoder passwordEncoder;

	public Member createMember() {
		MemberFromDto member = MemberFromDto.builder()
				.name("김우유")
				.email("test@test.com")
				.password("1234")
				.build();
		return Member.createMember(member, passwordEncoder);
	}

	@Test
	@DisplayName("회원가입 테스트")
	void saveMemberTest() {
		Member member = createMember();
		Member savedMember = memberService.saveMember(member);

		assertEquals(member.getEmail(), savedMember.getEmail());
		assertEquals(member.getName(), savedMember.getName());
		assertEquals(member.getPassword(), savedMember.getPassword());
		assertEquals(member.getRole(), savedMember.getRole());
	}

	@Test
	@DisplayName("중복 회원가입 테스트")
	void savedDuplicateMemberTest() {
		Member member1 = createMember();
		Member member2 = createMember();

		Member savedMember = memberService.saveMember(member1);

		Throwable e = assertThrows(IllegalStateException.class, () -> memberService.saveMember(member2));

		assertEquals("이미 가입된 이메일입니다.", e.getMessage());
	}
}
