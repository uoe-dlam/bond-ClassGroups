package au.edu.bond.classgroups.util;

import au.edu.bond.classgroups.model.Schedule;
import org.junit.Test;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;

import static org.junit.Assert.*;

public class ScheduleUtilTest {

    @Test
    public void testScheduleToNextOccurrence_dayAfterTimeAfter_expectFuture() throws Exception {
        Calendar now = Calendar.getInstance();
        now.set(2014, Calendar.JUNE, 25, 13, 30, 45);  //Wednesday

        Schedule schedule = new Schedule(Schedule.DayOfTheWeek.THURSDAY, 14, 45);
        Calendar result = ScheduleUtil.ScheduleToNextOccurrence(now, schedule);

        assertTrue(result.after(now));
        assertEquals("Day of the week is Thursday", Calendar.THURSDAY, result.get(Calendar.DAY_OF_WEEK));
        assertEquals("Year is 2014", 2014, result.get(Calendar.YEAR));
        assertEquals("Month is June", Calendar.JUNE, result.get(Calendar.MONTH));
        assertEquals("Day of the month is the 26th", 26, result.get(Calendar.DAY_OF_MONTH));
        assertEquals("Hour of the day is 14", 14, result.get(Calendar.HOUR_OF_DAY));
        assertEquals("Minute is 45", 45, result.get(Calendar.MINUTE));
        assertEquals("Seconds is 0", 0, result.get(Calendar.SECOND));
        assertEquals("Milliseconds is 0", 0, result.get(Calendar.MILLISECOND));
    }

    @Test
    public void testScheduleToNextOccurrence_dayAfterTimeBefore_expectFuture() throws Exception {
        Calendar now = Calendar.getInstance();
        now.set(2014, Calendar.JUNE, 25, 13, 30, 45);  //Wednesday

        Schedule schedule = new Schedule(Schedule.DayOfTheWeek.THURSDAY, 12, 45);
        Calendar result = ScheduleUtil.ScheduleToNextOccurrence(now, schedule);

        assertTrue(result.after(now));
        assertEquals("Day of the week is Thursday", Calendar.THURSDAY, result.get(Calendar.DAY_OF_WEEK));
        assertEquals("Year is 2014", 2014, result.get(Calendar.YEAR));
        assertEquals("Month is June", Calendar.JUNE, result.get(Calendar.MONTH));
        assertEquals("Day of the month is the 26th", 26, result.get(Calendar.DAY_OF_MONTH));
        assertEquals("Hour of the day is 12", 12, result.get(Calendar.HOUR_OF_DAY));
        assertEquals("Minute is 45", 45, result.get(Calendar.MINUTE));
        assertEquals("Seconds is 0", 0, result.get(Calendar.SECOND));
        assertEquals("Milliseconds is 0", 0, result.get(Calendar.MILLISECOND));
    }

    @Test
    public void testScheduleToNextOccurrence_dayBeforeTimeAfter_expectFuture() throws Exception {
        Calendar now = Calendar.getInstance();
        now.set(2014, Calendar.JUNE, 25, 13, 30, 45);  //Wednesday

        Schedule schedule = new Schedule(Schedule.DayOfTheWeek.MONDAY, 14, 45);
        Calendar result = ScheduleUtil.ScheduleToNextOccurrence(now, schedule);

        assertTrue(result.after(now));
        assertEquals("Day of the week is Monday", Calendar.MONDAY, result.get(Calendar.DAY_OF_WEEK));
        assertEquals("Year is 2014", 2014, result.get(Calendar.YEAR));
        assertEquals("Month is June", Calendar.JUNE, result.get(Calendar.MONTH));
        assertEquals("Day of the month is the 30th", 30, result.get(Calendar.DAY_OF_MONTH));
        assertEquals("Hour of the day is 14", 14, result.get(Calendar.HOUR_OF_DAY));
        assertEquals("Minute is 45", 45, result.get(Calendar.MINUTE));
        assertEquals("Seconds is 0", 0, result.get(Calendar.SECOND));
        assertEquals("Milliseconds is 0", 0, result.get(Calendar.MILLISECOND));
    }

