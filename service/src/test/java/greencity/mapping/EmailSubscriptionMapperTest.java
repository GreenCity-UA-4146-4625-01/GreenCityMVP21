package greencity.mapping;

import greencity.entity.EmailSubscription;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class EmailSubscriptionMapperTest {

    @InjectMocks
    private EmailSubscriptionMapper mapper;

    @Test
    void mapper_handlesNullCorrectly() {
        assertNull(mapper.convert((EmailSubscription) null));
    }

    @Test
    void mapper_mapsIdField() {
        UUID id = UUID.randomUUID();

        EmailSubscription emailSubscription = new EmailSubscription();
        emailSubscription.setId(id);

        assertEquals(id, mapper.convert(emailSubscription).id());
    }
}
