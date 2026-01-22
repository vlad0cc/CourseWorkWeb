package ru.rutmiit.models.enums;

public enum Genre {
    FICTION("Художественная"),
    NON_FICTION("Нон-фикшн"),
    SCIENCE("Научная"),
    HISTORY("История"),
    CHILDREN("Детская");

    private final String displayName;

    Genre(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
