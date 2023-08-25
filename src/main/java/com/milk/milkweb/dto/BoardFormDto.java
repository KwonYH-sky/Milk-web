package com.milk.milkweb.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BoardFormDto {

	@NotBlank
	private String title;

	@NotEmpty
	private String content;


}
