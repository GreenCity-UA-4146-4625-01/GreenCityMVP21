package greencity.service;

import greencity.client.RestClient;
import greencity.dto.econews.EcoNewsForDigestDto;
import greencity.dto.subscription.SubscriptionDto;
import greencity.entity.EcoNews;
import greencity.entity.EmailSubscription;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.SubscriptionAlreadyExistsException;
import greencity.repository.EcoNewsRepo;
import greencity.repository.EmailSubscriptionRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailSubscriptionServiceImplTest {

    @Mock
    private EcoNewsRepo ecoNewsRepo;

    @Mock
    private EmailSubscriptionRepo emailSubscriptionRepo;

    @Mock
    private RestClient restClient;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EmailSubscriptionServiceImpl emailSubscriptionService;

    private static final UUID SUBSCRIPTION_ID = UUID.randomUUID();
    private static final String EMAIL = "email@email.com";

    private static final EmailSubscription SUBSCRIPTION = new EmailSubscription(SUBSCRIPTION_ID, EMAIL, null);
    private static final EmailSubscription SUBSCRIPTION_WITH_SENT_EMAIL = new EmailSubscription(SUBSCRIPTION_ID, EMAIL,
            ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault()));

    private static final EcoNews NEWS = EcoNews.builder().id(1L).build();

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(emailSubscriptionService, "timeBetweenEmails", Duration.ofDays(1));
    }

    @Test
    void createSubscription_returnsId() {
        SubscriptionDto dto = new SubscriptionDto(SUBSCRIPTION_ID);

        when(emailSubscriptionRepo.findByEmail(EMAIL)).thenReturn(null);
        when(modelMapper.map(any(), eq(SubscriptionDto.class))).thenReturn(dto);

        assertEquals(dto, emailSubscriptionService.createSubscription(EMAIL));
        verify(emailSubscriptionRepo).save(eq(new EmailSubscription(null, EMAIL, null)));
    }

    @Test
    void createSubscription_throwsWhenAlreadyExists() {
        when(emailSubscriptionRepo.findByEmail(EMAIL)).thenReturn(SUBSCRIPTION);

        assertThrows(SubscriptionAlreadyExistsException.class, () -> emailSubscriptionService.createSubscription(EMAIL));
        verify(emailSubscriptionRepo, times(0)).save(any());
    }

    @Test
    void deleteSubscription_throwsNotFoundWhenNoSubscription() {
        when(emailSubscriptionRepo.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> emailSubscriptionService.deleteSubscription(SUBSCRIPTION_ID));
        verify(emailSubscriptionRepo, times(0)).delete(any());
    }

    @Test
    void deleteSubscription_deletesWhenFound() {
        when(emailSubscriptionRepo.findById(SUBSCRIPTION_ID)).thenReturn(Optional.of(SUBSCRIPTION));

        emailSubscriptionService.deleteSubscription(SUBSCRIPTION_ID);
        verify(emailSubscriptionRepo, times(1)).delete(any());
    }

    @Test
    void getNewsForEmail_throwsNotFoundWhenNoSubscription() {
        when(emailSubscriptionRepo.findById(SUBSCRIPTION_ID)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> emailSubscriptionService.getNewsForNextEmail(SUBSCRIPTION_ID));
    }

    @Test
    void getNewsForEmail_returnsAnyNewsForFirstEmail() {
        when(emailSubscriptionRepo.findById(SUBSCRIPTION_ID)).thenReturn(Optional.of(SUBSCRIPTION));
        when(ecoNewsRepo.getThreeLastEcoNews()).thenReturn(List.of(NEWS));
        when(modelMapper.map(NEWS, EcoNewsForDigestDto.class)).thenReturn(EcoNewsForDigestDto.builder()
                        .title("Test").build());

        List<EcoNewsForDigestDto> news = emailSubscriptionService.getNewsForNextEmail(SUBSCRIPTION_ID);
        assertNotNull(news);
        assertEquals(1, news.size());
        assertEquals("Test", news.getFirst().title());

        verify(ecoNewsRepo, times(1)).getThreeLastEcoNews();
        verify(ecoNewsRepo, times(0)).getThreeEcoNewsCreatedAfter(any());
    }


    @Test
    void getNewsForEmail_returnsMostRecentForSubsequentEmail() {
        when(emailSubscriptionRepo.findById(SUBSCRIPTION_ID)).thenReturn(Optional.of(SUBSCRIPTION_WITH_SENT_EMAIL));
        when(ecoNewsRepo.getThreeEcoNewsCreatedAfter(any())).thenReturn(List.of(NEWS));
        when(modelMapper.map(NEWS, EcoNewsForDigestDto.class)).thenReturn(EcoNewsForDigestDto.builder()
                .title("Test").build());

        List<EcoNewsForDigestDto> news = emailSubscriptionService.getNewsForNextEmail(SUBSCRIPTION_ID);
        assertNotNull(news);
        assertEquals(1, news.size());
        assertEquals("Test", news.getFirst().title());

        verify(ecoNewsRepo, times(0)).getThreeLastEcoNews();
        verify(ecoNewsRepo, times(1)).getThreeEcoNewsCreatedAfter(any());
    }

    @Test
    void shouldSendEmail_throwsNotFoundWhenNoSubscription() {
        when(emailSubscriptionRepo.findById(SUBSCRIPTION_ID)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> emailSubscriptionService.shouldSendNewEmail(SUBSCRIPTION_ID));
    }

    @Test
    void shouldSendEmail_returnsTrueForNewSubscription() {
        when(emailSubscriptionRepo.findById(SUBSCRIPTION_ID)).thenReturn(Optional.of(SUBSCRIPTION));
        assertTrue(emailSubscriptionService.shouldSendNewEmail(SUBSCRIPTION_ID));
    }

    @Test
    void shouldSendEmail_returnsTrueIfSentLongAgo() {
        when(emailSubscriptionRepo.findById(SUBSCRIPTION_ID)).thenReturn(Optional.of(SUBSCRIPTION));

        SUBSCRIPTION.setLastSentEmailAt(ZonedDateTime.now().minusDays(10));
        assertTrue(emailSubscriptionService.shouldSendNewEmail(SUBSCRIPTION_ID));
        SUBSCRIPTION.setLastSentEmailAt(null);
    }

    @Test
    void shouldSendEmail_returnsFalseIfSentRecently() {
        when(emailSubscriptionRepo.findById(SUBSCRIPTION_ID)).thenReturn(Optional.of(SUBSCRIPTION));

        SUBSCRIPTION.setLastSentEmailAt(ZonedDateTime.now().minusMinutes(1));
        assertFalse(emailSubscriptionService.shouldSendNewEmail(SUBSCRIPTION_ID));
        SUBSCRIPTION.setLastSentEmailAt(null);
    }

    @Test
    void sendEmail_throwsNotFoundWhenNoSubscription() {
        when(emailSubscriptionRepo.findById(SUBSCRIPTION_ID)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> emailSubscriptionService.sendEmail(SUBSCRIPTION_ID));
    }

    @Test
    void sendEmail_doesNotSendIfShouldNot() {
        when(emailSubscriptionRepo.findById(SUBSCRIPTION_ID)).thenReturn(Optional.of(SUBSCRIPTION));

        SUBSCRIPTION.setLastSentEmailAt(ZonedDateTime.now().minusMinutes(1));
        assertFalse(emailSubscriptionService.sendEmail(SUBSCRIPTION_ID));
        SUBSCRIPTION.setLastSentEmailAt(null);
        verify(restClient, times(0)).sendSubscriptionDigest(any());
    }

    @Test
    void sendEmail_doesNotSendIfNoNews() {
        when(emailSubscriptionRepo.findById(SUBSCRIPTION_ID)).thenReturn(Optional.of(SUBSCRIPTION));
        when(ecoNewsRepo.getThreeLastEcoNews()).thenReturn(List.of());

        assertFalse(emailSubscriptionService.sendEmail(SUBSCRIPTION_ID));
        verify(ecoNewsRepo, times(1)).getThreeLastEcoNews();
    }

    @Test
    void sendEmail_sendsIfShould() {
        when(emailSubscriptionRepo.findById(SUBSCRIPTION_ID)).thenReturn(Optional.of(SUBSCRIPTION));
        when(ecoNewsRepo.getThreeLastEcoNews()).thenReturn(List.of(NEWS));

        assertTrue(emailSubscriptionService.sendEmail(SUBSCRIPTION_ID));
        verify(restClient, times(1)).sendSubscriptionDigest(any());
        verify(emailSubscriptionRepo, times(1)).updateSubscriptionLastSentEmailAt(any());
    }

    @Test
    void sendEmailIfNeeded_sendsEmail() {
        when(emailSubscriptionRepo.findById(SUBSCRIPTION_ID)).thenReturn(Optional.of(SUBSCRIPTION));
        when(emailSubscriptionRepo.findSubscriptionsWithPendingEmails(any())).thenReturn(List.of(SUBSCRIPTION));
        when(ecoNewsRepo.getThreeLastEcoNews()).thenReturn(List.of(NEWS));

        emailSubscriptionService.sendEmailsIfNeeded();
        verify(restClient, times(1)).sendSubscriptionDigest(any());
    }
}
