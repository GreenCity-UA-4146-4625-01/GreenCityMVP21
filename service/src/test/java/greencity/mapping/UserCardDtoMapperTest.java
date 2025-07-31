package greencity.mapping;

import greencity.dto.userfriend.UserCardDto;
import greencity.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserCardDtoMapperTest {

    private UserCardDtoMapper mapper;

    @BeforeEach
    @DisplayName("Set up UserCardDtoMapper instance before each test")
    void setUp() {
        mapper = new UserCardDtoMapper();
    }

    @Test
    @DisplayName("Should correctly convert User to UserCardDto")
    void testConvert_UserToUserCardDto() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setCity("Kyiv");
        user.setRating(4.5);
        user.setProfilePicturePath("/images/john.jpg");

        UserCardDto dto = mapper.convert(user);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("John Doe");
        assertThat(dto.getCity()).isEqualTo("Kyiv");
        assertThat(dto.getRating()).isEqualTo(4.5);
        assertThat(dto.getProfilePicturePath()).isEqualTo("/images/john.jpg");
    }

}
