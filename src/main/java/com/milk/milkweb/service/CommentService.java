package com.milk.milkweb.service;

import com.milk.milkweb.dto.CommentFormDto;
import com.milk.milkweb.dto.CommentListDto;
import com.milk.milkweb.dto.CommentUpdateDto;
import com.milk.milkweb.entity.Board;
import com.milk.milkweb.entity.Comment;
import com.milk.milkweb.entity.Member;
import com.milk.milkweb.exception.MemberValidation;
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

	@Transactional(readOnly = true)
	public Page<CommentListDto> getCommentList(int page, Long boardId, String email) {
		Pageable pageable = PageRequest.of(page, 10);
		Page<Comment> comments = commentRepository.findByBoardId(boardId, pageable);
		Member member = Optional.of(memberRepository.findByEmail(email)).orElseThrow(EntityNotFoundException::new);

		Page<CommentListDto> commentListDtos = comments.map(comment -> CommentListDto.builder()
				.id(comment.getId())
				.memberName(comment.getMember().getName())
				.comment(comment.getComment())
				.isUserCommentAuthor(member.equals(comment.getMember()))
				.build());

		return commentListDtos;
	}

	public void updateComment(CommentUpdateDto commentUpdateDto, String email) {
		Comment comment = validateMemberComment(commentUpdateDto.getId(), email);
		comment.updateComment(commentUpdateDto);
	}

	public void deleteComment(Long id, String email) {
		Comment comment = validateMemberComment(id, email);
		commentRepository.delete(comment);
	}

	public Comment validateMemberComment(Long id, String email) {
		Comment comment = commentRepository.findById(id).orElseThrow(EntityNotFoundException::new);
		Member member = Optional.of(memberRepository.findByEmail(email)).orElseThrow(EntityNotFoundException::new);

		if (!member.equals(comment.getMember())) {
			throw new MemberValidation("수정 & 삭제 권한이 없습니다.");
		}

		return comment;
	}
}
