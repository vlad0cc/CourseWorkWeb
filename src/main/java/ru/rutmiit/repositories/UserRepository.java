package ru.rutmiit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.rutmiit.models.entities.User;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByReaderCardNumber(String readerCardNumber);

    Optional<User> findFirstByFullNameIgnoreCase(String fullName);

    @org.springframework.data.jpa.repository.Query(value = "select coalesce(max(cast(reader_card_number as bigint)),0) from users", nativeQuery = true)
    long findMaxReaderCardNumber();
}
