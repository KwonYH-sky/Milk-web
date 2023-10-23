package com.milk.milkweb.dto;

import com.milk.milkweb.entity.Board;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class MainBoardDto {

	private Long id;

	private String title;

	private int commentNum;

	private int likeNum;

	public static MainBoardDto of(Board board){
		return MainBoardDto.builder()
				.id(board.getId())
				.title(board.getTitle())
				.commentNum(board.getComments().size())
				.likeNum(board.getLikes().size())
				.build();
	}
}
