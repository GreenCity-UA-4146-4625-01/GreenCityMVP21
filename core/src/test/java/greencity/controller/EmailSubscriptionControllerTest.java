package greencity.controller;

import greencity.dto.subscription.SubscriptionDto;
import greencity.service.EmailSubscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmailSubscriptionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmailSubscriptionService emailSubscriptionService;

    @InjectMocks
    private EmailSubscriptionController emailSubscriptionController;

    private static final UUID SUBSCRIPTION_ID = UUID.randomUUID();
    private static final String BASE_URL = "/subscriptions";
    private static final String EMAIL = "email@gmail.com";

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(emailSubscriptionController).build();
    }

    @Test
    void subscribe_returnsSubscriptionId() throws Exception {
        when(emailSubscriptionService.createSubscription(eq(EMAIL)))
                .thenReturn(new SubscriptionDto(SUBSCRIPTION_ID));

        mockMvc.perform(post(BASE_URL)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("email", EMAIL))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(SUBSCRIPTION_ID.toString()));

        verify(emailSubscriptionService).createSubscription(eq(EMAIL));
    }

    @Test
    void unsubscribe_returnsNoContent() throws Exception {
        doNothing().when(emailSubscriptionService).deleteSubscription(eq(SUBSCRIPTION_ID));

        mockMvc.perform(delete(BASE_URL + "/" + SUBSCRIPTION_ID))
                .andExpect(status().isNoContent());

        verify(emailSubscriptionService).deleteSubscription(eq(SUBSCRIPTION_ID));
    }
}
