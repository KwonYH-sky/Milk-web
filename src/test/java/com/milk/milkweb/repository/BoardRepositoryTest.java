package com.milk.milkweb.repository;

import com.milk.milkweb.config.TestQueryDslConfig;
import com.milk.milkweb.constant.Role;
import com.milk.milkweb.dto.BoardListDto;
import com.milk.milkweb.dto.BoardSearchDto;
import com.milk.milkweb.entity.Board;
import com.milk.milkweb.entity.BoardLike;
import com.milk.milkweb.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestQueryDslConfig.class)
class BoardRepositoryTest {

	private Member member;
	
	@Autowired
	private BoardRepository boardRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private BoardLikeRepository boardLikeRepository;

	@BeforeEach
	void setUp() {
		member = Member.builder()
				.email("test@test")
				.password("1234")
				.name("김우유")
				.role(Role.ADMIN)
				.build();
		memberRepository.save(member);
	}

	@Test
	@DisplayName("게시물 생성 테스트")
	void createBoard() {
		// given
		Board board = createTestBoard();
		
		// when
		Board savedBoard = boardRepository.save(board);
		
		// then
		Assertions.assertThat(savedBoard).as("존재 여부").isNotNull();
		Assertions.assertThat(savedBoard.getTitle()).isEqualTo(board.getTitle());
		Assertions.assertThat(savedBoard.getContent()).isEqualTo(board.getContent());
		Assertions.assertThat(savedBoard.getMember()).isEqualTo(board.getMember());
	}

	@Test
	@DisplayName("페이징 불러오기 테스트")
	void findPage() {
		// given
		List<Board> boards = IntStream.rangeClosed(0, 10).mapToObj(e -> createTestBoard()).toList(); // 총 11개 만들어짐
		boardRepository.saveAll(boards);

		Pageable pageable = PageRequest.of(0, 10);

		// when
		Page<Board> page = boardRepository.findAll(pageable);

		// then
		Assertions.assertThat(page).as("존재 여부").isNotNull();
		Assertions.assertThat(page.getTotalElements()).as("저장된 Board 개수 일치").isGreaterThanOrEqualTo(boards.size());
		Assertions.assertThat(page.getNumber()).as("현재 페이지 일치").isEqualTo(0);
		Assertions.assertThat(page.getTotalPages()).as("전체 페이지 수").isGreaterThanOrEqualTo(2);
	}

	@Test
	@DisplayName("메인 전체 게시판 불러오기")
	void findMainBoards() {
		// given
		List<Board> boards = IntStream.rangeClosed(0, 10).mapToObj(e -> createTestBoard()).toList();
		boardRepository.saveAll(boards);

		// when
		List<Board> mainBoards = boardRepository.findMainBoards(PageRequest.of(0, 10));

		// then
		Assertions.assertThat(mainBoards).as("존재 여부").isNotNull();
		Assertions.assertThat(mainBoards.size()).as("10개 인지").isEqualTo(10);
	}

	@Test
	@DisplayName("데일리 베스트 찾기 테스트")
	void findDailyBestBoards() {
		// given
		List<Board> boards = IntStream.rangeClosed(0, 7).mapToObj(e -> createTestBoard()).toList();
		boardRepository.saveAll(boards);

		boards.forEach(board -> IntStream.range(0, 3).forEach(i -> boardLikeRepository.save(createTestBoardLike(board))));

		// when
		List<Board> dailyBoards = boardRepository.findDailyBest();

		// then
		Assertions.assertThat(dailyBoards).as("비어있는지 여부").isNotEmpty();
		Assertions.assertThat(dailyBoards.size()).as("5개인지").isEqualTo(5);
		Assertions.assertThat(dailyBoards.stream().findFirst().get().getTitle()).isEqualTo("테스트");
	}

