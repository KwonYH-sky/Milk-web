package com.milk.milkweb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hash_tag")
@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class HashTag {

	@Id
	@Column(name = "hash_tag_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false, unique = true)
	private String tag;

	public static HashTag of (String tag) {
		return HashTag.builder()
				.tag(tag)
				.build();
	}

}
