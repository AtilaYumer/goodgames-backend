package com.konak.goodgames.service;

import com.konak.goodgames.domain.dto.CreateGameTitleDto;
import com.konak.goodgames.domain.dto.GameTitleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GameTitleService {
    void createGameTitle(CreateGameTitleDto dto) throws Exception;

    Page<GameTitleDto> getGameTitles(Pageable pageable);
}
