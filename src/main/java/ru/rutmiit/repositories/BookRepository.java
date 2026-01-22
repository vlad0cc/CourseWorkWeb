package ru.rutmiit.repositories;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ru.rutmiit.models.entities.Book;
import java.util.List;


@Repository
public interface BookRepository extends JpaRepository<Book, String> {

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.library WHERE b.id = :id")
    Optional<Book> findDetailedById(@org.springframework.data.repository.query.Param("id") String id);

    Optional<Book> findByIsbn(String isbn);

    List<Book> findAllByOrderByTitleDesc(Pageable pageable);
}
