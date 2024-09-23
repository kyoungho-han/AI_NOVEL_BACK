package com.daelim.daelim_hackathon.novel.service;

import com.daelim.daelim_hackathon.author.domain.User;
import com.daelim.daelim_hackathon.author.repo.UserRepository;
import com.daelim.daelim_hackathon.drawing.domain.NovelDrawing;
import com.daelim.daelim_hackathon.drawing.repo.NovelDrawingRepository;
import com.daelim.daelim_hackathon.novel.domain.UserNovel;
import com.daelim.daelim_hackathon.novel.dto.NovelPageRequestDTO;
import com.daelim.daelim_hackathon.common.dto.PageResultDTO;
import com.daelim.daelim_hackathon.novel.domain.Novel;
import com.daelim.daelim_hackathon.novel.dto.NovelModifyDTO;
import com.daelim.daelim_hackathon.novel.dto.NovelDTO;
import com.daelim.daelim_hackathon.common.dto.StatusDTO;
import com.daelim.daelim_hackathon.novel.dto.SearchPageRequestDTO;
import com.daelim.daelim_hackathon.novel.exception.SearchException;
import com.daelim.daelim_hackathon.novel.repo.NovelRepository;
import com.daelim.daelim_hackathon.novel.repo.UserNovelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Function;

@Transactional
@Log4j2
@Service
@RequiredArgsConstructor
public class NovelServiceImpl implements NovelService{
    private final UserRepository userRepository;
    private final NovelRepository novelRepository;
    private final NovelDrawingRepository novelDrawingRepository;
    private final UserNovelRepository userNovelRepository;


    @Override
    public NovelDTO saveNovel(NovelDTO novelDTO) {
        Optional<User> option = userRepository.findByName(novelDTO.getName());
        if(option.isPresent()) {
            User user = option.get();
            Novel novel = dtoToEntity(novelDTO, user);
            novelRepository.save(novel);
            return entityToDTO(novel, user);
        } else {
            throw new RuntimeException("This account doesn't exist");
        }
    }


    @Override
    public NovelDTO getNovel(Long novelId) {
        Optional<Novel> novelOptional = novelRepository.findById(novelId);
        User author;
        Novel novel;
        if (novelOptional.isPresent()) {
            novel = novelOptional.get();
            author = novel.getAuthor();
            return entityToDTO(novel, author);
        } else {
            throw new RuntimeException("Not found this novel");
        }
    }

    @Override
    public PageResultDTO<NovelDTO, Object[]> getNovels(NovelPageRequestDTO pageRequestDTO) {
        Function<Object[], NovelDTO> fn = (
                entity -> entityToDTO(
                        (Novel)entity[0],
                        (User)entity[1]
                )
        );
        Page<Object[]> result;
        String name = pageRequestDTO.getName();
        if(name == null) {
            // 인기 순서
            if (pageRequestDTO.getIsBest()) {
                result = novelRepository.getNovelsByPublic(
                        pageRequestDTO.getPageable(Sort.by("love").descending()),
                        true
                );
                return new PageResultDTO<>(result, fn);
            // 출판날짜 순서
            } else {
                result = novelRepository.getNovelsByPublic(
                        pageRequestDTO.getPageable(Sort.by("id").descending()),
                        true
                );
                return new PageResultDTO<>(result, fn);
            }
        }
        if (pageRequestDTO.getIsMine()) {
            // 출판 유무로 본인 작품 가져오기
            if (userRepository.existsByName(name)) {
                result = novelRepository.getNovelsByAuthorAndPublic(
                    pageRequestDTO.getPageable(Sort.by("id").descending()),
                    userRepository.findByName(name).get().getId(),
                    pageRequestDTO.getIsPublic()
                );
                return new PageResultDTO<>(result, fn);
            } else {
                throw new RuntimeException("No permission");
            }
        } else {
            // 좋아요 누른 작품 가져오기
            if (userRepository.existsByName(name)) {
                result = userNovelRepository.getUserNovelsByUser_Id(
                    pageRequestDTO.getPageable(Sort.by("id").descending()),
                    userRepository.findByName(name).get().getId()
                );
                return new PageResultDTO<>(result, fn);
            } else {
                throw new RuntimeException("No permission");
            }
        }
    }

