package greencity.service;

import greencity.annotations.RatingCalculationEnum;
import greencity.constant.ErrorMessage;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.EventCommentDtoResponse;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.EventComment;
import greencity.entity.User;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.rating.RatingCalculation;
import greencity.repository.EventRepo;
import greencity.repository.options.EventCommentRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import static greencity.constant.AppConstant.AUTHORIZATION;

@Service
@AllArgsConstructor
public class EventCommentServiceImpl implements EventCommentService {
    private final EventCommentRepository eventCommentRepository;
    private final EventRepo eventRepository;
    private final ModelMapper mapper;
    private final HttpServletRequest httpServletRequest;
    private final RatingCalculation ratingCalculation;



    @Override
    public EventCommentDtoResponse createComment(AddEventCommentDtoRequest addEventCommentDtoRequest, Long eventId, UserVO userVO) {
        Event event = eventRepository.findEventById(eventId).orElseThrow(()->new NotFoundException("Event not found"));

        EventComment eventComment = mapper.map(addEventCommentDtoRequest, EventComment.class);
        eventComment.setEvent(event);
        eventComment.setUser(mapper.map(userVO, User.class));

        if(addEventCommentDtoRequest.getParentCommentId() != 0) {
            EventComment parentComment = eventCommentRepository.findById(addEventCommentDtoRequest.getParentCommentId()).orElseThrow(
                    ()->new BadRequestException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));

            if(parentComment == null){
                eventComment.setParentComment(parentComment);
            }else {
                throw new BadRequestException(ErrorMessage.CANNOT_REPLY_THE_REPLY);
            }
        }

        String accesToken = httpServletRequest.getHeader(AUTHORIZATION);
        CompletableFuture.runAsync(
                ()-> ratingCalculation.ratingCalculation(RatingCalculationEnum.ADD_COMMENT, userVO, accesToken));
        return mapper.map(eventCommentRepository.save(eventComment), EventCommentDtoResponse.class);
    }
}
