package com.milk.milkweb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
@AllArgsConstructor
public class BoardSearchDto {

	private String searchType;

	private String keyword;

}
