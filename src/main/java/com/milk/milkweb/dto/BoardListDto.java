package com.milk.milkweb.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @Builder
public class BoardListDto {

	private Long id;

	private String title;

	private String memberName;

	private int likes;

	private int views;

	private LocalDateTime createdTime;

}
