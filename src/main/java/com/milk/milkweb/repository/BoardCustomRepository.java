package com.milk.milkweb.repository;

import com.milk.milkweb.dto.BoardListDto;
import com.milk.milkweb.dto.BoardSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardCustomRepository {

	Page<BoardListDto> findBySearch(BoardSearchDto boardSearchDto, Pageable pageable);
}
