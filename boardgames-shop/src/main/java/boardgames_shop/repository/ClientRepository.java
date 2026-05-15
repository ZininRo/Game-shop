package boardgames_shop.repository;

import boardgames_shop.entity.Client;
import boardgames_shop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByUser(User user);
    Optional<Client> findByUserId(Long userId);

    @Query("SELECT c FROM Client c WHERE c.id != 5 ORDER BY c.id ASC")
    List<Client> findAllActiveClients();

}