package calendarservice;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class HolidayManager {
    // 내부적으로 연도별 공휴일 저장
    private Map<Integer, Map<LocalDate, String>> holidaysByYear;

    public HolidayManager() {
        holidaysByYear = new HashMap<>();
    }

    // 특정 연도의 공휴일 반환
    public Map<LocalDate, String> getHolidays(int year) {
        if (!holidaysByYear.containsKey(year)) {
            Map<LocalDate, String> holidays = new HashMap<>();
            addFixedHolidays(year, holidays);
            addVariableHolidays(year, holidays);
            holidaysByYear.put(year, holidays);
        }
        return holidaysByYear.get(year);
    }

    // 고정 공휴일 추가
    private void addFixedHolidays(int year, Map<LocalDate, String> holidays) {
        holidays.put(LocalDate.of(year, 1, 1), "신정");
        holidays.put(LocalDate.of(year, 3, 1), "삼일절");
        holidays.put(LocalDate.of(year, 5, 5), "어린이날");
        holidays.put(LocalDate.of(year, 6, 6), "현충일");
        holidays.put(LocalDate.of(year, 8, 15), "광복절");
        holidays.put(LocalDate.of(year, 10, 3), "개천절");
        holidays.put(LocalDate.of(year, 10, 9), "한글날");
        holidays.put(LocalDate.of(year, 12, 25), "크리스마스");
    }

    // 변동 공휴일 추가 (예: 설날, 추석)
    private void addVariableHolidays(int year, Map<LocalDate, String> holidays) {
        // 예제 데이터: 변동 공휴일 (음력을 양력으로 변환 필요)
        holidays.put(LocalDate.of(year, 2, 10), "설날");
        holidays.put(LocalDate.of(year, 9, 19), "추석");
    }

    // 특정 날짜가 공휴일인지 확인
    public boolean isHoliday(int year, LocalDate date) {
        return getHolidays(year).containsKey(date);
    }

    // 특정 날짜의 공휴일 이름 가져오기
    public String getHolidayName(int year, LocalDate date) {
        return getHolidays(year).get(date);
    }
}
