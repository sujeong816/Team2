package Project;

import java.awt.Color;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class HolidayManager {
    private Map<Integer, Map<LocalDate, String>> holidaysByYear;
    private static final Color HOLIDAY_TEXT_COLOR = Color.RED; // 공휴일 이름 텍스트 색상 (빨간색)

    public HolidayManager() {
        holidaysByYear = new HashMap<>();
    }

    public Map<LocalDate, String> getHolidays(int year) {
        if (!holidaysByYear.containsKey(year)) {
            Map<LocalDate, String> holidays = new HashMap<>();
            addFixedHolidays(year, holidays);
            addVariableHolidays(year, holidays);
            holidaysByYear.put(year, holidays);
        }
        return holidaysByYear.get(year);
    }

    private void addFixedHolidays(int year, Map<LocalDate, String> holidays) {
        holidays.put(LocalDate.of(year, 1, 1), "새해");
        holidays.put(LocalDate.of(year, 3, 1), "삼일절");
        holidays.put(LocalDate.of(year, 5, 5), "어린이날");
        holidays.put(LocalDate.of(year, 6, 6), "현충일");
        holidays.put(LocalDate.of(year, 8, 15), "광복절");
        holidays.put(LocalDate.of(year, 10, 3), "개천절");
        holidays.put(LocalDate.of(year, 10, 9), "한글날");
        holidays.put(LocalDate.of(year, 12, 25), "크리스마스");
    }

    private void addVariableHolidays(int year, Map<LocalDate, String> holidays) {
        // 음력 -> 양력 변환
        LocalDate seollal = convertLunarToSolar(year, 1, 1); // 설날
        LocalDate chuseok = convertLunarToSolar(year, 8, 15); // 추석
        LocalDate buddha = convertLunarToSolar(year, 4, 8); // 부처님오신날

        if (seollal != null) holidays.put(seollal, "설날");
        if (chuseok != null) holidays.put(chuseok, "추석");
        if (buddha != null) holidays.put(buddha, "부처님오신날");
    }

    private LocalDate convertLunarToSolar(int year, int lunarMonth, int lunarDay) {
        if (lunarMonth == 1 && lunarDay == 1) return LocalDate.of(year, 2, 10); // 설날 
        if (lunarMonth == 8 && lunarDay == 15) return LocalDate.of(year, 9, 17); // 추석
        if (lunarMonth == 4 && lunarDay == 8) return LocalDate.of(year, 5, 15); // 부처님오신날
        return null;
    }

    public boolean isHoliday(int year, LocalDate date) {
        return getHolidays(year).containsKey(date);
    }

    public String getHolidayName(int year, LocalDate date) {
        return getHolidays(year).get(date);
    }

    public Color getHolidayTextColor() {
        return HOLIDAY_TEXT_COLOR;
    }
}
