package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.tag.TagDto;
import greencity.entity.Tag;
import greencity.entity.localization.TagTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by Mykyta Sirobaba on 05.07.2025.
 * email mykyta.sirobaba@gmail.com
 */
@ExtendWith(MockitoExtension.class)
class TagDtoMapperTest {

    @InjectMocks
    private TagDtoMapper tagDtoMapper;

    @Test
    void convert() {
        TagDto tagDto = ModelUtils.getTagDto();

        TagTranslation tagTranslation = TagTranslation
                .builder()
                .id(1L)
                .name("News")
                .language(ModelUtils.getLanguage())
                .tag(Tag.builder().id(2L).build())
                .build();

        TagDto actual = tagDtoMapper.convert(tagTranslation);

        assertEquals(tagDto, actual);
        assertEquals(tagDto.getId(), actual.getId());
        assertEquals(tagDto.getName(), actual.getName());
    }
}