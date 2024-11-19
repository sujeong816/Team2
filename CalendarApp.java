package Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.io.*;
import java.util.List;

public class CalendarApp extends JFrame {
    private LocalDate currentDate;
    private JLabel monthYearLabel;
    private JPanel calendarPanel;
    private JTextArea scheduleTextArea;
    private Map<LocalDate, List<String>> scheduleMap;

    public CalendarApp() {
        currentDate = LocalDate.now();
        scheduleMap = new HashMap<>();
        loadSchedules();

        setTitle("캘린더 애플리케이션");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 기본 배경 흰색으로 설정
        getContentPane().setBackground(Color.WHITE);

        // 상단 패널
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE); // 상단 패널 배경 흰색
        JButton prevButton = createStyledButton("<");
        prevButton.addActionListener(e -> {
            currentDate = currentDate.minusMonths(1);
            updateCalendar();
        });

        JButton nextButton = createStyledButton(">");
        nextButton.addActionListener(e -> {
            currentDate = currentDate.plusMonths(1);
            updateCalendar();
        });

        monthYearLabel = new JLabel();
        monthYearLabel.setHorizontalAlignment(SwingConstants.CENTER);
        monthYearLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        topPanel.add(prevButton, BorderLayout.WEST);
        topPanel.add(monthYearLabel, BorderLayout.CENTER);
        topPanel.add(nextButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // 좌측 패널
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE); // 좌측 패널 배경 흰색
        JButton addScheduleButton = createStyledButton("내 캘린더 (일정 추가)");
        addScheduleButton.addActionListener(e -> openAddPlan());
        leftPanel.add(addScheduleButton, BorderLayout.NORTH);

        add(leftPanel, BorderLayout.WEST);

        // 중앙 패널: 달력
        calendarPanel = new JPanel(new GridLayout(0, 7));
        calendarPanel.setBackground(Color.WHITE); // 달력 패널 배경 흰색
        updateCalendar();
        add(calendarPanel, BorderLayout.CENTER);

        // 우측 패널: 일정 상세보기
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE); // 우측 패널 배경 흰색
        scheduleTextArea = createStyledTextArea(20, 10);
        rightPanel.add(new JScrollPane(scheduleTextArea), BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    private void updateCalendar() {
        monthYearLabel.setText(currentDate.getYear() + "년 " + currentDate.getMonth().getDisplayName(TextStyle.FULL, Locale.KOREAN));
        calendarPanel.removeAll();

        // 요일 표시
        String[] daysOfWeek = {"일", "월", "화", "수", "목", "금", "토"};
        for (String day : daysOfWeek) {
            JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
            dayLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            dayLabel.setOpaque(true);
            dayLabel.setBackground(Color.WHITE); // 요일 배경 흰색
            calendarPanel.add(dayLabel);
        }

        // 날짜 표시
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);
        int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;

        for (int i = 0; i < dayOfWeek; i++) {
            calendarPanel.add(new JLabel()); // 빈 칸
        }

        int daysInMonth = currentDate.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentDate.withDayOfMonth(day);
            JButton dayButton = new JButton(String.valueOf(day));
            dayButton.setBackground(Color.WHITE); // 날짜 버튼 배경 흰색
            dayButton.setFocusPainted(false);
            dayButton.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
            dayButton.addActionListener(e -> showSchedule(date));

            // 일정 있는 날짜 표시
            if (scheduleMap.containsKey(date)) {
                dayButton.setForeground(Color.BLUE);
            }
            calendarPanel.add(dayButton);
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private void openAddPlan() {
        AddPlan addPlan = new AddPlan();
        addPlan.setVisible(true);

        // AddPlan 창 닫힌 후 일정 데이터 새로고침
        addPlan.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                FileManager.LoadSaveData(); // FileManager에서 데이터 새로 로드
                updateCalendar(); // 달력 새로고침
            }
        });
    }


    private void addSchedule() {
        String dateInput = JOptionPane.showInputDialog("일정 날짜 (예: 2024-04-14): ");
        String time = JOptionPane.showInputDialog("일정 시간 (예: 오후 1시): ");
        String title = JOptionPane.showInputDialog("일정 제목: ");

        if (dateInput != null && time != null && title != null) {
            try {
                LocalDate date = LocalDate.parse(dateInput, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String schedule = time + " - " + title;

                scheduleMap.computeIfAbsent(date, k -> new ArrayList<>()).add(schedule);
                saveSchedules();
                JOptionPane.showMessageDialog(this, "일정이 저장되었습니다.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "잘못된 날짜 형식입니다. (예: 2024-04-14)");
            }
        }
    }

    private void showSchedule(LocalDate date) {
        scheduleTextArea.setText("날짜: " + date + "\n\n");
        List<String> scheduleList = scheduleMap.get(date);
        if (scheduleList != null && !scheduleList.isEmpty()) {
            scheduleTextArea.append("일정:\n");
            for (String schedule : scheduleList) {
                scheduleTextArea.append("- " + schedule + "\n");
            }
        } else {
            scheduleTextArea.append("일정이 없습니다.");
        }
    }

    private void saveSchedules() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("schedules.txt"))) {
            for (Map.Entry<LocalDate, List<String>> entry : scheduleMap.entrySet()) {
                LocalDate date = entry.getKey();
                List<String> schedules = entry.getValue();
                writer.write(date.toString() + "\n");
                for (String schedule : schedules) {
                    writer.write("  " + schedule + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSchedules() {
        try (BufferedReader reader = new BufferedReader(new FileReader("schedules.txt"))) {
            String line;
            LocalDate currentDate = null;
            List<String> schedules = null;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                if (line.startsWith("20")) {
                    if (currentDate != null && schedules != null) {
                        scheduleMap.put(currentDate, schedules);
                    }
                    currentDate = LocalDate.parse(line.trim());
                    schedules = new ArrayList<>();
                } else {
                    schedules.add(line.trim());
                }
            }
            if (currentDate != null && schedules != null) {
                scheduleMap.put(currentDate, schedules);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(34, 139, 34));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        button.setBorder(BorderFactory.createLineBorder(new Color(17, 122, 55), 1));
        return button;
    }

    private JTextArea createStyledTextArea(int rows, int columns) {
        JTextArea textArea = new JTextArea(rows, columns);
        textArea.setBackground(new Color(230, 230, 230)); // 회색
        textArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return textArea;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalendarApp().setVisible(true));
    }
}
