package ru.rutmiit.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class BorrowBookDto {

    @NotEmpty(message = "Не удалось определить книгу.")
    private String bookId;

    @NotNull(message = "Укажите срок выдачи.")
    @Min(value = 1, message = "Минимальный срок 1 день.")
    @Max(value = 14, message = "Нельзя брать книгу более чем на 14 дней.")
    private Integer days;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }
}
