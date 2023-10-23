package com.milk.milkweb.repository;

import com.milk.milkweb.dto.BoardListDto;
import com.milk.milkweb.dto.BoardSearchDto;
import com.milk.milkweb.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardCustomRepository {

	Page<BoardListDto> findBySearch(BoardSearchDto boardSearchDto, Pageable pageable);

	List<Board> findDailyBest();

	List<Board> findWeeklyBest();

}
