package mate.academy.onlinebookstore.mapper;

import java.util.List;
import mate.academy.onlinebookstore.config.MapperConfig;
import mate.academy.onlinebookstore.dto.book.BookDto;
import mate.academy.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.onlinebookstore.dto.book.CreateBookRequestDto;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.model.Category;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto intoBookDto(Book book);

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        List<Long> idList = book.getCategories().stream()
                .map(Category::getId)
                .toList();
        bookDto.setCategoryIds(idList);
    }

    Book intoModel(CreateBookRequestDto requestDto);

    void updateModelFromDto(CreateBookRequestDto requestDto, @MappingTarget Book book);

    BookDtoWithoutCategoryIds intoBookDtoWithoutCategories(Book book);
}

