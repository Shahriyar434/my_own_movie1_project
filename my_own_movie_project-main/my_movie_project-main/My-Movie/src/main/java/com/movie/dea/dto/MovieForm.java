package com.movie.dea.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MovieForm {

    private Integer id;

    @NotBlank(message = "{error.notblank}")
    @Size(min=5, max=100, message = "{error.size}")
    private String title;

    @NotBlank(message = "Genre is required!")
    @Size(min=5, max=100, message = "{error.size}")
    private String genre;

    @NotNull(message = "{error.notblank}")
    private LocalDate releaseDate;

    @NotNull(message = "{error.notblank}")
    @DecimalMin(value = "1.0", message = "Rating must be at least 0")
    @DecimalMax(value = "10.0", message = "Rating must be maximum 10")
    private Double rating;

    @NotNull(message = "{error.notblank}")

    @Min(value=20, message = "{error.size}")
    @Max(value=1000, message = "{error.size}")
    private Integer duration;

    @NotNull(message = "{error.notblank}")
    private Integer directorId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }


    public Integer getDirectorId() {
        return directorId;
    }

    public void setDirectorId(Integer directorId) {
        this.directorId = directorId;
    }
}
