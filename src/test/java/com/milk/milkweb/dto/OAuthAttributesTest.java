package com.milk.milkweb.dto;

import com.milk.milkweb.constant.Role;
import com.milk.milkweb.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class OAuthAttributesTest {

	@Test
	@DisplayName("구글 로그인 일때")
	void ofGoogleTest() {
		// given
		String userNameAttributeName = "name";
		Map<String, Object> attributes = Map.of("name", "우유", "email", "test@google.com");

		// when
		OAuthAttributes result = OAuthAttributes.of("google", userNameAttributeName, attributes);

		// then
		assertThat(result).as("존재 여부").isNotNull();
		assertThat(result.getEmail()).isEqualTo("test@google.com");
		assertThat(result.getName()).isEqualTo("우유");
		assertThat(result.getAttributes()).isEqualTo(attributes);
	}

	@Test
	@DisplayName("네이버 일때")
	void ofNaverTest() {
		// given
		String userNameAttributeName = "name";
		Map<String, Object> response = Map.of("nickname", "우유", "email", "test@naver.com");
		Map<String, Object> attributes = Map.of("response", response);

		// when
		OAuthAttributes result = OAuthAttributes.of("naver", userNameAttributeName, attributes);

		// then
		assertThat(result).as("존재 여부").isNotNull();
		assertThat(result.getEmail()).isEqualTo("test@naver.com");
		assertThat(result.getName()).isEqualTo("우유");
		assertThat(result.getAttributes()).isEqualTo(attributes);
	}

	@Test
	@DisplayName("to Entity 테스트")
	void toEntityTest() {
		// given
		OAuthAttributes attributes = OAuthAttributes.builder()
				.name("우유")
				.email("test@test")
				.build();
		
		// when
		Member member = attributes.toEntity();
		
		// then
		assertThat(member).isNotNull();
		assertThat(member.getName()).isEqualTo(attributes.getName());
		assertThat(member.getEmail()).isEqualTo(attributes.getEmail());
		assertThat(member.getRole()).isEqualTo(Role.SOCIAL);
	}
}