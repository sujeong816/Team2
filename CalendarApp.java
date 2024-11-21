package project1;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.Timer;

public class CalendarApp extends JFrame {

    private JLabel dateLabel;
    private JLabel timeLabel;

    public CalendarApp() {
        setTitle("일정 관리 앱");
        setSize(1000, 600);  // 창 크기를 1000x600으로 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 왼쪽 패널 - 버튼을 담는 패널
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(150, 600));

        // 캘린더 보기 버튼
        JButton calendarViewButton = new JButton("캘린더 보기");
        calendarViewButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        calendarViewButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "캘린더 구현"));

        // 일정 추가 버튼
        JButton addScheduleButton = new JButton("일정 추가");
        addScheduleButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addScheduleButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "일정 추가 기능 구현"));

        // 현재 날짜와 시간을 표시하는 라벨
        dateLabel = new JLabel();
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeLabel = new JLabel();
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateDateTime();

        // 날짜와 시간 업데이트 타이머 (1초마다)
        Timer timer = new Timer(1000, e -> updateDateTime());
        timer.start();

        // 왼쪽 패널에 버튼과 라벨 추가
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(calendarViewButton);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(addScheduleButton);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(dateLabel);
        leftPanel.add(timeLabel);
        add(leftPanel, BorderLayout.WEST);

        // 중앙 패널 - 일정 요약을 표시하는 패널
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("일정 요약"));

        // 카테고리 선택 및 완료 체크박스 패널 추가
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> categoryComboBox = new JComboBox<>(new String[]{"전체", "카테고리1", "카테고리2"});
        JCheckBox completeCheckBox = new JCheckBox("완료");

        controlPanel.add(new JLabel("카테고리:"));
        controlPanel.add(categoryComboBox);
        controlPanel.add(completeCheckBox);
        summaryPanel.add(controlPanel, BorderLayout.NORTH); // 상단에 추가

        // 탭 패널 생성
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("오늘", createSchedulePanel());
        tabbedPane.addTab("이번주", createSchedulePanel());
        tabbedPane.addTab("이번달", createSchedulePanel());

        summaryPanel.add(tabbedPane, BorderLayout.CENTER);
        add(summaryPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // 일정 패널 생성 메서드
    private JScrollPane createSchedulePanel() {
        JPanel schedulePanel = new JPanel();
        schedulePanel.setLayout(new BoxLayout(schedulePanel, BoxLayout.Y_AXIS));

        // 예제 일정 추가
        for (int i = 0; i < 10; i++) { // 예제 일정 10개 추가
            schedulePanel.add(createScheduleItem("제목 " + (i + 1), "메모 내용 " + (i + 1) + "\n추가 내용 줄바꿈 예시", 
                                                 "2024-11-01", "2024-11-03"));
            schedulePanel.add(Box.createVerticalStrut(10)); // 일정 간 간격
        }

        // 스크롤 가능 패널로 감싸기
        JScrollPane scrollPane = new JScrollPane(schedulePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        return scrollPane;
    }

    // 개별 일정 패널 생성 메서드
    private JPanel createScheduleItem(String title, String memo, String startDate, String endDate) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        itemPanel.setPreferredSize(new Dimension(300, 100));

        // 제목과 체크박스 패널
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCheckBox checkBox = new JCheckBox();
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 16));
        topPanel.add(checkBox);
        topPanel.add(titleLabel);

        // 날짜 표시
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel dateLabel = new JLabel("저장 날짜: " + startDate + " ~ 종료 날짜: " + endDate);
        datePanel.add(dateLabel);

        // 메모 내용과 토글 버튼 패널
        JPanel memoPanel = new JPanel(new BorderLayout());
        JLabel memoLabel = new JLabel("<html>" + memo.replace("\n", "<br>") + "</html>");
        JButton toggleButton = new JButton("펼치기");
        memoLabel.setVisible(false); // 초기 상태는 접힘
        toggleButton.addActionListener(e -> {
            memoLabel.setVisible(!memoLabel.isVisible());
            toggleButton.setText(memoLabel.isVisible() ? "접기" : "펼치기");
            itemPanel.revalidate(); // 레이아웃 갱신
            itemPanel.repaint();
        });

        memoPanel.add(memoLabel, BorderLayout.CENTER);
        memoPanel.add(toggleButton, BorderLayout.SOUTH);

        itemPanel.add(topPanel, BorderLayout.NORTH);
        itemPanel.add(datePanel, BorderLayout.CENTER);
        itemPanel.add(memoPanel, BorderLayout.SOUTH);

        return itemPanel;
    }

    // 현재 날짜와 시간을 업데이트하는 메소드
    private void updateDateTime() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
        dateLabel.setText("오늘 날짜: " + dateFormatter.format(new Date()));
        timeLabel.setText("현재 시간: " + timeFormatter.format(new Date()));
    }

    public static void main(String[] args) {
        new CalendarApp();
    }
}
