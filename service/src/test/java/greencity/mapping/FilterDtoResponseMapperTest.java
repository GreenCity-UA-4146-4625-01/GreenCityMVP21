package greencity.mapping;

import greencity.dto.user.UserFilterDtoRequest;
import greencity.dto.user.UserFilterDtoResponse;
import greencity.entity.Filter;
import greencity.enums.FilterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class FilterDtoResponseMapperTest {
    @InjectMocks
    private FilterDtoRequestMapper requestMapper;
    private FilterDtoResponseMapper responseMapper;

    @BeforeEach
    void setUp() {
        requestMapper = new FilterDtoRequestMapper();
        responseMapper = new FilterDtoResponseMapper();
    }
    @Test
    void convertRequestDto_toEntity_fullFields_mapsCorrectly() {

        UserFilterDtoRequest dto = new UserFilterDtoRequest();
        dto.setName("TestName");
        dto.setSearchCriteria("criteria");
        dto.setUserRole("role");
        dto.setUserStatus("status");


        Filter entity = requestMapper.convert(dto);


        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo(dto.getName());
        assertThat(entity.getType()).isEqualTo(FilterType.USERS.toString());
        assertThat(entity.getValues()).isEqualTo("criteria;role;status");
    }

    @Test
    void convertRequestDto_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> requestMapper.convert((UserFilterDtoRequest) null));
    }

    @Test
    void convertRequestDto_partialFields_throwsNullPointerException() {

        UserFilterDtoRequest dto = new UserFilterDtoRequest();
        dto.setName("Partial");
        dto.setSearchCriteria(null);
        dto.setUserRole("roleOnly");
        dto.setUserStatus(null);

        assertThrows(NullPointerException.class, () -> {
            requestMapper.convert(dto);
        });
    }



    @Test
    void convertResponseEntity_toDto_fullValues_mapsCorrectly() {
        Filter entity = Filter.builder()
                .id(10L)
                .name("FilterName")
                .values("criteria;role;status")
                .build();


        UserFilterDtoResponse dto = responseMapper.convert(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getName()).isEqualTo("FilterName");
        assertThat(dto.getSearchCriteria()).isEqualTo("criteria");
        assertThat(dto.getUserRole()).isEqualTo("role");
        assertThat(dto.getUserStatus()).isEqualTo("status");
    }

    @Test
    void convertResponseEntity_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> responseMapper.convert((Filter) null));
    }

    @Test
    void convertResponseEntity_valuesNull_throwsNullPointerException() {
        Filter entity = Filter.builder()
                .id(1L)
                .name("NullValues")
                .values(null)
                .build();

        assertThrows(NullPointerException.class, () -> responseMapper.convert(entity));
    }

    @Test
    void convertResponseEntity_valuesEmptyString_throwsArrayIndexOutOfBoundsException() {
        Filter entity = Filter.builder()
                .id(2L)
                .name("EmptyValues")
                .values("")
                .build();

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> responseMapper.convert(entity));
    }

    @Test
    void convertResponseEntity_valuesWithPartialParts_throwsArrayIndexOutOfBoundsException() {
        Filter entity = Filter.builder()
                .id(3L)
                .name("PartialValues")
                .values("criteriaOnly")
                .build();

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> responseMapper.convert(entity));
    }

    @Test
    void convert_emptyValues_throwsArrayIndexOutOfBoundsException() {
        Filter entity = Filter.builder()
                .id(4L)
                .name("")
                .values(";;")
                .build();

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> responseMapper.convert(entity));
    }
}
