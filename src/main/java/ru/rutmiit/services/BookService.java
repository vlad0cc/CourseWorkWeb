package ru.rutmiit.services;

import ru.rutmiit.dto.AddBookDto;
import ru.rutmiit.dto.ShowBookInfoDto;
import ru.rutmiit.dto.ShowDetailedBookInfoDto;

import java.util.List;

public interface BookService {
    void addBook(AddBookDto bookDto);

    List<ShowBookInfoDto> allBooks();

    ShowDetailedBookInfoDto bookInfo(String bookId);

    void removeBook(String bookId);

    List<ShowBookInfoDto> findTopByTitle(int limit);
}
