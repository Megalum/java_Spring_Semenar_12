package ru.gb.book_service;

import com.gb.Loggable;
import jakarta.annotation.PostConstruct;
import org.slf4j.event.Level;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("book")
public class BookController {
    private final List<Book> books = new ArrayList<>();

    @PostConstruct
    public void generateBooks() {
        for (int i = 0; i < 15; i++) {
            Book book = new Book();
            book.setId(UUID.randomUUID());
            book.setName("Book: " + i);

            books.add(book);
        }
    }

    @GetMapping()
    @Loggable(level = Level.INFO, stamp = 3000)
    public List<Book> getAll() {
        return books;
    }

    @GetMapping("random")
    @Loggable(level = Level.INFO)
    public Book getById(){
        Random random = new Random();

        return books.get(random.nextInt(books.size()));
    }
}
