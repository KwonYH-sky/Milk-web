package com.milk.milkweb.dto;

import lombok.*;
import org.apache.commons.lang3.StringUtils;

@Getter @Setter
@NoArgsConstructor
public class BoardSearchDto {

	private String searchType;

	private String keyword;

	public Boolean isSearching() {
		return StringUtils.isNotEmpty(searchType) && StringUtils.isNotBlank(keyword);
	}

}
