package com.milk.milkweb.repository;

import com.milk.milkweb.config.TestQueryDslConfig;
import com.milk.milkweb.entity.BoardImg;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestQueryDslConfig.class)
class BoardImgRepositoryTest {

	@Autowired
	private BoardImgRepository boardImgRepository;

	private BoardImg boardImg;

	@BeforeEach
	void setUp() {
		boardImg = BoardImg.builder()
				.imgName("test")
				.imgOriName("testImg")
				.path("testPath")
				.createdTime(LocalDateTime.now())
				.build();
	}

	@Test
	@DisplayName("BoardImg 저장 테스트")
	void saveTest() {
		// given, when
		BoardImg savedImg = boardImgRepository.save(boardImg);

		// then
		assertThat(savedImg).as("존재 여부").isNotNull();
		assertThat(savedImg.getImgName()).isEqualTo(boardImg.getImgName());
		assertThat(savedImg.getImgOriName()).isEqualTo(boardImg.getImgOriName());
	}

	@Test
	@DisplayName("BoardImg ImgName으로 찾기 테스트")
	void findByImgNameTest() {
		// given
		boardImgRepository.save(boardImg);

		// when
		BoardImg foundImg = boardImgRepository.findByImgName(boardImg.getImgName()).orElseThrow(EntityNotFoundException::new);

		// then
		assertThat(foundImg).as("존재 여부").isNotNull();

	}
}