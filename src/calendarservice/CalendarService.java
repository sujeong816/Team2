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
		holidayManager = new HolidayManager(LocalDate.now().getYear());  // 현재 연도의 공휴일 관리

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
			for (String day : days) {
				JLabel dayLabel = new JLabel(day, JLabel.CENTER);
				datePanel.add(dayLabel);
			}
		}

		//날짜 추가 메서드
		private void addDates() {
			datePanel.removeAll(); //매달 새롭게 날짜를 추가하기 위해서 초기화
			addDaysOfWeek(); //요일 표시

			Calendar cal = Calendar.getInstance(); //Calendar 객체를 생성하여 현재 연도와 월로 초기화
			cal.set(year, month, 1); //현재 월의 첫번째 날로 설정

			int startDay = cal.get(Calendar.DAY_OF_WEEK) - 1; //현재 달의 첫번째 요일을 가져옴(인덱스에 맞추기 위하여 1을 빼줌)
			int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH); //현재 월의 마지막 날짜를 가져옴

			// 빈칸 추가 (해당 월 시작 요일 전까지 빈 공간 채우기)
			for (int i = 0; i < startDay; i++) {
				datePanel.add(new JLabel(""));
			}

			// 해당 월의 각 날짜에 대해 버튼을 추가
			for (int day = 1; day <= daysInMonth; day++) {
				int currentDay = day;
				LocalDate currentDate = LocalDate.of(year, month + 1, currentDay); //지정된 연도, 월, 일로 LocalDate 객체를 생성
				cal.set(Calendar.DAY_OF_MONTH, day);  // 현재 날짜 설정

				JButton dayButton = new JButton(String.valueOf(currentDay)); //currentDay를 문자열로 바꿔서 dayButton에 내용에 출력

				// 공휴일 또는 주말 여부에 따라 스타일 변경
				if (holidayManager.isHoliday(currentDate) || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
					dayButton.setForeground(Color.RED); // 공휴일 및 주말을 빨간색으로 표시
				} else {
					dayButton.setForeground(Color.BLACK);
				}

				// 기본 스타일 설정
				dayButton.setContentAreaFilled(false);
				dayButton.setBorderPainted(false);
				dayButton.setFocusPainted(false);
				dayButton.setHorizontalAlignment(JButton.CENTER);

				// 날짜 버튼 추가
				dayButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						showScheduleDialog(year, month + 1, currentDay); // 클릭 시 실행할 메서드 호출
					}
				});
				datePanel.add(dayButton); // 패널에 버튼 추가
			}

			// 나머지 빈칸 추가 (해당 월의 마지막 날짜 이후 빈 공간 채우기)
			int totalCells = 42;
			for (int i = startDay + daysInMonth; i < totalCells; i++) {
				datePanel.add(new JLabel(""));
			}

			datePanel.revalidate();
			datePanel.repaint();
		}

		// 선택한 날짜의 저장된 일정을 보여주는 다이얼로그 창
		private void showScheduleDialog(int year, int month, int day) {
			String dateKey = year + "/" + month + "/" + day;
			String schedule = scheduleMap.getOrDefault(dateKey, "일정 없음");

			JDialog dialog = new JDialog(CalendarService.this, "일정: " + dateKey, true);
			dialog.setSize(300, 200);
			dialog.setLayout(new BorderLayout());

			JLabel dateLabel = new JLabel("일정 보기: " + dateKey, JLabel.CENTER);
			JTextArea scheduleArea = new JTextArea(schedule);
			scheduleArea.setEditable(false); // 읽기 전용으로 설정

			// 일정 추가 버튼
			JButton addButton = new JButton("일정 추가");
			addButton.addActionListener(new ActionListener() {
			    @Override
			    public void actionPerformed(ActionEvent e) {
			        openScheduleInputDialog(dateKey); // 클릭 시 실행할 메서드 호출
			    }
			});

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
	}

	public static void main(String[] args) {
		new CalendarService();
	}
}
