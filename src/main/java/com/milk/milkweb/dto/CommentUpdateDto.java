package com.milk.milkweb.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class CommentUpdateDto {
	private Long id;

	@NotBlank
	private String comment;
}
