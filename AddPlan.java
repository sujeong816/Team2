package Project;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class AddPlan extends JFrame {

    private DefaultListModel<Schedule> scheduleListModel;
    private JList<Schedule> scheduleList;
    private JTextField titleField;
    private JTextArea memoField;
    private JSpinner dateSpinner;
    private JSpinner startTimeSpinner;
    private JSpinner endTimeSpinner;
    private JButton categoryButton;
    private JButton addButton;
    private JButton editButton;
    private boolean isEditMode = false;
    private int editIndex = -1;
    private Category selectedCategory = new Category("강의", Color.BLACK);
    private List<Category> categories = new ArrayList<>(Arrays.asList(
            new Category("회의", Color.RED),
            new Category("운동", Color.BLUE),
            new Category("강의", Color.BLACK)
    ));

    public AddPlan() {
        setTitle("일정 관리");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout(10, 10));

        JLabel existingSchedulesLabel = new JLabel("오늘의 일정");
        existingSchedulesLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        leftPanel.add(existingSchedulesLabel, BorderLayout.NORTH);

        scheduleListModel = new DefaultListModel<>();
        scheduleListModel.addElement(new Schedule("회의", new Date(), new Date(), new Date(), "중요 회의", new Category("회의", Color.RED)));
        scheduleListModel.addElement(new Schedule("운동", new Date(), new Date(), new Date(), "운동 일정", new Category("운동", Color.BLUE)));

        scheduleList = new JList<>(scheduleListModel);
        scheduleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scheduleList.setCellRenderer(new ScheduleRenderer());
        JScrollPane scrollPane = new JScrollPane(scheduleList);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        editButton = createStyledButton("수정하기");
        JButton deleteButton = createStyledButton("삭제하기");

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
                scheduleListModel.remove(selectedIndex);
            }
        });

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JLabel addScheduleLabel = new JLabel("새 일정 추가");
        addScheduleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        addScheduleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(addScheduleLabel);
        rightPanel.add(Box.createVerticalStrut(10));

        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel categoryLabel = new JLabel("카테고리: ");
        categoryButton = createStyledButton("카테고리 선택");
        categoryButton.addActionListener(e -> showCategoryDialog());
        categoryPanel.add(categoryLabel);
        categoryPanel.add(categoryButton);
        rightPanel.add(categoryPanel);

        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel dateLabel = new JLabel("날짜: ");
        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH);
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);

        JLabel startTimeLabel = new JLabel("시작 시간: ");
        SpinnerDateModel startTimeModel = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.HOUR_OF_DAY);
        startTimeSpinner = new JSpinner(startTimeModel);
        JSpinner.DateEditor startTimeEditor = new JSpinner.DateEditor(startTimeSpinner, "HH:mm");
        startTimeSpinner.setEditor(startTimeEditor);

        JLabel endTimeLabel = new JLabel("종료 시간: ");
        SpinnerDateModel endTimeModel = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.HOUR_OF_DAY);
        endTimeSpinner = new JSpinner(endTimeModel);
        JSpinner.DateEditor endTimeEditor = new JSpinner.DateEditor(endTimeSpinner, "HH:mm");
        endTimeSpinner.setEditor(endTimeEditor);

        datePanel.add(dateLabel);
        datePanel.add(dateSpinner);
        datePanel.add(startTimeLabel);
        datePanel.add(startTimeSpinner);
        datePanel.add(endTimeLabel);
        datePanel.add(endTimeSpinner);
        rightPanel.add(datePanel);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("일정 제목: ");
        titleField = createStyledTextField(20);
        titlePanel.add(titleLabel);
        titlePanel.add(titleField);
        rightPanel.add(titlePanel);

        JPanel memoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel memoLabel = new JLabel("일정 메모: ");
        memoField = new JTextArea(4, 20);
        memoField.setLineWrap(true);
        memoField.setWrapStyleWord(true);
        memoField.setBackground(new Color(245, 245, 245));
        JScrollPane memoScrollPane = new JScrollPane(memoField);
        memoPanel.add(memoLabel);
        memoPanel.add(memoScrollPane);
        rightPanel.add(memoPanel);

        addButton = createStyledButton("추가하기");
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.addActionListener(e -> {
            Date selectedDate = (Date) dateSpinner.getValue();
            Date startTime = combineDateAndTime(selectedDate, (Date) startTimeSpinner.getValue());
            Date endTime = combineDateAndTime(selectedDate, (Date) endTimeSpinner.getValue());

            if (startTime.after(endTime)) {
                JOptionPane.showMessageDialog(this, "시작 시간이 종료 시간보다 빠를 수 없습니다.", "시간 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (isEditMode) {
                Schedule editedSchedule = new Schedule(
                        titleField.getText(),
                        selectedDate,
                        startTime,
                        endTime,
                        memoField.getText(),
                        selectedCategory
                );
                scheduleListModel.set(editIndex, editedSchedule);
                sortSchedules();
                exitEditMode();
            } else {
                Schedule newSchedule = new Schedule(
                        titleField.getText(),
                        selectedDate,
                        startTime,
                        endTime,
                        memoField.getText(),
                        selectedCategory
                );
                scheduleListModel.addElement(newSchedule);
                sortSchedules();
                resetFields();
            }
        });

        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(addButton);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        add(mainPanel);
        setVisible(true);
    }

    private Date combineDateAndTime(Date date, Date time) {
        Calendar calendarDate = Calendar.getInstance();
        calendarDate.setTime(date);

        Calendar calendarTime = Calendar.getInstance();
        calendarTime.setTime(time);

        calendarDate.set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY));
        calendarDate.set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE));
        calendarDate.set(Calendar.SECOND, 0);
        calendarDate.set(Calendar.MILLISECOND, 0);

        return calendarDate.getTime();
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(34, 139, 34)); // 초록색
        button.setForeground(Color.WHITE); // 흰색 글씨
        button.setFocusPainted(false);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        button.setUI(new BasicButtonUI());
        button.setBorder(BorderFactory.createLineBorder(new Color(34, 139, 34), 2));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        return button;
    }

    private JTextField createStyledTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setBackground(new Color(245, 245, 245)); // 회색 빛 도는 흰색
        textField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return textField;
    }

    private void showCategoryDialog() {
        CategoryDialog dialog = new CategoryDialog(this, categories, category -> {
            selectedCategory = category;
            categoryButton.setText("카테고리: " + selectedCategory.getName());
            categoryButton.setBackground(selectedCategory.getColor());
        });
        dialog.setVisible(true);
    }

    private void resetFields() {
        titleField.setText("");
        memoField.setText("");
        dateSpinner.setValue(new Date());
        startTimeSpinner.setValue(new Date());
        endTimeSpinner.setValue(new Date());
        selectedCategory = new Category("기본", Color.BLACK);
        categoryButton.setText("카테고리 선택");
        categoryButton.setBackground(null);
        isEditMode = false;
        addButton.setText("추가하기");
        editButton.setEnabled(true);
    }

    private void enterEditMode(Schedule schedule, int index) {
        titleField.setText(schedule.getTitle());
        memoField.setText(schedule.getMemo());
        dateSpinner.setValue(schedule.getDate());
        startTimeSpinner.setValue(schedule.getStartTime());
        endTimeSpinner.setValue(schedule.getEndTime());
        selectedCategory = schedule.getCategory();
        categoryButton.setText("카테고리: " + selectedCategory.getName());
        categoryButton.setBackground(selectedCategory.getColor());
        isEditMode = true;
        editIndex = index;
        addButton.setText("수정하기");
        editButton.setEnabled(false);
    }

    private void sortSchedules() {
        List<Schedule> sortedSchedules = Collections.list(scheduleListModel.elements());
        sortedSchedules.sort(Comparator.comparing(Schedule::getStartTime));
        scheduleListModel.clear();
        sortedSchedules.forEach(scheduleListModel::addElement);
    }

    private void exitEditMode() {
        resetFields();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AddPlan::new);
    }

    static class Schedule {
        private final String title;
        private final Date date;
        private final Date startTime;
        private final Date endTime;
        private final String memo;
        private final Category category;

        public Schedule(String title, Date date, Date startTime, Date endTime, String memo, Category category) {
            this.title = title;
            this.date = date;
            this.startTime = startTime;
            this.endTime = endTime;
            this.memo = memo;
            this.category = category;
        }

        public String getTitle() { return title; }
        public Date getDate() { return date; }
        public Date getStartTime() { return startTime; }
        public Date getEndTime() { return endTime; }
        public String getMemo() { return memo; }
        public Category getCategory() { return category; }

        @Override
        public String toString() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return title + " (" + sdf.format(date) + ", " + sdf.format(startTime) + " ~ " + sdf.format(endTime) + ")";
        }
    }

    static class Category {
        private final String name;
        private final Color color;

        public Category(String name, Color color) {
            this.name = name;
            this.color = color;
        }

        public String getName() { return name; }
        public Color getColor() { return color; }
        
        @Override
        public String toString() {
            return name;
        }
    }

    static class ScheduleRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Schedule) {
                Schedule schedule = (Schedule) value;
                setText(schedule.toString());
                setIcon(new ColorIcon(schedule.getCategory().getColor()));
            }
            return component;
        }
    }

    static class ColorIcon implements Icon {
        private final Color color;

        public ColorIcon(Color color) { this.color = color; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(color);
            g.fillOval(x, y, getIconWidth(), getIconHeight());
        }

        @Override
        public int getIconWidth() { return 10; }
        @Override
        public int getIconHeight() { return 10; }
    }
}