	@Test
	@DisplayName("데일리 베스트 못 찾기 테스트 - 좋아요가 없을 시")
	void notFindDailyBestBoards() {
		// given
		List<Board> boards = IntStream.rangeClosed(0, 7).mapToObj(e -> createTestBoard()).toList();
		boardRepository.saveAll(boards);

		// when
		List<Board> dailyBoards = boardRepository.findDailyBest();

		// then
		Assertions.assertThat(dailyBoards).as("비어있는지 여부").isEmpty();
	}

	@Test
	@DisplayName("주간 베스트 찾기 테스트")
	void findWeeklyBestBoards() {
		// given
		List<Board> boards = LongStream.rangeClosed(0, 10).mapToObj(this::createTestBoardTime).toList();
		boardRepository.saveAll(boards);

		boards.forEach(board -> IntStream.range(0, 3).forEach(i -> boardLikeRepository.save(createTestBoardLike(board))));

		// when
		List<Board> weeklyBoards = boardRepository.findWeeklyBest();

		// then
		Assertions.assertThat(weeklyBoards).as("비어있는지 여부").isNotEmpty();
		Assertions.assertThat(weeklyBoards.size()).as("5개인지").isEqualTo(5);
		Assertions.assertThat(weeklyBoards.stream().findFirst().get().getTitle()).isEqualTo("테스트");
	}

	@Test
	@DisplayName("주간 베스트 못 찾기 테스트 - 좋아요가 없을 시")
	void notFindWeeklyBestBoards() {
		// given
		List<Board> boards = LongStream.rangeClosed(0, 10).mapToObj(this::createTestBoardTime).toList();
		boardRepository.saveAll(boards);

		// when
		List<Board> weeklyBoards = boardRepository.findWeeklyBest();

		// then
		Assertions.assertThat(weeklyBoards).as("비어있는지 여부").isEmpty();
	}

	@Test
	@DisplayName("Search 테스트")
	void searchTest() {
		// given
		List<Board> boards = IntStream.rangeClosed(0, 10).mapToObj(e -> createTestBoard()).toList();
		boardRepository.saveAll(boards);

		Pageable pageable = PageRequest.of(0, 10);

		BoardSearchDto boardSearchDto = new BoardSearchDto();
		boardSearchDto.setSearchType("title");
		boardSearchDto.setKeyword("테");

		// when
		Page<BoardListDto> searchedBoards = boardRepository.findBySearch(boardSearchDto, pageable);

		// then
		Assertions.assertThat(searchedBoards).isNotEmpty();
		Assertions.assertThat(searchedBoards.stream().findFirst().get().getTitle()).containsAnyOf(boardSearchDto.getKeyword());
		Assertions.assertThat(searchedBoards.getTotalElements()).isGreaterThanOrEqualTo(boards.size());
	}

	@Test
	@DisplayName("NOT Search 테스트")
	void notSearchTest() {
		// given
		List<Board> boards = IntStream.rangeClosed(0, 10).mapToObj(e -> createTestBoard()).toList();
		boardRepository.saveAll(boards);

		Pageable pageable = PageRequest.of(0, 10);

		BoardSearchDto boardSearchDto = new BoardSearchDto();
		boardSearchDto.setSearchType("title");
		boardSearchDto.setKeyword("몰?루");

		// when
		Page<BoardListDto> searchedBoards = boardRepository.findBySearch(boardSearchDto, pageable);

		// then
		Assertions.assertThat(searchedBoards).isEmpty();
	}

	private Board createTestBoard() {
		return Board.builder()
				.title("테스트")
				.content("테스트")
				.views(0)
				.member(member)
				.createdTime(LocalDateTime.now())
				.build();
	}

	private Board createTestBoardTime(Long days) {
		return Board.builder()
				.title("테스트")
				.content("테스트")
				.views(0)
				.member(member)
				.createdTime(LocalDateTime.now().minusDays(days))
				.build();
	}


	private BoardLike createTestBoardLike(Board board) {
		return BoardLike.createBoardLikeEntity(board, member);
	}
}