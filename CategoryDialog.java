package Project;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class CategoryDialog extends JDialog {

    private final JList<FileManager.Category> categoryList;
    private final DefaultListModel<FileManager.Category> categoryListModel;
    private final Consumer<FileManager.Category> onCategorySelected;

    public CategoryDialog(JFrame parent, List<FileManager.Category> categories, Consumer<FileManager.Category> onCategorySelected) {
        super(parent, "카테고리 선택", true);
        this.onCategorySelected = onCategorySelected;

        setSize(400, 300);
        setLocationRelativeTo(parent);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);

        categoryListModel = new DefaultListModel<>();
        categories.forEach(categoryListModel::addElement);
        categoryList = new JList<>(categoryListModel);
        categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(categoryList);
        leftPanel.add(new JLabel("기존 카테고리"), BorderLayout.NORTH);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        JButton selectButton = createStyledButton("선택");
        selectButton.addActionListener(e -> {
            FileManager.Category selectedCategory = categoryList.getSelectedValue();
            if (selectedCategory != null) {
                onCategorySelected.accept(selectedCategory);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "카테고리를 선택하세요.", "알림", JOptionPane.WARNING_MESSAGE);
            }
        });
        leftPanel.add(selectButton, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);

        JTextField newCategoryField = new JTextField(10);
        newCategoryField.setBackground(new Color(230, 230, 230));
        newCategoryField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JButton colorButton = createStyledButton("색상 선택");
        JButton addCategoryButton = createStyledButton("추가");

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
                FileManager.Category newCategory = new FileManager.Category(categoryName, selectedColor[0]);
                categories.add(newCategory);
                FileManager.addCategory(newCategory);
                categoryListModel.addElement(newCategory);
                newCategoryField.setText("");
                selectedColor[0] = Color.BLACK;
                colorButton.setBackground(new Color(230, 230, 230));
            } else {
                JOptionPane.showMessageDialog(this, "카테고리 이름을 입력하세요.", "알림", JOptionPane.WARNING_MESSAGE);
            }
        });

        rightPanel.add(new JLabel("새 카테고리 이름"));
        rightPanel.add(newCategoryField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(colorButton);
        buttonPanel.add(addCategoryButton);
        rightPanel.add(buttonPanel);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        add(mainPanel);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(34, 139, 34));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        button.setFocusPainted(false);
        return button;
    }
}
