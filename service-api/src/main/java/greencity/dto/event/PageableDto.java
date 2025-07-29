package greencity.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageableDto<T> {

    private List<T> content;
    private int totalElements;
    private int currentPage;
    private int totalPages;
}