    @Test
    public void testScheduleToNextOccurrence_dayBeforeTimeBefore_expectFuture() throws Exception {
        Calendar now = Calendar.getInstance();
        now.set(2014, Calendar.JUNE, 25, 13, 30, 45);  //Wednesday

        Schedule schedule = new Schedule(Schedule.DayOfTheWeek.MONDAY, 12, 45);
        Calendar result = ScheduleUtil.ScheduleToNextOccurrence(now, schedule);

        assertTrue(result.after(now));
        assertEquals("Day of the week is Monday", Calendar.MONDAY, result.get(Calendar.DAY_OF_WEEK));
        assertEquals("Year is 2014", 2014, result.get(Calendar.YEAR));
        assertEquals("Month is June", Calendar.JUNE, result.get(Calendar.MONTH));
        assertEquals("Day of the month is the 30th", 30, result.get(Calendar.DAY_OF_MONTH));
        assertEquals("Hour of the day is 12", 12, result.get(Calendar.HOUR_OF_DAY));
        assertEquals("Minute is 45", 45, result.get(Calendar.MINUTE));
        assertEquals("Seconds is 0", 0, result.get(Calendar.SECOND));
        assertEquals("Milliseconds is 0", 0, result.get(Calendar.MILLISECOND));
    }

    @Test
    public void testScheduleToNextOccurrence_daySameTimeAfter_expectFuture() throws Exception {
        Calendar now = Calendar.getInstance();
        now.set(2014, Calendar.JUNE, 25, 13, 30, 45);  //Wednesday

        Schedule schedule = new Schedule(Schedule.DayOfTheWeek.WEDNESDAY, 14, 45);
        Calendar result = ScheduleUtil.ScheduleToNextOccurrence(now, schedule);

        assertTrue(result.after(now));
        assertEquals("Day of the week is Wednesday", Calendar.WEDNESDAY, result.get(Calendar.DAY_OF_WEEK));
        assertEquals("Year is 2014", 2014, result.get(Calendar.YEAR));
        assertEquals("Month is June", Calendar.JUNE, result.get(Calendar.MONTH));
        assertEquals("Day of the month is the 25th", 25, result.get(Calendar.DAY_OF_MONTH));
        assertEquals("Hour of the day is 14", 14, result.get(Calendar.HOUR_OF_DAY));
        assertEquals("Minute is 45", 45, result.get(Calendar.MINUTE));
        assertEquals("Seconds is 0", 0, result.get(Calendar.SECOND));
        assertEquals("Milliseconds is 0", 0, result.get(Calendar.MILLISECOND));
    }

    @Test
    public void testScheduleToNextOccurrence_daySameTimeBefore_expectFuture() throws Exception {
        Calendar now = Calendar.getInstance();
        now.set(2014, Calendar.JUNE, 25, 13, 30, 45);  //Wednesday

        Schedule schedule = new Schedule(Schedule.DayOfTheWeek.WEDNESDAY, 12, 45);
        Calendar result = ScheduleUtil.ScheduleToNextOccurrence(now, schedule);

        assertTrue(result.after(now));
        assertEquals("Day of the week is Wednesday", Calendar.WEDNESDAY, result.get(Calendar.DAY_OF_WEEK));
        assertEquals("Year is 2014", 2014, result.get(Calendar.YEAR));
        assertEquals("Month is July", Calendar.JULY, result.get(Calendar.MONTH));
        assertEquals("Day of the month is the 2nd", 2, result.get(Calendar.DAY_OF_MONTH));
        assertEquals("Hour of the day is 12", 12, result.get(Calendar.HOUR_OF_DAY));
        assertEquals("Minute is 45", 45, result.get(Calendar.MINUTE));
        assertEquals("Seconds is 0", 0, result.get(Calendar.SECOND));
        assertEquals("Milliseconds is 0", 0, result.get(Calendar.MILLISECOND));
    }

