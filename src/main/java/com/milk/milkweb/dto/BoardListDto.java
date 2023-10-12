package com.milk.milkweb.dto;

import com.querydsl.core.annotations.QueryProjection;
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

	@QueryProjection
	public BoardListDto(Long id, String title, String memberName, int likes, int views, LocalDateTime createdTime) {
		this.id = id;
		this.title = title;
		this.memberName = memberName;
		this.likes = likes;
		this.views = views;
		this.createdTime = createdTime;
	}
}
