package com.milk.milkweb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentListDto {
	private Long id;

	private String memberName;

	private String comment;

	private Boolean isUserCommentAuthor;

}
