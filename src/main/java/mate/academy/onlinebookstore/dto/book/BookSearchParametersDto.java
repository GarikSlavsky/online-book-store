package mate.academy.onlinebookstore.dto.book;

import java.math.BigDecimal;

public record BookSearchParametersDto(String title,
                                      String author,
                                      BigDecimal priceGreaterThan,
                                      BigDecimal priceLessThan,
                                      String description) {
    public static final String TITLE = "title";
    public static final String AUTHOR = "author";
    public static final String PRICE = "price";
    public static final String DESCRIPTION = "description";
}
