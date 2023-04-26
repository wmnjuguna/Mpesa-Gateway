package app.fortuneconnect.payments.Utils;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.Link;

import java.util.List;
@Data @NoArgsConstructor
public class PagedResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    List<Link> links;
    private String baseUrl;

    public PagedResponse(List<T> content, int currentPageNumber, int pageSize, long totalElements, List<Link> links, String baseUrl) {
        this.content = content;
        this.page = currentPageNumber;
        this.size = pageSize;
        this.totalElements = totalElements;
        this.links = links;
        this.baseUrl = baseUrl;
    }
}
