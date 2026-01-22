package ru.rutmiit.dto;

import jakarta.validation.constraints.*;
import ru.rutmiit.models.enums.Genre;
import ru.rutmiit.utils.validation.UniqueIsbn;

import java.time.LocalDate;

public class AddBookDto {

    private String title;
    private String author;
    private Genre genre;
    private String libraryName;
    private LocalDate publishedOn;
    private String isbn;
    private String publisher;
    private Integer printRun;
    private Integer availableCopies;

    @NotEmpty(message = "Название обязательно.")
    @Size(min = 2, max = 128, message = "Название должно быть от 2 до 128 символов.")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @NotEmpty(message = "Укажите автора.")
    @Size(min = 2, max = 128, message = "Имя автора должно быть от 2 до 128 символов.")
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @NotNull(message = "Выберите жанр.")
    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    @NotEmpty(message = "Выберите библиотеку.")
    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    @NotNull(message = "Укажите дату публикации.")
    @PastOrPresent(message = "Дата публикации не может быть из будущего.")
    public LocalDate getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(LocalDate publishedOn) {
        this.publishedOn = publishedOn;
    }

    @NotEmpty(message = "Укажите ISBN.")
    @UniqueIsbn
    @Pattern(regexp = "^[0-9-]{10,17}$", message = "ISBN должен содержать 10-17 цифр и дефисы.")
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    @NotEmpty(message = "Укажите издательство.")
    @Size(min = 2, max = 255, message = "Издательство должно быть от 2 до 255 символов.")
    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @NotNull(message = "Укажите тираж.")
    @Min(value = 1, message = "Тираж должен быть больше 0.")
    public Integer getPrintRun() {
        return printRun;
    }

    public void setPrintRun(Integer printRun) {
        this.printRun = printRun;
    }

    @NotNull(message = "Укажите доступное количество.")
    @Min(value = 0, message = "Количество не может быть отрицательным.")
    public Integer getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(Integer availableCopies) {
        this.availableCopies = availableCopies;
    }
}
