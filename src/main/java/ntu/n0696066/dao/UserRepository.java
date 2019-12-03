package ntu.n0696066.dao;

import ntu.n0696066.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername (String username);
}
