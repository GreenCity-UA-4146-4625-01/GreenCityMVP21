package greencity.filters;

import greencity.entity.ShoppingListItem_;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.entity.localization.ShoppingListItemTranslation_;
import greencity.filters.ShoppingListItemSpecification;
import greencity.entity.ShoppingListItem;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ShoppingListItemSpecificationTest {
    @Mock
    private Root<ShoppingListItem> ShoppingListItemRoot;

    @Mock
    private CriteriaBuilder builder;

    @Mock
    private CriteriaQuery MockCriteriaQuery;

    @Mock
    private Path<Object> id;

    //Path<Long> used in toPredicate_WithShoppingListItemTranslationJoin. And other which use content to check.
    @Mock
    private Path<Long> Id_for_second_test;

    @Mock
    private Path<Long> Id_root_for_second_test;

    @Mock
    private Join<ShoppingListItem,ShoppingListItemTranslation> translationJoin;

    @Mock
    private Path<String> contentPath;

    @Mock
    private Expression<String> as;
    ShoppingListItemSpecification specification;

    @Test
    void toPredicateId(){
        List<greencity.filters.SearchCriteria> searchCriteriaList = List.of(
        SearchCriteria.builder()
                .key("id")
                .type("id")
                .value(4L)
                .build());

        specification = new ShoppingListItemSpecification(searchCriteriaList);
        Predicate conjunctionPredicate = mock(Predicate.class);
        Predicate equalPredicate = mock(Predicate.class);
        Predicate andPredicate = mock(Predicate.class);
        when(builder.conjunction()).thenReturn(conjunctionPredicate);
        when(ShoppingListItemRoot.get("id")).thenReturn(id);
        when(builder.equal(id, 4L)).thenReturn(equalPredicate);
        when(builder.and(conjunctionPredicate, equalPredicate)).thenReturn(andPredicate);
        Predicate allPredicates = builder.conjunction();
        for (int i = 0; i < searchCriteriaList.size(); i++) {
            SearchCriteria criteria = searchCriteriaList.get(i);
            if (criteria.getType().equals("id")) {
                allPredicates = builder.and(allPredicates,
                        specification.getNumericPredicate(ShoppingListItemRoot, builder, criteria));
            }
        }
        verify(builder).equal(id, 4L);
        verify(ShoppingListItemRoot).get("id");
        assertNotNull(allPredicates);
        assertEquals(andPredicate, allPredicates);
    }

        @Test
    void toPredicate_WithShoppingListItemTranslationJoin(){
        List<SearchCriteria> searchCriteriaList = List.of(
        SearchCriteria.builder()
                .key("content")
                .type("content")
                .value("Smth")
                .build());
        specification = new ShoppingListItemSpecification(searchCriteriaList);
        Root<ShoppingListItemTranslation> shoppingListItemTranslationRoot = mock(Root.class);
        when(MockCriteriaQuery.from(ShoppingListItemTranslation.class)).thenReturn(shoppingListItemTranslationRoot);
        when(shoppingListItemTranslationRoot.get(ShoppingListItemTranslation_.content)).thenReturn(contentPath);
        when(shoppingListItemTranslationRoot.get(ShoppingListItemTranslation_.shoppingListItem).get(ShoppingListItem_.id)).thenReturn(Id_for_second_test);
        when(ShoppingListItemRoot.get(ShoppingListItem_.id)).thenReturn(Id_root_for_second_test);
        Predicate likePredicate=mock(Predicate.class);
        Predicate equalPredicate = mock(Predicate.class);
        Predicate andPredicate = mock(Predicate.class);
        Predicate conjunctionPredicate = mock(Predicate.class);
        when(builder.conjunction()).thenReturn(conjunctionPredicate);
        when(builder.like(contentPath,"%Smth%")).thenReturn(likePredicate);
        when(builder.equal(Id_for_second_test,Id_root_for_second_test)).thenReturn((equalPredicate));
        when(builder.and(likePredicate,equalPredicate)).thenReturn(andPredicate);
        when(builder.and(conjunctionPredicate,andPredicate)).thenReturn(andPredicate);
        Predicate result=specification.toPredicate(ShoppingListItemRoot,MockCriteriaQuery,builder);
        assertEquals(andPredicate, result);
        assertNotNull(result);
        assertEquals(andPredicate,result);
    }

        @Test
    void toPredicate_ContentNull(){
        List<SearchCriteria> searchCriteriaList = List.of(
        SearchCriteria.builder()
                .key("content")
                .type("content")
                .value(" ")
                .build());
        specification = new ShoppingListItemSpecification(searchCriteriaList);
        Predicate conjunction = mock(Predicate.class);
        when(builder.conjunction()).thenReturn(conjunction);
        Root<ShoppingListItemTranslation> shoppingListItemTranslationRoot = mock(Root.class);
        when(MockCriteriaQuery.from(ShoppingListItemTranslation.class)).thenReturn(shoppingListItemTranslationRoot);

        specification.toPredicate(ShoppingListItemRoot, MockCriteriaQuery, builder);

        verify(builder,times(2)).conjunction();
        verify(MockCriteriaQuery).from(ShoppingListItemTranslation.class);
        verifyNoMoreInteractions(MockCriteriaQuery);
    }

    @Test
    void toPredicate_multipleCriteria(){
        List<SearchCriteria> searchCriteriaList = List.of(
        SearchCriteria.builder()
                .key("id")
                .type("id")
                .value(2L)
                .build(),
        SearchCriteria.builder()
                .key("content")
                .type("content")
                .value("Smth")
                .build());

        specification = new ShoppingListItemSpecification(searchCriteriaList);

        Predicate conjunctionPredicate = mock(Predicate.class);
        when(builder.conjunction()).thenReturn(conjunctionPredicate);

        Predicate idEqualPredicate = mock(Predicate.class);
        when(ShoppingListItemRoot.get("id")).thenReturn(id);
        when(builder.equal(id, 2L)).thenReturn(idEqualPredicate);

        Root<ShoppingListItemTranslation> habitFactTranslationRoot = mock(Root.class);
        when(MockCriteriaQuery.from(ShoppingListItemTranslation.class)).thenReturn(habitFactTranslationRoot);
        when(habitFactTranslationRoot.get(ShoppingListItemTranslation_.content)).thenReturn(contentPath);
        when(habitFactTranslationRoot.get(ShoppingListItemTranslation_.shoppingListItem).get(ShoppingListItem_.id)).thenReturn(Id_for_second_test);
        when(ShoppingListItemRoot.get(ShoppingListItem_.id)).thenReturn(Id_root_for_second_test);

        Predicate likePredicate = mock(Predicate.class);
        Predicate contentEqualPredicate = mock(Predicate.class);
        Predicate contentAndPredicate = mock(Predicate.class);

        when(builder.like(contentPath, "%Smth%")).thenReturn(likePredicate);
        when(builder.equal(Id_for_second_test, Id_root_for_second_test)).thenReturn(contentEqualPredicate);
        when(builder.and(likePredicate, contentEqualPredicate)).thenReturn(contentAndPredicate);

        Predicate combinedPredicate = mock(Predicate.class);
        Predicate finalPredicate = mock(Predicate.class);

        when(builder.and(conjunctionPredicate, idEqualPredicate)).thenReturn(combinedPredicate);
        when(builder.and(combinedPredicate, contentAndPredicate)).thenReturn(finalPredicate);

        Predicate result = specification.toPredicate(ShoppingListItemRoot, MockCriteriaQuery, builder);
        assertNotNull(result);
        assertEquals(finalPredicate, result);

        verify(builder).conjunction();
        verify(builder).equal(id, 2L);
        verify(builder).like(contentPath, "%Smth%");
        verify(builder).equal(Id_for_second_test, Id_root_for_second_test);
        verify(builder).and(likePredicate, contentEqualPredicate);
        verify(builder).and(conjunctionPredicate, idEqualPredicate);
        verify(builder).and(combinedPredicate, contentAndPredicate);
        }

}
