package api;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.time.temporal.TemporalAdjusters.nextOrSame;

/**
 * @author fly
 * Created on 2018/5/21.
 */
public class TimeAPITest {

	@Test
	public void localDate() {
		LocalDate date = LocalDate.of(2014, 3, 18);
		int year = date.getYear();
		Month month = date.getMonth();
		int day = date.getDayOfMonth();
		DayOfWeek dow = date.getDayOfWeek();
		int len = date.lengthOfMonth();
		boolean leap = date.isLeapYear();

		date.get(ChronoField.YEAR);
		date.get(ChronoField.MONTH_OF_YEAR);
		date.get(ChronoField.DAY_OF_MONTH);

		LocalDate today = LocalDate.now();

		System.out.println(LocalDate.parse("2014-03-18"));

		LocalDate date1 = LocalDate.of(2014, 3, 18);
		LocalDate date2 = date1.withYear(2011);
		LocalDate date3 = date2.withDayOfMonth(25);
		LocalDate date4 = date3.with(ChronoField.MONTH_OF_YEAR, 9);
	}

	public void localTime() {
		LocalTime time = LocalTime.of(13, 45, 20);
		int hour = time.getHour();
		int minute = time.getMinute();
		int second = time.getSecond();

		LocalTime.parse("13:45:20");
	}

	public void localDateTime() {
		LocalDateTime dt1 = LocalDateTime.of(2014, Month.MARCH, 18, 13, 45, 20);
		LocalDate date = LocalDate.now();
		LocalTime time = LocalTime.now();
		LocalDateTime dt2 = LocalDateTime.of(date, time);
		LocalDateTime dt3 = date.atTime(13, 45, 20);
		LocalDateTime dt4 = date.atTime(time);
		LocalDateTime dt5 = time.atDate(date);
		LocalDate date1 = dt1.toLocalDate();
		LocalTime time1 = dt1.toLocalTime();
	}

	public void instant() {
		Instant.ofEpochSecond(3);
		Instant.ofEpochSecond(3, 0);
		Instant.ofEpochSecond(2, 1_000_000_000);
		Instant.ofEpochSecond(4, -1_000_000_000);
	}

	public void duration() {
		Duration d1 = Duration.between(LocalTime.now(), LocalTime.now().minusHours(1));
		Duration d2 = Duration.between(LocalDateTime.now(), LocalDateTime.now().minusHours(1));
		Duration d3 = Duration.between(Instant.now(), Instant.now().minusMillis(1));

		Duration threeMinutes = Duration.ofMinutes(3);
		Duration threeMinutes1 = Duration.of(3, ChronoUnit.MINUTES);
	}

	public void period() {
		Period tenDays = Period.between(LocalDate.of(2014, 3, 8),
				LocalDate.of(2014, 3, 18));

		Period tenDays1 = Period.ofDays(10);
		Period threeWeeks = Period.ofWeeks(3);
		Period twoYearsSixMonthsOneDay = Period.of(2, 6, 1);
	}

	public void temporalAdjuster() {
		LocalDate date1 = LocalDate.of(2014, 3, 18);
		LocalDate date2 = date1.with(nextOrSame(DayOfWeek.SUNDAY));
		LocalDate date3 = date2.with(lastDayOfMonth());

		LocalDate now = LocalDate.now();
		now.with(temporal -> {
			DayOfWeek dow = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
			int dayToAdd = 1;
			if (dow == DayOfWeek.FRIDAY) {
				dayToAdd = 3;
			} else if (dow == DayOfWeek.SATURDAY) {
				dayToAdd = 2;
			}
			return temporal.plus(dayToAdd, ChronoUnit.DAYS);
		});

		TemporalAdjuster nextWorkingDay = TemporalAdjusters.ofDateAdjuster(
				temporal -> {
					DayOfWeek dow = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
					int dayToAdd = 1;
					if (dow == DayOfWeek.FRIDAY) {
						dayToAdd = 3;
					}
					if (dow == DayOfWeek.SATURDAY) {
						dayToAdd = 2;
					}
					return temporal.plus(dayToAdd, ChronoUnit.DAYS);
				});

		now.with(nextWorkingDay);
	}

	public void dateTimeFormatter() {
		LocalDate date = LocalDate.of(2014, 3, 18);
		String s1 = date.format(DateTimeFormatter.BASIC_ISO_DATE);
		String s2 = date.format(DateTimeFormatter.ISO_LOCAL_DATE);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		LocalDate date1 = LocalDate.of(2014, 3, 18);
		String formattedDate = date1.format(formatter);
		LocalDate date2 = LocalDate.parse(formattedDate, formatter);

		DateTimeFormatter italianFormatter = new DateTimeFormatterBuilder()
				.appendText(ChronoField.DAY_OF_MONTH)
				.appendLiteral(". ")
				.appendText(ChronoField.MONTH_OF_YEAR)
				.appendLiteral(" ")
				.appendText(ChronoField.YEAR)
				.parseCaseInsensitive()
				.toFormatter(Locale.ITALIAN);
	}

	public void zoneId() {
		ZoneId romeZone = ZoneId.of("Europe/Rome");
		ZoneId zoneId = TimeZone.getDefault().toZoneId();

		LocalDate date = LocalDate.of(2014, Month.MARCH, 18);
		ZonedDateTime zdt1 = date.atStartOfDay(romeZone);

		LocalDateTime dateTime = LocalDateTime.of(2014, Month.MARCH, 18, 13, 45);
		ZonedDateTime zdt2 = dateTime.atZone(romeZone);

		Instant instant = Instant.now();
		ZonedDateTime zdt3 = instant.atZone(romeZone);

		LocalDateTime dateTime1 = LocalDateTime.of(2014, Month.MARCH, 18, 13, 45);
		Instant instantFromDateTime = dateTime1.toInstant(romeZone.getRules().getOffset(dateTime1));

		LocalDateTime timeFromInstant = LocalDateTime.ofInstant(Instant.now(), romeZone);
	}

}
