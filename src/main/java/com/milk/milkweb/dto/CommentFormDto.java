package com.milk.milkweb.dto;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class CommentFormDto {

	private String comment;

	private Long boardId;

}
