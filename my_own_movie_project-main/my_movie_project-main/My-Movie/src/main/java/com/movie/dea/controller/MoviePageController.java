package com.movie.dea.controller;


import com.movie.dea.dto.MovieDTO;
import com.movie.dea.dto.MovieForm;
import com.movie.dea.entity.Director;
import com.movie.dea.entity.Movie;
import com.movie.dea.repository.DirectorRepository;
import com.movie.dea.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/movies")
//@Tag(name="Movies", description="Movie management API")
public class MoviePageController {
    private final MovieService movieService;
    private final DirectorRepository directorRepository;

    public MoviePageController(MovieService movieService, DirectorRepository directorRepository) {
        this.movieService = movieService;
        this.directorRepository = directorRepository;
    }


    @GetMapping
    public String list(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String genre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "title") String sortby,
            @RequestParam(defaultValue = "asc") String direction,
            Model model,
            Authentication authentication
    ) {


        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));





        if(page<0){
            page=0;
        }
//
//        title = (title == null) ? "" : title;
//        genre = (genre == null) ? "" : genre;
//        sortby = (sortBy == null) ? "title" : sortBy;
//        direction = (direction == null) ? "asc" : direction;


        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortby).ascending()
                : Sort.by(sortby).descending();

        Page<MovieDTO> movies = movieService.searchPaginated(
                title,
                genre,
                page,
                size,
                sort
        );


        if (page >= movies.getTotalPages() && movies.getTotalPages() > 0 ) {
            page = movies.getTotalPages() - 1;
            movies = movies = movieService.searchPaginated(
                    title,
                    genre,
                    page,
                    size,
                    sort
            );
        }

        model.addAttribute("movies", movies.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", movies.getTotalPages());
        model.addAttribute("size", size);

       // model.addAttribute("movies", movieService.search(title, genre, sort));
        model.addAttribute("sortBy",sortby);
        model.addAttribute("direction",direction);
        model.addAttribute("title", title);
        model.addAttribute("genre", genre);





        model.addAttribute("isAdmin", isAdmin);
        return "movies/list";
    }

    //form of adding
    @GetMapping("/new")
    public String form(Model model) {
        model.addAttribute("movieForm", new MovieForm());
        model.addAttribute("directors", directorRepository.findAll());
        return "movies/new";
    }


    @PostMapping
    public String save(@Valid @ModelAttribute("movieForm") MovieForm form, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return form.getId() == null ?  "movies/new" : "movies/edit";
        }


        Movie movie;

        if (form.getId() == null) {
            movie = new Movie();
        } else {
            movie = movieService.getMovie(form.getId());
        }

       // movie.setId(form.getId());
        movie.setTitle(form.getTitle());
        movie.setGenre(form.getGenre());
        movie.setReleaseDate(form.getReleaseDate());
        movie.setRating(form.getRating());
        movie.setDuration(form.getDuration());
        Director director = directorRepository.findById(form.getDirectorId()).orElseThrow(null);

        movieService.createMovie(movie);
        return "redirect:/movies";
    }


    // form of update
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {


        Movie movie = movieService.getMovie(id);
        MovieForm form = new MovieForm();
        form.setId(movie.getId());
        form.setTitle(movie.getTitle());
        form.setGenre(movie.getGenre());
        form.setReleaseDate(movie.getReleaseDate());
        form.setRating(movie.getRating());
        form.setDuration(movie.getDuration());

        model.addAttribute("movieForm", form);
        model.addAttribute("directors", directorRepository.findAll());
        return "movies/edit";
    }
//    @PostMapping
//            ("/{id}/edit")
//    public String update(@PathVariable Integer id,
//                         @Valid @ModelAttribute Movie movie,
//                         BindingResult bindingResult,
//                         Model model) {
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("movie", movie); // keep the form values
//            return "movies/edit";
//        }
//
//        // call your existing service
//        movieService.updateMovie(id, movie);
//
//        return "redirect:/movies"; // redirect to the list after saving
//    }




    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes){
        try{
            movieService.deleteById(id);
            redirectAttributes.addFlashAttribute
                    ("success", "Movie deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage()
            );
        }
        return "redirect:/movies";
    }


}