    @Test
    public void testScheduleToNextOccurrence_everyDayYetToComeToday_expectToday() throws Exception {
        Calendar now = Calendar.getInstance();
        now.set(2014, Calendar.JUNE, 25, 13, 30, 45);  //Wednesday

        Schedule schedule = new Schedule(Schedule.DayOfTheWeek.EVERY_DAY, 14, 45);
        Calendar result = ScheduleUtil.ScheduleToNextOccurrence(now, schedule);

        assertTrue(result.after(now));
        assertEquals("Day of the week is Wednesday.", Calendar.WEDNESDAY, result.get(Calendar.DAY_OF_WEEK));
        assertEquals("Year is 2014", 2014, result.get(Calendar.YEAR));
        assertEquals("Month is June", Calendar.JUNE, result.get(Calendar.MONTH));
        assertEquals("Day of the month is the 25th", 25, result.get(Calendar.DAY_OF_MONTH));
        assertEquals("Hour of the day is 14", 14, result.get(Calendar.HOUR_OF_DAY));
        assertEquals("Minute is 45", 45, result.get(Calendar.MINUTE));
        assertEquals("Seconds is 0", 0, result.get(Calendar.SECOND));
        assertEquals("Milliseconds is 0", 0, result.get(Calendar.MILLISECOND));
    }

    @Test
    public void testScheduleToNextOccurrence_everyDayAlreadyComeToday_expectTomorrow() throws Exception {
        Calendar now = Calendar.getInstance();
        now.set(2014, Calendar.JUNE, 25, 13, 30, 45);  //Wednesday

        Schedule schedule = new Schedule(Schedule.DayOfTheWeek.EVERY_DAY, 12, 45);
        Calendar result = ScheduleUtil.ScheduleToNextOccurrence(now, schedule);

        assertTrue(result.after(now));
        assertEquals("Day of the week is Thursday.", Calendar.THURSDAY, result.get(Calendar.DAY_OF_WEEK));
        assertEquals("Year is 2014", 2014, result.get(Calendar.YEAR));
        assertEquals("Month is June", Calendar.JUNE, result.get(Calendar.MONTH));
        assertEquals("Day of the month is the 26th", 26, result.get(Calendar.DAY_OF_MONTH));
        assertEquals("Hour of the day is 12", 12, result.get(Calendar.HOUR_OF_DAY));
        assertEquals("Minute is 45", 45, result.get(Calendar.MINUTE));
        assertEquals("Seconds is 0", 0, result.get(Calendar.SECOND));
        assertEquals("Milliseconds is 0", 0, result.get(Calendar.MILLISECOND));
    }

    @Test
    public void testGetNextOccurrence_withOneSchedule_expectScheduleConvertedToCalendar() throws Exception {
        Calendar now = Calendar.getInstance();
        now.set(2014, Calendar.JUNE, 25, 13, 30, 45);  //Wednesday

        Collection<Schedule> schedules = new HashSet<Schedule>();
        schedules.add(new Schedule(Schedule.DayOfTheWeek.WEDNESDAY, 12, 45));
        Calendar result = ScheduleUtil.GetNextOccurrence(now, schedules);

        assertTrue(result.after(now));
        assertEquals("Day of the week is Wednesday.", Calendar.WEDNESDAY, result.get(Calendar.DAY_OF_WEEK));
        assertEquals("Year is 2014", 2014, result.get(Calendar.YEAR));
        assertEquals("Month is July", Calendar.JULY, result.get(Calendar.MONTH));
        assertEquals("Day of the month is the 2nd", 2, result.get(Calendar.DAY_OF_MONTH));
        assertEquals("Hour of the day is 12", 12, result.get(Calendar.HOUR_OF_DAY));
        assertEquals("Minute is 45", 45, result.get(Calendar.MINUTE));
        assertEquals("Seconds is 0", 0, result.get(Calendar.SECOND));
        assertEquals("Milliseconds is 0", 0, result.get(Calendar.MILLISECOND));

    }

    @Test
    public void testGetNextOccurrence_withMultipleSchedules_expectNext() throws Exception {
        Calendar now = Calendar.getInstance();
        now.set(2014, Calendar.JUNE, 25, 13, 30, 45);  //Wednesday

        Collection<Schedule> schedules = new HashSet<Schedule>();
        schedules.add(new Schedule(Schedule.DayOfTheWeek.WEDNESDAY, 12, 45));
        schedules.add(new Schedule(Schedule.DayOfTheWeek.WEDNESDAY, 14, 45));
        schedules.add(new Schedule(Schedule.DayOfTheWeek.THURSDAY, 9, 30));
        schedules.add(new Schedule(Schedule.DayOfTheWeek.MONDAY, 22, 15));
        Calendar result = ScheduleUtil.GetNextOccurrence(now, schedules);

        assertTrue(result.after(now));
        assertEquals("Day of the week is Wednesday.", Calendar.WEDNESDAY, result.get(Calendar.DAY_OF_WEEK));
        assertEquals("Year is 2014", 2014, result.get(Calendar.YEAR));
        assertEquals("Month is June", Calendar.JUNE, result.get(Calendar.MONTH));
        assertEquals("Day of the month is the 25th", 25, result.get(Calendar.DAY_OF_MONTH));
        assertEquals("Hour of the day is 14", 14, result.get(Calendar.HOUR_OF_DAY));
        assertEquals("Minute is 45", 45, result.get(Calendar.MINUTE));
        assertEquals("Seconds is 0", 0, result.get(Calendar.SECOND));
        assertEquals("Milliseconds is 0", 0, result.get(Calendar.MILLISECOND));
    }

