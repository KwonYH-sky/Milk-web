package com.milk.milkweb.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPageNameDto {

	@NotBlank(message = "이름을 입력해주세요.")
	private String name;

}
