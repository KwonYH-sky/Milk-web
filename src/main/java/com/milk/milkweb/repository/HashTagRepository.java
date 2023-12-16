package com.milk.milkweb.repository;

import com.milk.milkweb.entity.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HashTagRepository extends JpaRepository<HashTag, Long> {

	Boolean existsByTag(String tag);

	Optional<HashTag> findByTag(String tag);

}
