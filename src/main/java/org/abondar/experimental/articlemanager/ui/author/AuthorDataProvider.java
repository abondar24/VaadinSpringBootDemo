package org.abondar.experimental.articlemanager.ui.author;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import org.abondar.experimental.articlemanager.model.Author;
import org.abondar.experimental.articlemanager.service.AuthorService;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Component
public class AuthorDataProvider extends AbstractBackEndDataProvider<Author, AuthorFilter> {

    private final AuthorService authorService;

    public AuthorDataProvider(AuthorService authorService) {
        this.authorService = authorService;
    }

    @Override
    protected Stream<Author> fetchFromBackEnd(Query<Author, AuthorFilter> query) {
        var offset = query.getOffset();
        var limit = query.getLimit();

        var filter = query.getFilter();
        Stream<Author> stream;

        stream = filter
                .map(f -> authorService.searchAuthors(f.getSearchTerm()).stream())
                .orElse(authorService.getAuthors(offset, limit).stream());

        if (!query.getSortOrders().isEmpty()) {
            stream = stream.sorted(sortComparator(query.getSortOrders()));
        }

        return stream;
    }

    @Override
    protected int sizeInBackEnd(Query<Author, AuthorFilter> query) {
        var filter = query.getFilter();

        return filter
                .map(f -> authorService.searchAuthors(filter.get().getSearchTerm()).size())
                .orElse((int) authorService.countAuthors());
    }


    private Comparator<Author> sortComparator(List<QuerySortOrder> sortOrders) {
        return sortOrders.stream().map(sortOrder -> {
            Comparator<Author> comparator = authorFieldComparator(sortOrder.getSorted());
            if (sortOrder.getDirection() == SortDirection.DESCENDING) {
                comparator = comparator.reversed();
            }
            return comparator;
        }).reduce(Comparator::thenComparing).orElse((a1, a2) -> 0);
    }

    private Comparator<Author> authorFieldComparator(String sorted) {
        return switch (sorted) {
            case "name" -> Comparator.comparing(Author::getName);
            case "lastName" -> Comparator.comparing(Author::getLastName);
            case "email" -> Comparator.comparing(Author::getEmail);
            default -> (a1, a2) -> 0;
        };
    }
}
