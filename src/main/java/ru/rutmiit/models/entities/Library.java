package ru.rutmiit.models.entities;

import jakarta.persistence.*;
import ru.rutmiit.models.entities.Book;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "libraries")
public class Library extends BaseEntity {

    @Column(nullable = false, length = 512)
    private String address;

    @Column(nullable = false, length = 128)
    private String workingHours;

    @Column(length = 1024, nullable = false)
    private String description;

    @Column(unique = true, nullable = false, length = 255)
    private String name;

    @OneToMany(mappedBy = "library", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Book> books = new HashSet<>();

    public Library() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }
}
