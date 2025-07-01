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
import static org.hamcrest.Matchers.hasSize;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
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

    private static final Long USER_ID = 1L;

    @Test
    void getAllAvailableCustomShoppingListItems_Returns200AndListTest() throws Exception {

        CustomShoppingListItemResponseDto item1 = CustomShoppingListItemResponseDto.builder()
                .id(1L)
                .text("Item1")
                .build();

        CustomShoppingListItemResponseDto item2 = CustomShoppingListItemResponseDto.builder()
                .id(2L)
                .text("Item2")
                .build();

        List<CustomShoppingListItemResponseDto> mockedKList = List.of(item1, item2);

        when(customShoppingListItemService.findAllAvailableCustomShoppingListItems(1L, 2L))
                .thenReturn(mockedKList);

        mockMvc.perform(get("/custom/shopping-list-items/1/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].text").value("Item1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].text").value("Item2"));
    }

    @Test
    void saveUserCustomShoppingListItems_Returns201AndListTest() throws Exception {

        CustomShoppingListItemSaveRequestDto saveRequestDto = new CustomShoppingListItemSaveRequestDto();
        saveRequestDto.setText("Item1");

        BulkSaveCustomShoppingListItemDto bulkSaveRequestDto = new BulkSaveCustomShoppingListItemDto();
        bulkSaveRequestDto.setCustomShoppingListItemSaveRequestDtoList(List.of(saveRequestDto));

        CustomShoppingListItemResponseDto responseDto = CustomShoppingListItemResponseDto.builder()
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

    @Test
    void updateItemStatusTest() throws Exception {
        Long itemId = 2L;
        String status = "ACTIVE";

        CustomShoppingListItemResponseDto expectedResponse = CustomShoppingListItemResponseDto.builder()
                .id(itemId)
                .text("Test item")
                .status(ShoppingListItemStatus.valueOf(status))
                .build();

        when(customShoppingListItemService.updateItemStatus(USER_ID, itemId, status))
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

    @Test
    void updateItemStatusToDoneTest() throws Exception {
        Long itemId = 2L;

        doNothing().when(customShoppingListItemService)
                .updateItemStatusToDone(USER_ID, itemId);

        mockMvc.perform(patch("/custom/shopping-list-items/{userId}/done", USER_ID)
                        .param("itemId", itemId.toString())
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void bulkDeleteCustomShoppingListItemsTest() throws Exception {
        String ids = "1,2";

        List<Long> expectedIds = List.of(1L, 2L);

        when(customShoppingListItemService.bulkDelete(ids))
                .thenReturn(expectedIds);

        mockMvc.perform(delete("/custom/shopping-list-items/{userId}/custom-shopping-list-items", USER_ID)
                        .param("ids", ids)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value(1))
                .andExpect(jsonPath("$[1]").value(2));
        verify(customShoppingListItemService).bulkDelete(ids);
    }

    @Test
    void getAllCustomShoppingItemsByStatusTest() throws Exception {
        String status = "ACTIVE";

        CustomShoppingListItemResponseDto responseDto = CustomShoppingListItemResponseDto.builder()
                .id(USER_ID)
                .text("Test item")
                .status(ShoppingListItemStatus.ACTIVE)
                .build();

        List<CustomShoppingListItemResponseDto> responseDtoList = List.of(responseDto);

        when(customShoppingListItemService.findAllUsersCustomShoppingListItemsByStatus(USER_ID, status))
                .thenReturn(responseDtoList);

        mockMvc.perform(get("/custom/shopping-list-items/{userId}/custom-shopping-list-items", USER_ID)
                        .param("status", status)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(USER_ID))
                .andExpect(jsonPath("$[0].text").value("Test item"))
                .andExpect(jsonPath("$[0].status").value(status));
    }
}