package ru.rutmiit.services;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rutmiit.dto.AddLibraryDto;
import ru.rutmiit.dto.ShowDetailedLibraryInfoDto;
import ru.rutmiit.dto.ShowLibraryInfoDto;
import ru.rutmiit.models.entities.Library;
import ru.rutmiit.models.exceptions.LibraryNotFoundException;
import ru.rutmiit.repositories.BookLoanRepository;
import ru.rutmiit.repositories.BookRepository;
import ru.rutmiit.repositories.LibraryRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
public class LibraryServiceImpl implements LibraryService {
    private final LibraryRepository libraryRepository;
    private final BookRepository bookRepository;
    private final BookLoanRepository bookLoanRepository;
    private final ModelMapper mapper;

    public LibraryServiceImpl(LibraryRepository libraryRepository, BookRepository bookRepository, BookLoanRepository bookLoanRepository, ModelMapper mapper) {
        this.libraryRepository = libraryRepository;
        this.bookRepository = bookRepository;
        this.bookLoanRepository = bookLoanRepository;
        this.mapper = mapper;
        log.info("LibraryService инициализирован");
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "libraries", allEntries = true)
    public void addLibrary(AddLibraryDto libraryDto) {
        log.debug("Создание библиотеки: {}", libraryDto.getName());
        Library library = mapper.map(libraryDto, Library.class);
        libraryRepository.save(library);
        log.info("Библиотека '{}' добавлена по адресу {}", library.getName(), library.getAddress());
    }

    @Override
    @Cacheable(value = "libraries", key = "'all'")
    public List<ShowLibraryInfoDto> allLibraries() {
        log.debug("Загрузка списка библиотек");
        List<ShowLibraryInfoDto> libraries = libraryRepository.findAll().stream()
                .map(library -> mapper.map(library, ShowLibraryInfoDto.class))
                .collect(Collectors.toList());
        log.info("Найдено библиотек: {}", libraries.size());
        return libraries;
    }

    @Override
    public Page<ShowLibraryInfoDto> allLibrariesPaginated(Pageable pageable) {
        log.debug("Постраничная загрузка библиотек: страница {}, размер {}", pageable.getPageNumber(), pageable.getPageSize());
        return libraryRepository.findAll(pageable)
                .map(library -> mapper.map(library, ShowLibraryInfoDto.class));
    }

    @Override
    public List<ShowLibraryInfoDto> searchLibraries(String searchTerm) {
        log.debug("Поиск библиотек по запросу: {}", searchTerm);
        List<ShowLibraryInfoDto> results = libraryRepository.searchByNameOrDescription(searchTerm).stream()
                .map(library -> mapper.map(library, ShowLibraryInfoDto.class))
                .collect(Collectors.toList());
        log.info("По запросу '{}' найдено {} библиотек", searchTerm, results.size());
        return results;
    }

    @Override
    public List<ShowLibraryInfoDto> findByAddress(String address) {
        log.debug("Поиск библиотек по адресу: {}", address);
        return libraryRepository.findByAddressContainingIgnoreCase(address).stream()
                .map(library -> mapper.map(library, ShowLibraryInfoDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "library", key = "#libraryName", unless = "#result == null")
    public ShowDetailedLibraryInfoDto libraryDetails(String libraryName) {
        log.debug("Загрузка деталей библиотеки: {}", libraryName);
        Library library = libraryRepository.findByName(libraryName)
                .orElseThrow(() -> {
                    log.warn("Библиотека не найдена: {}", libraryName);
                    return new LibraryNotFoundException("Библиотека '" + libraryName + "' не найдена");
                });
        return mapper.map(library, ShowDetailedLibraryInfoDto.class);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"libraries", "library", "books"}, allEntries = true)
    public void removeLibrary(String libraryName) {
        log.debug("Удаление библиотеки: {}", libraryName);

        Library library = libraryRepository.findByName(libraryName)
                .orElseThrow(() -> {
                    log.warn("Библиотека для удаления не найдена: {}", libraryName);
                    return new LibraryNotFoundException("Библиотека '" + libraryName + "' не найдена");
                });

        library.getBooks().forEach(book -> {
            bookLoanRepository.findByBookId(book.getId()).forEach(bookLoanRepository::delete);
            bookRepository.delete(book);
        });

        libraryRepository.delete(library);
        log.info("Библиотека '{}' удалена", libraryName);
    }
}
