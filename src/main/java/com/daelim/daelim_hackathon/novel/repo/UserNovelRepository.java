package com.daelim.daelim_hackathon.novel.repo;

import com.daelim.daelim_hackathon.novel.domain.UserNovel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserNovelRepository extends JpaRepository<UserNovel, Long> {

    @Query(
            "SELECT un.novel, un.user " +
            "FROM UserNovel un " +
            "WHERE un.user.id=:id"
    )
    Page<Object[]> getUserNovelsByUser_Id(Pageable pageable, @Param("id") Long id);

    Optional<UserNovel> findByUserIdAndNovelId(Long userId, Long novelId);

}
