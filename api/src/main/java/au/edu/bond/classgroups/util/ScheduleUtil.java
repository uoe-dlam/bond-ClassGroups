package au.edu.bond.classgroups.util;

import au.edu.bond.classgroups.model.Schedule;

import java.util.Calendar;
import java.util.Collection;
import java.util.TreeSet;

/**
 * Created by Shane Argo on 7/07/2014.
 */
public class ScheduleUtil {

    public static Calendar GetNextOccurrence(Collection<Schedule> schedules) {
        return GetNextOccurrence(Calendar.getInstance(), schedules);
    }

    public static Calendar GetNextOccurrence(Calendar now, Collection<Schedule> schedules) {
        TreeSet<Calendar> sortSet = new TreeSet<Calendar>();
        for (Schedule schedule : schedules) {
            sortSet.add(ScheduleToNextOccurrence(now, schedule));
        }
        return sortSet.isEmpty() ? null : sortSet.first();
    }

    public static Calendar ScheduleToNextOccurrence(Schedule schedule) {
        return ScheduleToNextOccurrence(Calendar.getInstance(), schedule);
    }

    public static Calendar ScheduleToNextOccurrence(Calendar now, Schedule schedule) {
        int nowDay = now.get(Calendar.DAY_OF_WEEK);

        Calendar schedCal = Calendar.getInstance();
        schedCal.setTime(now.getTime());
        schedCal.set(Calendar.MILLISECOND, 0);
        schedCal.set(Calendar.SECOND, 0);
        schedCal.set(Calendar.MINUTE, schedule.getMinute());
        schedCal.set(Calendar.HOUR_OF_DAY, schedule.getHour());

        if(schedule.getDay() == Schedule.DayOfTheWeek.EVERY_DAY) {

            if(schedCal.before(now)) {
                schedCal.add(Calendar.DAY_OF_YEAR, 1);
            }

        } else {

            int dayAdd = (7 + ScheduleDayToCalendarDay(schedule.getDay()) - nowDay) % 7;
            if(dayAdd == 0 && schedCal.before(now)) {
                dayAdd = 7;
            }

            if(dayAdd != 0) {
                schedCal.add(Calendar.DAY_OF_YEAR, dayAdd);
            }

        }

        return schedCal;
    }

    public static int ScheduleDayToCalendarDay(Schedule.DayOfTheWeek scheduleDay) {
        switch (scheduleDay) {
            case MONDAY:
                return Calendar.MONDAY;
            case TUESDAY:
                return Calendar.TUESDAY;
            case WEDNESDAY:
                return Calendar.WEDNESDAY;
            case THURSDAY:
                return Calendar.THURSDAY;
            case FRIDAY:
                return Calendar.FRIDAY;
            case SATURDAY:
                return Calendar.SATURDAY;
            case SUNDAY:
                return Calendar.SUNDAY;
            case EVERY_DAY:
                throw new RuntimeException("EVERY_DAY does not have a day number.");
            default:
                throw new RuntimeException("Unknown Day");
        }
    }

}
