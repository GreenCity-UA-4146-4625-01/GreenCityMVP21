package greencity.filters;

import greencity.entity.RatingStatistics;
import greencity.entity.RatingStatistics_;
import greencity.entity.User;
import greencity.entity.User_;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class RatingStatisticsSpecificationTest {
    @Mock
    private CriteriaBuilder cb;

    @Mock
    private CriteriaQuery<?> cq;

    @Mock
    private Root<RatingStatistics> root;

    @Mock
    private Join<RatingStatistics, User> userJoin;

    @Mock
    private Path<Long> userIdPath;

    @Mock
    private Path<String> userEmailPath;

    @Mock
    private Predicate predicateConjunction;

    @Mock
    private Predicate predicateDisjunction;

    @Mock
    private SingularAttribute<RatingStatistics, User> ratingStatisticsUserAttr;

    @Mock
    private SingularAttribute<User, Long> userIdAttr;

    @Mock
    private SingularAttribute<User, String> userEmailAttr;

    private RatingStatisticsSpecification specification;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        RatingStatistics_.user = ratingStatisticsUserAttr;
        User_.id = userIdAttr;
        User_.email = userEmailAttr;

        when(root.join(ratingStatisticsUserAttr)).thenReturn(userJoin);
        when(userJoin.get(userIdAttr)).thenReturn(userIdPath);
        when(userJoin.get(userEmailAttr)).thenReturn(userEmailPath);

        when(cb.conjunction()).thenReturn(predicateConjunction);
        when(cb.disjunction()).thenReturn(predicateDisjunction);
        when(cb.and(any(Predicate.class), any(Predicate.class))).thenReturn(predicateConjunction);
        when(cb.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicateDisjunction);

        specification = new RatingStatisticsSpecification();
    }

    @Test
    void testNumericFilters() {
        Root<RatingStatistics> root = mock(Root.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<?> criteriaQuery = mock(CriteriaQuery.class);

        Path<Object> idPath = mock(Path.class);
        Path<Object> pointsChangedPath = mock(Path.class);
        Path<Object> currentRatingPath = mock(Path.class);

        when(root.get("id")).thenReturn(idPath);
        when(root.get("pointsChanged")).thenReturn(pointsChangedPath);
        when(root.get("currentRating")).thenReturn(currentRatingPath);

        Predicate idPredicate = mock(Predicate.class);
        Predicate pointsPredicate = mock(Predicate.class);
        Predicate currentPredicate = mock(Predicate.class);

        when(criteriaBuilder.equal(idPath, 1)).thenReturn(idPredicate);
        when(criteriaBuilder.equal(pointsChangedPath, 10)).thenReturn(pointsPredicate);
        when(criteriaBuilder.equal(currentRatingPath, 5)).thenReturn(currentPredicate);

        Predicate conjunction = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);
        when(criteriaBuilder.and(any(Predicate.class), any(Predicate.class))).thenAnswer(invocation -> invocation.getArgument(1));

        List<SearchCriteria> criteriaList = List.of(
                new SearchCriteria(1, "id", "id"),
                new SearchCriteria(10, "pointsChanged", "pointsChanged"),
                new SearchCriteria(5, "currentRating", "currentRating")
        );

        RatingStatisticsSpecification spec = new RatingStatisticsSpecification(criteriaList);
        Predicate result = spec.toPredicate(root, criteriaQuery, criteriaBuilder);

        verify(criteriaBuilder).equal(idPath, 1);
        verify(criteriaBuilder).equal(pointsChangedPath, 10);
        verify(criteriaBuilder).equal(currentRatingPath, 5);
    }


    @Test
    void testEnumFilter() {
        SearchCriteria c = new SearchCriteria("ADD_COMMENT", "ratingCalculationEnum", "enum");

        Predicate disjunctionPredicate = mock(Predicate.class);
        Predicate orPredicate = mock(Predicate.class);
        Predicate conjunctionPredicate = mock(Predicate.class);

        when(cb.conjunction()).thenReturn(conjunctionPredicate);
        when(cb.disjunction()).thenReturn(disjunctionPredicate);

        @SuppressWarnings("unchecked")
        Path<Object> ratingEnumPath = mock(Path.class);
        when(root.get(c.getKey())).thenReturn(ratingEnumPath);

        when(cb.equal(any(), any())).thenReturn(orPredicate);

        when(cb.or(any(Predicate.class), any(Predicate.class))).thenAnswer(invocation -> invocation.getArgument(1));

        when(cb.and(any(Predicate.class), any(Predicate.class))).thenAnswer(invocation -> invocation.getArgument(1));

        RatingStatisticsSpecification specification = new RatingStatisticsSpecification(List.of(c));
        Predicate result = specification.toPredicate(root, cq, cb);

        verify(cb).disjunction();
        verify(cb, atLeastOnce()).or(any(), any());
    }


    @Test
    void testUserIdFilterValid() {
        SearchCriteria c = new SearchCriteria(42, "userId", "userId");

        Join<RatingStatistics, User> userJoin = mock(Join.class);
        Path<Long> idPath = mock(Path.class);

        when(root.join(ratingStatisticsUserAttr)).thenReturn(userJoin);
        when(userJoin.get(userIdAttr)).thenReturn(idPath);

        Predicate p = mock(Predicate.class);
        when(cb.equal(idPath, 42)).thenReturn(p);
        when(cb.and(any(), any())).thenAnswer(invocation -> invocation.getArgument(1));

        specification = new RatingStatisticsSpecification(List.of(c));
        Predicate result = specification.toPredicate(root, cq, cb);

        assertNotNull(result);
        verify(root).join(ratingStatisticsUserAttr);
        verify(userJoin).get(userIdAttr);
        verify(cb).equal(idPath, 42);
    }

    @Test
    void testUserIdFilterInvalid() {
        SearchCriteria c = new SearchCriteria("", "userId", "userId");

        Join<RatingStatistics, User> userJoin = mock(Join.class);
        Path<Long> idPath = mock(Path.class);

        when(root.join(ratingStatisticsUserAttr)).thenReturn(userJoin);
        when(userJoin.get(userIdAttr)).thenReturn(idPath);

        when(cb.and(any(), any())).thenAnswer(invocation -> invocation.getArgument(1));

        specification = new RatingStatisticsSpecification(List.of(c));
        Predicate result = specification.toPredicate(root, cq, cb);

        verify(cb, never()).equal(any(), any());
    }

    @Test
    void testUserMailFilter() {
        SearchCriteria c = new SearchCriteria("test@example.com", "userMail", "userMail");

        Join<RatingStatistics, User> userJoin = mock(Join.class);
        Path<String> emailPath = mock(Path.class);

        when(root.join(ratingStatisticsUserAttr)).thenReturn(userJoin);
        when(userJoin.get(userEmailAttr)).thenReturn(emailPath);

        Predicate p = mock(Predicate.class);
        when(cb.like(emailPath, "%test@example.com%")).thenReturn(p);
        when(cb.and(any(), any())).thenAnswer(invocation -> invocation.getArgument(1));

        specification = new RatingStatisticsSpecification(List.of(c));
        Predicate result = specification.toPredicate(root, cq, cb);

        assertNotNull(result);
        verify(root).join(ratingStatisticsUserAttr);
        verify(userJoin).get(userEmailAttr);
        verify(cb).like(emailPath, "%test@example.com%");
    }

    @Test
    void testMultipleCriteriaAnd() {
        SearchCriteria c1 = new SearchCriteria(1, "id", "id");
        SearchCriteria c2 = new SearchCriteria(20, "pointsChanged", "pointsChanged");

        Predicate predicateMock = mock(Predicate.class);
        Predicate combinedPredicateMock = mock(Predicate.class);

        when(cb.conjunction()).thenReturn(predicateMock);
        when(cb.and(any(Predicate.class), any(Predicate.class))).thenReturn(combinedPredicateMock);

        RatingStatisticsSpecification spySpec = spy(new RatingStatisticsSpecification(List.of(c1, c2)));

        doReturn(predicateMock).when(spySpec).getNumericPredicate(any(), any(), any());

        Predicate result = spySpec.toPredicate(root, cq, cb);

        assertNotNull(result);

        verify(spySpec, times(2)).getNumericPredicate(any(), any(), any());
        verify(cb, atLeastOnce()).and(any(), any());
        verify(cb, atLeastOnce()).conjunction();
    }
}