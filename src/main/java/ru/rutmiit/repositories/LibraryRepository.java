package ru.rutmiit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.rutmiit.models.entities.Library;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibraryRepository extends JpaRepository<Library, String> {

    Optional<Library> findByName(String name);

    boolean existsByName(String name);

    List<Library> findByAddressContainingIgnoreCase(String address);

    @Query("SELECT l FROM Library l WHERE " +
           "LOWER(l.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(l.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(l.address) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Library> searchByNameOrDescription(@Param("searchTerm") String searchTerm);

    @Modifying
    @Transactional
    void deleteByName(String name);
}
