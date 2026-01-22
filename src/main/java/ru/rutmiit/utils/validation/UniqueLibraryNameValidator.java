package ru.rutmiit.utils.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.rutmiit.repositories.LibraryRepository;

public class UniqueLibraryNameValidator implements ConstraintValidator<UniqueLibraryName, String> {
    private final LibraryRepository libraryRepository;

    public UniqueLibraryNameValidator(LibraryRepository libraryRepository) {
        this.libraryRepository = libraryRepository;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return libraryRepository.findByName(value).isEmpty();
    }
}
