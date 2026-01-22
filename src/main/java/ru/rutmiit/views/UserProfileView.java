package ru.rutmiit.views;

public class UserProfileView {
    private String username;

    private String email;

    private String fullName;

    private String readerCardNumber;

    private int age;

    public UserProfileView() {
    }

    public UserProfileView(String username, String email, String fullName, String readerCardNumber, int age) {
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.readerCardNumber = readerCardNumber;
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getReaderCardNumber() {
        return readerCardNumber;
    }

    public void setReaderCardNumber(String readerCardNumber) {
        this.readerCardNumber = readerCardNumber;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

}
