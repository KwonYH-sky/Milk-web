package com.milk.milkweb.entity;

import com.milk.milkweb.constant.Role;
import com.milk.milkweb.dto.MemberFormDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;


@Entity
@Table(name = "member")
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

	@Id
	@Column(name = "member_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(unique = true)
	private String name;

	@Column(unique = true)
	private String email;

	private String password;

	@Enumerated(EnumType.STRING)
	private Role role;

	public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
		String password = passwordEncoder.encode(memberFormDto.getPassword());
		return Member.builder()
				.email(memberFormDto.getEmail())
				.name(memberFormDto.getName())
				.password(password)
				.role(Role.USER)
				.build();
	}
	public void updateName(String newName) {
		name = newName;
	}

	public void updatePassword(String newPwd, PasswordEncoder passwordEncoder) {
		String PwdEncoding = passwordEncoder.encode(newPwd);
		this.password = PwdEncoding;
	}

	public void toUSER() {
		role = Role.USER;
	}

	public String getRoleKey(){
		return this.role.getKey();
	}
}
