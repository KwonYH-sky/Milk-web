package com.milk.milkweb.service;

import com.milk.milkweb.constant.Role;
import com.milk.milkweb.dto.BoardFormDto;
import com.milk.milkweb.dto.BoardListDto;
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
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

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
		MockitoAnnotations.openMocks(this);
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
		Board checkBoard = boardService.validateMemberBoard(board.getId(), member.getEmail());

		// then
		Assertions.assertThat(checkBoard).isNotNull();
	}

	@Test
	@DisplayName("Board 권한 테스트 - ADMIN")
	void validateAdminBoard() {
		// given
		Member admin = Member.builder().role(Role.ADMIN).build();
		given(memberRepository.findByEmail(member.getEmail())).willReturn(admin);
		given(boardRepository.findById(board.getId())).willReturn(Optional.ofNullable(board));

		// when
		Board checkBoard = boardService.validateMemberBoard(board.getId(), member.getEmail());

		// then
		Assertions.assertThat(checkBoard).isNotNull();
	}

	@Test
	@DisplayName("Board 권한 실패 테스트")
	void notValidateMemberBoard() {
		// given
		Member user = Member.builder().role(Role.USER).build();
		given(memberRepository.findByEmail(any())).willReturn(user);
		given(boardRepository.findById(board.getId())).willReturn(Optional.ofNullable(board));

		// when, then
		Assertions.assertThatThrownBy(() -> boardService.validateMemberBoard(board.getId(), member.getEmail())).isInstanceOf(MemberValidationException.class);
	}

	@Test
	@DisplayName("BoardList Test")
	void getBoardListTest() {
		// given
		List<Board> dummyBoardList = LongStream.range(2, 22).mapToObj(this::createBoard).toList();
		Page<Board> dummyBoardPage = new PageImpl<>(dummyBoardList, PageRequest.of(0, 10), dummyBoardList.size());
		given(boardRepository.findAll(any(Pageable.class))).willReturn(dummyBoardPage);

		// when
		Page<BoardListDto> page = boardService.getBoardList(0);

		// then
		Assertions.assertThat(page).as("존재 하는지").isNotEmpty();
	}



	public Board createBoard(Long n) {
		return Board.builder()
				.id(n)
				.title("test " + n)
				.content("test")
				.member(member)
				.views(0)
				.createdTime(LocalDateTime.now())
				.build();
	}
}