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
        CalendarPanel calendarPanel = new CalendarPanel();
        c.add(calendarPanel, BorderLayout.CENTER);

        // 서쪽 패널 생성 및 구성
        JPanel westPanel = new JPanel(new BorderLayout(5, 5));
        westPanel.setPreferredSize(new Dimension(200, 0)); // 창 크기의 약 30%로 설정 (창 너비가 1000이므로 약 300)

        // 카테고리 패널
        JPanel categoryPanel = new JPanel();
        categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.Y_AXIS)); // 수직
        categoryPanel.setBorder(BorderFactory.createTitledBorder("카테고리"));
        categoryComboBox = new JComboBox<>(); // 카테고리 콤보박스 생성
        updateCategoryComboBox(categoryComboBox); // 저장된 카테고리 추가
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
            // 카테고리와 이번달 일정 새로고침
            updateCategoryComboBox(categoryComboBox);
            calendarPanel.updateDatesWithCategory((String) categoryComboBox.getSelectedItem());
            updateMonthSchedulePanel(monthSchedulePanel);
        });

        // 새로고침 버튼 아래 추가를 위해 서쪽 패널 수정
        JPanel westPanelContent = new JPanel();
        westPanelContent.setLayout(new BorderLayout());
        westPanelContent.add(tabbedPane, BorderLayout.CENTER); // 탭을 가운데에 추가
        westPanelContent.add(refreshButton, BorderLayout.SOUTH); // 버튼을 아래에 추가

        // 카테고리 패널을 NORTH에 추가하고 서쪽 패널에 추가
        westPanel.add(categoryPanel, BorderLayout.NORTH);
        westPanel.add(westPanelContent, BorderLayout.CENTER);

        // 서쪽 패널 전체를 컨테이너에 추가
        c.add(westPanel, BorderLayout.WEST);

     // CalendarService 클래스의 생성자에 추가
//        JButton resetButton = new JButton("초기화");
//        resetButton.addActionListener(e -> {
//            int confirm = JOptionPane.showConfirmDialog(this, "모든 데이터를 초기화하시겠습니까?", "초기화 확인", JOptionPane.YES_NO_OPTION);
//            if (confirm == JOptionPane.YES_OPTION) {
//                FileManager.resetData(); // 초기화 메서드 호출
//                updateScheduleTab(); // UI 갱신
//                JOptionPane.showMessageDialog(this, "모든 데이터가 초기화되었습니다.", "초기화 완료", JOptionPane.INFORMATION_MESSAGE);
//            }
//        });
//
//        // 서쪽 패널에 버튼 추가 (예: 카테고리 패널 아래)
//        categoryPanel.add(Box.createVerticalStrut(10)); // 간격 추가
//        categoryPanel.add(resetButton);


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
    private void updateCategoryComboBox(JComboBox<String> categoryComboBox) {
        categoryComboBox.removeAllItems(); // 기존 아이템 제거

        // 저장된 카테고리를 추가
        for (FileManager.Category category : FileManager.categories) {
            categoryComboBox.addItem(category.getName());
        }

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
            for (Component comp : datePanel.getComponents()) {
                if (comp instanceof JButton button) {
                    try {
                        String buttonText = button.getText().replaceAll("<.*?>", ""); // Remove HTML tags
                        int day = Integer.parseInt(buttonText);
                        LocalDate buttonDate = LocalDate.of(year, month + 1, day);

                        // 초기화
                        button.setBackground(Color.WHITE);

                        if (categoryName != null) {
                            FileManager.Category selectedCategory = FileManager.categories.stream()
                                    .filter(category -> category.getName().equals(categoryName))
                                    .findFirst()
                                    .orElse(null);

                            if (selectedCategory != null) {
                                // 일정 필터링
                                boolean hasScheduleInCategory = FileManager.schedules.stream()
                                        .anyMatch(schedule -> !schedule.getStartDate().toLocalDate().isAfter(buttonDate) &&
                                                !schedule.getEndDate().toLocalDate().isBefore(buttonDate) &&
                                                schedule.getCategory() != null &&
                                                schedule.getCategory().equals(selectedCategory));

                                if (hasScheduleInCategory) {
                                    button.setBackground(selectedCategory.getColor());
                                }
                            }
                        }
                    } catch (NumberFormatException ignored) {
                        // 날짜가 아닌 버튼은 무시
                    }
                }
            }
            datePanel.repaint();
        }

    }

    public static void main(String[] args) {
        new CalendarService();
    }
}
