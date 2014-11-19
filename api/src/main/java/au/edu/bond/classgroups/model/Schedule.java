package au.edu.bond.classgroups.model;

/**
 * Created by Shane Argo on 7/07/2014.
 */
public class Schedule {

    public enum DayOfTheWeek {
        EVERY_DAY (null),
        MONDAY (1),
        TUESDAY (2),
        WEDNESDAY (3),
        THURSDAY (4),
        FRIDAY (5),
        SATURDAY (6),
        SUNDAY (7);

        private Integer dayNumber;
        DayOfTheWeek(Integer dayNumber) {
            this.dayNumber = dayNumber;
        }
        public Integer getDayNumber() {
            return dayNumber;
        }
    }

    private DayOfTheWeek day;
    private int hour;
    private int minute;

    public Schedule() {
    }
    public Schedule(DayOfTheWeek day, int hour, int minute) {
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    public DayOfTheWeek getDay() {
        return day;
    }

    public void setDay(DayOfTheWeek day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
