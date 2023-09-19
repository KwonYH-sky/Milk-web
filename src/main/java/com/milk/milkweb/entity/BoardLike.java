package com.milk.milkweb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "board_like")
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardLike {

	@Id
	@Column(name = "board_like_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "board_id")
	private Board board;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	public static BoardLike createBoardLikeEntity(Board board, Member member) {
		return BoardLike.builder()
				.board(board)
				.member(member)
				.build();
	}
}
