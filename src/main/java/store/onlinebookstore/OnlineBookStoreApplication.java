package store.onlinebookstore;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import store.onlinebookstore.model.Book;
import store.onlinebookstore.service.BookService;

@SpringBootApplication
public class OnlineBookStoreApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(OnlineBookStoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                Book book = new Book();
                book.setTitle("1984");
                book.setAuthor("George Orwell");
                book.setIsbn("some isbn code");
                book.setPrice(BigDecimal.valueOf(250));

                bookService.save(book);

                System.out.println(bookService.findAll());
            }
        };
    }

}
