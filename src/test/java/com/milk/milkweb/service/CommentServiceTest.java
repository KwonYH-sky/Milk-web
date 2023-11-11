package com.milk.milkweb.service;

import com.milk.milkweb.constant.Role;
import com.milk.milkweb.dto.CommentFormDto;
import com.milk.milkweb.dto.CommentListDto;
import com.milk.milkweb.dto.CommentUpdateDto;
import com.milk.milkweb.entity.Board;
import com.milk.milkweb.entity.Comment;
import com.milk.milkweb.entity.Member;
import com.milk.milkweb.exception.MemberValidationException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

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

	@Test
	@DisplayName("Comment 권한 실패 테스트")
	void notValidateMemberCommentTest() {
		// given
		given(commentRepository.findById(comment.getId())).willReturn(Optional.ofNullable(comment));
		given(memberRepository.findByEmail(any())).willReturn(Member.builder().role(Role.USER).build());

		// when, then
		assertThatThrownBy(() -> commentService.validateMemberComment(comment.getId(), anyString())).as("수정, 삭제 권한 실패")
				.isInstanceOf(MemberValidationException.class);
	}

	@Test
	@DisplayName("댓글 페이지 테스트")
	void getCommentListTest() {
		// given
		List<Comment> dummyComment = LongStream.range(2, 17).mapToObj(this::createComment).toList();
		Page<Comment> dummyPage = new PageImpl<>(dummyComment, PageRequest.of(0, 15), dummyComment.size());
		given(commentRepository.findByBoardId(any(), any())).willReturn(dummyPage);
		given(memberRepository.findByEmail(member.getEmail())).willReturn(member);

		// when
		Page<CommentListDto> page = commentService.getCommentList(0, board.getId(), member.getEmail());

		// then
		assertThat(page).as("존재 여부").isNotEmpty();
	}

	@Test
	@DisplayName("댓글 수정 테스트")
	void updateCommentTest() {
		// given
		CommentUpdateDto commentUpdateDto = CommentUpdateDto.builder()
				.id(comment.getId())
				.comment("뿡")
				.build();
		given(commentRepository.findById(comment.getId())).willReturn(Optional.ofNullable(comment));
		given(memberRepository.findByEmail(member.getEmail())).willReturn(member);

		// when
		commentService.updateComment(commentUpdateDto, member.getEmail());

		// then
		assertThat(comment.getComment()).as("변경 되었는지").isEqualTo(commentUpdateDto.getComment());
		assertThat(comment.getUpdatedTime()).as("업데이트 시점 확인").isBeforeOrEqualTo(LocalDateTime.now());
	}

	private Comment createComment(Long id) {
		return Comment.builder()
				.id(id)
				.comment("Without a fight")
				.createdTime(LocalDateTime.now())
				.board(board)
				.member(member)
				.build();
	}
}