package calendarservice;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
		holidayManager = new HolidayManager();  // 공휴일 관리자를 모든 연도로 초기화

		// 중앙에 캘린더 패널 추가
		CalendarPanel calendarPanel = new CalendarPanel();
		c.add(calendarPanel, BorderLayout.CENTER);

		// 서쪽 패널 생성 및 구성
		JPanel westPanel = new JPanel(new BorderLayout(5, 5));

		// 카테고리 패널
		JPanel categoryPanel = new JPanel();
		categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.Y_AXIS)); //수직
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


			prevButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					changeMonth(-1);
				}
			}); //뒤로 넘기는 경우
			nextButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					changeMonth(1);
				}
			}); //앞으로 넘기는 경우

			navPanel.add(prevButton, BorderLayout.WEST);
			navPanel.add(monthLabel, BorderLayout.CENTER);
			navPanel.add(nextButton, BorderLayout.EAST);
			add(navPanel, BorderLayout.NORTH);

			datePanel = new JPanel(new GridLayout(7, 7));
			addDaysOfWeek();
			addDates();
			add(datePanel, BorderLayout.CENTER); //캘린더 패널 center에 datepanel 추가
		}

		// 캘린더 월 바꾸는 메서드
		private void changeMonth(int delta) {
			month += delta;
			if (month < 0) { //month가 0보다 작을 경우
				month = 11; //월은 11이 된다. 
				year--; //년도는 감소
			} else if (month > 11) { //month가 11보다 클 경우
				month = 0; //month는 0이 된다.
				year++; //년도는 증가
			}
			updateMonthLabel();
			addDates();
			
			 // 이번 달과 이번 주 일정을 업데이트
	        updateMonthSchedulePanel();
		}

		//월 업데이트 메서드
		private void updateMonthLabel() {
			if (year == 0 && month == 0) { //년도가 0이고 월이 0인 경우
				Calendar cal = Calendar.getInstance(); //캘린더 내용 불러옴
				year = cal.get(Calendar.YEAR); //년도 불러오기
				month = cal.get(Calendar.MONTH); //월 불러오기
			}
			monthLabel.setText(year + "년 " + (month + 1) + "월"); //monthLabel에 년도와 월 출력
		}

		//요일 표시
		private void addDaysOfWeek() {
			String[] days = {"일", "월", "화", "수", "목", "금", "토"};
			Font dayLabelFont = new Font("Arial", Font.BOLD, 18);
			for (String day : days) {
				JLabel dayLabel = new JLabel(day, JLabel.CENTER);
				// dayLabel.setFont(dayLabelFont); // 요일 글씨 크기 설정
				datePanel.add(dayLabel);
			}
		}

		//날짜 추가 메서드
		private void addDates() {
		    datePanel.removeAll(); // 이전 데이터 제거
		    addDaysOfWeek(); // 요일 추가

		    Calendar cal = Calendar.getInstance();
		    cal.set(year, month, 1);

		    int startDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
		    int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		    Font dayFont = new Font("Arial", Font.BOLD, 20); // 날짜 버튼 폰트

		    for (int i = 0; i < startDay; i++) {
		        datePanel.add(new JLabel(""));
		    }

		    for (int day = 1; day <= daysInMonth; day++) {
		        int currentDay = day;
		        LocalDate currentDate = LocalDate.of(year, month + 1, currentDay);

		        // 일정 개수 확인
		        String dateKey = year + "/" + (month + 1) + "/" + currentDay;
		        int scheduleCount = countSchedulesForDate(dateKey);

		        // 버튼 텍스트 생성 (HTML 사용)
		        String buttonText = "<html><div style='text-align: center;'>" + currentDay;
		        String holidayName = holidayManager.getHolidayName(year, currentDate);
		        if (holidayName != null) {
		            buttonText += "<br><span style='font-size:10px; color:red;'>" + holidayName + "</span>";
		        }
		        if (scheduleCount > 0) {
		            buttonText += "<br><span style='font-size:10px; color:blue;'>일정 " + scheduleCount + "개</span>";
		        }
		        buttonText += "</div></html>";

		        JButton dayButton = new JButton(buttonText);
		        dayButton.setFont(dayFont);
		        dayButton.setContentAreaFilled(false);

		        // 오늘 날짜 강조
		        if (currentDate.equals(LocalDate.now())) {
		            dayButton.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
		        }

		        // 주말 및 공휴일 색상
		        if (holidayName != null || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
		            dayButton.setForeground(Color.RED);
		        } else {
		            dayButton.setForeground(Color.BLACK);
		        }

		        // 날짜 클릭 이벤트
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
		
		// 이번 달 일정을 업데이트하는 메서드
	    private void updateMonthSchedulePanel() {
	        JPanel monthSchedulePanel = new JPanel();
	        monthSchedulePanel.setLayout(new BoxLayout(monthSchedulePanel, BoxLayout.Y_AXIS));
	        LocalDate currentDate = LocalDate.of(year, month + 1, 1);

	        // 현재 달의 일정을 검색하여 패널에 추가
	        scheduleMap.forEach((date, schedule) -> {
	            LocalDate eventDate = LocalDate.parse(date);
	            if (eventDate.getYear() == currentDate.getYear() && eventDate.getMonthValue() == currentDate.getMonthValue()) {
	                monthSchedulePanel.add(new JLabel(date + " ➔ " + schedule));
	            }
	        });

	        tabbedPane.setComponentAt(0, monthSchedulePanel);
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

		// 일정 입력 창으로 연결하는 메서드
		private void openSchedule(String dateKey) {
			System.out.println("일정 입력 창을 호출합니다. 날짜: " + dateKey);
		}
		
		// 특정 날짜의 일정 개수를 반환
		private int countSchedulesForDate(String dateKey) {
		    int count = 0;
		    for (String key : scheduleMap.keySet()) {
		        if (key.equals(dateKey)) {
		            count++;
		        }
		    }
		    return count;
		}
	}

	public static void main(String[] args) {
		new CalendarService();
	}
}
