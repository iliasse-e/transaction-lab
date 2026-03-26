package ma.charika.transaction_lab.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ma.charika.transaction_lab.common.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
