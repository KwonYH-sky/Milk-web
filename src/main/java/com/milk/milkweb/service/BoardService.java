package com.milk.milkweb.service;


import com.milk.milkweb.dto.BoardDetailDto;
import com.milk.milkweb.dto.BoardFormDto;
import com.milk.milkweb.dto.BoardListDto;
import com.milk.milkweb.dto.BoardUpdateDto;
import com.milk.milkweb.entity.Board;
import com.milk.milkweb.entity.Member;
import com.milk.milkweb.repository.BoardRepository;
import com.milk.milkweb.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

	@Transactional(readOnly = true)
	public List<BoardListDto> getBoardList() {
		List<Board> boards = boardRepository.findAll();
		List<BoardListDto> boardListDtos = new ArrayList<>();

		for (Board board : boards) {
			BoardListDto boardListDto = BoardListDto.builder()
					.id(board.getId())
					.title(board.getTitle())
					.memberName(board.getMember().getName())
					.views(board.getViews())
					.likes(board.getLikes())
					.createdTime(board.getCreatedTime())
					.build();
			boardListDtos.add(boardListDto);
		}

		return boardListDtos;
	}

	@Transactional(readOnly = true)
	public BoardDetailDto getDetail(Long id) {
		Board board = boardRepository.findById(id).orElseThrow(EntityNotFoundException::new);

		board.increaseView();

		BoardDetailDto boardDetailDto = BoardDetailDto.builder()
				.id(board.getId())
				.title(board.getTitle())
				.content(board.getContent())
				.memberName(board.getMember().getName())
				.createdTime(board.getCreatedTime())
				.updatedTime(board.getUpdatedTime())
				.view(board.getViews())
				.likes(board.getLikes())
				.build();

		return boardDetailDto;
	}

	@Transactional(readOnly = true)
	public BoardUpdateDto getUpdateForm(Long id, String email) throws Exception {
		Member member = memberRepository.findByEmail(email);
		Board board = boardRepository.findById(id).orElseThrow(EntityNotFoundException::new);
		if (member != board.getMember()) {
			throw new Exception("수정 권한이 없습니다.");
		}

		BoardUpdateDto boardUpdateDto = BoardUpdateDto.builder()
				.id(board.getId())
				.title(board.getTitle())
				.content(board.getContent())
				.build();
		return boardUpdateDto;
	}

	public Board updateBoard(BoardUpdateDto boardUpdateDto, Long id) {
		Board board = boardRepository.findById(id).orElseThrow(EntityNotFoundException::new);
		board.update(boardUpdateDto);
		return board;
	}

	public void deleteBoard(Long id) {
		boardRepository.deleteById(id);
	}
}
