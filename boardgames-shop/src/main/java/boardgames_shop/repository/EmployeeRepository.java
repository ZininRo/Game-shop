package boardgames_shop.repository;

import boardgames_shop.entity.Client;
import boardgames_shop.entity.Employee;
import boardgames_shop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByUser(User user);
    Optional<Employee> findByUserId(Long userId);

    @Query("SELECT c FROM Employee c WHERE c.id != 4 ORDER BY c.id ASC")
    List<Employee> findAllActiveEmployees();

}