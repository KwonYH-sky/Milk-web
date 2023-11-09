package com.milk.milkweb.service;

import com.milk.milkweb.constant.Role;
import com.milk.milkweb.dto.*;
import com.milk.milkweb.entity.Board;
import com.milk.milkweb.entity.Member;
import com.milk.milkweb.exception.MemberValidationException;
import com.milk.milkweb.repository.BoardRepository;
import com.milk.milkweb.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
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

@ExtendWith(MockitoExtension.class)
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
	@DisplayName("전체 게시물 목록 테스트")
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
	
	@Test
	@DisplayName("게시물 상세정보 테스트")
	void getDetailBoardTest() {
		// given
		given(boardRepository.findById(board.getId())).willReturn(Optional.ofNullable(board));

		// when
		BoardDetailDto boardDetailDto = boardService.getDetail(board.getId(), member);

		// then
		Assertions.assertThat(boardDetailDto).as("존재 여부").isNotNull();
		Assertions.assertThat(boardDetailDto.getTitle()).isEqualTo(board.getTitle());
		Assertions.assertThat(boardDetailDto.getContent()).isEqualTo(board.getContent());
		Assertions.assertThat(board.getViews()).as("조회수 증가 했는지").isGreaterThanOrEqualTo(1);
		Assertions.assertThat(boardDetailDto.isAuthorized()).as("수정 삭제 권한이 있는지").isTrue();

	}

	@Test
	@DisplayName("게시물 수정 폼 테스트")
	void getUpdateFormTest() {
		// given
		given(memberRepository.findByEmail(member.getEmail())).willReturn(member);
		given(boardRepository.findById(board.getId())).willReturn(Optional.ofNullable(board));

		// when
		BoardUpdateDto boardUpdateDto = boardService.getUpdateForm(board.getId(), member.getEmail());

		// then
		Assertions.assertThat(boardUpdateDto).as("존재 여부").isNotNull();
		Assertions.assertThat(boardUpdateDto.getId()).isEqualTo(board.getId());
		Assertions.assertThat(boardUpdateDto.getTitle()).isEqualTo(board.getTitle());
		Assertions.assertThat(boardUpdateDto.getContent()).isEqualTo(board.getContent());
	}

	@Test
	@DisplayName("메인 게시물 테스트")
	void getMainBoardListTest() {
		// given
		List<Board> dummyBoardList = LongStream.range(2, 22).mapToObj(this::createBoard).toList();
		given(boardRepository.findMainBoards(any(Pageable.class))).willReturn(dummyBoardList);

		// when
		List<MainBoardDto> mainBoard = boardService.getMainBoard();

		// then
		Assertions.assertThat(mainBoard).as("존재 하는지").isNotEmpty();
	}

	@Test
	@DisplayName("메인 Daily Best 테스트")
	void getDailyBoardTest() {
		List<Board> dummyBoardList = LongStream.range(2, 22).mapToObj(this::createBoard).toList();
		given(boardRepository.findDailyBest()).willReturn(dummyBoardList);

		// when
		List<MainBoardDto> dailyBest = boardService.getDailyBest();

		// then
		Assertions.assertThat(dailyBest).as("존재 하는지").isNotEmpty();
	}

	@Test
	@DisplayName("메인 Weekly Best 테스트")
	void getWeeklyBoardTest() {
		List<Board> dummyBoardList = LongStream.range(2, 22).mapToObj(this::createBoard).toList();
		given(boardRepository.findWeeklyBest()).willReturn(dummyBoardList);

		// when
		List<MainBoardDto> dailyBest = boardService.getWeeklyBest();

		// then
		Assertions.assertThat(dailyBest).as("존재 하는지").isNotEmpty();
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