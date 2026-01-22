package ru.rutmiit.dto;

import java.time.LocalDate;
import ru.rutmiit.models.enums.Genre;

public class ShowDetailedBookInfoDto {
    private String id;
    private String title;
    private String author;
    private Genre genre;
    private LocalDate publishedOn;
    private String isbn;
    private String publisher;
    private Integer printRun;
    private Integer availableCopies;
    private String libraryName;
    private String genreLabel;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public LocalDate getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(LocalDate publishedOn) {
        this.publishedOn = publishedOn;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Integer getPrintRun() {
        return printRun;
    }

    public void setPrintRun(Integer printRun) {
        this.printRun = printRun;
    }

    public Integer getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(Integer availableCopies) {
        this.availableCopies = availableCopies;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public String getGenreLabel() {
        return genreLabel;
    }

    public void setGenreLabel(String genreLabel) {
        this.genreLabel = genreLabel;
    }
}
