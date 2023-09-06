package com.milk.milkweb.repository;

import com.milk.milkweb.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
	Page<Board> findAll(Pageable pageable);
}
