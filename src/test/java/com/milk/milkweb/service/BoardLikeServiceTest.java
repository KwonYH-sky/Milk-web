package com.milk.milkweb.service;

import com.milk.milkweb.constant.Role;
import com.milk.milkweb.dto.BoardLikeDto;
import com.milk.milkweb.entity.Board;
import com.milk.milkweb.entity.BoardLike;
import com.milk.milkweb.entity.Member;
import com.milk.milkweb.exception.AlreadyLikeException;
import com.milk.milkweb.repository.BoardLikeRepository;
import com.milk.milkweb.repository.BoardRepository;
import com.milk.milkweb.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardLikeServiceTest {

	private Member member;
	private Board board;

	@Mock
	private MemberRepository memberRepository;
	@Mock
	private BoardRepository boardRepository;
	@Mock
	private BoardLikeRepository boardLikeRepository;

	@InjectMocks
	private BoardLikeService boardLikeService;

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
	}

	@Test
	@DisplayName("좋아요 추가 테스트")
	void addBoardLikeTest() {
		// given
		given(memberRepository.findByEmail(member.getEmail())).willReturn(member);
		given(boardRepository.findById(board.getId())).willReturn(Optional.ofNullable(board));
		given(boardLikeRepository.findByBoardAndMember(board, member)).willReturn(Optional.empty());
		given(boardLikeRepository.save(any())).willReturn(BoardLike.createBoardLikeEntity(board, member));
		BoardLikeDto boardLikeDto = BoardLikeDto.builder()
				.boardId(board.getId())
				.build();

		// when
		boardLikeService.addBoardLike(boardLikeDto, member.getEmail());

		// then
		verify(boardLikeRepository, times(1)).save(any());
	}

	@Test
	@DisplayName("좋아요 이미 있음 테스트")
	void alreadyBoardLikeTest() {
		// given
		BoardLike boardLike = BoardLike.createBoardLikeEntity(board, member);
		given(memberRepository.findByEmail(member.getEmail())).willReturn(member);
		given(boardRepository.findById(board.getId())).willReturn(Optional.ofNullable(board));
		given(boardLikeRepository.findByBoardAndMember(board, member)).willReturn(Optional.ofNullable(boardLike));
		BoardLikeDto boardLikeDto = BoardLikeDto.builder()
				.boardId(board.getId())
				.build();

		// when, then
		Assertions.assertThatThrownBy(() -> boardLikeService.addBoardLike(boardLikeDto, member.getEmail())).isInstanceOf(AlreadyLikeException.class);
		verify(boardLikeRepository, never()).save(any());
	}

	@Test
	@DisplayName("좋아수 수 테스트")
	void getBoardLikeTest() {
		// given
		board.getLikes().add(BoardLike.builder().build());
		given(boardRepository.findById(board.getId())).willReturn(Optional.ofNullable(board));
		
		// when
		int likeCnt = boardLikeService.getBoardLike(board.getId());
		
		// then
		Assertions.assertThat(likeCnt).as("개수 체크").isEqualTo(1);

	}

}