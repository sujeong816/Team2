package project1;

import java.awt.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class CalendarService extends JFrame {
    private JTabbedPane tabbedPane; // 일정 탭
    private HolidayManager holidayManager; // 공휴일 관리 객체
    private JComboBox<String> categoryComboBox; // 카테고리 선택 콤보박스
    private CalendarPanel calendarPanel; // 캘린더 화면 패널

    public CalendarService() {
        setTitle("캘린더 화면"); // 창 제목
        setDefaultCloseOperation(EXIT_ON_CLOSE); // 창 닫기 시 종료
        Container c = getContentPane();
        c.setLayout(new BorderLayout(5, 5)); // 레이아웃 설정
        c.setBackground(Color.white); // 배경색 설정

        FileManager.LoadSaveData(); // 파일에서 저장된 데이터 로드
        holidayManager = new HolidayManager(); // 공휴일 관리 객체 초기화

        // 중앙에 캘린더 패널 추가
        calendarPanel = new CalendarPanel(); 
        c.add(calendarPanel, BorderLayout.CENTER);

        // 왼쪽 패널 구성
        JPanel westPanel = new JPanel(new BorderLayout(5, 5));
        westPanel.setPreferredSize(new Dimension(200, 0)); // 왼쪽 패널 너비 설정

        // 카테고리 패널 구성
        JPanel categoryPanel = new JPanel();
        categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.Y_AXIS)); // 세로 배치
        categoryPanel.setBorder(BorderFactory.createTitledBorder("카테고리")); // 테두리 제목 설정
        categoryComboBox = new JComboBox<>();
        updateCategoryComboBox(); // 카테고리 데이터 업데이트
        categoryPanel.add(categoryComboBox); // 카테고리 선택 콤보박스 추가

        // 일정 탭 생성 및 초기화
        tabbedPane = new JTabbedPane();
        JPanel monthSchedulePanel = new JPanel();
        monthSchedulePanel.setLayout(new BoxLayout(monthSchedulePanel, BoxLayout.Y_AXIS)); // 세로 배치
        monthSchedulePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 패널 내부 여백 추가
        updateMonthSchedulePanel(monthSchedulePanel); // 이번달 일정 데이터 추가
        tabbedPane.addTab("이번달 일정", monthSchedulePanel); // 탭 추가

        // 버튼 패널 생성 및 설정
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // 버튼 오른쪽 정렬
        JButton refreshButton = new JButton("새로고침"); // 새로고침 버튼 생성
        refreshButton.setPreferredSize(new Dimension(90, 30)); // 버튼 크기 설정
        refreshButton.addActionListener(e -> {
            updateCategoryComboBox(); // 카테고리 콤보박스 갱신
            calendarPanel.updateDatesWithCategory((String) categoryComboBox.getSelectedItem()); // 선택된 카테고리로 캘린더 업데이트
            updateMonthSchedulePanel(monthSchedulePanel); // 일정 패널 업데이트
        });

        JButton mainButton = new JButton("메인화면"); // 메인화면 이동 버튼
        mainButton.setPreferredSize(new Dimension(90, 30)); // 버튼 크기 설정
        mainButton.addActionListener(e -> {
            System.out.println("메인화면으로 이동합니다."); // 디버깅 메시지
            dispose(); // 현재 창 닫기
            SwingUtilities.invokeLater(() -> new CalendarApp().setVisible(true)); // 메인화면 실행
        });

        buttonPanel.add(refreshButton); // 새로고침 버튼 추가
        buttonPanel.add(mainButton); // 메인화면 버튼 추가

        // 왼쪽 패널에 일정 탭 및 버튼 패널 추가
        JPanel westPanelContent = new JPanel(new BorderLayout());
        westPanelContent.add(tabbedPane, BorderLayout.CENTER); // 일정 탭 추가
        westPanelContent.add(buttonPanel, BorderLayout.SOUTH); // 버튼 패널 추가

        westPanel.add(categoryPanel, BorderLayout.NORTH); // 카테고리 패널 추가
        westPanel.add(westPanelContent, BorderLayout.CENTER); // 탭과 버튼 패널 추가

        c.add(westPanel, BorderLayout.WEST); // 왼쪽 패널 메인 컨테이너에 추가

        setSize(1000, 600); // 창 크기 설정
        setLocationRelativeTo(null); // 창을 화면 중앙에 위치
        setVisible(true); // 창 보이기
    }

    // 일정 탭 업데이트 메서드
    public void updateScheduleTab() {
        if (tabbedPane.getComponentAt(0) instanceof JPanel) {
            JPanel monthPanel = (JPanel) tabbedPane.getComponentAt(0);
            updateMonthSchedulePanel(monthPanel); // 이번달 일정 탭 데이터 갱신
            monthPanel.revalidate(); // 레이아웃 재검토
            monthPanel.repaint(); // 화면 갱신
        }
    }

    // 이번달 일정 패널 업데이트
    private void updateMonthSchedulePanel(JPanel panel) {
        panel.removeAll(); // 기존 데이터 제거

        int selectedYear = calendarPanel.year; // 선택된 연도
        int selectedMonth = calendarPanel.month + 1; // 선택된 월

        Map<Integer, List<String>> dailySchedules = new TreeMap<>(); // 일정 저장용 맵
        for (FileManager.Schedule schedule : FileManager.schedules) {
            if (schedule.getStartDate().getYear() == selectedYear &&
                schedule.getStartDate().getMonthValue() == selectedMonth) {
                LocalDate startDate = schedule.getStartDate().toLocalDate();
                LocalDate endDate = schedule.getEndDate().toLocalDate();

                while (!startDate.isAfter(endDate)) {
                    int day = startDate.getDayOfMonth();
                    dailySchedules.computeIfAbsent(day, k -> new ArrayList<>()).add(schedule.getTitle()); // 일정 제목 추가
                    startDate = startDate.plusDays(1); // 다음 날짜로 이동
                }
            }
        }

        // 일정 데이터를 패널에 추가
        for (Map.Entry<Integer, List<String>> entry : dailySchedules.entrySet()) {
            int day = entry.getKey();
            List<String> titles = entry.getValue();

            StringBuilder schedulesForDay = new StringBuilder();
            schedulesForDay.append(day).append("일:\n");
            for (String title : titles) {
                schedulesForDay.append("- ").append(title).append("\n"); // 하루 일정 나열
            }

            JTextArea dayArea = new JTextArea(schedulesForDay.toString()); // 텍스트 영역 생성
            dayArea.setEditable(false); // 수정 불가
            dayArea.setLineWrap(true); // 텍스트 줄바꿈 허용
            dayArea.setWrapStyleWord(true); // 단어 단위 줄바꿈
            dayArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // 패딩 추가
            dayArea.setBackground(panel.getBackground()); // 배경색 동일 설정

            panel.add(dayArea); // 일정 추가
        }

        panel.revalidate(); // 레이아웃 재검토
        panel.repaint(); // 화면 갱신
    }

    // 카테고리 콤보박스 업데이트
    private void updateCategoryComboBox() {
        categoryComboBox.removeAllItems(); // 기존 항목 제거

        Set<String> uniqueCategoryNames = new HashSet<>(); // 중복 방지
        for (FileManager.Schedule schedule : FileManager.schedules) {
            if (schedule.getCategory() != null) {
                uniqueCategoryNames.add(schedule.getCategory().getName()); // 카테고리 추가
            }
        }

        for (String categoryName : uniqueCategoryNames) {
            categoryComboBox.addItem(categoryName); // 콤보박스에 추가
        }

        // 카테고리 선택 이벤트
        categoryComboBox.addActionListener(e -> {
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            System.out.println("Selected Category: " + selectedCategory); // 디버깅 메시지
            calendarPanel.updateDatesWithCategory(selectedCategory); // 선택된 카테고리에 따라 캘린더 업데이트
        });

        categoryComboBox.revalidate();
        categoryComboBox.repaint();
    }

    // 캘린더 화면 패널
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

            prevButton.addActionListener(e -> changeMonth(-1)); // 이전 달로 이동
            nextButton.addActionListener(e -> changeMonth(1)); // 다음 달로 이동

            navPanel.add(prevButton, BorderLayout.WEST);
            navPanel.add(monthLabel, BorderLayout.CENTER);
            navPanel.add(nextButton, BorderLayout.EAST);
            add(navPanel, BorderLayout.NORTH);

            datePanel = new JPanel(new GridLayout(7, 7)); // 7x7 달력 그리드
            addDaysOfWeek();
            addDates();
            add(datePanel, BorderLayout.CENTER);

            updateDatesWithCategory(null); // 초기 데이터 설정
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
            updateMonthSchedulePanel((JPanel) tabbedPane.getComponentAt(0));
        }

        private void updateMonthLabel() {
            if (year == 0 && month == 0) {
                Calendar cal = Calendar.getInstance();
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
            }
            monthLabel.setText(year + "년 " + (month + 1) + "월"); // 연도와 월 갱신
        }

        private void addDaysOfWeek() {
            String[] days = {"일", "월", "화", "수", "목", "금", "토"};
            for (String day : days) {
                JLabel dayLabel = new JLabel(day, JLabel.CENTER); // 요일 추가
                datePanel.add(dayLabel);
            }
        }

        private void addDates() {
            datePanel.removeAll(); // 기존 날짜 제거
            addDaysOfWeek();

            Calendar cal = Calendar.getInstance();
            cal.set(year, month, 1);

            int startDay = cal.get(Calendar.DAY_OF_WEEK) - 1; // 시작 요일
            int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH); // 해당 월의 일 수

            Font dayFont = new Font("Arial", Font.BOLD, 20); // 폰트 설정
            Map<LocalDate, String> holidays = holidayManager.getHolidays(year);

            for (int i = 0; i < startDay; i++) {
                datePanel.add(new JLabel("")); // 빈 칸 추가
            }

            for (int day = 1; day <= daysInMonth; day++) {
                final int currentDay = day;
                LocalDate currentDate = LocalDate.of(year, month + 1, currentDay);

                String holidayName = holidays.get(currentDate);
                boolean isToday = currentDate.equals(LocalDate.now());

                StringBuilder buttonText = new StringBuilder();
                buttonText.append("<html><div style='text-align: center;'>");
                buttonText.append("<b>").append(currentDay).append("</b>");

                if (holidayName != null) {
                    buttonText.append("<br><span style='font-size:10px; color:red;'>").append(holidayName).append("</span>");
                }

                buttonText.append("</div></html>");

                JButton dayButton = new JButton(buttonText.toString());
                dayButton.setFont(dayFont);
                dayButton.setContentAreaFilled(true);
                dayButton.setBackground(Color.WHITE);

                if (holidayName != null) {
                    dayButton.setForeground(Color.RED);
                } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                    dayButton.setForeground(Color.RED);
                } else {
                    dayButton.setForeground(Color.BLACK);
                }

                if (isToday) {
                    dayButton.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
                }

                dayButton.addActionListener(e -> SwingUtilities.invokeLater(AddPlan::new));
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
            System.out.println("Updating dates for category: " + categoryName);

            for (Component comp : datePanel.getComponents()) {
                if (comp instanceof JButton button) {
                    try {
                        String buttonText = button.getText().replaceAll("<[^>]*>", "");
                        int day = Integer.parseInt(buttonText.trim());
                        LocalDate buttonDate = LocalDate.of(year, month + 1, day);

                        button.setBackground(Color.WHITE);

                        if (categoryName != null) {
                            for (FileManager.Schedule schedule : FileManager.schedules) {
                                if (schedule.getCategory() != null &&
                                    schedule.getCategory().getName().equals(categoryName) &&
                                    !schedule.getStartDate().toLocalDate().isAfter(buttonDate) &&
                                    !schedule.getEndDate().toLocalDate().isBefore(buttonDate)) {
                                    button.setBackground(schedule.getCategory().getColor());
                                    break;
                                }
                            }
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            datePanel.repaint();
        }
    }

    public static void main(String[] args) {
        new CalendarService(); // 메인 실행
    }
}
