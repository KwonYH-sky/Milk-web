package com.milk.milkweb.repository;

import com.milk.milkweb.dto.BoardListDto;
import com.milk.milkweb.dto.BoardSearchDto;
import com.milk.milkweb.dto.QBoardListDto;
import com.milk.milkweb.entity.Board;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.milk.milkweb.entity.QBoard.board;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardCustomRepository{

	private final JPAQueryFactory queryFactory;

	private BooleanExpression searchType(BoardSearchDto boardSearchDto) {
		String TYPE = boardSearchDto.getSearchType();
		String QUERY = "%" + boardSearchDto.getKeyword() + "%";

		if (StringUtils.equalsIgnoreCase(TYPE, "writer"))
			return board.member.name.like(QUERY);
		else if (StringUtils.equalsIgnoreCase(TYPE, "content"))
			return board.content.like(QUERY);
		else
			return board.title.like(QUERY);
	}

	@Override
	public Page<BoardListDto> findBySearch(BoardSearchDto boardSearchDto, Pageable pageable) {
		List<BoardListDto> contents = queryFactory
				.select(new QBoardListDto(
						board.id,
						board.title,
						board.member.name,
						board.likes.size(),
						board.views,
						board.createdTime))
				.from(board)
				.where(searchType(boardSearchDto))
				.orderBy(board.createdTime.desc())
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		Long count = queryFactory
				.select(board.count())
				.from(board)
				.where(searchType(boardSearchDto))
				.fetchOne();

		return new PageImpl<>(contents, pageable, count);
	}

	@Override
	public List<Board> findDailyBest() {
		return queryFactory
				.selectFrom(board)
				.where(
						board.createdTime.between(LocalDateTime.now().minusDays(1), LocalDateTime.now()),
						board.likes.size().goe(3))
				.orderBy(board.likes.size().desc())
				.limit(5)
				.fetch();
	}

	@Override
	public List<Board> findWeeklyBest() {
		return queryFactory
				.selectFrom(board)
				.where(
						board.likes.size().goe(3),
						board.createdTime.between(LocalDateTime.now().minusDays(7), LocalDateTime.now()))
				.orderBy(board.likes.size().desc())
				.limit(5)
				.fetch();
	}
}
