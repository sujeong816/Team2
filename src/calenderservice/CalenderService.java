package calenderservice;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

public class CalenderService extends JFrame {
    private String[] search = {"자바프로그래밍", "술약속", "정기모임"};
    private Map<String, String> scheduleMap; // 날짜별 일정 저장
    private JTabbedPane tabbedPane;

    public CalenderService() {
        setTitle("캘린더 화면");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container c = getContentPane();
        c.setLayout(new BorderLayout(5, 5));
        c.setBackground(Color.white);

        scheduleMap = new HashMap<>(); // 일정 저장용 Map 초기화

        // 중앙에 캘린더 패널 추가
        CalenderPanel calendarPanel = new CalenderPanel();
        c.add(calendarPanel, BorderLayout.CENTER);

        // 서쪽 패널 생성 및 구성
        JPanel westPanel = new JPanel(new BorderLayout(5, 5));

        // 카테고리 패널
        JPanel categoryPanel = new JPanel();
        categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.Y_AXIS));
        categoryPanel.setBorder(BorderFactory.createTitledBorder("카테고리"));
        JComboBox<String> categoryComboBox = new JComboBox<>(search);
        categoryPanel.add(categoryComboBox);

        // 일정 탭 패널 생성
        tabbedPane = new JTabbedPane();

        // 첫 번째 탭: 이번달 일정
        JPanel monthSchedulePanel = new JPanel();
        monthSchedulePanel.setLayout(new BoxLayout(monthSchedulePanel, BoxLayout.Y_AXIS));
        monthSchedulePanel.add(new JLabel("10/7 ➔ 자격증 시험"));
        monthSchedulePanel.add(new JLabel("10/21-10/25 ➔ 시험"));
        tabbedPane.addTab("이번달 일정", monthSchedulePanel);

        // 두 번째 탭: 이번주 일정
        JPanel weekSchedulePanel = new JPanel();
        weekSchedulePanel.setLayout(new BoxLayout(weekSchedulePanel, BoxLayout.Y_AXIS));
        weekSchedulePanel.add(new JLabel("10/30 ➔ 13:30 동아리"));
        weekSchedulePanel.add(new JLabel("10/30 ➔ 18:00 회식"));
        tabbedPane.addTab("이번주 일정", weekSchedulePanel);

        // 카테고리 패널을 NORTH에, 탭 패널을 CENTER에 추가하여 서쪽에 배치
        westPanel.add(categoryPanel, BorderLayout.NORTH);
        westPanel.add(tabbedPane, BorderLayout.CENTER);
        c.add(westPanel, BorderLayout.WEST);

        setSize(1000, 800);
        setVisible(true);
    }
    
    // 캘린더 패널 클래스
    class CalenderPanel extends JPanel {
        private int year;
        private int month;
        private JLabel monthLabel;
        private JPanel datePanel;

        public CalenderPanel() {
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

            for (int i = 0; i < startDay; i++) {
                datePanel.add(new JLabel(""));
            }

            for (int day = 1; day <= daysInMonth; day++) {
                int currentDay = day;
                JButton dayButton = new JButton(String.valueOf(currentDay));
                dayButton.setContentAreaFilled(false);
                dayButton.setBorderPainted(false);
                dayButton.setFocusPainted(false);
                dayButton.setForeground(Color.BLACK);
                dayButton.setHorizontalAlignment(JButton.CENTER);

                dayButton.addActionListener(e -> showScheduleDialog(year, month + 1, currentDay));
                datePanel.add(dayButton);
            }

            int totalCells = 42;
            for (int i = startDay + daysInMonth; i < totalCells; i++) {
                datePanel.add(new JLabel(""));
            }

            datePanel.revalidate();
            datePanel.repaint();
        }
    }

    // 선택한 날짜의 저장된 일정을 보여주는 다이얼로그 창
    private void showScheduleDialog(int year, int month, int day) {
        String dateKey = year + "/" + month + "/" + day;
        String schedule = scheduleMap.getOrDefault(dateKey, "일정 없음");

        JDialog dialog = new JDialog(this, "일정: " + dateKey, true);
        dialog.setSize(300, 200);
        dialog.setLayout(new BorderLayout());

        JLabel dateLabel = new JLabel("일정 보기: " + dateKey, JLabel.CENTER);
        JTextArea scheduleArea = new JTextArea(schedule);
        scheduleArea.setEditable(false); // 읽기 전용으로 설정

        // 일정 추가 버튼
        JButton addButton = new JButton("일정 추가");
        addButton.addActionListener(e -> openScheduleInputDialog(dateKey));

        dialog.add(dateLabel, BorderLayout.NORTH);
        dialog.add(scheduleArea, BorderLayout.CENTER);
        dialog.add(addButton, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // 일정 입력 창으로 연결하는 메서드 (임시 구현)
    private void openScheduleInputDialog(String dateKey) {
        System.out.println("일정 입력 창을 호출합니다. 날짜: " + dateKey);
        // 일정 입력 창 호출 구현을 다른 담당자에게 넘깁니다.
    }

    public static void main(String[] args) {
        new CalenderService();
    }
}
