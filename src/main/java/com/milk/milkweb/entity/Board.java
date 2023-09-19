package com.milk.milkweb.entity;

import com.milk.milkweb.dto.BoardUpdateDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "board")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board {

	@Id
	@Column(name = "board_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false, length = 50)
	private String title;

	@Lob
	@Column(nullable = false)
	private String content;

	private LocalDateTime createdTime;

	private LocalDateTime updatedTime;

	private int views;

	private int likes;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	public void increaseView() {
		this.views++;
	}

	public void increaseLike() {
		this.likes++;
	}

	public void update(BoardUpdateDto boardUpdateDto) {
		this.title = boardUpdateDto.getTitle();
		this.content = boardUpdateDto.getContent();
		this.updatedTime = LocalDateTime.now();
	}
}
