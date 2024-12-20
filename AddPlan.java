package Project;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class AddPlan extends JFrame {

    private DefaultListModel<FileManager.Schedule> scheduleListModel;
    private JList<FileManager.Schedule> scheduleList;
    private JTextField titleField;
    private JTextArea memoField;
    private JSpinner startDateTimeSpinner;
    private JSpinner endDateTimeSpinner;
    private JButton categoryButton;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private FileManager.Category selectedCategory;
    private boolean isEditMode = false;
    private int editIndex = -1;

    public AddPlan() {
        this(LocalDate.now());
    }

    public AddPlan(LocalDate selectedDate) {
        setTitle("일정 관리");
        setSize(900, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        FileManager.LoadSaveData();

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Left panel: Schedule list
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(Color.WHITE);

        JLabel scheduleListLabel = new JLabel("오늘의 일정");
        scheduleListLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        leftPanel.add(scheduleListLabel, BorderLayout.NORTH);

        scheduleListModel = new DefaultListModel<>();
        // 오늘 날짜와 일치하는 일정만 추가
        FileManager.schedules.stream()
                .filter(schedule -> {
                    LocalDate scheduleDate = schedule.getStartDate().toLocalDate();
                    return scheduleDate.equals(selectedDate);
                })
                .forEach(scheduleListModel::addElement);

        scheduleList = new JList<>(scheduleListModel);
        scheduleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scheduleList.setCellRenderer(new ScheduleRenderer());
        JScrollPane scrollPane = new JScrollPane(scheduleList);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons: Edit and Delete
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        editButton = createStyledButton("수정하기");
        deleteButton = createStyledButton("삭제하기");

        editButton.addActionListener(e -> {
            int selectedIndex = scheduleList.getSelectedIndex();
            if (selectedIndex != -1) {
                FileManager.Schedule selectedSchedule = scheduleListModel.getElementAt(selectedIndex);
                enterEditMode(selectedSchedule, selectedIndex);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedIndex = scheduleList.getSelectedIndex();
            if (selectedIndex != -1) {
                FileManager.Schedule schedule = scheduleListModel.getElementAt(selectedIndex);
                scheduleListModel.remove(selectedIndex);
                FileManager.schedules.remove(schedule);
                FileManager.SaveAllData();
            }
        });

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(leftPanel);

        // Right panel: Add/Edit schedule
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);

        JLabel addScheduleLabel = new JLabel("새 일정 추가");
        addScheduleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        addScheduleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(addScheduleLabel);
        rightPanel.add(Box.createVerticalStrut(10));

        // Category selection
        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        categoryPanel.setBackground(Color.WHITE);

        JLabel categoryLabel = new JLabel("카테고리:");
        categoryButton = createStyledButton("카테고리 선택");
        categoryButton.addActionListener(e -> showCategoryDialog());
        categoryPanel.add(categoryLabel);
        categoryPanel.add(categoryButton);
        rightPanel.add(categoryPanel);

        // Date and time selection
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.setBackground(Color.WHITE);

        JLabel startDateTimeLabel = new JLabel("시작일시:");
        startDateTimeSpinner = new JSpinner(new SpinnerDateModel());
        startDateTimeSpinner.setEditor(new JSpinner.DateEditor(startDateTimeSpinner, "yyyy-MM-dd HH:mm"));

        JLabel endDateTimeLabel = new JLabel("종료일시:");
        endDateTimeSpinner = new JSpinner(new SpinnerDateModel());
        endDateTimeSpinner.setEditor(new JSpinner.DateEditor(endDateTimeSpinner, "yyyy-MM-dd HH:mm"));

        Date defaultDate = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        startDateTimeSpinner.setValue(defaultDate);
        endDateTimeSpinner.setValue(defaultDate);

        datePanel.add(startDateTimeLabel);
        datePanel.add(startDateTimeSpinner);
        datePanel.add(endDateTimeLabel);
        datePanel.add(endDateTimeSpinner);
        rightPanel.add(datePanel);

        // Title input
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("일정 제목:");
        titleField = createStyledTextField(20);
        titlePanel.add(titleLabel);
        titlePanel.add(titleField);
        rightPanel.add(titlePanel);

        // Memo input
        JPanel memoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        memoPanel.setBackground(Color.WHITE);

        JLabel memoLabel = new JLabel("일정 메모:");
        memoField = new JTextArea(4, 20);
        memoField.setLineWrap(true);
        memoField.setWrapStyleWord(true);
        memoField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        memoField.setBackground(new Color(230, 230, 230));
        JScrollPane memoScrollPane = new JScrollPane(memoField);
        memoPanel.add(memoLabel);
        memoPanel.add(memoScrollPane);
        rightPanel.add(memoPanel);

        // Add button
        addButton = createStyledButton("추가하기");
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.addActionListener(e -> {
            LocalDateTime startDateTime = LocalDateTime.ofInstant(((Date) startDateTimeSpinner.getValue()).toInstant(), ZoneId.systemDefault());
            LocalDateTime endDateTime = LocalDateTime.ofInstant(((Date) endDateTimeSpinner.getValue()).toInstant(), ZoneId.systemDefault());

            if (startDateTime.isAfter(endDateTime)) {
                JOptionPane.showMessageDialog(this, "시작 시간이 종료 시간보다 빠를 수 없습니다.", "시간 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (selectedCategory == null) {
                selectedCategory = FileManager.defaultCategory;
            }

            FileManager.Schedule newSchedule = new FileManager.Schedule(
                    selectedCategory, titleField.getText(), memoField.getText(), startDateTime, endDateTime);

            
            if (isEditMode) {
                // 수정 모드일 경우 기존 일정 수정
                scheduleListModel.set(editIndex, newSchedule);
                FileManager.schedules.set(editIndex, newSchedule);
                JOptionPane.showMessageDialog(this, "일정이 수정되었습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
                isEditMode = false; // 수정 모드 해제
                addButton.setText("추가하기"); // 버튼 텍스트를 다시 '추가하기'로 변경
            } else {
                // 추가 모드일 경우 새 일정 추가
                FileManager.addSchedule(newSchedule);
                if (newSchedule.getStartDate().toLocalDate().equals(selectedDate)) {
                    scheduleListModel.addElement(newSchedule);
                }
                JOptionPane.showMessageDialog(this, "일정이 추가되었습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(addButton);

        mainPanel.add(rightPanel);
        add(mainPanel);

        setVisible(true);
    }

    private void showCategoryDialog() {
        CategoryDialog dialog = new CategoryDialog(this, FileManager.categories, category -> {
            selectedCategory = category;
            categoryButton.setText("카테고리: " + selectedCategory.getName());
            categoryButton.setBackground(category.getColor());
        });
        dialog.setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(34, 139, 34));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        button.setFocusPainted(false);
        return button;
    }

    private JTextField createStyledTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setBackground(new Color(230, 230, 230));
        textField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return textField;
    }

    private void enterEditMode(FileManager.Schedule schedule, int index) {
        titleField.setText(schedule.getTitle());
        memoField.setText(schedule.getContent());
        startDateTimeSpinner.setValue(Date.from(schedule.getStartDate().atZone(ZoneId.systemDefault()).toInstant()));
        endDateTimeSpinner.setValue(Date.from(schedule.getEndDate().atZone(ZoneId.systemDefault()).toInstant()));
        selectedCategory = schedule.getCategory();
        categoryButton.setText("카테고리: " + selectedCategory.getName());
        categoryButton.setBackground(selectedCategory.getColor());
        isEditMode = true;
        editIndex = index;
        addButton.setText("수정하기");
    }
    
    private static class ScheduleRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof FileManager.Schedule) {
                FileManager.Schedule schedule = (FileManager.Schedule) value;
                setText(schedule.getTitle());
                setIcon(new ColorIcon(schedule.getCategory().getColor()));
            }
            return component;
        }
    }

    private static class ColorIcon implements Icon {
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
