package greencity.controller;

import greencity.service.LanguageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class LanguageControllerTest {

    private MockMvc mockMvc;

    @Mock
    LanguageService languageService;

    @InjectMocks
    LanguageController languageController;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(languageController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void getAllLanguages_returnsLanguageList_andStatus200() throws Exception {
        List<String> mockLanguages = List.of("en", "ua", "fr");
        when(languageService.findAllLanguageCodes()).thenReturn(mockLanguages);

        mockMvc.perform(get("/language")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").value("en"))
            .andExpect(jsonPath("$[1]").value("ua"))
            .andExpect(jsonPath("$[2]").value("fr"));

        verify(languageService).findAllLanguageCodes();
    }

}
