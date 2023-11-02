package com.milk.milkweb.repository;

import com.milk.milkweb.config.TestQueryDslConfig;
import com.milk.milkweb.constant.Role;
import com.milk.milkweb.entity.Board;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestQueryDslConfig.class)
class BoardRepositoryTest {

	private Member member;
	
	@Autowired
	private BoardRepository boardRepository;

	@Autowired
	private MemberRepository memberRepository;

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
		Assertions.assertThat(page.getTotalElements()).as("저장된 Board 개수 일치").isEqualTo(boards.size());
		Assertions.assertThat(page.getNumber()).as("현재 페이지 일치").isEqualTo(0);
		Assertions.assertThat(page.getTotalPages()).as("전체 페이지 수").isEqualTo(2);
	}

	@Test
	void findMainBoards() {
		// given
		List<Board> boards = IntStream.rangeClosed(0, 10).mapToObj(e -> createTestBoard()).toList();
		boardRepository.saveAll(boards);

		// when
		List<Board> mainBoards = boardRepository.findMainBoards();

		// then
		Assertions.assertThat(mainBoards).as("존재 여부").isNotNull();
		Assertions.assertThat(mainBoards.size()).as("10개 인지").isEqualTo(10);
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
}