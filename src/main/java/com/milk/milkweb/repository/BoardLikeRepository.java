package com.milk.milkweb.repository;

import com.milk.milkweb.entity.Board;
import com.milk.milkweb.entity.BoardLike;
import com.milk.milkweb.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
	Optional<BoardLike> findByBoardAndMember(Board board, Member member);


}
