package com.milk.milkweb.entity;

import com.milk.milkweb.constant.Role;
import com.milk.milkweb.dto.BoardUpdateDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class BoardTest {

	static Member member;

	@BeforeAll
	static void beforeAll() {
		member = Member.builder()
				.name("김우유")
				.email("test@test")
				.password("1234")
				.role(Role.ADMIN)
				.build();
	}

	@Test
	@DisplayName("Board 조회수 증가 테스트")
	void increaseView() {
		// given
		Board board = Board.builder()
				.title("test")
				.content("test")
				.views(0)
				.createdTime(LocalDateTime.now())
				.member(member)
				.build();

		// when
		board.increaseView();

		// then
		Assertions.assertThat(board.getViews()).isEqualTo(1);
	}

	@Test
	@DisplayName("Board 수정 테스트")
	void update() {
		// given
		Board board = Board.builder()
				.title("test")
				.content("test")
				.views(0)
				.createdTime(LocalDateTime.now())
				.member(member)
				.build();

		BoardUpdateDto boardUpdateDto = BoardUpdateDto.builder()
				.title("테스트")
				.content("테스트")
				.build();

		// when
		board.update(boardUpdateDto);

		// then
		Assertions.assertThat(board.getTitle()).isEqualTo("테스트");
		Assertions.assertThat(board.getContent()).isEqualTo("테스트");
		Assertions.assertThat(board.getUpdatedTime()).isNotNull()
				.isBeforeOrEqualTo(LocalDateTime.now());
	}

}