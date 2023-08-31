package com.milk.milkweb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class BoardUpdateDto {

	private Long id;

	@NotBlank
	private String title;

	@NotEmpty
	private String content;


}
