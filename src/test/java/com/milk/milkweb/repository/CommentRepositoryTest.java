package com.milk.milkweb.repository;

import com.milk.milkweb.config.TestQueryDslConfig;
import com.milk.milkweb.constant.Role;
import com.milk.milkweb.entity.Board;
import com.milk.milkweb.entity.Comment;
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
@Import(TestQueryDslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentRepositoryTest {

	private Member member;

	private Board board;

	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private BoardRepository boardRepository;
	@Autowired
	private CommentRepository commentRepository;

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
	@DisplayName("Comment 저장 테스트")
	void savedComment() {
		// given
		Comment comment = createComment();

		// when
		Comment savedComment = commentRepository.save(comment);

		// then
		Assertions.assertThat(savedComment).as("잘 저장?").isNotNull();
		Assertions.assertThat(savedComment.getComment()).isEqualTo("테스트");
		Assertions.assertThat(savedComment.getBoard()).isEqualTo(board);
		Assertions.assertThat(savedComment.getMember()).isEqualTo(member);
	}

	@Test
	@DisplayName("Board Id로 Comment Page 찾기")
	void findByBoardId() {
		// given
		List<Comment> comments = IntStream.rangeClosed(0, 20).mapToObj(e -> createComment()).toList();
		commentRepository.saveAll(comments);

		Pageable pageable = PageRequest.of(0, 15);

		// when
		Page<Comment> foundCommentPage = commentRepository.findByBoardId(board.getId(), pageable);

		// then
		Assertions.assertThat(foundCommentPage).as("존재 여부").isNotNull();
		Assertions.assertThat(foundCommentPage.getTotalElements()).as("요소 개수").isEqualTo(comments.size());
		Assertions.assertThat(foundCommentPage.stream().findFirst()).isEqualTo(comments.stream().findFirst());
		Assertions.assertThat(foundCommentPage.getNumber()).isEqualTo(0);
	}

	private Comment createComment() {
		return  Comment.builder()
				.comment("테스트")
				.board(board)
				.member(member)
				.createdTime(LocalDateTime.now())
				.build();
	}
}