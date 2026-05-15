package boardgames_shop.repository;

import boardgames_shop.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {
    // Для каталога (только активные, сортировка по id)
    List<Game> findAllByActiveTrueOrderByIdAsc();

    // Для админа (все игры, сортировка по id)
    List<Game> findAllByOrderByIdAsc();
    List<Game> findByActiveTrueOrderByIdAsc(Pageable pageable);

}