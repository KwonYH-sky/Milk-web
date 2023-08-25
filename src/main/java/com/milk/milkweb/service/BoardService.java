package com.milk.milkweb.service;


import com.milk.milkweb.dto.BoardFormDto;
import com.milk.milkweb.entity.Board;
import com.milk.milkweb.entity.Member;
import com.milk.milkweb.repository.BoardRepository;
import com.milk.milkweb.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

	private final BoardRepository boardRepository;
	private final MemberRepository memberRepository;


	public Board saveBoard(BoardFormDto boardFormDto, String email) {
		Member member = memberRepository.findByEmail(email);
		Board board = Board.builder()
					.title(boardFormDto.getTitle())
					.content(boardFormDto.getContent())
					.createdTime(LocalDateTime.now())
					.updatedTime(LocalDateTime.now())
					.views(0)
					.likes(0)
					.member(member)
					.build();

		return boardRepository.save(board);
	}


}
