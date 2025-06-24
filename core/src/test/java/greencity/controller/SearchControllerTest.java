package greencity.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchResponseDto;
import greencity.service.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@ExtendWith(MockitoExtension.class)
class SearchControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private SearchController searchController;

    @Mock
    private SearchService searchService;

    @Mock
    private Validator validator;

    private final Locale locale = Locale.ENGLISH;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(searchController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setValidator(validator)
                .build();
    }

    @Test
    void searchSuccess() throws Exception {
        SearchResponseDto responseDto = SearchResponseDto.builder()
                .ecoNews(List.of())
                .countOfResults(0L)
                .build();

        when(searchService.search("eco", locale.getLanguage())).thenReturn(responseDto);

        mockMvc.perform(get("/search")
                        .param("searchQuery", "eco")
                        .header(HttpHeaders.ACCEPT_LANGUAGE, locale.getLanguage())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ecoNews").isArray())
                .andExpect(jsonPath("$.countOfResults").value(0));

        verify(searchService).search("eco", locale.getLanguage());
    }

    @Test
    void searchFail() throws Exception {
        mockMvc.perform(get("/search")
                        .header(HttpHeaders.ACCEPT_LANGUAGE, locale.getLanguage()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(searchService);
    }

    @Test
    void searchEcoNewsSuccess() throws Exception {
        when(searchService.searchAllNews(PageRequest.of(0, 5), "eco", locale.getLanguage()))
                .thenReturn(new PageableDto<>(Collections.emptyList(), 0, 0, 0));

        mockMvc.perform(get("/search/econews")
                        .param("searchQuery", "eco")
                        .param("page", "0")
                        .param("size", "5")
                        .header(HttpHeaders.ACCEPT_LANGUAGE, locale.getLanguage())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").isArray())
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));

        verify(searchService).
                searchAllNews(PageRequest.of(0, 5), "eco", locale.getLanguage());
    }

    @Test
    void searchEcoNewsFail() throws Exception {
        mockMvc.perform(get("/search/econews")
                        .header(HttpHeaders.ACCEPT_LANGUAGE, locale.getLanguage()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(searchService);
    }
}
