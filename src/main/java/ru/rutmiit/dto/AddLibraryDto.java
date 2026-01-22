package ru.rutmiit.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import ru.rutmiit.utils.validation.UniqueLibraryName;

public class AddLibraryDto {

    @UniqueLibraryName
    private String name;
    private String address;
    private String workingHours;
    private String description;

    @NotEmpty(message = "Название библиотеки обязательно.")
    @Size(min = 2, max = 64, message = "Название должно быть от 2 до 64 символов.")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotEmpty(message = "Укажите адрес библиотеки.")
    @Size(min = 4, max = 512, message = "Адрес должен быть от 4 до 512 символов.")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @NotEmpty(message = "Добавьте часы работы.")
    @Size(min = 2, max = 128, message = "Часы работы должны быть от 2 до 128 символов.")
    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }

    @NotEmpty(message = "Добавьте краткое описание.")
    @Size(min = 10, max = 1024, message = "Описание должно быть от 10 до 1024 символов.")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
