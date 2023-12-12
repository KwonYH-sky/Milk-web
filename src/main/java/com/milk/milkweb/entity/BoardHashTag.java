package com.milk.milkweb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "board_hash_tag")
@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class BoardHashTag {

	@Id
	@Column(name = "b_hash_tag_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "board_id")
	private Board board;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hash_tag_id")
	private HashTag hashTag;

	public static BoardHashTag of(Board board, HashTag tag) {
		return BoardHashTag.builder()
				.board(board)
				.hashTag(tag)
				.build();
	}

}
