package greencity.service;

import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.EventCommentDtoResponse;
import greencity.mapping.EventCommentToDtoResponseMapper;
import greencity.repository.EventRepo;
import greencity.repository.UserRepo;
import greencity.repository.options.EventCommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EventCommentServiceImpl implements EventCommentService {
    private final EventCommentRepository eventCommentRepository;
    private final EventRepo eventRepository;
    private final UserRepo userRepository;
    private final EventCommentToDtoResponseMapper mapper;


    @Override
    public EventCommentDtoResponse createComment(AddEventCommentDtoRequest addEventCommentDtoRequest, Long userId) {
        return null;
    }
}
