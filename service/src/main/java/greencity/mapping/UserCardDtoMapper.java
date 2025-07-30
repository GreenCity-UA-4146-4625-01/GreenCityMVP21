package greencity.mapping;

import greencity.dto.userfriend.UserCardDto;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

/**
 * Mapper class that converts {@link User} entities into {@link UserCardDto} DTOs.
 * <p>
 * This class is used for simplifying user information for displaying in user cards
 * (e.g., in search results, friend lists). It does not include fields like mutual friends or friend status.
 */
@Component
public class UserCardDtoMapper extends AbstractConverter<User, UserCardDto> {

    /**
     * Converts a {@link User} entity into a {@link UserCardDto}.
     *
     * @param user the User entity to convert
     * @return the corresponding UserCardDto with basic user info (id, name, city, rating, profile picture)
     */
    @Override
    protected UserCardDto convert(User user) {
        return UserCardDto.builder()
                .id(user.getId())
                .name(user.getName())
                .city(user.getCity())
                .rating(user.getRating())
                .profilePicturePath(user.getProfilePicturePath())
                .build();
    }
}
