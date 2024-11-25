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

        setSize(500, 400);
        setLocationRelativeTo(parent);

        // 왼쪽 패널: 기존 카테고리 표시 및 선택
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);

        categoryListModel = new DefaultListModel<>();
        categories.forEach(categoryListModel::addElement);

        categoryList = new JList<>(categoryListModel);
        categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoryList.setCellRenderer(new CategoryRenderer());

        JScrollPane scrollPane = new JScrollPane(categoryList);
        leftPanel.add(new JLabel("기존 카테고리"), BorderLayout.NORTH);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel leftButtonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        leftButtonPanel.setBackground(Color.WHITE);

        JButton selectButton = createStyledButton("선택");
        JButton deleteButton = createStyledButton("삭제");

        selectButton.addActionListener(e -> {
            FileManager.Category selectedCategory = categoryList.getSelectedValue();
            if (selectedCategory != null) {
                onCategorySelected.accept(selectedCategory);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "카테고리를 선택하세요.", "알림", JOptionPane.WARNING_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            FileManager.Category selectedCategory = categoryList.getSelectedValue();
            if (selectedCategory != null) {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "정말로 이 카테고리를 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    categories.remove(selectedCategory);
                    FileManager.SaveAllData();
                    categoryListModel.removeElement(selectedCategory);
                }
            } else {
                JOptionPane.showMessageDialog(this, "삭제할 카테고리를 선택하세요.", "알림", JOptionPane.WARNING_MESSAGE);
            }
        });

        leftButtonPanel.add(selectButton);
        leftButtonPanel.add(deleteButton);
        leftPanel.add(leftButtonPanel, BorderLayout.SOUTH);

        // 오른쪽 패널: 새 카테고리 추가
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);

        JTextField newCategoryField = new JTextField(15);
        newCategoryField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
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
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(newCategoryField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(colorButton);
        buttonPanel.add(addCategoryButton);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(buttonPanel);

        // 메인 패널에 왼쪽/오른쪽 추가
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 10));
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

    // 카테고리 렌더러: 이름 옆에 색상 원 표시
    private static class CategoryRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof FileManager.Category) {
                FileManager.Category category = (FileManager.Category) value;

                setText(category.getName());
                setIcon(new ColorIcon(category.getColor()));
            }
            return component;
        }
    }

    // 색상 원 아이콘
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
