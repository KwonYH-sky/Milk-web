package com.milk.milkweb.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @Builder
public class BoardDetailDto {

	private Long id;

	private String title;

	private String content;

	private String memberName;

	private LocalDateTime createdTime;

	private LocalDateTime updatedTime;

	private int view;

	private int likes;


}
