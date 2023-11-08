package com.milk.milkweb.repository;

import com.milk.milkweb.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardCustomRepository {
	Page<Board> findAll(Pageable pageable);

	@Query("SELECT b FROM Board b ORDER BY b.id DESC")
	List<Board> findMainBoards(Pageable pageable);
}
