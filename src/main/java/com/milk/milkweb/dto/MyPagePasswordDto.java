package com.milk.milkweb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@NoArgsConstructor @AllArgsConstructor
public class MyPagePasswordDto {

	@NotBlank
	@Size(min = 8, max = 16, message = "8자 이상, 16자 이하로 입력해주세요.")
	private String password;

}
