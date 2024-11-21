import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.Timer;

public class CalendarApp extends JFrame {

    private JLabel dateLabel;
    private JLabel timeLabel;

    public CalendarApp() {
        setTitle("���� ���� ��");
        setSize(1000, 600);  // â ũ�⸦ 1000x600���� ����
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ���� �г� - ��ư�� ��� �г�
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(150, 600));

        // Ķ���� ���� ��ư
        JButton calendarViewButton = new JButton("Ķ���� ����");
        calendarViewButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        calendarViewButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "Ķ���� ����"));

        // ���� �߰� ��ư
        JButton addScheduleButton = new JButton("���� �߰�");
        addScheduleButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addScheduleButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "���� �߰� ��� ����"));

        // ���� ��¥�� �ð��� ǥ���ϴ� ��
        dateLabel = new JLabel();
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeLabel = new JLabel();
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateDateTime();

        // ��¥�� �ð� ������Ʈ Ÿ�̸� (1�ʸ���)
        Timer timer = new Timer(1000, e -> updateDateTime());
        timer.start();

        // ���� �гο� ��ư�� �� �߰�
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(calendarViewButton);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(addScheduleButton);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(dateLabel);
        leftPanel.add(timeLabel);
        add(leftPanel, BorderLayout.WEST);

        // �߾� �г� - ���� ����� ǥ���ϴ� �г�
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("���� ���"));

        // ī�װ� ���� �� �Ϸ� üũ�ڽ� �г� �߰�
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> categoryComboBox = new JComboBox<>(new String[]{"��ü", "ī�װ�1", "ī�װ�2"});
        JCheckBox completeCheckBox = new JCheckBox("�Ϸ�");

        controlPanel.add(new JLabel("ī�װ�:"));
        controlPanel.add(categoryComboBox);
        controlPanel.add(completeCheckBox);
        summaryPanel.add(controlPanel, BorderLayout.NORTH); // ��ܿ� �߰�

        // �� �г� ����
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("����", createSchedulePanel());
        tabbedPane.addTab("�̹���", createSchedulePanel());
        tabbedPane.addTab("�̹���", createSchedulePanel());

        summaryPanel.add(tabbedPane, BorderLayout.CENTER);
        add(summaryPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // ���� �г� ���� �޼���
    private JScrollPane createSchedulePanel() {
        JPanel schedulePanel = new JPanel();
        schedulePanel.setLayout(new BoxLayout(schedulePanel, BoxLayout.Y_AXIS));

        // ���� ���� �߰�
        for (int i = 0; i < 10; i++) { // ���� ���� 10�� �߰�
            schedulePanel.add(createScheduleItem("���� " + (i + 1), "�޸� ���� " + (i + 1) + "\n�߰� ���� �ٹٲ� ����", 
                                                 "2024-11-01", "2024-11-03"));
            schedulePanel.add(Box.createVerticalStrut(10)); // ���� �� ����
        }

        // ��ũ�� ���� �гη� ���α�
        JScrollPane scrollPane = new JScrollPane(schedulePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        return scrollPane;
    }

    // ���� ���� �г� ���� �޼���
    private JPanel createScheduleItem(String title, String memo, String startDate, String endDate) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        itemPanel.setPreferredSize(new Dimension(300, 100));

        // ����� üũ�ڽ� �г�
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCheckBox checkBox = new JCheckBox();
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 16));
        topPanel.add(checkBox);
        topPanel.add(titleLabel);

        // ��¥ ǥ��
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel dateLabel = new JLabel("���� ��¥: " + startDate + " ~ ���� ��¥: " + endDate);
        datePanel.add(dateLabel);

        // �޸� ����� ��� ��ư �г�
        JPanel memoPanel = new JPanel(new BorderLayout());
        JLabel memoLabel = new JLabel("<html>" + memo.replace("\n", "<br>") + "</html>");
        JButton toggleButton = new JButton("��ġ��");
        memoLabel.setVisible(false); // �ʱ� ���´� ����
        toggleButton.addActionListener(e -> {
            memoLabel.setVisible(!memoLabel.isVisible());
            toggleButton.setText(memoLabel.isVisible() ? "����" : "��ġ��");
            itemPanel.revalidate(); // ���̾ƿ� ����
            itemPanel.repaint();
        });

        memoPanel.add(memoLabel, BorderLayout.CENTER);
        memoPanel.add(toggleButton, BorderLayout.SOUTH);

        itemPanel.add(topPanel, BorderLayout.NORTH);
        itemPanel.add(datePanel, BorderLayout.CENTER);
        itemPanel.add(memoPanel, BorderLayout.SOUTH);

        return itemPanel;
    }

    // ���� ��¥�� �ð��� ������Ʈ�ϴ� �޼ҵ�
    private void updateDateTime() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
        dateLabel.setText("���� ��¥: " + dateFormatter.format(new Date()));
        timeLabel.setText("���� �ð�: " + timeFormatter.format(new Date()));
    }

    public static void main(String[] args) {
        new CalendarApp();
    }
}
