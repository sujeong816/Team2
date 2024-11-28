package Project;

import java.awt.*;
import java.time.DayOfWeek;
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
        westPanel.setBackground(Color.WHITE); // 왼쪽 전체 배경 흰색
        westPanel.setPreferredSize(new Dimension(200, 0)); // 왼쪽 패널 너비 설정

        // 카테고리 패널 구성
        JPanel categoryPanel = new JPanel();
        categoryPanel.setBackground(Color.WHITE); // 카테고리 패널 배경 흰색
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
        buttonPanel.setBackground(Color.WHITE); // 버튼 패널 배경 흰색

        JButton mainButton = new JButton("메인화면"); // 메인화면 이동 버튼
        mainButton.setPreferredSize(new Dimension(190, 30)); // 버튼 크기 설정
        mainButton.setBackground(new Color(34, 139, 34)); // 녹색
        mainButton.setForeground(Color.WHITE); // 흰색 글씨
        mainButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        mainButton.setFocusPainted(false);
        mainButton.addActionListener(e -> {
            System.out.println("메인화면으로 이동합니다."); // 디버깅 메시지
            dispose(); // 현재 창 닫기
            SwingUtilities.invokeLater(() -> new CalendarApp().setVisible(true)); // 메인화면 실행
        });

        // 버튼 패널에 메인 버튼 추가
        buttonPanel.add(mainButton);

        // 왼쪽 패널에 일정 탭 및 버튼 패널 추가
        JPanel westPanelContent = new JPanel(new BorderLayout());
        westPanelContent.setBackground(Color.WHITE); // 왼쪽 패널 콘텐츠 배경 흰색
        westPanelContent.add(tabbedPane, BorderLayout.CENTER); // 일정 탭 추가
        westPanelContent.add(buttonPanel, BorderLayout.SOUTH); // 버튼 패널 추가

        westPanel.add(categoryPanel, BorderLayout.NORTH); // 카테고리 패널 추가
        westPanel.add(westPanelContent, BorderLayout.CENTER); // 탭과 버튼 패널 추가

        c.add(westPanel, BorderLayout.WEST); // 왼쪽 패널 메인 컨테이너에 추가

        setSize(1000, 600); // 창 크기 설정
        setLocationRelativeTo(null); // 창을 화면 중앙에 위치
        setVisible(true); // 창 보이기

        // 기본 카테고리로 캘린더 초기화
        calendarPanel.updateDatesWithCategory(FileManager.defaultCategory.getName());
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

    private void updateMonthSchedulePanel(JPanel panel) {
        panel.removeAll(); // 기존 데이터 제거
        panel.setBackground(Color.WHITE); // 이번달 일정 탭 배경 흰색

        // 현재 선택된 연도와 달 가져오기
        int selectedYear = calendarPanel.year;
        int selectedMonth = calendarPanel.month + 1; // 0부터 시작하므로 +1 필요
        LocalDate selectedDate = LocalDate.of(selectedYear, selectedMonth, 1);

        // FileManager의 getScheduleMonth 함수 호출
        List<FileManager.Schedule> schedules = FileManager.getScheduleMonth(selectedDate);

        // 일정 데이터를 시작 날짜 기준으로 정렬, 동일한 시작 날짜일 경우 종료 날짜 기준으로 정렬
        schedules.sort(Comparator.comparing((FileManager.Schedule schedule) -> schedule.getStartDate().toLocalDate())
                                 .thenComparing(schedule -> schedule.getEndDate().toLocalDate()));

        // 일정 내용을 담을 패널 생성
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // 세로 방향 레이아웃
        contentPanel.setBackground(Color.WHITE); // 일정 내용 패널 배경 흰색

        // 가져온 일정 데이터를 contentPanel에 추가
        for (FileManager.Schedule schedule : schedules) {
            JTextArea dayArea = new JTextArea();
            StringBuilder text = new StringBuilder();

            // 일정 정보 출력
            text.append(schedule.getStartDate().toLocalDate())
                .append(" ~ ")
                .append(schedule.getEndDate().toLocalDate())
                .append("\n");
            text.append("[").append(schedule.getCategory().getName()).append("] ").append(schedule.getTitle()).append("\n");
            text.append(schedule.getContent()).append("\n");

            dayArea.setText(text.toString());
            dayArea.setEditable(false); // 텍스트 수정 불가
            dayArea.setLineWrap(true); // 줄바꿈 허용
            dayArea.setWrapStyleWord(true); // 단어 단위로 줄바꿈
            dayArea.setBorder(BorderFactory.createLineBorder(schedule.getCategory().getColor(), 2)); // 카테고리 색상으로 테두리 설정
            dayArea.setBackground(panel.getBackground()); // 패널 배경색과 동일하게 설정

            contentPanel.add(dayArea); // contentPanel에 일정 추가
        }

        // 스크롤 기능 추가
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // 필요 시 세로 스크롤
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // 가로 스크롤 비활성화

        panel.setLayout(new BorderLayout()); // 스크롤 패널 추가를 위한 레이아웃 설정
        panel.add(scrollPane, BorderLayout.CENTER); // 스크롤 패널을 중심에 배치

        panel.revalidate(); // 패널 레이아웃 재검토
        panel.repaint(); // 화면 갱신
    }

    // 카테고리 콤보박스 업데이트
    private void updateCategoryComboBox() {
        categoryComboBox.removeAllItems(); // 기존 항목 제거

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

        // 기본 카테고리를 선택
        categoryComboBox.setSelectedItem(FileManager.defaultCategory.getName());

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
            setBackground(Color.WHITE); // CalendarPanel 전체 배경 흰색

            JPanel navPanel = new JPanel();
            navPanel.setBackground(Color.WHITE); // 내비게이션 패널 배경 흰색
            JButton prevButton = new JButton("<");
            JButton nextButton = new JButton(">");
            prevButton.setBackground(new Color(34, 139, 34)); // 녹색
            prevButton.setForeground(Color.WHITE); // 흰색 글씨
            prevButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
            prevButton.setFocusPainted(false);
            nextButton.setBackground(new Color(34, 139, 34)); // 녹색
            nextButton.setForeground(Color.WHITE); // 흰색 글씨
            nextButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
            nextButton.setFocusPainted(false);
            
            monthLabel = new JLabel("", JLabel.CENTER);
            monthLabel.setBackground(Color.WHITE); // 월 표시 레이블 배경 흰색
            updateMonthLabel();

            prevButton.addActionListener(e -> changeMonth(-1)); // 이전 달로 이동
            nextButton.addActionListener(e -> changeMonth(1)); // 다음 달로 이동

            navPanel.add(prevButton, BorderLayout.WEST);
            navPanel.add(monthLabel, BorderLayout.CENTER);
            navPanel.add(nextButton, BorderLayout.EAST);
            add(navPanel, BorderLayout.NORTH);

            datePanel = new JPanel(new GridLayout(7, 7)); // 7x7 달력 그리드
            datePanel.setBackground(Color.WHITE); // 날짜 패널 배경 흰색
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

            // 카테고리를 기본 카테고리로 리셋
            categoryComboBox.setSelectedItem(FileManager.defaultCategory.getName());
            updateDatesWithCategory(FileManager.defaultCategory.getName());

            // 일정 패널 업데이트
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

            Font dayFont = new Font("맑은 고딕", Font.BOLD, 20); // 폰트 설정
            Map<LocalDate, String> holidays = holidayManager.getHolidays(year); // 공휴일 정보 가져오기

            for (int i = 0; i < startDay; i++) {
                datePanel.add(new JLabel("")); // 빈 칸 추가
            }

            for (int day = 1; day <= daysInMonth; day++) {
                LocalDate currentDate = LocalDate.of(year, month + 1, day);

                // 공휴일 이름 가져오기
                String holidayName = holidays.get(currentDate);

                // 버튼 텍스트 설정 (HTML로 다중 텍스트 구성)
                StringBuilder buttonText = new StringBuilder("<html><div style='text-align: center;'>");
                buttonText.append("<b>").append(day).append("</b>"); // 날짜 추가

                if (holidayName != null) {
                    buttonText.append("<br><span style='font-size:10px; color:red;'>")
                              .append(holidayName).append("</span>"); // 공휴일 이름 추가
                }

                buttonText.append("</div></html>");

                JButton dayButton = new JButton(buttonText.toString());
                dayButton.setFont(dayFont);

                // 기본 글자 색상 처리
                dayButton.setForeground(Color.BLACK);

                // 주말 처리
                DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
                if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                    dayButton.setForeground(Color.RED); // 주말 글자 색상
                }

                // 기본 배경색 설정
                dayButton.setBackground(Color.WHITE);

                // 오늘 날짜 강조
                if (currentDate.equals(LocalDate.now())) {
                    dayButton.setBorder(BorderFactory.createLineBorder(new Color(34, 139, 34), 5));
                }

                // 날짜 속성을 버튼에 저장
                dayButton.putClientProperty("date", currentDate);

                // 버튼 클릭 이벤트 추가
                dayButton.addActionListener(e -> {
                    AddPlan addPlan = new AddPlan(currentDate);
                    
                    // AddPlan 창에 WindowListener 추가
                    addPlan.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosed(java.awt.event.WindowEvent e) {
                            // AddPlan 창이 닫힐 때 호출
                            updateUIComponents();
                        }
                    });

                    addPlan.setVisible(true);
                });

                datePanel.add(dayButton); // 버튼을 패널에 추가
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }

            // 빈 칸 채우기
            for (int i = startDay + daysInMonth; i < 42; i++) {
                datePanel.add(new JLabel(""));
            }

            datePanel.revalidate();
            datePanel.repaint();
        }
        
        public void updateUIComponents() {
            // 카테고리 콤보박스 갱신
            updateCategoryComboBox();

            // 현재 선택된 카테고리 가져오기
            String selectedCategory = (String) categoryComboBox.getSelectedItem();

            // 캘린더 화면 갱신
            calendarPanel.updateDatesWithCategory(selectedCategory);

            // 일정 탭 갱신
            updateScheduleTab();
        }

        
        public void updateDatesWithCategory(String categoryName) {
            for (Component comp : datePanel.getComponents()) {
                if (comp instanceof JButton button) {
                    LocalDate buttonDate = (LocalDate) button.getClientProperty("date");
                    if (buttonDate != null) {
                        // 1. 기본 배경색과 글자색 초기화
                        button.setBackground(Color.WHITE); // 기본 배경색
                        button.setForeground(Color.BLACK); // 기본 글자색

                        // 2. 공휴일 글자 색상 처리
                        String holidayName = holidayManager.getHolidays(year).get(buttonDate);
                        if (holidayName != null) {
                            button.setForeground(Color.RED); // 공휴일 글자 색상
                        }

                        // 3. 주말 글자 색상 처리
                        DayOfWeek dayOfWeek = buttonDate.getDayOfWeek();
                        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                            button.setForeground(Color.RED); // 주말 글자 색상
                        }

                        // 4. 카테고리 배경색 처리
                        if (categoryName != null) {
                            for (FileManager.Schedule schedule : FileManager.schedules) {
                                if (schedule.getCategory() != null &&
                                    schedule.getCategory().getName().equals(categoryName) &&
                                    !schedule.getStartDate().toLocalDate().isAfter(buttonDate) &&
                                    !schedule.getEndDate().toLocalDate().isBefore(buttonDate)) {
                                    button.setBackground(schedule.getCategory().getColor()); // 카테고리 색상 설정
                                    break; // 첫 번째로 일치하는 카테고리만 적용
                                }
                            }
                        }
                    }
                }
            }

            datePanel.repaint(); // 화면 갱신
        }
    }

    public static void main(String[] args) {
        new CalendarService(); // 메인 실행
    }
}