package com.milk.milkweb.repository;

import com.milk.milkweb.entity.BoardImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardImgRepository extends JpaRepository<BoardImg, Long>{

}
