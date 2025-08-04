package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.TestConst;
import greencity.converters.UserArgumentResolver;
import greencity.dto.PageableDto;
import greencity.dto.user.UserVO;
import greencity.dto.userfriend.UserCardDto;
import greencity.enums.FriendStatus;
import greencity.service.FriendService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FriendControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FriendService friendService;
    @Mock
    private UserService userService;

    @InjectMocks
    private FriendController friendController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ModelMapper modelMapper = new ModelMapper();
    private UserVO mockUserVO;

    @BeforeEach
    void setup() {
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders
                .standaloneSetup(friendController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                        new UserArgumentResolver(userService, modelMapper))
                .build();

        mockUserVO = ModelUtils.getUserVO();
    }

    @Test
    @DisplayName("Search friends with filters should return OK")
    void searchFriends_ShouldReturnOk() throws Exception {
        PageableDto<UserCardDto> responseDto = new PageableDto<>(List.of(), 0, 0, 0);
        when(userService.findByEmail(mockUserVO.getEmail())).thenReturn(mockUserVO);
        when(friendService.searchUsers(anyString(), anyBoolean(), anyBoolean(), any(Pageable.class), anyLong()))
                .thenReturn(responseDto);

        mockMvc.perform(get("/friends/search")
                        .principal(() -> mockUserVO.getEmail())
                        .param("query", mockUserVO.getName())
                        .param("city", "true")
                        .param("mutualFriends", "false")
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(status().isOk());

        verify(friendService).searchUsers(eq(mockUserVO.getName()), eq(true), eq(false), any(Pageable.class), eq(1L));
    }

    @Test
    @DisplayName("Send a friend request should return OK")
    void addNewFriend_ShouldReturnOk() throws Exception {
        when(userService.findByEmail(TestConst.EMAIL)).thenReturn(mockUserVO);

        mockMvc.perform(post("/friends/{friendId}", 2L)
                        .principal(() -> TestConst.EMAIL)
                )
                .andExpect(status().isOk());

        verify(friendService).sendFriendRequest(1L, 2L);
    }

    @Test
    @DisplayName("Accept a friend request should return OK")
    void acceptFriendRequest_ShouldReturnOk() throws Exception {
        when(userService.findByEmail(TestConst.EMAIL)).thenReturn(mockUserVO);

        mockMvc.perform(patch("/friends/{friendId}/acceptFriend", 2L)
                        .principal(() -> TestConst.EMAIL)
                )
                .andExpect(status().isOk());

        verify(friendService).acceptFriendRequest(1L, 2L);
    }

    @Test
    @DisplayName("Cancel a friend request should return OK")
    void cancelFriendRequest_ShouldReturnOk() throws Exception {
        when(userService.findByEmail(TestConst.EMAIL)).thenReturn(mockUserVO);

        mockMvc.perform(delete("/friends/{friendId}", 2L)
                        .principal(() -> TestConst.EMAIL)
                )
                .andExpect(status().isOk());

        verify(friendService).cancelFriendRequest(1L, 2L);
    }

    @Test
    @DisplayName("Get all friends should return OK")
    void getAllFriends_ShouldReturnOk() throws Exception {
        PageableDto<UserCardDto> responseDto = new PageableDto<>(List.of(), 0, 0, 0);

        when(userService.findByEmail(TestConst.EMAIL)).thenReturn(mockUserVO);
        when(friendService.getAllFriends(anyLong(), any(Pageable.class))).thenReturn(responseDto);

        mockMvc.perform(get("/friends")
                        .param("page", "0")
                        .param("size", "10")
                        .principal(() -> TestConst.EMAIL)
                )
                .andExpect(status().isOk());

        verify(friendService).getAllFriends(eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("Get friend status should return OK if friendship exists")
    void getFriendStatus_ShouldReturnOk() throws Exception {
        when(userService.findByEmail(TestConst.EMAIL)).thenReturn(mockUserVO);
        when(friendService.getFriendStatus(1L, 2L)).thenReturn(Optional.of(FriendStatus.FRIEND));

        mockMvc.perform(get("/friends/{friendId}/status", 2L)
                        .principal(() -> TestConst.EMAIL)
                )
                .andExpect(status().isOk())
                .andExpect(content().xml("<FriendStatus>FRIEND</FriendStatus>"));

        verify(friendService).getFriendStatus(1L, 2L);
    }

    @Test
    @DisplayName("Get friend status should return NOT_FOUND if friendship does not exist")
    void getFriendStatus_ShouldReturnNotFound() throws Exception {
        when(userService.findByEmail(TestConst.EMAIL)).thenReturn(mockUserVO);
        when(friendService.getFriendStatus(1L, 2L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/friends/{friendId}/status", 2L)
                        .principal(() -> TestConst.EMAIL)
                )
                .andExpect(status().isNotFound());

        verify(friendService).getFriendStatus(1L, 2L);
    }

    @Test
    @DisplayName("Revoke a sent friend request should return OK")
    void revokeFriendRequest_ShouldReturnOk() throws Exception {
        when(userService.findByEmail(TestConst.EMAIL)).thenReturn(mockUserVO);
        doNothing().when(friendService).revokeFriendRequest(1L, 2L);

        mockMvc.perform(delete("/friends/{friendId}/revoke", 2L)
                        .principal(() -> TestConst.EMAIL)
                )
                .andExpect(status().isOk());

        verify(friendService).revokeFriendRequest(1L, 2L);
    }
}
