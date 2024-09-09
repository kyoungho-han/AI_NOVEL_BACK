package com.daelim.daelim_hackathon.drawing.repo;

import com.daelim.daelim_hackathon.drawing.domain.NovelDrawing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NovelDrawingRepository extends JpaRepository<NovelDrawing, Long> {
    NovelDrawing findByNovel_Id(Long novelId);
    int deleteNovelDrawingByNovel_Id(Long novelId);

}
