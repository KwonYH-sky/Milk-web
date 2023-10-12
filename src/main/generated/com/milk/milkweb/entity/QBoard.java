package com.milk.milkweb.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBoard is a Querydsl query type for Board
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoard extends EntityPathBase<Board> {

    private static final long serialVersionUID = -1163601610L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBoard board = new QBoard("board");

    public final ListPath<Comment, QComment> comments = this.<Comment, QComment>createList("comments", Comment.class, QComment.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdTime = createDateTime("createdTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<BoardLike, QBoardLike> likes = this.<BoardLike, QBoardLike>createList("likes", BoardLike.class, QBoardLike.class, PathInits.DIRECT2);

    public final QMember member;

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.LocalDateTime> updatedTime = createDateTime("updatedTime", java.time.LocalDateTime.class);

    public final NumberPath<Integer> views = createNumber("views", Integer.class);

    public QBoard(String variable) {
        this(Board.class, forVariable(variable), INITS);
    }

    public QBoard(Path<? extends Board> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBoard(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBoard(PathMetadata metadata, PathInits inits) {
        this(Board.class, metadata, inits);
    }

    public QBoard(Class<? extends Board> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