    @Test
    public void testGetNextOccurrence_withMultipleSchedulesIncludingEveryDayThatHasPassed_expectSpecificDay() throws Exception {
        Calendar now = Calendar.getInstance();
        now.set(2014, Calendar.JUNE, 25, 13, 30, 45);  //Wednesday

        Collection<Schedule> schedules = new HashSet<Schedule>();
        schedules.add(new Schedule(Schedule.DayOfTheWeek.WEDNESDAY, 12, 45));
        schedules.add(new Schedule(Schedule.DayOfTheWeek.EVERY_DAY, 13, 15));
        schedules.add(new Schedule(Schedule.DayOfTheWeek.WEDNESDAY, 14, 45));
        schedules.add(new Schedule(Schedule.DayOfTheWeek.THURSDAY, 9, 30));
        schedules.add(new Schedule(Schedule.DayOfTheWeek.MONDAY, 22, 15));
        Calendar result = ScheduleUtil.GetNextOccurrence(now, schedules);

        assertTrue(result.after(now));
        assertEquals("Day of the week is Wednesday.", Calendar.WEDNESDAY, result.get(Calendar.DAY_OF_WEEK));
        assertEquals("Year is 2014", 2014, result.get(Calendar.YEAR));
        assertEquals("Month is June", Calendar.JUNE, result.get(Calendar.MONTH));
        assertEquals("Day of the month is the 25th", 25, result.get(Calendar.DAY_OF_MONTH));
        assertEquals("Hour of the day is 14", 14, result.get(Calendar.HOUR_OF_DAY));
        assertEquals("Minute is 45", 45, result.get(Calendar.MINUTE));
        assertEquals("Seconds is 0", 0, result.get(Calendar.SECOND));
        assertEquals("Milliseconds is 0", 0, result.get(Calendar.MILLISECOND));
    }

    @Test
    public void testGetNextOccurrence_withMultipleSchedulesIncludingEveryDayThatHasntPassed_expectEveryDay() throws Exception {
        Calendar now = Calendar.getInstance();
        now.set(2014, Calendar.JUNE, 25, 13, 30, 45);  //Wednesday

        Collection<Schedule> schedules = new HashSet<Schedule>();
        schedules.add(new Schedule(Schedule.DayOfTheWeek.WEDNESDAY, 12, 45));
        schedules.add(new Schedule(Schedule.DayOfTheWeek.EVERY_DAY, 13, 45));
        schedules.add(new Schedule(Schedule.DayOfTheWeek.WEDNESDAY, 14, 45));
        schedules.add(new Schedule(Schedule.DayOfTheWeek.THURSDAY, 9, 30));
        schedules.add(new Schedule(Schedule.DayOfTheWeek.MONDAY, 22, 15));
        Calendar result = ScheduleUtil.GetNextOccurrence(now, schedules);

        assertTrue(result.after(now));
        assertEquals("Day of the week is Wednesday.", Calendar.WEDNESDAY, result.get(Calendar.DAY_OF_WEEK));
        assertEquals("Year is 2014", 2014, result.get(Calendar.YEAR));
        assertEquals("Month is June", Calendar.JUNE, result.get(Calendar.MONTH));
        assertEquals("Day of the month is the 25th", 25, result.get(Calendar.DAY_OF_MONTH));
        assertEquals("Hour of the day is 13", 13, result.get(Calendar.HOUR_OF_DAY));
        assertEquals("Minute is 45", 45, result.get(Calendar.MINUTE));
        assertEquals("Seconds is 0", 0, result.get(Calendar.SECOND));
        assertEquals("Milliseconds is 0", 0, result.get(Calendar.MILLISECOND));
    }


}