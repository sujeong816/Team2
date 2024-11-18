import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.io.*;
import java.util.List;  // java.util.List 임포트
import java.util.Map;   // java.util.Map 임포트
import java.util.ArrayList; // java.util.ArrayList 임포트
import java.util.HashMap;  // java.util.HashMap 임포트


public class CalendarApp extends JFrame {
    private LocalDate currentDate;
    private JLabel monthYearLabel;
    private JPanel calendarPanel;
    private JTextArea scheduleTextArea;
    private Map<LocalDate, List<String>> scheduleMap; // 날짜별 일정 리스트 저장

    public CalendarApp() {
        currentDate = LocalDate.now();
        scheduleMap = new HashMap<>(); // 일정 저장용 맵 초기화
        loadSchedules();  // 프로그램 시작 시 일정 불러오기

        setTitle("캘린더 애플리케이션");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 상단 패널: 월 이동 버튼 및 현재 월 표시
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton prevButton = new JButton("<");
        prevButton.addActionListener(e -> {
            currentDate = currentDate.minusMonths(1);
            updateCalendar();
        });

        JButton nextButton = new JButton(">");
        nextButton.addActionListener(e -> {
            currentDate = currentDate.plusMonths(1);
            updateCalendar();
        });

        monthYearLabel = new JLabel();
        monthYearLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(prevButton, BorderLayout.WEST);
        topPanel.add(monthYearLabel, BorderLayout.CENTER);
        topPanel.add(nextButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // 좌측 패널: 일정 추가 버튼
        JPanel leftPanel = new JPanel(new BorderLayout());
        JButton addScheduleButton = new JButton("내 캘린더 (일정 추가)");
        addScheduleButton.addActionListener(e -> addSchedule());
        leftPanel.add(addScheduleButton, BorderLayout.NORTH);

        add(leftPanel, BorderLayout.WEST);

        // 중앙 패널: 달력
        calendarPanel = new JPanel(new GridLayout(0, 7));
        updateCalendar();
        add(calendarPanel, BorderLayout.CENTER);

        // 우측 패널: 일정 상세보기
        scheduleTextArea = new JTextArea();
        scheduleTextArea.setEditable(false);
        add(new JScrollPane(scheduleTextArea), BorderLayout.EAST);
    }

    private void updateCalendar() {
        monthYearLabel.setText(currentDate.getYear() + "년 " + currentDate.getMonth().getDisplayName(TextStyle.FULL, Locale.KOREAN));
        calendarPanel.removeAll();

        // 요일 표시
        String[] daysOfWeek = {"일", "월", "화", "수", "목", "금", "토"};
        for (String day : daysOfWeek) {
            JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
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
            LocalDate date = currentDate.withDayOfMonth(day); // 날짜 객체를 직접 생성
            JButton dayButton = new JButton(String.valueOf(day));
            dayButton.addActionListener(e -> showSchedule(date)); // 해당 날짜로 showSchedule 호출

            // 일정이 있는 날짜는 색상으로 표시
            if (scheduleMap.containsKey(date)) {
                dayButton.setBackground(Color.CYAN);
            }

            calendarPanel.add(dayButton);
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private void addSchedule() {
        String dateInput = JOptionPane.showInputDialog("일정 날짜 (예: 2024-04-14): ");
        String time = JOptionPane.showInputDialog("일정 시간 (예: 오후 1시): ");
        String title = JOptionPane.showInputDialog("일정 제목: ");

        if (dateInput != null && time != null && title != null) {
            try {
                LocalDate date = LocalDate.parse(dateInput, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String schedule = time + " - " + title;

                // 일정 추가 또는 새로운 리스트에 추가
                scheduleMap.computeIfAbsent(date, k -> new ArrayList<>()).add(schedule);
                saveSchedules();  // 일정 저장
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

    // 일정 데이터를 텍스트 파일로 저장
    private void saveSchedules() {
        try {
            FileWriter writer = new FileWriter("schedules.txt");
            for (Map.Entry<LocalDate, List<String>> entry : scheduleMap.entrySet()) {
                LocalDate date = entry.getKey();
                List<String> schedules = entry.getValue();
                writer.write(date.toString() + "\n");
                for (String schedule : schedules) {
                    writer.write("  " + schedule + "\n");
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 텍스트 파일에서 일정 데이터 불러오기
    private void loadSchedules() {
        try {
            File file = new File("schedules.txt");
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                LocalDate currentDate = null;
                List<String> schedules = null;

                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue; // 빈 줄 건너뛰기

                    if (line.startsWith("20")) { // 날짜 형식 (예: 2024-04-14)
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
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalendarApp().setVisible(true));
    }
}
