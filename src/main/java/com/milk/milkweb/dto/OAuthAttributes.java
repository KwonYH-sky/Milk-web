package com.milk.milkweb.dto;

import com.milk.milkweb.constant.Role;
import com.milk.milkweb.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class OAuthAttributes {
	private Map<String, Object> attributes;
	private String nameAttributeKey;
	private String name;
	private String email;
	private Role role;

	public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes){
		if (registrationId.equals("google"))
			return ofGoogle(userNameAttributeName, attributes);
		else
			return ofNaver(userNameAttributeName, attributes);
	}


	private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
		return OAuthAttributes.builder()
				.name((String) attributes.get("name"))
				.email((String) attributes.get("email"))
				.attributes(attributes)
				.nameAttributeKey(userNameAttributeName)
				.build();
	}

	private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
		Map<String, Object> response = (Map<String, Object>) attributes.get("response");

		return OAuthAttributes.builder()
				.name((String) response.get("nickname"))
				.email((String) response.get("email"))
				.attributes(attributes)
				.nameAttributeKey(userNameAttributeName)
				.build();
	}

	public Member toEntity() {
		return Member.builder()
				.name(name)
				.email(email)
				.role(Role.SOCIAL)
				.build();
	}
}
