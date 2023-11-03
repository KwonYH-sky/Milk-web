package com.milk.milkweb.repository;


import com.milk.milkweb.config.TestQueryDslConfig;
import com.milk.milkweb.constant.Role;
import com.milk.milkweb.entity.Board;
import com.milk.milkweb.entity.BoardLike;
import com.milk.milkweb.entity.Member;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

@DataJpaTest
@Import(TestQueryDslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BoardLikeRepositoryTest {

	private Board board;

	private Member member;

	@Autowired
	private BoardLikeRepository boardLikeRepository;

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
		member = memberRepository.save(member);

		board = Board.builder()
				.title("게시물")
				.content("냉무")
				.views(0)
				.createdTime(LocalDateTime.now())
				.build();
		board = boardRepository.save(board);
	}

	@Test
	@DisplayName("BoardLike 저장 테스트")
	void saveBoardLike() {
		// given
		BoardLike boardLike = BoardLike.createBoardLikeEntity(board, member);

		// when
		BoardLike savedBoardLike = boardLikeRepository.save(boardLike);

		// then
		Assertions.assertThat(savedBoardLike).as("존재 여부").isNotNull();
		Assertions.assertThat(savedBoardLike.getBoard()).as("Board 일치").isEqualTo(board);
		Assertions.assertThat(savedBoardLike.getMember()).as("Member 일치").isEqualTo(member);

	}

	@Test
	@DisplayName("Member와 Board를 통해 Like 찾기")
	void findBoardLike() {
		// given
		BoardLike boardLike = BoardLike.createBoardLikeEntity(board, member);
		boardLikeRepository.save(boardLike);

		// when
		BoardLike foundBoardLike = boardLikeRepository.findByBoardAndMember(board, member).orElseThrow(EntityNotFoundException::new);

		// then
		Assertions.assertThat(foundBoardLike).as("존재 여부").isNotNull();
		Assertions.assertThat(foundBoardLike.getBoard().getId()).as("Board 일치").isEqualTo(board.getId());
		Assertions.assertThat(foundBoardLike.getMember().getId()).as("Member 일치").isEqualTo(member.getId());

	}
}