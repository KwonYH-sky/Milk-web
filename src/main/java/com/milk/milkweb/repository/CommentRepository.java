package com.milk.milkweb.repository;

import com.milk.milkweb.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	Page<Comment> findByBoardId(Long boardId, Pageable pageable);
}
