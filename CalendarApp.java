package project1;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CalendarApp extends JFrame {

    private JLabel dateLabel;
    private JLabel timeLabel;

    public CalendarApp() {
        setTitle("캘린더 애플리케이션");
        setSize(1000, 600);  // 창 크기를 1000x600으로 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 좌측 패널 - 버튼과 날짜/시간 표시 패널
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(150, 600));
        leftPanel.setBackground(Color.WHITE);

        // 캘린더 보기 버튼
        JButton calendarViewButton = createStyledButton("캘린더 보기");
        calendarViewButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        calendarViewButton.addActionListener(e -> openCalendarService());

        // 일정 추가 버튼
        JButton addScheduleButton = createStyledButton("일정 추가");
        addScheduleButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addScheduleButton.addActionListener(e -> openAddPlan());

        // 현재 날짜와 시간을 표시하는 라벨
        dateLabel = new JLabel();
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeLabel = new JLabel();
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateDateTime();

        // 날짜와 시간 업데이트 타이머 (1초마다)
        Timer timer = new Timer(1000, e -> updateDateTime());
        timer.start();

        // 버튼과 라벨 추가
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(calendarViewButton);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(addScheduleButton);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(dateLabel);
        leftPanel.add(timeLabel);
        add(leftPanel, BorderLayout.WEST);

        // 중앙 패널 - 일정 요약
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("일정 요약"));
        summaryPanel.setBackground(Color.WHITE);

        // 필터 패널 추가
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(Color.WHITE);
        JComboBox<String> categoryComboBox = new JComboBox<>(new String[]{"전체", "카테고리1", "카테고리2"});
        JCheckBox completeCheckBox = new JCheckBox("완료");

        controlPanel.add(new JLabel("카테고리:"));
        controlPanel.add(categoryComboBox);
        controlPanel.add(completeCheckBox);
        summaryPanel.add(controlPanel, BorderLayout.NORTH); // 요약 상단에 추가

        // 탭 패널 추가
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("일정", createSchedulePanel());
        tabbedPane.addTab("사진", createSchedulePanel());
        tabbedPane.addTab("기타", createSchedulePanel());

        summaryPanel.add(tabbedPane, BorderLayout.CENTER);
        add(summaryPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // 일정 패널 생성
    private JScrollPane createSchedulePanel() {
        JPanel schedulePanel = new JPanel();
        schedulePanel.setLayout(new BoxLayout(schedulePanel, BoxLayout.Y_AXIS));
        schedulePanel.setBackground(Color.WHITE);

        for (int i = 0; i < 10; i++) {
            schedulePanel.add(createScheduleItem("일정 " + (i + 1), "메모 내용 " + (i + 1),
                    "2024-11-01", "2024-11-03"));
            schedulePanel.add(Box.createVerticalStrut(10));
        }

        // 스타일 적용된 스크롤바 리턴
        return createStyledScrollPane(schedulePanel);
    }
    
    private JScrollPane createStyledScrollPane(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBackground(Color.WHITE);

        // 스크롤바 색상 설정
        scrollPane.getVerticalScrollBar().setBackground(new Color(230, 230, 230));
        scrollPane.getHorizontalScrollBar().setBackground(new Color(230, 230, 230));

        // 스크롤바 UI 커스터마이징
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = Color.WHITE;
                this.trackColor = new Color(230, 230, 230);
            }
        });

        scrollPane.getHorizontalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = Color.WHITE;
                this.trackColor = new Color(230, 230, 230);
            }
        });

        return scrollPane;
    }

    // 일정 아이템 생성
    private JPanel createScheduleItem(String title, String memo, String startDate, String endDate) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        itemPanel.setPreferredSize(new Dimension(300, 100));
        itemPanel.setBackground(Color.WHITE);

        // 제목과 체크박스
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.WHITE);
        JCheckBox checkBox = new JCheckBox();
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        topPanel.add(checkBox);
        topPanel.add(titleLabel);

        // 날짜 표시
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        datePanel.setBackground(Color.WHITE);
        JLabel dateLabel = new JLabel("시작 날짜: " + startDate + " ~ 종료 날짜: " + endDate);
        datePanel.add(dateLabel);

        // 메모 표시와 토글 버튼
        JPanel memoPanel = new JPanel(new BorderLayout());
        memoPanel.setBackground(Color.WHITE);
        JLabel memoLabel = new JLabel("<html>" + memo.replace("\n", "<br>") + "</html>");
        JButton toggleButton = createStyledButton("자세히");
        memoLabel.setVisible(false); // 초기에는 숨김 상태
        toggleButton.addActionListener(e -> {
            memoLabel.setVisible(!memoLabel.isVisible());
            toggleButton.setText(memoLabel.isVisible() ? "닫기" : "자세히");
            itemPanel.revalidate();
            itemPanel.repaint();
        });

        memoPanel.add(memoLabel, BorderLayout.CENTER);
        memoPanel.add(toggleButton, BorderLayout.SOUTH);

        itemPanel.add(topPanel, BorderLayout.NORTH);
        itemPanel.add(datePanel, BorderLayout.CENTER);
        itemPanel.add(memoPanel, BorderLayout.SOUTH);

        return itemPanel;
    }

    // 날짜와 시간을 업데이트하는 메서드
    private void updateDateTime() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
        dateLabel.setText("현재 날짜: " + dateFormatter.format(new Date()));
        timeLabel.setText("현재 시간: " + timeFormatter.format(new Date()));
    }

    // 스타일 버튼 생성
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(34, 139, 34)); // 녹색
        button.setForeground(Color.WHITE); // 흰색 글씨
        button.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        button.setFocusPainted(false);
        return button;
    }
    
    private void openCalendarService() {
        CalendarService calendarService = new CalendarService();
        calendarService.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        calendarService.setVisible(true);
    }

    private void openAddPlan() {
        AddPlan addPlan = new AddPlan();
        addPlan.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addPlan.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CalendarApp::new);
    }
}
