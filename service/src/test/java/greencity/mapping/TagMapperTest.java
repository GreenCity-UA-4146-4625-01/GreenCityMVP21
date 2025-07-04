package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.tag.TagVO;
import greencity.entity.Language;
import greencity.entity.Tag;
import greencity.entity.localization.TagTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by Mykyta Sirobaba on 05.07.2025.
 * email mykyta.sirobaba@gmail.com
 */

@ExtendWith(MockitoExtension.class)
class TagMapperTest {

    @InjectMocks
    private TagMapper tagMapper;

    @Test
    void convert() {
        TagVO tagVO = ModelUtils.getTagVO();

        Tag expected = Tag
                .builder()
                .id(tagVO.getId())
                .type(tagVO.getType())
                .tagTranslations(
                        tagVO.getTagTranslations().stream()
                                .map(tt -> TagTranslation.builder()
                                        .id(tt.getId())
                                        .name(tt.getName())
                                        .language(Language.builder()
                                                .code(tt.getLanguageVO().getCode())
                                                .id(tt.getLanguageVO().getId())
                                                .build())
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();

        Tag actual = tagMapper.convert(tagVO);

        assertEquals(expected, actual);
    }
}