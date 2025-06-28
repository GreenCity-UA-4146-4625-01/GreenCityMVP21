package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.GreenCityApplication;
import greencity.dto.shoppinglistitem.BulkSaveCustomShoppingListItemDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemSaveRequestDto;
import greencity.enums.ShoppingListItemStatus;
import greencity.service.CustomShoppingListItemService;
import greencity.service.UserService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomShoppingListItemController.class)
@ContextConfiguration(classes = GreenCityApplication.class)
class CustomShoppingListItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomShoppingListItemService customShoppingListItemService;

    @MockBean
    UserService userService;

    @MockBean
    private ModelMapper modelMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @WithMockUser
    @Test
    void getAllAvailableCustomShoppingListItems_Returns200AndListTest() throws Exception {

        CustomShoppingListItemResponseDto item1 = new CustomShoppingListItemResponseDto();
        item1.setId(1L);
        item1.setText("Item1");

        CustomShoppingListItemResponseDto item2 = new CustomShoppingListItemResponseDto();
        item2.setId(2L);
        item2.setText("Item2");

        List<CustomShoppingListItemResponseDto> mockedKList = List.of(item1, item2);

        when(customShoppingListItemService.findAllAvailableCustomShoppingListItems(1L, 2L))
                .thenReturn(mockedKList);

        mockMvc.perform(get("/custom/shopping-list-items/1/10"))
                .andExpect(status().isOk());
    }

    @WithMockUser
    @Test
    void saveUserCustomShoppingListItems_Returns201AndListTest() throws Exception {

        CustomShoppingListItemSaveRequestDto saveRequestDto = new CustomShoppingListItemSaveRequestDto();
        saveRequestDto.setText("Item1");

        BulkSaveCustomShoppingListItemDto bulkSaveRequestDto = new BulkSaveCustomShoppingListItemDto();
        bulkSaveRequestDto.setCustomShoppingListItemSaveRequestDtoList(List.of(saveRequestDto));

        CustomShoppingListItemResponseDto responseDto = new CustomShoppingListItemResponseDto().builder()
                .id(1L)
                .text("Item1")
                .status(ShoppingListItemStatus.ACTIVE)
                .build();
        List<CustomShoppingListItemResponseDto> responseList = List.of(responseDto);

        when(customShoppingListItemService.save(any(BulkSaveCustomShoppingListItemDto.class), eq(1L), eq(10L)))
                .thenReturn(responseList);

        String jsonRequest = new ObjectMapper().writeValueAsString(bulkSaveRequestDto);

        mockMvc.perform(post("/custom/shopping-list-items/1/10/custom-shopping-list-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .with(csrf())
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].text").value("Item1"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    @WithMockUser
    @Test
    void updateItemStatusTest() throws Exception {
        Long userId = 1L;
        Long itemId = 2L;
        String status = "ACTIVE";

        CustomShoppingListItemResponseDto expectedResponse = CustomShoppingListItemResponseDto.builder()
                .id(itemId)
                .text("Test item")
                .status(ShoppingListItemStatus.valueOf(status))
                .build();

        when(customShoppingListItemService.updateItemStatus(userId, itemId, status))
                .thenReturn(expectedResponse);

        mockMvc.perform(patch("/custom/shopping-list-items/1/custom-shopping-list-items")
                        .param("itemId", itemId.toString())
                        .param("status", status)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.text").value("Test item"))
                .andExpect(jsonPath("$.status").value(status));
    }

    @WithMockUser
    @Test
    void updateItemStatusToDoneTest() throws Exception {
        Long userId = 1L;
        Long itemId = 2L;

        doNothing().when(customShoppingListItemService)
                .updateItemStatusToDone(userId, itemId);

        mockMvc.perform(patch("/custom/shopping-list-items/{userId}/done", userId)
                        .param("itemId", itemId.toString())
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @WithMockUser
    @Test
    void bulkDeleteCustomShoppingListItemsTest() throws Exception {
        String ids = "1,2";
        Long userId = 1L;

        List<Long> expectedIds = List.of(1L, 2L);

        when(customShoppingListItemService.bulkDelete(ids))
                .thenReturn(expectedIds);

        mockMvc.perform(delete("/custom/shopping-list-items/{userId}/custom-shopping-list-items", userId)
                        .param("ids", ids)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value(1))
                .andExpect(jsonPath("$[1]").value(2));
        verify(customShoppingListItemService).bulkDelete(ids);
    }

    @WithMockUser
    @Test
    void getAllCustomShoppingItemsByStatusTest() throws Exception {
        Long userId = 1L;
        String status = "ACTIVE";

        CustomShoppingListItemResponseDto responseDto = new CustomShoppingListItemResponseDto().builder()
                .id(userId)
                .text("Test item")
                .status(ShoppingListItemStatus.ACTIVE)
                .build();

        List<CustomShoppingListItemResponseDto> responseDtoList = List.of(responseDto);

        when(customShoppingListItemService.findAllUsersCustomShoppingListItemsByStatus(userId, status))
                .thenReturn(responseDtoList);

        mockMvc.perform(get("/custom/shopping-list-items/{userId}/custom-shopping-list-items", userId)
                        .param("status", status)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(userId))
                .andExpect(jsonPath("$[0].text").value("Test item"))
                .andExpect(jsonPath("$[0].status").value(status));
    }
}
