package greencity.filters;

import greencity.entity.*;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class HabitFactSpecificationTest {

    @Mock
    private Root<HabitFact> HabitFactRoot;

    @Mock
    private CriteriaBuilder builder;

    @Mock
    private CriteriaQuery MockCriteriaQuery;

    @Mock
    private Path<Object> id;

    @Mock
    private Path<Long> habitIdPath;

    @Mock
    private Path<Long> habitFactRootIdPath;

    @Mock
    private Join<HabitFact,Habit> habitJoin;

    @Mock
    private Path<String> contentPath;

    @Mock
    private Predicate likePredicate;

    @Mock
    private Expression<String> as;
    HabitFactSpecification specification;

    @Test
    void toPredicate_Id(){
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        searchCriteriaList.add(SearchCriteria.builder()
                .key("id")
                .type("id")
                .value(2L)
                .build());

        specification = new HabitFactSpecification(searchCriteriaList);
        Predicate conjunctionPredicate = mock(Predicate.class);
        Predicate equalPredicate = mock(Predicate.class);
        Predicate andPredicate = mock(Predicate.class);
        when(builder.conjunction()).thenReturn(conjunctionPredicate);
        when(HabitFactRoot.get("id")).thenReturn(id);
        when(builder.equal(id, 2L)).thenReturn(equalPredicate);
        when(builder.and(conjunctionPredicate, equalPredicate)).thenReturn(andPredicate);
        Predicate allPredicates = builder.conjunction();
        for (int i = 0; i < searchCriteriaList.size(); i++) {
            SearchCriteria criteria = searchCriteriaList.get(i);
            if (criteria.getType().equals("id")) {
                allPredicates = builder.and(allPredicates,
                        specification.getNumericPredicate(HabitFactRoot, builder, criteria));
            }
        }
        verify(builder).equal(id, 2L);
        verify(HabitFactRoot).get("id");
        assertNotNull(allPredicates);
        assertEquals(andPredicate, allPredicates);
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void toPredicate_HabitIdWithJoin() {
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        searchCriteriaList.add(SearchCriteria.builder()
                .key("habitId")
                .type("habitId")
                .value(5L)
                .build());
        specification = new HabitFactSpecification(searchCriteriaList);
        when(HabitFactRoot.join(HabitFact_.habit)).thenReturn(habitJoin);
        when(habitJoin.get(Habit_.id)).thenReturn(habitIdPath);
        Predicate mockPredicate = mock(Predicate.class);
        when(builder.equal(habitIdPath, 5L)).thenReturn(mockPredicate);
        Predicate conjunctionPredicate = mock(Predicate.class);
        when(builder.conjunction()).thenReturn(conjunctionPredicate);
        when(builder.and(conjunctionPredicate, mockPredicate)).thenReturn(mockPredicate);
        Predicate result = specification.toPredicate(HabitFactRoot, MockCriteriaQuery, builder);
        assertNotNull(result);
        assertEquals(mockPredicate,result);

    }

    @Test
    void toPredicate_WithTranslationJoin(){
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        searchCriteriaList.add(SearchCriteria.builder()
                .key("content")
                .type("content")
                .value("Smth")
                .build());
        specification = new HabitFactSpecification(searchCriteriaList);

        Root<HabitFactTranslation> habitFactTranslationRoot = mock(Root.class);
        when(MockCriteriaQuery.from(HabitFactTranslation.class)).thenReturn(habitFactTranslationRoot);

        when(habitFactTranslationRoot.get(HabitFactTranslation_.content)).thenReturn(contentPath);
        when(habitFactTranslationRoot.get(HabitFactTranslation_.habitFact).get(HabitFact_.id)).thenReturn(habitIdPath);
        when(HabitFactRoot.get(HabitFact_.id)).thenReturn(habitFactRootIdPath);

        Predicate likePredicate = mock(Predicate.class);
        Predicate equalPredicate = mock(Predicate.class);
        Predicate andPredicate = mock(Predicate.class);
        Predicate conjunctionPredicate = mock(Predicate.class);

        when(builder.conjunction()).thenReturn(conjunctionPredicate);
        when(builder.like(contentPath, "%Smth%")).thenReturn(likePredicate);
        when(builder.equal(habitIdPath, habitFactRootIdPath)).thenReturn(equalPredicate);
        when(builder.and(likePredicate, equalPredicate)).thenReturn(andPredicate);
        when(builder.and(conjunctionPredicate, andPredicate)).thenReturn(andPredicate);

        Predicate result = specification.toPredicate(HabitFactRoot, MockCriteriaQuery, builder);
        assertNotNull(result);
        assertEquals(andPredicate, result);
    }

    @Test
    void toPredicate_ContentNull(){
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        searchCriteriaList.add(SearchCriteria.builder()
                .key("content")
                .type("content")
                .value(" ")
                .build());
        specification = new HabitFactSpecification(searchCriteriaList);
        Predicate conjunction = mock(Predicate.class);
        when(builder.conjunction()).thenReturn(conjunction);
        Root<HabitFactTranslation> habitFactTranslationRoot = mock(Root.class);
        when(MockCriteriaQuery.from(HabitFactTranslation.class)).thenReturn(habitFactTranslationRoot);

        specification.toPredicate(HabitFactRoot, MockCriteriaQuery, builder);

        verify(builder,times(2)).conjunction();
        verify(MockCriteriaQuery).from(HabitFactTranslation.class);
        verifyNoMoreInteractions(MockCriteriaQuery);
    }


    @Test
    void toPredicate_multipleCriteria(){
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        searchCriteriaList.add(SearchCriteria.builder()
                .key("id")
                .type("id")
                .value(2L)
                .build());
        searchCriteriaList.add(SearchCriteria.builder()
                .key("habitId")
                .type("habitId")
                .value(5L)
                .build());
        searchCriteriaList.add(SearchCriteria.builder()
                .key("content")
                .type("content")
                .value("Smth")
                .build());
        specification=new HabitFactSpecification(searchCriteriaList);

        Predicate conjunctionPredicate = mock(Predicate.class);
        when(builder.conjunction()).thenReturn(conjunctionPredicate);

        Predicate idEqualPredicate = mock(Predicate.class);
        when(HabitFactRoot.get("id")).thenReturn(id);
        when(builder.equal(id, 2L)).thenReturn(idEqualPredicate);

        Predicate habitIdEqualPredicate = mock(Predicate.class);
        when(HabitFactRoot.join(HabitFact_.habit)).thenReturn(habitJoin);
        when(habitJoin.get(Habit_.id)).thenReturn(habitIdPath);
        when(builder.equal(habitIdPath, 5L)).thenReturn(habitIdEqualPredicate);

        Root<HabitFactTranslation> habitFactTranslationRoot = mock(Root.class);
        when(MockCriteriaQuery.from(HabitFactTranslation.class)).thenReturn(habitFactTranslationRoot);
        when(habitFactTranslationRoot.get(HabitFactTranslation_.content)).thenReturn(contentPath);
        when(habitFactTranslationRoot.get(HabitFactTranslation_.habitFact).get(HabitFact_.id)).thenReturn(habitIdPath);
        when(HabitFactRoot.get(HabitFact_.id)).thenReturn(habitFactRootIdPath);

        Predicate likePredicate = mock(Predicate.class);
        Predicate contentEqualPredicate = mock(Predicate.class);
        Predicate contentAndPredicate = mock(Predicate.class);

        when(builder.like(contentPath, "%Smth%")).thenReturn(likePredicate);
        when(builder.equal(habitIdPath, habitFactRootIdPath)).thenReturn(contentEqualPredicate);
        when(builder.and(likePredicate, contentEqualPredicate)).thenReturn(contentAndPredicate);

        Predicate firstCombination = mock(Predicate.class);
        Predicate secondCombination = mock(Predicate.class);
        Predicate finalPredicate = mock(Predicate.class);

        when(builder.and(conjunctionPredicate, idEqualPredicate)).thenReturn(firstCombination);
        when(builder.and(firstCombination, habitIdEqualPredicate)).thenReturn(secondCombination);
        when(builder.and(secondCombination, contentAndPredicate)).thenReturn(finalPredicate);

        Predicate result = specification.toPredicate(HabitFactRoot, MockCriteriaQuery, builder);
        assertNotNull(result);
        assertEquals(finalPredicate, result);

        verify(builder).conjunction();
        verify(builder).equal(id, 2L);
        verify(builder).equal(habitIdPath, 5L);
        verify(builder).like(contentPath, "%Smth%");
        verify(builder).equal(habitIdPath, habitFactRootIdPath);
        verify(builder).and(conjunctionPredicate, idEqualPredicate);
        verify(builder).and(firstCombination, habitIdEqualPredicate);
        verify(builder).and(secondCombination, contentAndPredicate);

        }


}



