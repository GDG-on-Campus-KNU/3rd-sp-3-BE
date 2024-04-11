package gdsc.comunity.repository.user;

import gdsc.comunity.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
