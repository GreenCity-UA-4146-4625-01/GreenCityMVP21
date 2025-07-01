package greencity.mapping;

import greencity.dto.user.UserFilterDtoRequest;
import greencity.entity.Filter;
import greencity.enums.FilterType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class FilterDtoRequestMapperTest {
    @InjectMocks
    FilterDtoRequestMapper mapper;

    @Test
    void convert_validInput_returnsCorrectFilter() {
        var dto = UserFilterDtoRequest.builder()
            .name("UserFilter")
            .searchCriteria("email")
            .userRole("ADMIN")
            .userStatus("ACTIVE")
            .build();

        Filter result = mapper.convert(dto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("UserFilter");
        assertThat(result.getType()).isEqualTo(FilterType.USERS.toString());
        assertThat(result.getValues()).isEqualTo("email;ADMIN;ACTIVE");
    }

    @Test
    void convert_nullInput_ThrowsNullPointerException() {

        assertThrows(NullPointerException.class, () -> {
            mapper.convert((UserFilterDtoRequest) null);
        });
    }

    @Test
    void convert_emptyFields_stillReturnsFilter() {
        var dto = UserFilterDtoRequest.builder()
            .name("")
            .searchCriteria("")
            .userRole("")
            .userStatus("")
            .build();

        Filter result = mapper.convert(dto);

        assertThat(result.getName()).isEqualTo("");
        assertThat(result.getType()).isEqualTo("USERS");
        assertThat(result.getValues()).isEqualTo(";;");
    }
}
