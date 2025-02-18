package mate.academy.onlinebookstore.dto.item;

import lombok.Data;

@Data
public class ItemResponseDto {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private int quantity;
}
