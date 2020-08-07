package io.javabrains.ratingsdataservice.dto;

public class CustomDto {
    public CustomDto(String name, String title) {
        this.name = name;
        this.title = title;
    }

    public CustomDto() {



    }

    protected String name;
    protected String title;

    public String getTitle() {
        return title;
    }

    public CustomDto setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getName() {
        return name;
    }

    public CustomDto setName(String name) {
        this.name = name;
        return this;
    }
}
