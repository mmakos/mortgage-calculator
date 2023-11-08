package pl.mmakos.mortgage.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class PropertiesUtils {
  public static void storeStream(Stream<Properties> stream, Properties properties, String prefix) {
    int[] counter = new int[1];
    stream.map(StreamUtils::stream)
            .forEach(props -> {
                      props.mapFirst(key -> prefix + "." + counter[0] + "." + key)
                              .forEach(properties::put);
                      counter[0]++;
                    }
            );
  }

  public static Stream<Properties> loadStream(Properties properties, String prefix) {
    Pattern prefixPattern = Pattern.compile(prefix + "\\.(\\d+)\\.(\\w+)");
    return StreamUtils.stream(properties)
            .filterAnd(String.class::isInstance, String.class::isInstance)
            .map(String.class::cast, String.class::cast)
            .mapFirst(prefixPattern::matcher)
            .filterFirst(Matcher::matches)
            .mapPair((matcher, value) -> Pair.of(matcher.group(1), Pair.of(matcher.group(2), value)))
            .mapFirst(PropertiesUtils::parseInt)
            .filterFirst(OptionalInt::isPresent)
            .mapFirst(OptionalInt::getAsInt)
            .toRegularStream()
            .collect(Collectors.groupingBy(Pair::getFirst, Collectors.mapping(Pair::getSecond, StreamUtils.toProperties(Pair::getFirst, Pair::getSecond))))
            .values()
            .stream();
  }

  public static OptionalInt loadInt(Properties properties, String key) {
    String property = properties.getProperty(key);
    if (property == null) {
      return OptionalInt.empty();
    } else {
      return parseInt(property);
    }
  }

  public static OptionalDouble loadDouble(Properties properties, String key) {
    String property = properties.getProperty(key);
    if (property == null) {
      return OptionalDouble.empty();
    } else {
      return parseDouble(property);
    }
  }

  public static Optional<Date> loadDate(Properties properties, String key) {
    return java.util.Optional.ofNullable(properties.getProperty(key))
            .flatMap(PropertiesUtils::parseDate)
            .map(DateUtils::toDate);
  }

  public static Optional<Boolean> loadBoolean(Properties properties, String key) {
    return java.util.Optional.ofNullable(properties.getProperty(key))
            .map(Boolean::parseBoolean);
  }

  public static <T> Optional<T> loadEnum(Properties properties, String key, Function<String, T> enumValueOf) {
    return java.util.Optional.ofNullable(properties.getProperty(key))
            .flatMap(name -> parseEnum(name, enumValueOf));
  }

  public static void storeEnum(Properties properties, String key, Object value) {
    if (value instanceof Enum<?> en) properties.put(key, en.name());
  }

  private static OptionalInt parseInt(String string) {
    try {
      return OptionalInt.of(Integer.parseInt(string));
    } catch (NumberFormatException e) {
      return OptionalInt.empty();
    }
  }

  private static OptionalDouble parseDouble(String string) {
    try {
      return OptionalDouble.of(Double.parseDouble(string));
    } catch (NumberFormatException e) {
      return OptionalDouble.empty();
    }
  }

  private static Optional<LocalDate> parseDate(String string) {
    try {
      return Optional.of(LocalDate.parse(string));
    } catch (DateTimeParseException e) {
      return Optional.empty();
    }
  }

  private static <T> Optional<T> parseEnum(String string, Function<String, T> enumValueOf) {
    try {
      return Optional.of(enumValueOf.apply(string));
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }
}
