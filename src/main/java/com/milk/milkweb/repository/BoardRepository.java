package com.milk.milkweb.repository;

import com.milk.milkweb.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {

}
