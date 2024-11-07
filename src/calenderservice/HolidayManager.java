package calenderservice;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class HolidayManager {
    private Set<LocalDate> holidays;

    public HolidayManager(int year) {
        holidays = new HashSet<>();
        addFixedHolidays(year);  // 고정 공휴일 추가
        // addVariableHolidays(year); // 변동 공휴일이 필요하면 추가
    }

    // 고정된 공휴일을 추가하는 메서드
    private void addFixedHolidays(int year) {
        holidays.add(LocalDate.of(year, 1, 1));   // 신정
        holidays.add(LocalDate.of(year, 3, 1));   // 삼일절
        holidays.add(LocalDate.of(year, 5, 5));   // 어린이날
        holidays.add(LocalDate.of(year, 6, 6));   // 현충일
        holidays.add(LocalDate.of(year, 8, 15));  // 광복절
        holidays.add(LocalDate.of(year, 10, 3));  // 개천절
        holidays.add(LocalDate.of(year, 10, 9));  // 한글날
        holidays.add(LocalDate.of(year, 12, 25)); // 크리스마스
    }

    // 변동 공휴일을 추가하는 메서드 (필요시 구현)
    private void addVariableHolidays(int year) {
        // 예를 들어 부활절 같은 공휴일 추가
        holidays.add(calculateEaster(year));
    }

    // 부활절 날짜 계산 (예시)
    private LocalDate calculateEaster(int year) {
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int month = (h + l - 7 * m + 114) / 31;
        int day = ((h + l - 7 * m + 114) % 31) + 1;
        return LocalDate.of(year, month, day);
    }

    // 특정 날짜가 공휴일인지 확인하는 메서드
    public boolean isHoliday(LocalDate date) {
        return holidays.contains(date);
    }

    // 현재 등록된 모든 공휴일을 반환하는 메서드
    public Set<LocalDate> getHolidays() {
        return holidays;
    }
}

