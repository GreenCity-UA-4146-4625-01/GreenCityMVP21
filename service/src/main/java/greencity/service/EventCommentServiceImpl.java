package greencity.service;

import greencity.annotations.RatingCalculationEnum;
import greencity.constant.ErrorMessage;
import greencity.dto.eventcomment.*;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.EventComment;
import greencity.entity.User;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.mapping.AddEventCommentDtoRequestToEventCommentMapper;
import greencity.mapping.EditEventCommentDtoRequestToEventCommentMapper;
import greencity.rating.RatingCalculation;
import greencity.repository.EventCommentRepository;
import greencity.repository.EventRepo;
import greencity.repository.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static greencity.constant.AppConstant.AUTHORIZATION;

@Service
@AllArgsConstructor
public class EventCommentServiceImpl implements EventCommentService {
    private final EventCommentRepository eventCommentRepository;
    private final EventRepo eventRepository;
    private final HttpServletRequest httpServletRequest;
    private final RatingCalculation ratingCalculation;
    private final UserRepo userRepo;
    private final AddEventCommentDtoRequestToEventCommentMapper addCommentMapper;
    private final EditEventCommentDtoRequestToEventCommentMapper editCommentMapper;

    @Override
    public EventCommentDtoResponse createComment(AddEventCommentDtoRequest addEventCommentDtoRequest, Long eventId, UserVO userVO) {
        Event event = eventRepository.findEventById(eventId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));

        Set<User> mentionedUsers = Collections.emptySet();
        if (addEventCommentDtoRequest.getMentionedUserIds() != null && !addEventCommentDtoRequest.getMentionedUserIds().isEmpty()) {
            mentionedUsers = new HashSet<>(userRepo.findAllById(addEventCommentDtoRequest.getMentionedUserIds()));
        }

        EventComment eventComment = addCommentMapper.convert(addEventCommentDtoRequest, mentionedUsers);

        eventComment.setEvent(event);
        User author = User.builder()
                .id(userVO.getId())
                .name(userVO.getName())
                .email(userVO.getEmail())
                .build();

        if (addEventCommentDtoRequest.getParentCommentId() != null && addEventCommentDtoRequest.getParentCommentId() != 0) {
            EventComment parentComment = eventCommentRepository.findById(addEventCommentDtoRequest.getParentCommentId())
                    .orElseThrow(() -> new BadRequestException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));

