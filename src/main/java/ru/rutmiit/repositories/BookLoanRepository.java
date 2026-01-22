package ru.rutmiit.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.rutmiit.models.entities.BookLoan;

@Repository
public interface BookLoanRepository extends JpaRepository<BookLoan, String> {

    @Query("SELECT bl FROM BookLoan bl JOIN FETCH bl.book b JOIN FETCH bl.user u WHERE bl.returnedAt IS NULL")
    List<BookLoan> findAllActive();

    @Query("SELECT bl FROM BookLoan bl JOIN FETCH bl.book b JOIN FETCH bl.user u WHERE bl.returnedAt IS NULL AND u.username = :username")
    List<BookLoan> findActiveByUsername(@org.springframework.data.repository.query.Param("username") String username);

    Optional<BookLoan> findFirstByBookIdAndReturnedAtIsNull(String bookId);

    List<BookLoan> findByUserIdOrderByStartDateDesc(String userId);

    List<BookLoan> findByBookId(String bookId);
}
