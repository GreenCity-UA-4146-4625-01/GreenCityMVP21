package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.tag.TagVO;
import greencity.entity.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Mykyta Sirobaba on 05.07.2025.
 * email mykyta.sirobaba@gmail.com
 */
class UtilsMapperTest {

    @Test
    void map() {
        TagVO tagVO = ModelUtils.getTagVO();

        Tag result = UtilsMapper.map(tagVO, Tag.class);

        assertNotNull(result);
        assertEquals(tagVO.getId(), result.getId());
        assertEquals(tagVO.getType(), result.getType());
    }

    @Test
    void mapAllToList() {
        List<TagVO> tagVOS = List.of(
                ModelUtils.getTagVO(),
                ModelUtils.getTagVO()
        );

        List<Tag> result = UtilsMapper.mapAllToList(tagVOS, Tag.class);

        assertNotNull(result);
        assertEquals(tagVOS.size(), result.size());
        assertEquals(tagVOS.get(1).getId(), result.get(1).getId());
        assertEquals(tagVOS.get(0).getId(), result.get(0).getId());
    }

    @Test
    void mapAllToSet() {
        List<TagVO> tagVOS = List.of(
                ModelUtils.getTagVO(),
                ModelUtils.getTagVO().setId(2L),
                ModelUtils.getTagVO().setId(3L)
        );

        Set<Tag> results = UtilsMapper.mapAllToSet(tagVOS, Tag.class);

        assertEquals(3, results.size());
        assertTrue(results.stream().anyMatch(t -> t.getId().equals(tagVOS.getFirst().getId())));
        assertTrue(results.stream().anyMatch(t -> t.getId().equals(tagVOS.get(1).getId())));
        assertTrue(results.stream().anyMatch(t -> t.getId().equals(tagVOS.get(2).getId())));
    }
}