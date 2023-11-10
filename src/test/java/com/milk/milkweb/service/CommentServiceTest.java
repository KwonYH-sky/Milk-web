package com.milk.milkweb.service;

import com.milk.milkweb.constant.Role;
import com.milk.milkweb.dto.CommentFormDto;
import com.milk.milkweb.entity.Board;
import com.milk.milkweb.entity.Comment;
import com.milk.milkweb.entity.Member;
import com.milk.milkweb.repository.BoardRepository;
import com.milk.milkweb.repository.CommentRepository;
import com.milk.milkweb.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

	private Member member;

	private Board board;

	private Comment comment;

	@Mock
	private BoardRepository boardRepository;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private CommentRepository commentRepository;
	@InjectMocks
	private CommentService commentService;

	@BeforeEach
	void setUp() {
		member = Member.builder()
				.id(1L)
				.name("김우유")
				.email("test@test")
				.password("1234")
				.role(Role.USER)
				.build();

		board = Board.builder()
				.id(1L)
				.title("TEST")
				.content("Test")
				.views(0)
				.createdTime(LocalDateTime.now())
				.member(member)
				.build();
		comment = Comment.builder()
				.id(1L)
				.comment("테스트")
				.board(board)
				.member(member)
				.createdTime(LocalDateTime.now())
				.build();
	}

	@Test
	@DisplayName("댓글 작성 저장 테스트")
	void saveCommentTest() {
		// given
		CommentFormDto commentFormDto = CommentFormDto.builder()
				.comment("test")
				.boardId(board.getId())
				.build();
		Comment dummyComment = Comment.builder()
				.comment(commentFormDto.getComment())
				.createdTime(LocalDateTime.now())
				.updatedTime(LocalDateTime.now())
				.member(member)
				.board(board)
				.build();
		given(boardRepository.findById(board.getId())).willReturn(Optional.ofNullable(board));
		given(memberRepository.findByEmail(member.getEmail())).willReturn(member);
		given(commentRepository.save(any())).willReturn(dummyComment);

		// when
		commentService.saveComment(commentFormDto, member.getEmail());

		// then
		verify(commentRepository, times(1)).save(any());
	}

	@Test
	@DisplayName("Comment 권한 테스트")
	void validateMemberCommentTest() {
		// given
		given(commentRepository.findById(comment.getId())).willReturn(Optional.ofNullable(comment));
		given(memberRepository.findByEmail(member.getEmail())).willReturn(member);

		// when
		Comment validateComment = commentService.validateMemberComment(comment.getId(), member.getEmail());

		// then
		assertThat(validateComment).as("존재 여부").isNotNull();
	}

	@Test
	@DisplayName("Comment 권한 테스트 - ADMIN")
	void validateAdminMemberCommentTest() {
		// given
		given(commentRepository.findById(comment.getId())).willReturn(Optional.ofNullable(comment));
		given(memberRepository.findByEmail(any())).willReturn(Member.builder().role(Role.ADMIN).build());

		// when
		Comment validateComment = commentService.validateMemberComment(comment.getId(), anyString());

		// then
		assertThat(validateComment).as("존재 여부").isNotNull();
	}

}