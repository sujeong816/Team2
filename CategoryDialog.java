package Project;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class CategoryDialog extends JDialog {

    private final JList<AddPlan.Category> categoryList;
    private final DefaultListModel<AddPlan.Category> categoryListModel;
    private final Consumer<AddPlan.Category> onCategorySelected;

    public CategoryDialog(JFrame parent, List<AddPlan.Category> categories, Consumer<AddPlan.Category> onCategorySelected) {
        super(parent, "카테고리 선택", true);
        this.onCategorySelected = onCategorySelected;

        setSize(400, 300);
        setLocationRelativeTo(parent);

        // 기존 카테고리 리스트 패널
        categoryListModel = new DefaultListModel<>();
        categories.forEach(categoryListModel::addElement);
        categoryList = new JList<>(categoryListModel);
        categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(categoryList);
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JLabel("기존 카테고리"), BorderLayout.NORTH);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        JButton selectButton = createStyledButton("선택", 100, 30);
        selectButton.addActionListener(e -> selectCategory());
        leftPanel.add(selectButton, BorderLayout.SOUTH);

        // 새 카테고리 추가 패널
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JTextArea newCategoryField = createStyledTextArea(10, 4);

        // 색상 선택과 추가 버튼 한 줄 배치
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton colorButton = createStyledButton("색상 선택", 80, 30);
        JButton addCategoryButton = createStyledButton("추가", 80, 30);

        Color[] selectedColor = {Color.BLACK};

        colorButton.addActionListener(e -> {
            Color color = JColorChooser.showDialog(this, "색상 선택", selectedColor[0]);
            if (color != null) {
                selectedColor[0] = color;
                colorButton.setBackground(color);
            }
        });

        addCategoryButton.addActionListener(e -> {
            String categoryName = newCategoryField.getText().trim();
            if (!categoryName.isEmpty()) {
                AddPlan.Category newCategory = new AddPlan.Category(categoryName, selectedColor[0]);
                categoryListModel.addElement(newCategory);
                newCategoryField.setText("");
                selectedColor[0] = Color.BLACK;
                colorButton.setBackground(null);
                categories.add(newCategory);
            } else {
                JOptionPane.showMessageDialog(this, "카테고리 이름을 입력하세요.", "알림", JOptionPane.WARNING_MESSAGE);
            }
        });

        buttonPanel.add(colorButton);
        buttonPanel.add(addCategoryButton);

        rightPanel.add(new JLabel("새 카테고리"));
        JScrollPane textScrollPane = new JScrollPane(newCategoryField);
        rightPanel.add(textScrollPane);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(buttonPanel);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        add(mainPanel);
    }

    private void selectCategory() {
        AddPlan.Category selectedCategory = categoryList.getSelectedValue();
        if (selectedCategory != null) {
            onCategorySelected.accept(selectedCategory);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "카테고리를 선택하세요.", "알림", JOptionPane.WARNING_MESSAGE);
        }
    }

    private JButton createStyledButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setBackground(new Color(34, 139, 34)); // 초록색
        button.setForeground(Color.WHITE); // 흰색 글씨
        button.setFocusPainted(false);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        button.setBorder(BorderFactory.createLineBorder(new Color(34, 139, 34), 1));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        return button;
    }

    private JTextArea createStyledTextArea(int columns, int rows) {
        JTextArea textArea = new JTextArea(rows, columns);
        textArea.setBackground(new Color(245, 245, 245));
        textArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        textArea.setLineWrap(true); // 자동 줄 바꿈
        textArea.setWrapStyleWord(true); // 단어 단위로 줄 바꿈
        return textArea;
    }
}
