package com.milk.milkweb.dto;

import com.milk.milkweb.entity.BoardImg;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class BoardImgUploadDto {

	private Long boardImgID;

	private String imgName;

}

