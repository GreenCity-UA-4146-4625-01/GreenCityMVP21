package greencity.service;

import greencity.dto.event.EditEventRequestDto;
import greencity.dto.user.UserVO;

public interface EventService {

    EditEventRequestDto updateEventById(Long eventId, EditEventRequestDto dto, UserVO user);
}
