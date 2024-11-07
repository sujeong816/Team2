package calendarservice;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class HolidayManager {
    private Set<LocalDate> holidays;

    public HolidayManager(int year) {
        holidays = new HashSet<>(); //중복없이 공휴일을 저장하기 위해 Hashset 사용
        addFixedHolidays(year);  // 고정 공휴일 추가
    }

    // 고정된 공휴일을 추가하는 메서드
    private void addFixedHolidays(int year) {
        holidays.add(LocalDate.of(year, 1, 1));   // 신정
        holidays.add(LocalDate.of(year, 3, 1));   // 삼일절
        holidays.add(LocalDate.of(year, 5, 5));   // 어린이날
        holidays.add(LocalDate.of(year, 6, 6));   // 현충일
        holidays.add(LocalDate.of(year, 8, 15));  // 광복절
        holidays.add(LocalDate.of(year, 10, 3));  // 개천절
        holidays.add(LocalDate.of(year, 6, 6));   // 현충일
        holidays.add(LocalDate.of(year, 5, 15));  // 부처님오신날
        holidays.add(LocalDate.of(year, 10, 9));  // 한글날
        holidays.add(LocalDate.of(year, 12, 25)); // 크리스마스
    }


    //설날, 추석 추가 하는 메서드 구현 추가
    
    // 특정 날짜가 공휴일인지 확인하는 메서드
    public boolean isHoliday(LocalDate date) {
        return holidays.contains(date);
    }

    // 현재 등록된 모든 공휴일을 반환하는 메서드
    public Set<LocalDate> getHolidays() {
        return holidays;
    }
}

