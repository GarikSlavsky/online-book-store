package mate.academy.onlinebookstore;

import java.math.BigDecimal;
import java.util.List;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OnlineBookStoreApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(OnlineBookStoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book book = new Book();
            book.setTitle("Book Title");
            book.setAuthor("Author");
            book.setIsbn("qwe");
            book.setPrice(BigDecimal.valueOf(25));
            book.setDescription("asdfg");
            book.setCoverImage("zxc");

            bookService.saveBook(book);
            List<Book> bookList = bookService.findAllBooks();
            System.out.println(bookList);
        };
    }

}
