import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.io.*;
import java.util.List;  // java.util.List ����Ʈ
import java.util.Map;   // java.util.Map ����Ʈ
import java.util.ArrayList; // java.util.ArrayList ����Ʈ
import java.util.HashMap;  // java.util.HashMap ����Ʈ


public class CalendarApp extends JFrame {
    private LocalDate currentDate;
    private JLabel monthYearLabel;
    private JPanel calendarPanel;
    private JTextArea scheduleTextArea;
    private Map<LocalDate, List<String>> scheduleMap; // ��¥�� ���� ����Ʈ ����

    public CalendarApp() {
        currentDate = LocalDate.now();
        scheduleMap = new HashMap<>(); // ���� ����� �� �ʱ�ȭ
        loadSchedules();  // ���α׷� ���� �� ���� �ҷ�����

        setTitle("Ķ���� ���ø����̼�");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ��� �г�: �� �̵� ��ư �� ���� �� ǥ��
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

        // ���� �г�: ���� �߰� ��ư
        JPanel leftPanel = new JPanel(new BorderLayout());
        JButton addScheduleButton = new JButton("�� Ķ���� (���� �߰�)");
        addScheduleButton.addActionListener(e -> addSchedule());
        leftPanel.add(addScheduleButton, BorderLayout.NORTH);

        add(leftPanel, BorderLayout.WEST);

        // �߾� �г�: �޷�
        calendarPanel = new JPanel(new GridLayout(0, 7));
        updateCalendar();
        add(calendarPanel, BorderLayout.CENTER);

        // ���� �г�: ���� �󼼺���
        scheduleTextArea = new JTextArea();
        scheduleTextArea.setEditable(false);
        add(new JScrollPane(scheduleTextArea), BorderLayout.EAST);
    }

    private void updateCalendar() {
        monthYearLabel.setText(currentDate.getYear() + "�� " + currentDate.getMonth().getDisplayName(TextStyle.FULL, Locale.KOREAN));
        calendarPanel.removeAll();

        // ���� ǥ��
        String[] daysOfWeek = {"��", "��", "ȭ", "��", "��", "��", "��"};
        for (String day : daysOfWeek) {
            JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
            calendarPanel.add(dayLabel);
        }

        // ��¥ ǥ��
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);
        int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;

        for (int i = 0; i < dayOfWeek; i++) {
            calendarPanel.add(new JLabel()); // �� ĭ
        }

        int daysInMonth = currentDate.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentDate.withDayOfMonth(day); // ��¥ ��ü�� ���� ����
            JButton dayButton = new JButton(String.valueOf(day));
            dayButton.addActionListener(e -> showSchedule(date)); // �ش� ��¥�� showSchedule ȣ��

            // ������ �ִ� ��¥�� �������� ǥ��
            if (scheduleMap.containsKey(date)) {
                dayButton.setBackground(Color.CYAN);
            }

            calendarPanel.add(dayButton);
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private void addSchedule() {
        String dateInput = JOptionPane.showInputDialog("���� ��¥ (��: 2024-04-14): ");
        String time = JOptionPane.showInputDialog("���� �ð� (��: ���� 1��): ");
        String title = JOptionPane.showInputDialog("���� ����: ");

        if (dateInput != null && time != null && title != null) {
            try {
                LocalDate date = LocalDate.parse(dateInput, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String schedule = time + " - " + title;

                // ���� �߰� �Ǵ� ���ο� ����Ʈ�� �߰�
                scheduleMap.computeIfAbsent(date, k -> new ArrayList<>()).add(schedule);
                saveSchedules();  // ���� ����
                JOptionPane.showMessageDialog(this, "������ ����Ǿ����ϴ�.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "�߸��� ��¥ �����Դϴ�. (��: 2024-04-14)");
            }
        }
    }

    private void showSchedule(LocalDate date) {
        scheduleTextArea.setText("��¥: " + date + "\n\n");
        List<String> scheduleList = scheduleMap.get(date);
        if (scheduleList != null && !scheduleList.isEmpty()) {
            scheduleTextArea.append("����:\n");
            for (String schedule : scheduleList) {
                scheduleTextArea.append("- " + schedule + "\n");
            }
        } else {
            scheduleTextArea.append("������ �����ϴ�.");
        }
    }

    // ���� �����͸� �ؽ�Ʈ ���Ϸ� ����
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

    // �ؽ�Ʈ ���Ͽ��� ���� ������ �ҷ�����
    private void loadSchedules() {
        try {
            File file = new File("schedules.txt");
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                LocalDate currentDate = null;
                List<String> schedules = null;

                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue; // �� �� �ǳʶٱ�

                    if (line.startsWith("20")) { // ��¥ ���� (��: 2024-04-14)
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
