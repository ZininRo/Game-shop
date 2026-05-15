package boardgames_shop.controller;

import boardgames_shop.dto.game.GameDto;
import boardgames_shop.service.GameService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    // Каталог
    @GetMapping
    public List<GameDto> getAllGames() {
        return gameService.getActiveGames();
    }

    // Детальная страница
    @GetMapping("/{id}")
    public GameDto getGame(@PathVariable Long id) {
        return gameService.getGameById(id);
    }
}