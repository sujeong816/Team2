package project;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

import javax.swing.*;

public class CalendarService extends JFrame {
    private JTabbedPane tabbedPane;
    private HolidayManager holidayManager; // HolidayManager 인스턴스 추가
    private JComboBox<String> categoryComboBox; // 카테고리 콤보박스 추가
    private CalendarPanel calendarPanel; // 캘린더 패널 멤버 변수로 선언
    
    public CalendarService() {
    	setTitle("캘린더 화면");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container c = getContentPane();
        c.setLayout(new BorderLayout(5, 5));
        c.setBackground(Color.white);

        // FileManager 데이터 로드
        FileManager.LoadSaveData();

        holidayManager = new HolidayManager(); // 공휴일 관리자를 초기화

        // 중앙에 캘린더 패널 추가
        calendarPanel = new CalendarPanel(); // 캘린더 패널 생성 및 초기화
        c.add(calendarPanel, BorderLayout.CENTER);

        // 서쪽 패널 생성 및 구성
        JPanel westPanel = new JPanel(new BorderLayout(5, 5));
        westPanel.setPreferredSize(new Dimension(200, 0)); // 창 크기의 약 30%로 설정 (창 너비가 1000이므로 약 300)

        // 카테고리 패널
        JPanel categoryPanel = new JPanel();
        categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.Y_AXIS)); // 수직
        categoryPanel.setBorder(BorderFactory.createTitledBorder("카테고리"));
        categoryComboBox = new JComboBox<>(); // 카테고리 콤보박스 생성
        updateCategoryComboBox(); // 저장된 카테고리 추가
        categoryPanel.add(categoryComboBox);

        // 일정 탭 패널 생성
        tabbedPane = new JTabbedPane();

        // 첫 번째 탭: 이번 달 일정
        JPanel monthSchedulePanel = new JPanel();
        monthSchedulePanel.setLayout(new BoxLayout(monthSchedulePanel, BoxLayout.Y_AXIS));
        monthSchedulePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 패딩 추가
        updateMonthSchedulePanel(monthSchedulePanel);
        tabbedPane.addTab("이번달 일정", monthSchedulePanel);

        // 새로고침 버튼
        JButton refreshButton = new JButton("새로고침");
        refreshButton.addActionListener(e -> {
            updateCategoryComboBox();
            calendarPanel.updateDatesWithCategory((String) categoryComboBox.getSelectedItem());
            updateMonthSchedulePanel(monthSchedulePanel);
        });

        JPanel westPanelContent = new JPanel(new BorderLayout());
        westPanelContent.add(tabbedPane, BorderLayout.CENTER);
        westPanelContent.add(refreshButton, BorderLayout.SOUTH);

        westPanel.add(categoryPanel, BorderLayout.NORTH);
        westPanel.add(westPanelContent, BorderLayout.CENTER);

        c.add(westPanel, BorderLayout.WEST);

        setSize(1000, 600); // 창 크기 고정
        setVisible(true);
    }
    
    public void updateScheduleTab() {
        if (tabbedPane.getComponentAt(0) instanceof JPanel) {
            JPanel monthPanel = (JPanel) tabbedPane.getComponentAt(0);
            updateMonthSchedulePanel(monthPanel); // "이번달 일정" 탭 갱신
            monthPanel.revalidate(); // 레이아웃 갱신
            monthPanel.repaint();    // 화면 갱신
        }
    }

    // 이번 달 일정 패널 업데이트 메서드
    private void updateMonthSchedulePanel(JPanel panel) {
        panel.removeAll(); // 기존 패널 초기화

        // 현재 월의 일정 필터링
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        Map<Integer, List<String>> dailySchedules = new TreeMap<>();
        for (FileManager.Schedule schedule : FileManager.schedules) {
            if (schedule.getStartDate().getYear() == currentYear &&
                schedule.getStartDate().getMonthValue() == currentMonth) {
                LocalDate startDate = schedule.getStartDate().toLocalDate();
                LocalDate endDate = schedule.getEndDate().toLocalDate();

                while (!startDate.isAfter(endDate)) {
                    int day = startDate.getDayOfMonth();
                    dailySchedules.computeIfAbsent(day, k -> new ArrayList<>()).add(schedule.getTitle());
                    startDate = startDate.plusDays(1);
                }
        }
        }

        // 일정 데이터를 정렬하여 출력
        for (Map.Entry<Integer, List<String>> entry : dailySchedules.entrySet()) {
            int day = entry.getKey();
            List<String> titles = entry.getValue();

            StringBuilder schedulesForDay = new StringBuilder();
            schedulesForDay.append(day).append("일: ");

            for (int i = 0; i < titles.size(); i++) {
                schedulesForDay.append(titles.get(i));
                if (i < titles.size() - 1) {
                    schedulesForDay.append(", "); // 여러 일정 간 구분
                }
            }

            // JLabel로 패널에 추가
            JLabel dayLabel = new JLabel(schedulesForDay.toString());
            panel.add(dayLabel);
        }

        // 레이아웃 갱신
        panel.revalidate();
        panel.repaint();
    }

    // 카테고리 콤보박스 업데이트 메서드
    private void updateCategoryComboBox() {
        categoryComboBox.removeAllItems(); // 기존 아이템 제거

        // 일정 데이터를 순회하여 사용된 카테고리를 추출
        Set<String> uniqueCategoryNames = new HashSet<>();
        for (FileManager.Schedule schedule : FileManager.schedules) {
            if (schedule.getCategory() != null) {
                uniqueCategoryNames.add(schedule.getCategory().getName());
            }
        }

        // 추출한 카테고리 이름을 콤보박스에 추가
        for (String categoryName : uniqueCategoryNames) {
            categoryComboBox.addItem(categoryName);
        }

        // 카테고리 선택 시 동작 추가
        categoryComboBox.addActionListener(e -> {
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            System.out.println("Selected Category: " + selectedCategory); // 디버깅용
            calendarPanel.updateDatesWithCategory(selectedCategory); // 선택된 카테고리로 캘린더 업데이트
        });

        categoryComboBox.revalidate();
        categoryComboBox.repaint();
    }






    // 캘린더 패널 클래스
    class CalendarPanel extends JPanel {
        private int year;
        private int month;
        private JLabel monthLabel;
        private JPanel datePanel;

        public CalendarPanel() {
            setLayout(new BorderLayout());

            JPanel navPanel = new JPanel();
            JButton prevButton = new JButton("<");
            JButton nextButton = new JButton(">");
            monthLabel = new JLabel("", JLabel.CENTER);
            updateMonthLabel();

            prevButton.addActionListener(e -> changeMonth(-1));
            nextButton.addActionListener(e -> changeMonth(1));

            navPanel.add(prevButton, BorderLayout.WEST);
            navPanel.add(monthLabel, BorderLayout.CENTER);
            navPanel.add(nextButton, BorderLayout.EAST);
            add(navPanel, BorderLayout.NORTH);

            datePanel = new JPanel(new GridLayout(7, 7));
            addDaysOfWeek();
            addDates();
            add(datePanel, BorderLayout.CENTER);
            
            updateDatesWithCategory(null);
        }

        private void changeMonth(int delta) {
            month += delta;
            if (month < 0) {
                month = 11;
                year--;
            } else if (month > 11) {
                month = 0;
                year++;
            }
            updateMonthLabel();
            addDates();
            updateDatesWithCategory((String) categoryComboBox.getSelectedItem());

            // 일정 탭 갱신
            updateMonthSchedulePanel((JPanel) tabbedPane.getComponentAt(0));
        }

        private void updateMonthLabel() {
            if (year == 0 && month == 0) {
                Calendar cal = Calendar.getInstance();
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
            }
            monthLabel.setText(year + "년 " + (month + 1) + "월");
        }

        private void addDaysOfWeek() {
            String[] days = {"일", "월", "화", "수", "목", "금", "토"};
            for (String day : days) {
                JLabel dayLabel = new JLabel(day, JLabel.CENTER);
                datePanel.add(dayLabel);
            }
        }

        private void addDates() {
            datePanel.removeAll();
            addDaysOfWeek();

            Calendar cal = Calendar.getInstance();
            cal.set(year, month, 1);

            int startDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
            int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            Font dayFont = new Font("Arial", Font.BOLD, 20);
            Map<LocalDate, String> holidays = holidayManager.getHolidays(year);

            for (int i = 0; i < startDay; i++) {
                datePanel.add(new JLabel(""));
            }

            for (int day = 1; day <= daysInMonth; day++) {
                final int currentDay = day;
                LocalDate currentDate = LocalDate.of(year, month + 1, currentDay);

                String holidayName = holidays.get(currentDate);
                boolean isToday = currentDate.equals(LocalDate.now());

                // 날짜 버튼 생성
                String buttonText = "<html><div style='text-align: center;'>" + currentDay;
                if (holidayName != null) {
                    buttonText += "<br><span style='font-size:10px; color:red;'>" + holidayName + "</span>";
                }
                buttonText += "</div></html>";

                JButton dayButton = new JButton(buttonText);
                dayButton.setFont(dayFont);
                dayButton.setContentAreaFilled(true);
                dayButton.setBackground(Color.WHITE);

                // 공휴일 및 주말 색상 처리
                if (holidayName != null) {
                    dayButton.setForeground(Color.RED); // 공휴일
                } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                    dayButton.setForeground(Color.RED); // 주말
                } else {
                    dayButton.setForeground(Color.BLACK); // 평일
                }

                // 오늘 날짜 테두리 표시
                if (isToday) {
                    dayButton.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
                }

                // 날짜 클릭 시 AddPlan 창 호출
                dayButton.addActionListener(e -> {
                    SwingUtilities.invokeLater(AddPlan::new); // AddPlan 창 열기
                });

                datePanel.add(dayButton);
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }

            for (int i = startDay + daysInMonth; i < 42; i++) {
                datePanel.add(new JLabel(""));
            }

            datePanel.revalidate();
            datePanel.repaint();
        }
        
        public void updateDatesWithCategory(String categoryName) {
            System.out.println("Updating dates for category: " + categoryName); // 디버깅 로그

            for (Component comp : datePanel.getComponents()) {
                if (comp instanceof JButton button) {
                    try {
                        // 버튼 텍스트에서 날짜 추출
                        String buttonText = button.getText().replaceAll("<[^>]*>", ""); // HTML 태그 제거
                        int day = Integer.parseInt(buttonText.trim());
                        LocalDate buttonDate = LocalDate.of(year, month + 1, day);

                        // 버튼 초기화
                        button.setBackground(Color.WHITE);

                        // 선택된 카테고리가 있을 경우
                        if (categoryName != null) {
                            // 일정 데이터에서 필터링
                            for (FileManager.Schedule schedule : FileManager.schedules) {
                                if (schedule.getCategory() != null &&
                                        schedule.getCategory().getName().equals(categoryName) &&
                                        !schedule.getStartDate().toLocalDate().isAfter(buttonDate) &&
                                        !schedule.getEndDate().toLocalDate().isBefore(buttonDate)) {
                                    // 해당 일정의 카테고리 색상으로 버튼 배경 설정
                                    button.setBackground(schedule.getCategory().getColor());
                                    System.out.println("Updated button for date: " + buttonDate +
                                            " with color: " + schedule.getCategory().getColor());
                                    break;
                                }
                            }
                        }
                    } catch (NumberFormatException ignored) {
                        // 날짜가 아닌 버튼(예: 요일)은 무시
                    }
                }
            }

            datePanel.repaint(); // 화면 갱신
        }




    }

    public static void main(String[] args) {
        new CalendarService();


    }
}
