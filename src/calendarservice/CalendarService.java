package calendarservice;

import java.awt.*;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class CalendarService extends JFrame {
    private String[] search = {"자바프로그래밍", "술약속", "정기모임"};
    private Map<String, String> scheduleMap; // 날짜별 일정 저장
    private JTabbedPane tabbedPane;
    private HolidayManager holidayManager;  // HolidayManager 인스턴스 추가

    public CalendarService() {
        setTitle("캘린더 화면");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container c = getContentPane();
        c.setLayout(new BorderLayout(5, 5));
        c.setBackground(Color.white);

        scheduleMap = new HashMap<>(); // 일정 저장용 Map 초기화
        holidayManager = new HolidayManager();  // 공휴일 관리자를 초기화

        // 중앙에 캘린더 패널 추가
        CalendarPanel calendarPanel = new CalendarPanel();
        c.add(calendarPanel, BorderLayout.CENTER);

        // 서쪽 패널 생성 및 구성
        JPanel westPanel = new JPanel(new BorderLayout(5, 5));

        // 카테고리 패널
        JPanel categoryPanel = new JPanel();
        categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.Y_AXIS)); // 수직
        categoryPanel.setBorder(BorderFactory.createTitledBorder("카테고리"));
        JComboBox<String> categoryComboBox = new JComboBox<>(search);
        categoryPanel.add(categoryComboBox);

        // 일정 탭 패널 생성
        tabbedPane = new JTabbedPane();

        // 첫 번째 탭: 이번 달 일정
        JPanel monthSchedulePanel = new JPanel();
        monthSchedulePanel.setLayout(new BoxLayout(monthSchedulePanel, BoxLayout.Y_AXIS));
        monthSchedulePanel.add(new JLabel("10/7 ➔ 자격증 시험"));
        monthSchedulePanel.add(new JLabel("10/21-10/25 ➔ 시험"));
        tabbedPane.addTab("이번달 일정", monthSchedulePanel);

        // 카테고리 패널을 NORTH에, 탭 패널을 CENTER에 추가하여 서쪽에 배치
        westPanel.add(categoryPanel, BorderLayout.NORTH);
        westPanel.add(tabbedPane, BorderLayout.CENTER);
        c.add(westPanel, BorderLayout.WEST);

        setSize(1000, 600);
        setVisible(true);
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

            prevButton.addActionListener(e -> changeMonth(-1)); // 뒤로 넘기는 경우
            nextButton.addActionListener(e -> changeMonth(1)); // 앞으로 넘기는 경우

            navPanel.add(prevButton, BorderLayout.WEST);
            navPanel.add(monthLabel, BorderLayout.CENTER);
            navPanel.add(nextButton, BorderLayout.EAST);
            add(navPanel, BorderLayout.NORTH);

            datePanel = new JPanel(new GridLayout(7, 7));
            addDaysOfWeek();
            addDates();
            add(datePanel, BorderLayout.CENTER); // 캘린더 패널 center에 datePanel 추가
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

            for (int i = 0; i < startDay; i++) {
                datePanel.add(new JLabel("")); // 빈 공간 채우기
            }

            for (int day = 1; day <= daysInMonth; day++) {
                final int currentDay = day; // 복사본 생성
                LocalDate currentDate = LocalDate.of(year, month + 1, currentDay);

                // 공휴일 이름 가져오기
                String holidayName = holidayManager.getHolidayName(year, currentDate);

                String buttonText = "<html><div style='text-align: center;'>" + currentDay;
                if (holidayName != null) {
                    buttonText += "<br><span style='font-size:10px; color:red;'>" + holidayName + "</span>";
                }
                buttonText += "</div></html>";

                JButton dayButton = new JButton(buttonText);
                dayButton.setFont(dayFont); // 폰트를 적용
                dayButton.setContentAreaFilled(false);

                // 공휴일 및 주말 색상
                if (holidayName != null) {
                    dayButton.setForeground(Color.RED);
                } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                    dayButton.setForeground(Color.RED);
                } else {
                    dayButton.setForeground(Color.BLACK);
                }

                if (currentDate.equals(LocalDate.now())) {
                    dayButton.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
                }

                // currentDay 사용
                dayButton.addActionListener(e -> showScheduleDialog(year, month + 1, currentDay));

                datePanel.add(dayButton);
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }

            for (int i = startDay + daysInMonth; i < 42; i++) {
                datePanel.add(new JLabel(""));
            }

            datePanel.revalidate();
            datePanel.repaint();
        }


        private void showScheduleDialog(int year, int month, int day) {
            String dateKey = year + "/" + month + "/" + day;
            String schedule = scheduleMap.getOrDefault(dateKey, "일정 없음");

            JDialog dialog = new JDialog(CalendarService.this, "일정: " + dateKey, true);
            dialog.setSize(300, 200);
            dialog.setLayout(new BorderLayout());

            JLabel dateLabel = new JLabel("일정 보기: " + dateKey, JLabel.CENTER);
            JTextArea scheduleArea = new JTextArea(schedule);
            scheduleArea.setEditable(false);

            JButton addButton = new JButton("일정 추가");
            addButton.addActionListener(e -> openSchedule(dateKey));

            dialog.add(dateLabel, BorderLayout.NORTH);
            dialog.add(scheduleArea, BorderLayout.CENTER);
            dialog.add(addButton, BorderLayout.SOUTH);

            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        }

        private void openSchedule(String dateKey) {
            System.out.println("일정 입력 창을 호출합니다. 날짜: " + dateKey);
        }
    }

    public static void main(String[] args) {
        new CalendarService();
    }
}
