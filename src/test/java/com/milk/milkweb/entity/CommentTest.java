package com.milk.milkweb.entity;

import com.milk.milkweb.constant.Role;
import com.milk.milkweb.dto.CommentUpdateDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class CommentTest {

	private Member member;

	private Board board;

	@BeforeEach
	void setUp() {
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
	void createComment() {
		// given, when
		Comment comment = Comment.builder()
				.comment("안녕 김우유")
				.createdTime(LocalDateTime.now())
				.member(member)
				.board(board)
				.build();

		// then
		Assertions.assertThat(comment).as("잘 만들어졌니?").isNotNull();
		Assertions.assertThat(comment.getComment()).isEqualTo("안녕 김우유");
		Assertions.assertThat(comment.getMember()).isEqualTo(member);
		Assertions.assertThat(comment.getBoard()).isEqualTo(board);
	}

	@Test
	void updateComment() {
		// given
		Comment comment = Comment.builder()
				.comment("안녕 김우유")
				.createdTime(LocalDateTime.now())
				.member(member)
				.board(board)
				.build();

		CommentUpdateDto commentUpdateDto = CommentUpdateDto.builder()
				.comment("Hello KimMilk")
				.build();

		// when
		comment.updateComment(commentUpdateDto);

		// then
		Assertions.assertThat(comment.getComment()).as("내용이 잘 바뀌었는가?").isEqualTo(commentUpdateDto.getComment());
	}
}