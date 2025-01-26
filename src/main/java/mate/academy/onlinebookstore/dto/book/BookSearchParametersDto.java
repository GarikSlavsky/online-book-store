package mate.academy.onlinebookstore.dto.book;

import java.math.BigDecimal;
import mate.academy.onlinebookstore.dto.ParameterSearcher;

public record BookSearchParametersDto(String title,
                                      String author,
                                      BigDecimal priceGreaterThan,
                                      BigDecimal priceLessThan,
                                      String description) implements ParameterSearcher {
}