    @Override
    public PageResultDTO<NovelDTO, Object[]> searchNovels(SearchPageRequestDTO pageRequestDTO) {
        Function<Object[], NovelDTO> fn = (
                entity -> entityToDTO(
                        (Novel)entity[0],
                        (User)entity[1]
                )
        );
        Page<Object[]> result;
        String category = pageRequestDTO.getCategory();
        switch (category) {
            case "g":
                if (pageRequestDTO.isBest()) result = novelRepository.getNovelsByGenre(
                        pageRequestDTO.getPageable(Sort.by("love").descending()),
                        pageRequestDTO.getKeyword()
                );
                else result = novelRepository.getNovelsByGenre(
                        pageRequestDTO.getPageable(Sort.by("id").ascending()),
                        pageRequestDTO.getKeyword()
                );
                return new PageResultDTO<>(result, fn);

            case "t":
                if (pageRequestDTO.isBest()) result = novelRepository.getNovelsByTitle(
                        pageRequestDTO.getPageable(Sort.by("love").descending()),
                        pageRequestDTO.getKeyword()
                );
                else result = novelRepository.getNovelsByTitle(
                        pageRequestDTO.getPageable(Sort.by("id").ascending()),
                        pageRequestDTO.getKeyword()
                );
                return new PageResultDTO<>(result, fn);

            case "n":
                if (pageRequestDTO.isBest()) result = novelRepository.getNovelsByAuthor_Name(
                        pageRequestDTO.getPageable(Sort.by("love").descending()),
                        pageRequestDTO.getKeyword()
                );
                else result = novelRepository.getNovelsByAuthor_Name(
                        pageRequestDTO.getPageable(Sort.by("id").ascending()),
                        pageRequestDTO.getKeyword()
                );
                return new PageResultDTO<>(result, fn);

            default:
                throw new SearchException("검색 카테고리 및 키워드 확인하기");
        }

    }


    @Override
    public StatusDTO deleteNovel(Long novelId, String name) {
        Optional<User> option = userRepository.findByName(name);
        if(option.isPresent()) {
            novelRepository.delete(
                    Novel.builder()
                            .id(novelId)
                            .author(option.get())
                            .build()
            );
            return StatusDTO.builder().status("success").build();
        } else {
            throw new RuntimeException("This account doesn't exist");
        }
    }


    @Override
    public StatusDTO updateNovel(Long novelId, NovelModifyDTO modifyDTO) {

        Novel novel = novelRepository.getReferenceById(novelId);
        novel.changeTitle(modifyDTO.getTitle());
        novel.changeIsPublic(modifyDTO.getIsPublic());
        novelRepository.save(novel);
        return StatusDTO.builder().status("success").build();
    }


    @Override
    public StatusDTO love(Long novelId, String name) {
        try {
            User user = userRepository.findByName(name).get();
            Novel novel = novelRepository.getReferenceById(novelId);
            Long result;
            Optional<UserNovel> userNovelOptional = userNovelRepository.findByUserIdAndNovelId(user.getId(), novelId);
            if (userNovelOptional.isEmpty()) {
                result = novel.getLove() + 1;
                userNovelRepository.save(UserNovel.builder()
                        .user(user)
                        .novel(novel)
                        .build()
                );
            } else {
                result = novel.getLove() - 1;
                UserNovel userNovel = userNovelOptional.get();
                log.info(userNovel);
                userNovelRepository.delete(userNovel);
            }
            novel.changeLove(result);
            novelRepository.save(novel);
            return StatusDTO.builder().status("success").build();
        } catch(Exception e) {
            throw e;
        }

    }


    @Override
    public String getFileName(Long novelId) {
        return novelDrawingRepository.findByNovel_Id(novelId).getUuid();
    }


    @Override
    public String deleteFile(Long novelId) {
        String uuid = novelDrawingRepository.findByNovel_Id(novelId).getUuid();
        novelDrawingRepository.deleteNovelDrawingByNovel_Id(novelId);
        return uuid;
    }

    @Override
    public void visible(Long novelId) {
        Novel novel = novelRepository.getReferenceById(novelId);
        novel.changeIsPublic(true);
        novelRepository.save(novel);
    }

    @Override
    public String uploadURL(String url, Long novelId) {
        novelDrawingRepository.save(NovelDrawing.builder()
                .novel(novelRepository.getReferenceById(novelId))
                .uuid(url)
                .build()
        );
        return url;
    }

    @Override
    public String getURL(Long novelId) {
        return novelDrawingRepository.findByNovel_Id(novelId).getUuid();
    }


}
