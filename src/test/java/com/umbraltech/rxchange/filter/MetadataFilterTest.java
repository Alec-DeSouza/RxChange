package com.umbraltech.rxchange.filter;

import com.umbraltech.rxchange.message.ChangeMessage;
import com.umbraltech.rxchange.message.MetaChangeMessage;
import com.umbraltech.rxchange.type.ChangeType;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class MetadataFilterTest {

    @Test
    public void test() {
        final MetaChangeMessage<Integer, Integer> changeMessage =
                new MetaChangeMessage<>(0, 1, ChangeType.UPDATE, 1);

        final MetadataFilter integerMetadataFilter = new MetadataFilter(Integer.class);
        final MetadataFilter listMetadataFilter = new MetadataFilter(List.class);

        assertEquals("Metadata type integer", true, integerMetadataFilter.test(changeMessage));
        assertEquals("Metadata type list", false, listMetadataFilter.test(changeMessage));
    }

    @Test
    public void testNoMetadata() {
        final MetaChangeMessage<Integer, Integer> changeMessage =
                new MetaChangeMessage<>(0, 1, ChangeType.UPDATE, null);

        final MetadataFilter integerMetadataFilter = new MetadataFilter(Integer.class);
        final MetadataFilter listMetadataFilter = new MetadataFilter(List.class);

        assertEquals("Metadata null", false, integerMetadataFilter.test(changeMessage));
        assertEquals("Metadata type integer", false, integerMetadataFilter.test(changeMessage));
        assertEquals("Metadata type list", false, listMetadataFilter.test(changeMessage));
    }

    @Test
    public void testNotMetadataInstance() {
        final ChangeMessage<Integer> changeMessage = new ChangeMessage<>(0, 1, ChangeType.UPDATE);

        final MetadataFilter integerMetadataFilter = new MetadataFilter(Integer.class);
        final MetadataFilter listMetadataFilter = new MetadataFilter(List.class);

        assertEquals("Metadata instance", false, integerMetadataFilter.test(changeMessage));
        assertEquals("Metadata type integer", false, integerMetadataFilter.test(changeMessage));
        assertEquals("Metadata type list", false, listMetadataFilter.test(changeMessage));
    }
}