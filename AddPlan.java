package Project;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class AddPlan extends JFrame {

    private DefaultListModel<Schedule> scheduleListModel;
    private JList<Schedule> scheduleList;
    private JTextField titleField;
    private JSpinner dateSpinner;
    private JButton colorButton;
    private JButton addButton;
    private JButton editButton;
    private boolean isEditMode = false; // 수정 모드 확인용
    private int editIndex = -1; // 수정할 일정 인덱스
    private Color selectedColor = Color.BLACK; // 선택한 색상
    private HashMap<Schedule, Color> scheduleColors = new HashMap<>(); // 일정과 색상 매핑

    public AddPlan() {
        setTitle("일정 관리");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 전체 레이아웃을 수평으로 나누기
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 왼쪽 패널 (오늘의 일정 목록)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout(10, 10));

        JLabel existingSchedulesLabel = new JLabel("오늘의 일정");
        existingSchedulesLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        leftPanel.add(existingSchedulesLabel, BorderLayout.NORTH);

        // 오늘의 일정 목록을 보여줄 스크롤 패널
        scheduleListModel = new DefaultListModel<>();
        scheduleListModel.addElement(new Schedule("회의", new Date(), Color.RED));
        scheduleListModel.addElement(new Schedule("운동", new Date(), Color.BLUE));

        scheduleList = new JList<>(scheduleListModel);
        scheduleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scheduleList.setCellRenderer(new ScheduleRenderer(scheduleColors)); // 일정 색상 렌더러 설정
        JScrollPane scrollPane = new JScrollPane(scheduleList);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        // 수정, 삭제 버튼
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        editButton = new JButton("수정하기");
        JButton deleteButton = new JButton("삭제하기");

        editButton.addActionListener(e -> {
            int selectedIndex = scheduleList.getSelectedIndex();
            if (selectedIndex != -1) {
                Schedule selectedSchedule = scheduleListModel.getElementAt(selectedIndex);
                enterEditMode(selectedSchedule, selectedIndex);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedIndex = scheduleList.getSelectedIndex();
            if (selectedIndex != -1) {
                scheduleColors.remove(scheduleListModel.get(selectedIndex)); // 색상 정보 제거
                scheduleListModel.remove(selectedIndex);
            }
        });

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 오른쪽 패널 (새 일정 추가/수정)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JLabel addScheduleLabel = new JLabel("새 일정 추가");
        addScheduleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        addScheduleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(addScheduleLabel);
        rightPanel.add(Box.createVerticalStrut(10));

        // 일정 색상 선택
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel colorLabel = new JLabel("일정 색상: ");
        colorButton = new JButton("색상 선택");
        colorButton.addActionListener(e -> {
            Color color = JColorChooser.showDialog(null, "색상 선택", selectedColor);
            if (color != null) {
                selectedColor = color;
                colorButton.setBackground(selectedColor);
            }
        });
        colorPanel.add(colorLabel);
        colorPanel.add(colorButton);
        rightPanel.add(colorPanel);

        // 날짜 선택 (JSpinner 사용)
        JPanel datePanel = new JPanel();
        datePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel dateLabel = new JLabel("날짜: ");

        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH);
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);

        datePanel.add(dateLabel);
        datePanel.add(dateSpinner);
        rightPanel.add(datePanel);

        // 일정 제목 입력
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("일정 제목: ");
        titleField = new JTextField(20);
        titlePanel.add(titleLabel);
        titlePanel.add(titleField);
        rightPanel.add(titlePanel);

        // 추가/수정 버튼
        addButton = new JButton("추가하기");
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.addActionListener(e -> {
            if (isEditMode) {
                // 수정 모드일 경우 일정 업데이트
                Schedule editedSchedule = new Schedule(titleField.getText(), (Date) dateSpinner.getValue(), selectedColor);
                scheduleListModel.set(editIndex, editedSchedule);
                scheduleColors.put(editedSchedule, selectedColor);
                exitEditMode();
            } else {
                // 추가 모드일 경우 일정 추가
                Schedule newSchedule = new Schedule(titleField.getText(), (Date) dateSpinner.getValue(), selectedColor);
                scheduleListModel.addElement(newSchedule);
                scheduleColors.put(newSchedule, selectedColor);
                resetFields();
            }
        });

        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(addButton);

        // 패널 추가
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        add(mainPanel);
        setVisible(true);
    }

    // 일정 추가/수정 후 필드 초기화
    private void resetFields() {
        titleField.setText("");
        dateSpinner.setValue(new Date());
        selectedColor = Color.BLACK;
        colorButton.setBackground(null);
        isEditMode = false;
        addButton.setText("추가하기");
        editButton.setEnabled(true);
    }

    // 수정 모드로 전환
    private void enterEditMode(Schedule schedule, int index) {
        titleField.setText(schedule.getTitle());
        dateSpinner.setValue(schedule.getDate());
        selectedColor = schedule.getColor();
        colorButton.setBackground(selectedColor);
        isEditMode = true;
        editIndex = index;
        addButton.setText("수정하기");
        editButton.setEnabled(false); // 수정 모드에서는 수정 버튼 비활성화
    }

    // 수정 모드 종료
    private void exitEditMode() {
        resetFields();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AddPlan::new);
    }

    // 일정 데이터를 나타내는 Schedule 클래스
    static class Schedule {
        private final String title;
        private final Date date;
        private final Color color;

        public Schedule(String title, Date date, Color color) {
            this.title = title;
            this.date = date;
            this.color = color;
        }

        public String getTitle() {
            return title;
        }

        public Date getDate() {
            return date;
        }

        public Color getColor() {
            return color;
        }

        @Override
        public String toString() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return title + " (" + sdf.format(date) + ")";
        }
    }

    // 일정에 색상 동그라미를 함께 표시하는 렌더러
    static class ScheduleRenderer extends DefaultListCellRenderer {
        private final HashMap<Schedule, Color> colorMap;

        public ScheduleRenderer(HashMap<Schedule, Color> colorMap) {
            this.colorMap = colorMap;
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Schedule) {
                Schedule schedule = (Schedule) value;
                setText(schedule.toString());

                // 아이콘으로 색상 동그라미 표시
                setIcon(new ColorIcon(colorMap.getOrDefault(schedule, Color.BLACK)));
            }
            return component;
        }
    }

    // 색상 동그라미 아이콘을 만드는 클래스
    static class ColorIcon implements Icon {
        private final Color color;

        public ColorIcon(Color color) {
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(color);
            g.fillOval(x, y, getIconWidth(), getIconHeight());
        }

        @Override
        public int getIconWidth() {
            return 10;
        }

        @Override
        public int getIconHeight() {
            return 10;
        }
    }
}
