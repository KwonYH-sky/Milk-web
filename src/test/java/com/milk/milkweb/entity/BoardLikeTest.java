package com.milk.milkweb.entity;

import com.milk.milkweb.constant.Role;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class BoardLikeTest {

	private Member member;
	
	private Board board;
	
	@BeforeEach
	void beforeEach() {
		member = Member.builder()
				.email("test@test")
				.password("1234")
				.name("김우유")
				.role(Role.ADMIN)
				.build();
		
		board = Board.builder()
				.title("게시물")
				.content("냉무")
				.views(0)
				.createdTime(LocalDateTime.now())
				.build();
	}

	@Test
	@DisplayName("게시물 좋아요 생성 테스트")
	void createBoardLikeEntity() {
		// given, when
		BoardLike boardLike = BoardLike.createBoardLikeEntity(board, member);

		// then
		Assertions.assertThat(boardLike).as("해당 값이 잘 들어 갔는지 체크").isNotNull();
		Assertions.assertThat(boardLike.getBoard()).as("게시물 매핑 체크").isEqualTo(board);
		Assertions.assertThat(boardLike.getMember()).as("회원 매핑 체크").isEqualTo(member);
	}
}