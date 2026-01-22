package ru.rutmiit.web;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.rutmiit.dto.AddLibraryDto;
import ru.rutmiit.dto.ShowLibraryInfoDto;
import ru.rutmiit.services.LibraryService;

@Slf4j
@Controller
@RequestMapping("/libraries")
public class LibraryController {

    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
        log.info("LibraryController готов");
    }

    @GetMapping("/add")
    public String addLibrary() {
        log.debug("GET форма добавления библиотеки");
        return "library-add";
    }

    @ModelAttribute("libraryModel")
    public AddLibraryDto initLibrary() {
        return new AddLibraryDto();
    }

    @PostMapping("/add")
    public String addLibrary(@Valid AddLibraryDto libraryModel,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        log.debug("POST добавление библиотеки");

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки при сохранении библиотеки: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("libraryModel", libraryModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.libraryModel",
                    bindingResult);
            return "redirect:/libraries/add";
        }

        libraryService.addLibrary(libraryModel);
        redirectAttributes.addFlashAttribute("successMessage",
            "Библиотека \"" + libraryModel.getName() + "\" успешно добавлена!");

        return "redirect:/libraries/all";
    }

    @GetMapping("/all")
    public String showAllLibraries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(required = false) String search,
            Model model) {

        log.debug("Список библиотек: page={}, size={}, sort={}, search={}",
                  page, size, sortBy, search);

        if (search != null && !search.trim().isEmpty()) {
            model.addAttribute("libraryInfos", libraryService.searchLibraries(search));
            model.addAttribute("search", search);
        } else {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
            Page<ShowLibraryInfoDto> libraryPage = libraryService.allLibrariesPaginated(pageable);

            model.addAttribute("libraryInfos", libraryPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", libraryPage.getTotalPages());
            model.addAttribute("totalItems", libraryPage.getTotalElements());
        }

        return "library-all";
    }

    @GetMapping("/library-details/{library-name}")
    public String libraryDetails(@PathVariable("library-name") String libraryName, Model model) {
        log.debug("Детали библиотеки: {}", libraryName);
        model.addAttribute("libraryDetails", libraryService.libraryDetails(libraryName));
        return "library-details";
    }

    @GetMapping("/library-delete/{library-name}")
    public String deleteLibrary(@PathVariable("library-name") String libraryName,
                                RedirectAttributes redirectAttributes) {
        log.debug("Удаление библиотеки: {}", libraryName);
        libraryService.removeLibrary(libraryName);
        redirectAttributes.addFlashAttribute("successMessage",
            "Библиотека \"" + libraryName + "\" удалена!");
        return "redirect:/libraries/all";
    }
}