            if (parentComment.getParentComment() != null) {
                throw new BadRequestException(ErrorMessage.CANNOT_REPLY_THE_REPLY);
            }
            eventComment.setParentComment(parentComment);
        }

        String accessToken = httpServletRequest.getHeader(AUTHORIZATION);
        CompletableFuture.runAsync(() -> ratingCalculation.ratingCalculation(RatingCalculationEnum.ADD_COMMENT, userVO, accessToken));

        EventComment saved = eventCommentRepository.save(eventComment);

        return EventCommentDtoResponse.builder()
                .id(saved.getId())
                .text(saved.getText())
                .modifiedDate(saved.getModifiedDate())
                .replies(saved.getReplies() != null ? saved.getReplies().size() : 0)
                .likes(saved.getUsersLiked() != null ? saved.getUsersLiked().size() : 0)
                .author(EventCommentAuthorDto.builder()
                        .id(author.getId())
                        .name(author.getName())
                        .userProfilePicturePath(author.getProfilePicturePath())
                        .build())
                .mentionedUser(saved.getMentionedUsers().stream()
                        .map(user -> EventShortInfoUserVO.builder()
                                .id(user.getId())
                                .name(user.getName())
                                .userProfilePicturePath(user.getProfilePicturePath())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }

    @Override
    public Page<EventShortInfoUserVO> getMentionableUsers(String query, Pageable pageable) {
        return userRepo.findAuthorizedUsersForMention(
                query,
                UserStatus.ACTIVATED,
                pageable);
    }

    @Override
    public Page<EventCommentViewDto> getCommentsByEventId(Long eventId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("modifiedDate").descending());
        Page<EventComment> commentsPage = eventCommentRepository.findTopLevelCommentsByEventId(eventId, pageable);

        List<EventCommentViewDto> filteredDtos = commentsPage
                .stream()
                .filter(comment -> !comment.isDeleted()) // фильтрация удалённых
                .map(comment -> {
                    String authorName = comment.getUser() != null ? comment.getUser().getName() : "Unknown";
                    String authorAvatar = comment.getUser() != null ? comment.getUser().getProfilePicturePath() : null;
                    int likesCount = comment.getUsersLiked() != null ? comment.getUsersLiked().size() : 0;

                    return new EventCommentViewDto(
                            comment.getId(),
                            authorName,
                            authorAvatar,
                            comment.getModifiedDate(),
                            likesCount,
                            comment.getText()
                    );
                })
                .collect(Collectors.toList());

        return new PageImpl<>(filteredDtos, pageable, commentsPage.getTotalElements());
    }

    @Override
    public EventCommentViewDto getCommentById(Long commentId) {
        EventComment eventComment = eventCommentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment not found"));
        if (eventComment.isDeleted()) {
            throw new EntityNotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION);
        }

        String authorName = eventComment.getUser() != null ? eventComment.getUser().getName() : "Unknown";
        String authorAvatar = eventComment.getUser() != null ? eventComment.getUser().getProfilePicturePath() : null;
        int likes = eventComment.getUsersLiked() != null ? eventComment.getUsersLiked().size() : 0;

        return new EventCommentViewDto(
                eventComment.getId(),
                authorName,
                authorAvatar,
                eventComment.getModifiedDate(),
                likes,
                eventComment.getText()
        );
    }

    @Override
    public int countOfCommentsByEventId(Long eventId) {
        Event event = eventRepository.findEventById(eventId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));

        return eventCommentRepository.countOfComments(event.getId());
    }

    @Override
    public void like(UserVO userVO, Long id) {
        EventComment comment = eventCommentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
        if (comment.isDeleted()) {
            throw new EntityNotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION);
        }

        User user = userRepo.findById(userVO.getId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID));

        Set<User> likedUsers = comment.getUsersLiked();

        if (likedUsers.contains(user)) {
            likedUsers.remove(user);
        } else {
            likedUsers.add(user);
        }

        comment.setUsersLiked(likedUsers);
        eventCommentRepository.save(comment);
    }

    /**
     * Edits an existing comment identified by {@code commentId}.
     * <p>
     * The method checks if the comment with the given ID exists; if not, it throws {@link NotFoundException}.
     * It also verifies that the current user is the author of the comment; otherwise, it throws {@link UserHasNoPermissionToAccessException}.
     * Then it updates the comment's text, the set of mentioned users, and the modification timestamp.
     * The updated comment is saved in the repository.
     * Finally, it returns the updated comment data as {@link EventCommentEditViewDto}.
     *
     * @param commentId the ID of the comment to be edited
     * @param currentUser the user performing the edit operation
     * @param editEventCommentDtoRequest the DTO containing updated comment data (text and mentioned users)
     * @return the updated comment DTO {@link EventCommentEditViewDto} with current comment information
     * @throws NotFoundException if no comment with the specified ID is found
     * @throws UserHasNoPermissionToAccessException if the current user is not the author of the comment and has no permission to edit
     */
    @Override
    public EventCommentEditViewDto editComment(Long commentId, UserVO currentUser, EditEventCommentDtoRequest editEventCommentDtoRequest) {
        EventComment comment = eventCommentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
        if (comment.isDeleted()) {
            throw new EntityNotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION);
        }

        if (!currentUser.getId().equals(comment.getUser().getId())) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_IS_NOT_THE_AUTHOR_OF_THE_COMMENT + commentId);
        }

        Set<User> mentionedUsers = Collections.emptySet();
        if (editEventCommentDtoRequest.getMentionedUserIds() != null && !editEventCommentDtoRequest.getMentionedUserIds().isEmpty()) {
            mentionedUsers = new HashSet<>(userRepo.findAllById(editEventCommentDtoRequest.getMentionedUserIds()));
        }

        editCommentMapper.update(comment, editEventCommentDtoRequest, mentionedUsers);
        eventCommentRepository.save(comment);

        return eventCommentRepository.getEventCommentByID(commentId);
    }

    @Override
    public List<EventShortInfoUserVO> getUsersWhoLikedComment(Long commentId) {
        EventComment comment = eventCommentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
        if (comment.isDeleted()) {
            throw new EntityNotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION);
        }

        return comment.getUsersLiked().stream()
                .map(user -> EventShortInfoUserVO.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .userProfilePicturePath(user.getProfilePicturePath())
                        .build())
                .sorted(Comparator.comparing(EventShortInfoUserVO::getName)) // or by like time if tracked
                .toList();
    }
  
    @Override  
    public void deleteById(Long id, UserVO user) {
        EventComment eventComment = eventCommentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));

        if(user.getRole() != Role.ROLE_ADMIN && !user.getId().equals(eventComment.getUser().getId())) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        if (eventComment.getReplies() != null && !eventComment.getReplies().isEmpty()) {
            eventComment.getReplies().forEach(c->c.setDeleted(true));
        }
        eventComment.setDeleted(true);
        String accessToken = httpServletRequest.getHeader(AUTHORIZATION);
        CompletableFuture.runAsync(
                () -> ratingCalculation.ratingCalculation(RatingCalculationEnum.DELETE_COMMENT, user, accessToken));
        eventCommentRepository.save(eventComment);
    }
}
