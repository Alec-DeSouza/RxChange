package adapter.collections;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import filter.ChangeTypeFilter;
import message.ChangeMessage;
import message.MetaChangeMessage;
import observer.ChangeMessageObserver;
import org.junit.Before;
import org.junit.Test;
import type.ChangeType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static org.junit.Assert.assertEquals;

public class MapChangeAdapterTest {
    private MapChangeAdapter<Integer, String> changeAdapter;

    @Before
    public void setUp() {
        changeAdapter = new MapChangeAdapter<>();
    }

    @Test
    public void add() {
        final Queue<Map.Entry<Integer, String>> testQueue = new LinkedList<>();

        for (int i = 0; i < 3; i++) {
            testQueue.add(Maps.immutableEntry(i, String.valueOf(i)));
        }

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.ADD))
                .subscribe(new ChangeMessageObserver<Map<Integer, String>>() {
                    @Override
                    public void onNext(ChangeMessage<Map<Integer, String>> changeMessage) {
                        System.out.println(changeMessage.toString());

                        final MetaChangeMessage<Map<Integer, String>, Map.Entry<Integer, String>> metaChangeMessage =
                                (MetaChangeMessage<Map<Integer, String>, Map.Entry<Integer, String>>) changeMessage;

                        final MapDifference<Integer, String> dataDiff = Maps.difference(changeMessage.getOldData(),
                                changeMessage.getNewData());

                        assertEquals("Difference count (left)", 0, dataDiff.entriesOnlyOnLeft().size());
                        assertEquals("Difference count (right)", 1, dataDiff.entriesOnlyOnRight().size());

                        assertEquals("Difference (key)", true,
                                dataDiff.entriesOnlyOnRight().containsKey(testQueue.peek().getKey()));
                        assertEquals("Difference (value)", testQueue.peek().getValue(),
                                dataDiff.entriesOnlyOnRight().get(testQueue.peek().getKey()));

                        assertEquals("Metadata key", testQueue.peek().getKey(),
                                metaChangeMessage.getMetadata().getKey());
                        assertEquals("Metadata value", testQueue.poll().getValue(),
                                metaChangeMessage.getMetadata().getValue());
                    }
                });

        for (int i = 0; i < 3; i++) {
            assertEquals("Data", null, changeAdapter.add(i, String.valueOf(i)));
        }

        // Verify queue was emptied
        assertEquals("Test queue", 0, testQueue.size());
    }

    @Test
    public void remove() {
        final Queue<Map.Entry<Integer, String>> testQueue = new LinkedList<>();

        for (int i = 0; i < 3; i++) {
            testQueue.add(Maps.immutableEntry(i, String.valueOf(i)));
            changeAdapter.add(i, String.valueOf(i));
        }

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(new ChangeMessageObserver<Map<Integer, String>>() {
                    @Override
                    public void onNext(ChangeMessage<Map<Integer, String>> changeMessage) {
                        System.out.println(changeMessage.toString());

                        final MetaChangeMessage<Map<Integer, String>, Map.Entry<Integer, String>> metaChangeMessage =
                                (MetaChangeMessage<Map<Integer, String>, Map.Entry<Integer, String>>) changeMessage;

                        final MapDifference<Integer, String> dataDiff = Maps.difference(changeMessage.getOldData(),
                                changeMessage.getNewData());

                        assertEquals("Difference count (left)", 1, dataDiff.entriesOnlyOnLeft().size());
                        assertEquals("Difference count (right)", 0, dataDiff.entriesOnlyOnRight().size());

                        assertEquals("Difference (key)", true,
                                dataDiff.entriesOnlyOnLeft().containsKey(testQueue.peek().getKey()));
                        assertEquals("Difference (value)", testQueue.peek().getValue(),
                                dataDiff.entriesOnlyOnLeft().get(testQueue.peek().getKey()));

                        assertEquals("Metadata key", testQueue.peek().getKey(),
                                metaChangeMessage.getMetadata().getKey());
                        assertEquals("Metadata value", testQueue.poll().getValue(),
                                metaChangeMessage.getMetadata().getValue());
                    }
                });

        for (int i = 0; i < 3; i++) {
            assertEquals("Data", String.valueOf(i), changeAdapter.remove(i));
        }

        // Verify queue was emptied
        assertEquals("Test queue", 0, testQueue.size());
    }

    @Test
    public void update() {
        final Queue<Map.Entry<Integer, String>> testQueue = new LinkedList<>();

        for (int i = 0; i < 3; i++) {
            testQueue.add(Maps.immutableEntry(i, String.valueOf(i + 1)));
            changeAdapter.add(i, String.valueOf(i));
        }

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.UPDATE))
                .subscribe(new ChangeMessageObserver<Map<Integer, String>>() {
                    @Override
                    public void onNext(ChangeMessage<Map<Integer, String>> changeMessage) {
                        System.out.println(changeMessage.toString());

                        final MetaChangeMessage<Map<Integer, String>, Map.Entry<Integer, String>> metaChangeMessage =
                                (MetaChangeMessage<Map<Integer, String>, Map.Entry<Integer, String>>) changeMessage;

                        final MapDifference<Integer, String> dataDiff = Maps.difference(changeMessage.getOldData(),
                                changeMessage.getNewData());

                        assertEquals("Different keys", 0, dataDiff.entriesOnlyOnLeft().size()
                                + dataDiff.entriesOnlyOnRight().size());
                        assertEquals("Different values", 1, dataDiff.entriesDiffering().size());

                        assertEquals("Metadata key", testQueue.peek().getKey(),
                                metaChangeMessage.getMetadata().getKey());
                        assertEquals("Metadata value", testQueue.poll().getValue(),
                                metaChangeMessage.getMetadata().getValue());
                    }
                });

        for (int i = 0; i < 3; i++) {
            assertEquals("Data", String.valueOf(i), changeAdapter.update(i, String.valueOf(i + 1)));
        }

        // Verify queue was emptied
        assertEquals("Test queue", 0, testQueue.size());
    }

    @Test
    public void get() {
        final Map<Integer, String> testMap = new HashMap<>();

        for (int i = 0; i < 3; i++) {
            testMap.put(i, String.valueOf(i));
            changeAdapter.add(i, String.valueOf(i));
        }

        for (int i = 0; i < 3; i++) {
            assertEquals("Data", testMap.get(i), changeAdapter.get(i));
        }
    }

    @Test
    public void getAll() {
        final Map<Integer, String> testMap = new HashMap<>();

        for (int i = 0; i < 3; i++) {
            testMap.put(i, String.valueOf(i));
            changeAdapter.add(i, String.valueOf(i));
        }

        final MapDifference<Integer, String> dataDiff = Maps.difference(testMap, changeAdapter.getAll());

        assertEquals("Data (common)", 3, dataDiff.entriesInCommon().size());
        assertEquals("Data (different)", 0, dataDiff.entriesDiffering().size()
                + dataDiff.entriesOnlyOnLeft().size()
                + dataDiff.entriesOnlyOnRight().size());
    }
}