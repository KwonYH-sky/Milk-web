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
		return ofGoogle(userNameAttributeName, attributes);
	}


	private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
		return OAuthAttributes.builder()
				.name((String) attributes.get("name"))
				.email((String) attributes.get("email"))
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
