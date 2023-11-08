package pl.mmakos.mortgage.utils;

import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("LombokGetterMayBeUsed")
public class Pair<T1, T2> {
  private final T1 first;
  private final T2 second;

  public Pair(T1 first, T2 second) {
    this.first = first;
    this.second = second;
  }

  public Pair(Map.Entry<T1, T2> entry) {
    this(entry.getKey(), entry.getValue());
  }

  public static <T1, T2> Pair<T1, T2> of(T1 first, T2 second) {
    return new Pair<>(first, second);
  }

  public T1 getFirst() {
    return first;
  }

  public T2 getSecond() {
    return second;
  }

  public <V1, V2> Pair<V1, V2> map(Function<T1, V1> firstMapper, Function<T2, V2> secondMapper) {
    return new Pair<>(firstMapper.apply(first), secondMapper.apply(second));
  }

  public <V> Pair<V, T2> mapFirst(Function<T1, V> mapper) {
    return new Pair<>(mapper.apply(first), second);
  }

  public <V> Pair<T1, V> mapSecond(Function<T2, V> mapper) {
    return new Pair<>(first, mapper.apply(second));
  }
}
