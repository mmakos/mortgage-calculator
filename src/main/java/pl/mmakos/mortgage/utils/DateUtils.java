package pl.mmakos.mortgage.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.NONE)
public class DateUtils {
  private static final List<LocalDate> CONST_HOLIDAYS = List.of(
          LocalDate.of(1970, Month.JANUARY, 1),
          LocalDate.of(1970, Month.JANUARY, 6),
          LocalDate.of(1970, Month.MAY, 1),
          LocalDate.of(1970, Month.MAY, 3),
          LocalDate.of(1970, Month.APRIL, 15),
          LocalDate.of(1970, Month.NOVEMBER, 1),
          LocalDate.of(1970, Month.NOVEMBER, 11),
          LocalDate.of(1970, Month.DECEMBER, 25),
          LocalDate.of(1970, Month.DECEMBER, 26)
  );

  @SuppressWarnings("RedundantIfStatement")
  public static boolean isHoliday(LocalDate localDate) {
    if (localDate.getDayOfWeek() == DayOfWeek.SATURDAY || localDate.getDayOfWeek() == DayOfWeek.SUNDAY) return true;
    if (CONST_HOLIDAYS.contains(localDate.withYear(1970))) return true;
    if (getEasterSundayDate(localDate.getYear()).equals(localDate)) return true;
    if (getPentecostDate(localDate.getYear()).equals(localDate)) return true;
    if (getFeastOfCorpusChristiDate(localDate.getYear()).equals(localDate)) return true;

    return false;
  }

  @SuppressWarnings("java:S1659")
  public static LocalDate getEasterSundayDate(int year) {
    int a = year % 19,
            b = year / 100,
            c = year % 100,
            d = b / 4,
            e = b % 4,
            g = (8 * b + 13) / 25,
            h = (19 * a + b - d - g + 15) % 30,
            j = c / 4,
            k = c % 4,
            m = (a + 11 * h) / 319,
            r = (2 * e + 2 * j - k - h + m + 32) % 7,
            month = (h - m + r + 90) / 25,
            day = (h - m + r + month + 19) % 32;
    return LocalDate.of(year, month, day);
  }

  public static LocalDate getPentecostDate(int year) {
    return getEasterSundayDate(year).plusDays(49);
  }

  public static LocalDate getFeastOfCorpusChristiDate(int year) {
    return getEasterSundayDate(year).plusDays(60);
  }

  public static double getYearFactor(LocalDate start, LocalDate end) {
    double result = 0.;
    while (start.isBefore(end)) {
      if (start.getYear() == end.getYear()) {
        return result + (double) start.until(end, ChronoUnit.DAYS) / Year.of(start.getYear()).length();
      } else {
        LocalDate newStart = LocalDate.of(start.getYear() + 1, 1, 1);
        long days = start.until(newStart, ChronoUnit.DAYS);
        result += (double) days / Year.of(start.getYear()).length();
        start = newStart;
      }
    }
    return result;
  }

  public static LocalDate toLocalDate(Date date) {
    return LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
  }

  public static Date toDate(LocalDate localDate) {
    return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
  }
}
