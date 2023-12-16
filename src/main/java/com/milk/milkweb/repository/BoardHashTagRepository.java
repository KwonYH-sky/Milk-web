package com.milk.milkweb.repository;

import com.milk.milkweb.entity.BoardHashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardHashTagRepository extends JpaRepository<BoardHashTag, Long> {

	List<BoardHashTag> findAllByBoardId(Long boardId);
}
