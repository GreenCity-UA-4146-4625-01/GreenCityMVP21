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
    private Root<HabitFact> habitFactRoot;

    @Mock
    private CriteriaBuilder builder;

    @Mock
    private CriteriaQuery mockCriteriaQuery;

    // For tests which checks id(without join)
    @Mock
    private Path<Object> id;

    // For tests which checks id with join
    @Mock
    private Path<Long> habitIdPath;

    @Mock
    private Path<Long> habitFactRootIdPath;

    @Mock
    private Join<HabitFact,Habit> habitJoin;
    
    // For content tests 
    
    @Mock
    private Path<String> contentPath;

    //Translation join 
    @Mock
    private Predicate likePredicate;

    HabitFactSpecification specification;

    private static final Long test_Id=2L;
    private static final Long test_Habit_Id=5L;
    private static final String test_Content="Smth";
    
    @Test
    void toPredicate_Id(){
        List<SearchCriteria> searchCriteriaList = List.of(
                SearchCriteria.builder()
                        .key("id")
                        .type("id")
                        .value(test_Id)
                        .build());

        specification = new HabitFactSpecification(searchCriteriaList);
        Predicate conjunctionPredicate = mock(Predicate.class);
        Predicate equalPredicate = mock(Predicate.class);
        Predicate andPredicate = mock(Predicate.class);
        when(builder.conjunction()).thenReturn(conjunctionPredicate);
        when(habitFactRoot.get("id")).thenReturn(id);
        when(builder.equal(id, test_Id)).thenReturn(equalPredicate);
        when(builder.and(conjunctionPredicate, equalPredicate)).thenReturn(andPredicate);
        Predicate allPredicates = builder.conjunction();
        for (int i = 0; i < searchCriteriaList.size(); i++) {
            SearchCriteria criteria = searchCriteriaList.get(i);
            if (criteria.getType().equals("id")) {
                allPredicates = builder.and(allPredicates,
                        specification.getNumericPredicate(habitFactRoot, builder, criteria));
            }
        }
        verify(builder).equal(id, test_Id);
        verify(habitFactRoot).get("id");
        verify(builder).conjunction();
        verify(builder).and(conjunctionPredicate, equalPredicate);
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void toPredicate_HabitIdWithJoin() {
        List<SearchCriteria> searchCriteriaList = List.of(
        SearchCriteria.builder()
                .key("habitId")
                .type("habitId")
                .value(test_Habit_Id)
                .build());
        specification = new HabitFactSpecification(searchCriteriaList);
        when(habitFactRoot.join(HabitFact_.habit)).thenReturn(habitJoin);
        when(habitJoin.get(Habit_.id)).thenReturn(habitIdPath);
        Predicate mockPredicate = mock(Predicate.class);
        when(builder.equal(habitIdPath, test_Habit_Id)).thenReturn(mockPredicate);
        Predicate conjunctionPredicate = mock(Predicate.class);
        when(builder.conjunction()).thenReturn(conjunctionPredicate);
        when(builder.and(conjunctionPredicate, mockPredicate)).thenReturn(mockPredicate);
        Predicate result = specification.toPredicate(habitFactRoot, mockCriteriaQuery, builder);
        assertNotNull(result);
        assertEquals(mockPredicate,result);

    }

    @Test
    void toPredicate_WithTranslationJoin(){
        List<SearchCriteria> searchCriteriaList = List.of(
        SearchCriteria.builder()
                .key("content")
                .type("content")
                .value(test_Content)
                .build());
        specification = new HabitFactSpecification(searchCriteriaList);

        Root<HabitFactTranslation> habitFactTranslationRoot = mock(Root.class);
        when(mockCriteriaQuery.from(HabitFactTranslation.class)).thenReturn(habitFactTranslationRoot);

        when(habitFactTranslationRoot.get(HabitFactTranslation_.content)).thenReturn(contentPath);
        when(habitFactTranslationRoot.get(HabitFactTranslation_.habitFact).get(HabitFact_.id)).thenReturn(habitIdPath);
        when(habitFactRoot.get(HabitFact_.id)).thenReturn(habitFactRootIdPath);
        
        Predicate equalPredicate = mock(Predicate.class);
        Predicate andPredicate = mock(Predicate.class);
        Predicate conjunctionPredicate = mock(Predicate.class);

        when(builder.conjunction()).thenReturn(conjunctionPredicate);
        when(builder.like(contentPath, "%Smth%")).thenReturn(likePredicate);
        when(builder.equal(habitIdPath, habitFactRootIdPath)).thenReturn(equalPredicate);
        when(builder.and(likePredicate, equalPredicate)).thenReturn(andPredicate);
        when(builder.and(conjunctionPredicate, andPredicate)).thenReturn(andPredicate);

        Predicate result = specification.toPredicate(habitFactRoot, mockCriteriaQuery, builder);
        assertNotNull(result);
        assertEquals(andPredicate, result);
    }
    
    @Test
    void toPredicate_IdNull() {
        List<SearchCriteria> searchCriteriaList = List.of(
                SearchCriteria.builder()
                        .key("id")
                        .type("id")
                        .value(null)
                        .build());

        specification = new HabitFactSpecification(searchCriteriaList);

        Predicate conjunction = mock(Predicate.class);

        when(builder.conjunction()).thenReturn(conjunction);

        specification.toPredicate(habitFactRoot, mockCriteriaQuery, builder);

        verify(builder).conjunction();
        verifyNoMoreInteractions(builder, mockCriteriaQuery);
    }

    @Test
    void toPredicate_HabitIdNull() {
        List<SearchCriteria> searchCriteriaList = List.of(
                SearchCriteria.builder()
                        .key("habitId")
                        .type("habitId")
                        .value(null)
                        .build());

        specification = new HabitFactSpecification(searchCriteriaList);

        Predicate conjunction = mock(Predicate.class);

        when(builder.conjunction()).thenReturn(conjunction);

        specification.toPredicate(habitFactRoot, mockCriteriaQuery, builder);

        verify(builder).conjunction();
        verifyNoMoreInteractions(builder, mockCriteriaQuery);
    }
    
    @Test
    void toPredicate_ContentEmpty(){
        List<SearchCriteria> searchCriteriaList = List.of(
        SearchCriteria.builder()
                .key("content")
                .type("content")
                .value(" ")
                .build());
        specification = new HabitFactSpecification(searchCriteriaList);
        Predicate conjunction = mock(Predicate.class);
        when(builder.conjunction()).thenReturn(conjunction);
        Root<HabitFactTranslation> habitFactTranslationRoot = mock(Root.class);
        when(mockCriteriaQuery.from(HabitFactTranslation.class)).thenReturn(habitFactTranslationRoot);

        specification.toPredicate(habitFactRoot, mockCriteriaQuery, builder);

        verify(builder,times(2)).conjunction();
        verify(mockCriteriaQuery).from(HabitFactTranslation.class);
        verifyNoMoreInteractions(mockCriteriaQuery);
    }


    @Test
    void toPredicate_multipleCriteria(){
        List<SearchCriteria> searchCriteriaList = List.of(
        SearchCriteria.builder()
                .key("id")
                .type("id")
                .value(test_Id)
                .build(),
        SearchCriteria.builder()
                .key("habitId")
                .type("habitId")
                .value(test_Habit_Id)
                .build(),
        SearchCriteria.builder()
                .key("content")
                .type("content")
                .value(test_Content)
                .build());
        specification=new HabitFactSpecification(searchCriteriaList);

        Predicate conjunctionPredicate = mock(Predicate.class);
        when(builder.conjunction()).thenReturn(conjunctionPredicate);

        Predicate idEqualPredicate = mock(Predicate.class);
        when(habitFactRoot.get("id")).thenReturn(id);
        when(builder.equal(id, test_Id)).thenReturn(idEqualPredicate);

        Predicate habitIdEqualPredicate = mock(Predicate.class);
        when(habitFactRoot.join(HabitFact_.habit)).thenReturn(habitJoin);
        when(habitJoin.get(Habit_.id)).thenReturn(habitIdPath);
        when(builder.equal(habitIdPath, test_Habit_Id)).thenReturn(habitIdEqualPredicate);

        Root<HabitFactTranslation> habitFactTranslationRoot = mock(Root.class);
        when(mockCriteriaQuery.from(HabitFactTranslation.class)).thenReturn(habitFactTranslationRoot);
        when(habitFactTranslationRoot.get(HabitFactTranslation_.content)).thenReturn(contentPath);
        when(habitFactTranslationRoot.get(HabitFactTranslation_.habitFact).get(HabitFact_.id)).thenReturn(habitIdPath);
        when(habitFactRoot.get(HabitFact_.id)).thenReturn(habitFactRootIdPath);

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

        Predicate result = specification.toPredicate(habitFactRoot, mockCriteriaQuery, builder);
        assertNotNull(result);
        assertEquals(finalPredicate, result);

        verify(builder).conjunction();
        verify(builder).equal(id, test_Id);
        verify(builder).equal(habitIdPath, test_Habit_Id);
        verify(builder).like(contentPath, "%Smth%");
        verify(builder).equal(habitIdPath, habitFactRootIdPath);
        verify(builder).and(conjunctionPredicate, idEqualPredicate);
        verify(builder).and(firstCombination, habitIdEqualPredicate);
        verify(builder).and(secondCombination, contentAndPredicate);

        }
}



