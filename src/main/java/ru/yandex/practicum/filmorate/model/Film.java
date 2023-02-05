package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.internal.LoadingCache;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.validator.MinDateRelease;

import javax.validation.constraints.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
public class Film {
    private long id;
    @NotBlank
    private String name;
    @NotBlank
    @Size(min = 1, max = 200)
    private String description;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @MinDateRelease
    @Past(message = "Incorrect date release")
    private LocalDate releaseDate;
    @NotNull
    @Positive
    private int duration;

    private String mpa;

    private List<String> genre;

    private Set<Long> likes;

    public boolean addLike(Long id) {
        return  likes.add(id);
    }

    public boolean deleteLike(Long id) {
        return  likes.remove(id);
    }
}
