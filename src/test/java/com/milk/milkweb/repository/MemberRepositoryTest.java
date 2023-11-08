package com.milk.milkweb.repository;

import com.milk.milkweb.config.TestQueryDslConfig;
import com.milk.milkweb.constant.Role;
import com.milk.milkweb.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestQueryDslConfig.class)
class MemberRepositoryTest {

	private Member member;

	@Autowired
	private MemberRepository memberRepository;

	@BeforeEach
	void setUp() {
		member = Member.builder()
				.email("test@test")
				.password("1234")
				.name("김우유")
				.role(Role.ADMIN)
				.build();
	}

	@Test
	@DisplayName("Member Repository 저장 테스트")
	void saveMember() {
		// given, when
		Member savedMember =  memberRepository.save(member);

		// then
		Assertions.assertThat(savedMember).as("잘 저장되는가?").isNotNull();
		Assertions.assertThat(savedMember.getName()).as("이름이 일치하는가?").isEqualTo("김우유");
	}

	@Test
	@DisplayName("회원 이메일 통해 찾기 테스트")
	void findByEmail() {
		// given
		memberRepository.save(member);

		// when
		Member findedMember = memberRepository.findByEmail("test@test");

		// then
		Assertions.assertThat(findedMember).as("null 체크").isNotNull();
		Assertions.assertThat(findedMember.getEmail()).as("이메일 체크").isEqualTo("test@test");
		Assertions.assertThat(findedMember.getName()).as("이름 체크").isEqualTo("김우유");
	}

	@Test
	@DisplayName("회원 이름을 통해 존재하는지 테스트")
	void existsByName() {
		// given
		memberRepository.save(member);
		
		// when
		boolean check = memberRepository.existsByName(member.getName());
		
		// then
		Assertions.assertThat(check).as("존재하니?").isTrue();
	}

	@Test
	@DisplayName("회원 이름 존재 안하는지 테스트")
	void existsByNameFail() {
		// given
		memberRepository.save(member);

		// when
		boolean check = memberRepository.existsByName("");

		// then
		Assertions.assertThat(check).as("존재 하면 안돼").isFalse();
	}
}