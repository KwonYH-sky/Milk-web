package com.milk.milkweb.service;

import com.milk.milkweb.constant.Role;
import com.milk.milkweb.dto.BoardFormDto;
import com.milk.milkweb.entity.Board;
import com.milk.milkweb.entity.Member;
import com.milk.milkweb.exception.MemberValidationException;
import com.milk.milkweb.repository.BoardRepository;
import com.milk.milkweb.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;


import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class BoardServiceTest {

	private Board board;

	private Member member;

	@Mock
	private BoardRepository boardRepository;

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private BoardService boardService;

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
	@DisplayName("Board 저장 테스트")
	void saveBoard() {
		// given
		BoardFormDto boardFormDto = new BoardFormDto();
		boardFormDto.setTitle(board.getTitle());
		boardFormDto.setContent(board.getContent());

		given(memberRepository.findByEmail(member.getEmail())).willReturn(member);
		given(boardRepository.save(any())).willReturn(board);

		// when
		Board savedBoard = boardService.saveBoard(boardFormDto, member.getEmail());

		// then
		Assertions.assertThat(savedBoard.getTitle()).isEqualTo(board.getTitle());
		Assertions.assertThat(savedBoard.getContent()).isEqualTo(board.getContent());
		Assertions.assertThat(savedBoard.getMember()).isEqualTo(board.getMember());
	}

	@Test
	@DisplayName("Board 권한 테스트")
	void validateMemberBoard() {
		// given
		given(memberRepository.findByEmail(member.getEmail())).willReturn(member);
		given(boardRepository.findById(board.getId())).willReturn(Optional.ofNullable(board));

		// when
		Board checkBoarad = boardService.validateMemberBoard(board.getId(), member.getEmail());

		// then
		Assertions.assertThat(checkBoarad).isNotNull();
	}

	@Test
	@DisplayName("Board 권한 테스트 - ADMIN")
	void validateAdminBoard() {
		// given
		given(memberRepository.findByEmail(member.getEmail())).willReturn(Member.builder().role(Role.ADMIN).build());
		given(boardRepository.findById(board.getId())).willReturn(Optional.ofNullable(board));

		// when
		Board checkBoarad = boardService.validateMemberBoard(board.getId(), member.getEmail());

		// then
		Assertions.assertThat(checkBoarad).isNotNull();
	}

	@Test
	@DisplayName("Board 권한 실패 테스트")
	void notValidateMemberBoard() {
		// given
		given(memberRepository.findByEmail(any())).willReturn(any());
		given(boardRepository.findById(board.getId())).willReturn(Optional.ofNullable(board));

		// when, then
		Assertions.assertThatThrownBy(() -> boardService.validateMemberBoard(board.getId(), member.getEmail())).isInstanceOf(MemberValidationException.class);
	}
}