package com.milk.milkweb.dto;

import com.milk.milkweb.entity.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class CustomUserDetails implements UserDetails, OAuth2User {

	private Member member;
	private Map<String, Object> attributes;

	// Form 로그인 회원 정보
	public CustomUserDetails(Member member) {
		this.member = member;
	}

	// OAuth 로그인 회원 정보
	public CustomUserDetails(Member member, Map<String, Object> attributes) {
		this.member = member;
		this.attributes = attributes;
	}


	@Override
	public String getName() {
		return member.getEmail();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singleton(new SimpleGrantedAuthority(member.getRoleKey()));
	}

	@Override
	public String getPassword() {
		return member.getPassword();
	}

	@Override
	public String getUsername() {
		return member.getName();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}
}
