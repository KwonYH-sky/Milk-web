package com.milk.milkweb.entity;

import com.milk.milkweb.constant.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;


@Entity
@Table(name = "member")
@Getter @Builder
public class Member {

	@Id
	@Column(name = "member_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	private String email;

	private String password;

	@Enumerated(EnumType.STRING)
	private Role role;
}
