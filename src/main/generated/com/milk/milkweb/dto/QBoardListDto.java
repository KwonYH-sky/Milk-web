package com.milk.milkweb.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.milk.milkweb.dto.QBoardListDto is a Querydsl Projection type for BoardListDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QBoardListDto extends ConstructorExpression<BoardListDto> {

    private static final long serialVersionUID = -544779995L;

    public QBoardListDto(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> title, com.querydsl.core.types.Expression<String> memberName, com.querydsl.core.types.Expression<Integer> likes, com.querydsl.core.types.Expression<Integer> views, com.querydsl.core.types.Expression<java.time.LocalDateTime> createdTime) {
        super(BoardListDto.class, new Class<?>[]{long.class, String.class, String.class, int.class, int.class, java.time.LocalDateTime.class}, id, title, memberName, likes, views, createdTime);
    }

}

