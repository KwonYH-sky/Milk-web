package com.milk.milkweb.service;

import com.milk.milkweb.dto.CommentFormDto;
import com.milk.milkweb.dto.CommentListDto;
import com.milk.milkweb.entity.Board;
import com.milk.milkweb.entity.Comment;
import com.milk.milkweb.entity.Member;
import com.milk.milkweb.repository.BoardRepository;
import com.milk.milkweb.repository.CommentRepository;
import com.milk.milkweb.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

	private final BoardRepository boardRepository;
	private final MemberRepository memberRepository;
	private final CommentRepository commentRepository;

	public Comment saveComment(CommentFormDto commentFormDto, String email) {
		Board board = boardRepository.findById(commentFormDto.getBoardId()).orElseThrow(EntityNotFoundException::new);
		Member member = Optional.of(memberRepository.findByEmail(email)).orElseThrow(EntityNotFoundException::new);

		Comment comment = Comment.builder()
				.comment(commentFormDto.getComment())
				.createdTime(LocalDateTime.now())
				.updatedTime(LocalDateTime.now())
				.member(member)
				.board(board)
				.build();

		return commentRepository.save(comment);
	}

	public Page<CommentListDto> getCommentList(int page, Long boardId) {
		Pageable pageable = PageRequest.of(page, 10);
		Page<Comment> comments = commentRepository.findByBoardId(boardId, pageable);

		 Page<CommentListDto> commentListDtos = comments.map(comment -> CommentListDto.builder()
				.memberName(comment.getMember().getName())
				.comment(comment.getComment())
				.build());

		 return commentListDtos;
	}

}
