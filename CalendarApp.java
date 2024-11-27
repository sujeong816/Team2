package Project;

import Project.FileManager.Schedule;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CalendarApp extends JFrame {

    private JLabel dateLabel;
    private JLabel timeLabel;
    private JScrollPane[] schedulePanes = new JScrollPane[3];
    private int[] schedulePaneSize = new int[3];
    private List<JCheckBox> checkBoxes = new ArrayList<>();
    private JTabbedPane tabbedPane;
    private JComboBox<String> categoryComboBox;

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

        updateCategoryComboBox();

        controlPanel.add(new JLabel("카테고리:"));
        controlPanel.add(categoryComboBox);
        summaryPanel.add(controlPanel, BorderLayout.NORTH); // 요약 상단에 추가
        
        // 탭 패널 추가
        tabbedPane = new JTabbedPane();
        updateTabbedPane();
        
        updateSchedulePaneSize();

        summaryPanel.add(tabbedPane, BorderLayout.CENTER);
        add(summaryPanel, BorderLayout.CENTER);

        setVisible(true);
    }
    
    // categoryComboBox update
    private void updateCategoryComboBox() {
    	if(categoryComboBox == null)
    		categoryComboBox = new JComboBox<>();
    	
    	categoryComboBox.removeAllItems();
    	
        categoryComboBox.addItem("전체");
        // 기본 카테고리를 콤보박스에 추가
        categoryComboBox.addItem(FileManager.defaultCategory.getName());
        // 중복되지 않은 카테고리 이름 추가
        Set<String> uniqueCategoryNames = new HashSet<>();
        for (FileManager.Schedule schedule : FileManager.schedules) {
            if (schedule.getCategory() != null) {
                uniqueCategoryNames.add(schedule.getCategory().getName());
            }
        }
        for (String categoryName : uniqueCategoryNames) {
            if (!categoryName.equals(FileManager.defaultCategory.getName())) {
                categoryComboBox.addItem(categoryName);
            }
        }
        categoryComboBox.addActionListener(e -> {
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            System.out.println("Selected Category: " + selectedCategory); // 디버깅 메시지
            updateTabbedPane();
        });
    }
    
    // tabbedPane update
    private void updateTabbedPane() {
    	tabbedPane.removeAll();
        
        // 스케쥴 패널 생성
        for(int i=0; i<3; i++) {
        	schedulePaneSize[i] = 0;
        	schedulePanes[i] = createSchedulePanel(i);
        }
        
        JPanel schedulePanel1 = new JPanel(null);
        schedulePanel1.add(schedulePanes[0]);
        tabbedPane.addTab("오늘", schedulePanel1);
        JPanel schedulePanel2 = new JPanel(null);
        schedulePanel2.add(schedulePanes[1]);
        tabbedPane.addTab("이번 주", schedulePanel2);
        JPanel schedulePanel3 = new JPanel(null);
        schedulePanel3.add(schedulePanes[2]);
        tabbedPane.addTab("이번 달", schedulePanel3);
        
        updateSchedulePaneSize();
    }

    // 일정 패널 생성
    private JScrollPane createSchedulePanel(int flag) {
    	JPanel schedulePanel = new JPanel();
    	schedulePanel.setLayout(new BoxLayout(schedulePanel, BoxLayout.Y_AXIS));
        schedulePanel.setBackground(Color.WHITE);
        
        List<Schedule> searched = new ArrayList<>();
        switch(flag) {
        case 0:
        	searched = FileManager.getScheduleToday(LocalDate.now());
        	break;
        case 1:
        	searched = FileManager.getScheduleWeek(LocalDate.now());
        	break;
        case 2:
        	searched = FileManager.getScheduleMonth(LocalDate.now());
        }
        
        searched.sort(Comparator.comparing((FileManager.Schedule schedule) -> schedule.getStartDate().toLocalDate())
        		.thenComparing(schedule -> schedule.getEndDate().toLocalDate()));
        
        for (Schedule schedule : searched) {
        	if(categoryComboBox.getSelectedIndex() != 0)
        		if(!(schedule.getCategory().getName().equals(categoryComboBox.getSelectedItem())))
        				continue;
            schedulePanel.add(createScheduleItem(schedule, flag));
            schedulePanel.add(Box.createVerticalStrut(5));
        }
        
        // 스타일 적용된 스크롤바 리턴
        return createStyledScrollPane(schedulePanel);
    }
    
    private JScrollPane createStyledScrollPane(Component component) {JScrollPane scrollPane = new JScrollPane(component);
    	scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setSize(824, 480);
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

        return scrollPane;
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

    // 일정 아이템 생성
    private JPanel createScheduleItem(Schedule schedule, int flag) {
    	schedulePaneSize[flag] += 55;
    	
    	JPanel returnPanel = new JPanel(null);
    	returnPanel.setPreferredSize(new Dimension(810, 50));
    	
    	// schedule.setContent("asd\nqwe\nzxc");
    	// schedule.setContent(schedule.getContent() + "\nasd\nqwe\nzxc\nrty\nfgh");
    	
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        itemPanel.setSize(800, 50);
        itemPanel.setBackground(Color.WHITE);

        // 제목과 체크박스
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.WHITE);
        JLabel lbIsDone = new JLabel("완료됨");
        lbIsDone.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(schedule.getIsDone());
        // ActionListener를 update로 사용
        checkBox.addActionListener(e -> {
        	checkBox.setSelected(schedule.getIsDone());
        });
        checkBox.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
            	schedule.setIsDone(true);
            } else {
            	schedule.setIsDone(false);
            }
            updateCheckBoxes();
            FileManager.SaveAllData();
        });
        checkBoxes.add(checkBox);
        JLabel titleLabel = new JLabel(schedule.getTitle());
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        topPanel.add(lbIsDone);
        topPanel.add(checkBox);
        topPanel.add(titleLabel);

        // 날짜 표시
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        datePanel.setBackground(Color.WHITE);
        JLabel dateLabel = new JLabel("시작 날짜: " + schedule.getStartDate().toString() + " ~ 종료 날짜: " + schedule.getEndDate().toString());
        datePanel.add(dateLabel);

        // 메모 표시와 토글 버튼
        String contentStr = schedule.getContent().replace("\n", "<br>");
        JLabel contentLabel = new JLabel(String.format("<html><div style='text-align: center;'> %s </div></html>", contentStr));
        JButton toggleButton = createStyledButton("메모 표시");
        toggleButton.setBounds(370, 30, 100, 20);
        contentLabel.setVisible(false); // 초기에는 숨김 상태
        int lineCount = schedule.getContent().split("\n").length;
        toggleButton.addActionListener(e -> {
            contentLabel.setVisible(!contentLabel.isVisible());
            toggleButton.setText(contentLabel.isVisible() ? "닫기" : "메모 표시");
            returnPanel.setPreferredSize(new Dimension(810, contentLabel.isVisible() ? (50 + 20 * lineCount) : 50));
            itemPanel.setSize(800, contentLabel.isVisible() ? (50 + 20 * lineCount) : 50);
            schedulePaneSize[flag] += contentLabel.isVisible() ? (20 * lineCount) : (-20 * lineCount);
            updateSchedulePaneSize();
            itemPanel.revalidate();
            itemPanel.repaint();
        });

        itemPanel.add(topPanel, BorderLayout.WEST);
        itemPanel.add(datePanel, BorderLayout.EAST);
        itemPanel.add(contentLabel, BorderLayout.SOUTH);
        
        returnPanel.add(toggleButton);
        returnPanel.add(itemPanel);

        return returnPanel;
    }

    // 날짜와 시간을 업데이트하는 메서드
    private void updateDateTime() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
        dateLabel.setText("현재 날짜: " + dateFormatter.format(new Date()));
        timeLabel.setText("현재 시간: " + timeFormatter.format(new Date()));
    }
    
    // 스케쥴 패널 크기 조정
    private void updateSchedulePaneSize() {
    	for(int i=0; i<3; i++) {
    		schedulePanes[i].setSize(820, schedulePaneSize[i] > 480 ? 480 : schedulePaneSize[i]);
    	}
    }
    
    // 다른 패널에 있는 페크박스 표시 상태 동기화
    private void updateCheckBoxes() {
    	for(JCheckBox checkBox : checkBoxes) {
    		checkBox.getActionListeners()[0].actionPerformed(null);
    	}
    }
    
    private void openCalendarService() {
        dispose(); // 현재 창 닫기
        SwingUtilities.invokeLater(() -> new CalendarService().setVisible(true));
    }

    private void openAddPlan() {
    	AddPlan addPlan = new AddPlan();
        
        // AddPlan 창에 WindowListener 추가
        addPlan.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                // AddPlan 창이 닫힐 때 호출
            	updateCategoryComboBox();
            	updateTabbedPane();
            }
        });

        addPlan.setVisible(true);
    }

    public static void main(String[] args) {
    	FileManager.LoadSaveData();
        SwingUtilities.invokeLater(CalendarApp::new);
    }
}