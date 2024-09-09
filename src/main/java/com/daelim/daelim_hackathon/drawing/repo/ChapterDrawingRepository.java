package com.daelim.daelim_hackathon.drawing.repo;

import com.daelim.daelim_hackathon.drawing.domain.ChapterDrawing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChapterDrawingRepository extends JpaRepository<ChapterDrawing, Long> {
    ChapterDrawing findByChapter_Id(Long chapterId);

    int deleteChapterDrawingByChapter_Id(Long chapterId);

    @Transactional
    int deleteChapterDrawingByNovel_Id(Long novelId);

    @Transactional
    List<ChapterDrawingMapping> findAllByNovel_Id(Long novelId);
}
