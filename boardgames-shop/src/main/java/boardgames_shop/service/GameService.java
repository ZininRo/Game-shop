package boardgames_shop.service;

import boardgames_shop.dto.game.GameDto;
import boardgames_shop.entity.Game;
import boardgames_shop.repository.GameRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<GameDto> getActiveGames() {
        List<Game> games = gameRepository.findAllByActiveTrueOrderByIdAsc();
        return games.stream()
                .map(g -> new GameDto(
                        g.getId(),
                        g.getName(),
                        g.getPrice(),
                        g.getDescription(),
                        true   // активна
                ))
                .collect(Collectors.toList());
    }

    public GameDto getGameById(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found"));
        // Для покупателя: если игра неактивна – выдавать ошибку
        if (!game.isActive()) {
            throw new RuntimeException("Игра временно недоступна");
        }
        return new GameDto(
                game.getId(),
                game.getName(),
                game.getPrice(),
                game.getDescription(),
                game.isActive()
        );
    }
}