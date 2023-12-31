package com.milk.milkweb.service;

import com.milk.milkweb.dto.BoardLikeDto;
import com.milk.milkweb.entity.Board;
import com.milk.milkweb.entity.BoardLike;
import com.milk.milkweb.entity.Member;
import com.milk.milkweb.exception.AlreadyLikeException;
import com.milk.milkweb.repository.BoardLikeRepository;
import com.milk.milkweb.repository.BoardRepository;
import com.milk.milkweb.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardLikeService {

	private final BoardRepository boardRepository;
	private final MemberRepository memberRepository;
	private final BoardLikeRepository boardLikeRepository;

	public void addBoardLike(BoardLikeDto boardLikeDto, String email) {
		Board board = boardRepository.findById(boardLikeDto.getBoardId()).orElseThrow(EntityNotFoundException::new);
		Member member = Optional.ofNullable(memberRepository.findByEmail(email)).orElseThrow(EntityNotFoundException::new);

		if(boardLikeRepository.findByBoardAndMember(board, member).isPresent()){
			throw new AlreadyLikeException("이미 좋아요를 했습니다.");
		}

		boardLikeRepository.save(BoardLike.createBoardLikeEntity(board, member));
	}

	public int getBoardLike(Long id) {
		Board board = boardRepository.findById(id).orElseThrow(EntityNotFoundException::new);
		return board.getLikes().size();
	}
}
