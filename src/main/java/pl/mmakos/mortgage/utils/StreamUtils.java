package pl.mmakos.mortgage.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Properties;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class StreamUtils {
  public static <T, K, U> Collector<T, ?, Properties> toProperties(Function<? super T, ? extends K> keyMapper,
          Function<? super T, ? extends U> valueMapper) {
    return Collector.of(Properties::new,
            (props, element) -> props.put(keyMapper.apply(element), valueMapper.apply(element)),
            (p1, p2) -> {
              p1.putAll(p2);
              return p1;
            });
  }

  public static <K, V> PairStream<K, V> stream(Map<K, V> map) {
    Stream<Pair<K, V>> mapStream = map.entrySet()
            .stream()
            .map(Pair::new);

    return new PairStream<>(mapStream);
  }

  @SuppressWarnings({"java:S3958", "unused"})
  public static final class PairStream<T1, T2> {
    private Stream<Pair<T1, T2>> innerStream;

    private PairStream(Stream<Pair<T1, T2>> innerStream) {
      this.innerStream = innerStream;
    }

    public PairStream<T1, T2> filter(BiPredicate<T1, T2> predicate) {
      innerStream = innerStream.filter(pair -> predicate.test(pair.getFirst(), pair.getSecond()));
      return this;
    }

    public PairStream<T1, T2> filterAnd(Predicate<T1> firstPredicate, Predicate<T2> secondPredicate) {
      innerStream = innerStream.filter(pair -> firstPredicate.test(pair.getFirst()) && secondPredicate.test(pair.getSecond()));
      return this;
    }

    public PairStream<T1, T2> filterOr(Predicate<T1> firstPredicate, Predicate<T2> secondPredicate) {
      innerStream = innerStream.filter(pair -> firstPredicate.test(pair.getFirst()) || secondPredicate.test(pair.getSecond()));
      return this;
    }

    public PairStream<T1, T2> filterFirst(Predicate<T1> predicate) {
      innerStream = innerStream.filter(pair -> predicate.test(pair.getFirst()));
      return this;
    }

    public PairStream<T1, T2> filterSecond(Predicate<T2> predicate) {
      innerStream = innerStream.filter(pair -> predicate.test(pair.getSecond()));
      return this;
    }

    public <T> Stream<T> map(BiFunction<T1, T2, T> mapper) {
      return innerStream.map(pair -> mapper.apply(pair.getFirst(), pair.getSecond()));
    }

    public <T3, T4> PairStream<T3, T4> mapPair(BiFunction<T1, T2, Pair<T3, T4>> mapper) {
      return new PairStream<>(innerStream.map(pair -> mapper.apply(pair.getFirst(), pair.getSecond())));
    }

    public <V1, V2> PairStream<V1, V2> map(Function<T1, V1> firstMapper, Function<T2, V2> secondMapper) {
      return new PairStream<>(innerStream.map(pair -> pair.map(firstMapper, secondMapper)));
    }

    public <V> PairStream<V, T2> mapFirst(Function<T1, V> mapper) {
      return new PairStream<>(innerStream.map(pair -> pair.mapFirst(mapper)));
    }

    public <V> PairStream<T1, V> mapSecond(Function<T2, V> mapper) {
      return new PairStream<>(innerStream.map(pair -> pair.mapSecond(mapper)));
    }

    public void forEach(BiConsumer<T1, T2> action) {
      innerStream.forEach(pair -> action.accept(pair.getFirst(), pair.getSecond()));
    }

    public Map<T1, T2> toMap() {
      return innerStream.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    public Stream<Pair<T1, T2>> toRegularStream() {
      return innerStream;
    }
  }
}
