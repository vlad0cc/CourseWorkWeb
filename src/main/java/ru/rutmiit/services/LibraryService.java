package ru.rutmiit.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.rutmiit.dto.AddLibraryDto;
import ru.rutmiit.dto.ShowDetailedLibraryInfoDto;
import ru.rutmiit.dto.ShowLibraryInfoDto;

import java.util.List;

public interface LibraryService {

    void addLibrary(AddLibraryDto libraryDto);

    List<ShowLibraryInfoDto> allLibraries();

    Page<ShowLibraryInfoDto> allLibrariesPaginated(Pageable pageable);

    List<ShowLibraryInfoDto> searchLibraries(String searchTerm);

    List<ShowLibraryInfoDto> findByAddress(String address);

    ShowDetailedLibraryInfoDto libraryDetails(String libraryName);

    void removeLibrary(String libraryName);
}
