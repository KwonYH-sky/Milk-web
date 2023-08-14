package com.milk.milkweb.entity;

import com.milk.milkweb.constant.Role;
import com.milk.milkweb.dto.MemberFromDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;


@Entity
@Table(name = "member")
@Getter @Builder
@AllArgsConstructor
public class Member {

	@Id
	@Column(name = "member_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	@Column(unique = true)
	private String email;

	private String password;

	@Enumerated(EnumType.STRING)
	private Role role;

	public static Member createMember(MemberFromDto memberFromDto, PasswordEncoder passwordEncoder) {
		String password = passwordEncoder.encode(memberFromDto.getPassword());
		return Member.builder()
				.email(memberFromDto.getEmail())
				.name(memberFromDto.getName())
				.password(password)
				.role(Role.USER)
				.build();
	}
}
