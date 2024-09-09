package com.daelim.daelim_hackathon.novel.repo;

import com.daelim.daelim_hackathon.novel.domain.Novel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NovelRepository extends JpaRepository<Novel, Long> {

    @Query(
            "SELECT n, n.author " +
            "FROM Novel n " +
            "WHERE n.author.id=:id AND n.isPublic=:bool"
    )
    Page<Object[]> getNovelsByAuthorAndPublic(Pageable pageable, @Param("id") Long id, @Param("bool") boolean bool);


    @Query(
            "SELECT n, n.author " +
            "FROM Novel n " +
            "WHERE n.isPublic=:Y"
    )
    Page<Object[]> getNovelsByPublic(Pageable pageable, @Param("Y") boolean y);


    @Query(
            "SELECT n, n.author " +
            "FROM Novel n " +
            "WHERE n.genre LIKE %:genre% AND n.isPublic=true"
    )
    Page<Object[]> getNovelsByGenre(Pageable pageable, @Param("genre") String genre);

    @Query(
            "SELECT n, n.author " +
            "FROM Novel n " +
            "WHERE n.title LIKE %:title% AND n.isPublic=true"
    )
    Page<Object[]> getNovelsByTitle(Pageable pageable, @Param("title") String title);

    @Query(
            "SELECT n, n.author " +
            "FROM Novel n " +
            "WHERE n.author.name LIKE %:name% AND n.isPublic=true"
    )
    Page<Object[]> getNovelsByAuthor_Name(Pageable pageable, @Param("name") String name);
}
