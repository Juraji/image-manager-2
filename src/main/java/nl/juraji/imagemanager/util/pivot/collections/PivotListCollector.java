package nl.juraji.imagemanager.util.pivot.collections;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Created by Juraji on 16-5-2019.
 * image-manager
 */
public final class PivotListCollector<T> implements Collector<T, List<T>, List<T>> {
    @Override
    public Supplier<List<T>> supplier() {
        return ArrayList::new;
    }

    @Override
    public BiConsumer<List<T>, T> accumulator() {
        return List::add;
    }

    @Override
    public BinaryOperator<List<T>> combiner() {
        return (l, r) -> {
            r.forEach(l::add);
            return l;
        };
    }

    @Override
    public Function<List<T>, List<T>> finisher() {
        return l -> l;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return new HashSet<>(Arrays.asList(
                Characteristics.CONCURRENT,
                Characteristics.IDENTITY_FINISH
        ));
    }

    public static <T> java.util.List<T> pivotListToJavaList(ArrayList<T> pivotList) {
        final java.util.ArrayList<T> ts = new java.util.ArrayList<>();
        pivotList.forEach(ts::add);
        return ts;
    }
}
