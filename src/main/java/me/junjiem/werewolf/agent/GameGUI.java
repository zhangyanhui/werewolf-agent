package me.junjiem.werewolf.agent;

import me.junjiem.werewolf.agent.util.GameData;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;


public class GameGUI extends JFrame {
    private JTextArea votingArea;
    private JPanel leftPanel;  // 左侧列（1-5号玩家）
    private JPanel rightPanel; // 右侧列（6-9号玩家）

    // 在GameGUI类中添加颜色常量
    private static final Color VOTE_COLOR = new Color(0x2D3748);
    private static final Color TIMESTAMP_COLOR = new Color(0x4A90E2);
    // 在GameGUI类中添加
    private static final Map<String, String> SERVICE_ICONS = new HashMap<>();
    private final Map<Integer, JLabel> playerLogos = new HashMap<>();


    private static ImageIcon createScaledIcon(String path) {
        ImageIcon originalIcon = new ImageIcon(GameGUI.class.getResource(path));
        Image scaled = originalIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    public GameGUI() {
        // 顶部投票区域（保持原样）
        votingArea = new JTextArea(10, 150);
        votingArea.setEditable(false);
        add(new JScrollPane(votingArea), BorderLayout.NORTH);

        // 主容器使用水平布局
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

        // 左侧列（垂直布局5行）
        leftPanel = new JPanel(new GridLayout(5, 1, 0, 10));
        for (int i = 1; i <= 5; i++) {
            leftPanel.add(createPlayerPanel(i));
        }

        // 右侧列（垂直布局4行）
        rightPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        for (int i = 6; i <= 9; i++) {
            rightPanel.add(createPlayerPanel(i));
        }

        // 添加左右列到主容器
        mainPanel.add(leftPanel);
        mainPanel.add(Box.createHorizontalStrut(200)); // 左右列间距
        mainPanel.add(rightPanel);

        add(mainPanel, BorderLayout.CENTER);
        setSize(1200, 800); // 适当调整窗口尺寸
    }

    // 创建玩家面板（保持原样）
    private JPanel createPlayerPanel(int playerId) {
        JPanel panel = new JPanel(new BorderLayout());
//        System.out.println(GameData.getPlayer(playerId-1).getRoleName());
        JLabel label = new JLabel(playerId + "号玩家", JLabel.CENTER);
        JTextArea area = new JTextArea(5, 20);
        playerLogos.put(playerId, label);
        // 添加服务Logo
//        JLabel logoLabel = new JLabel();
//        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        playerLogos.put(playerId, logoLabel);
//        panel.add(logoLabel, BorderLayout.WEST);

        boolean isDead = GameData.isPlayerDead(playerId);
        if (isDead) {
            area.setEditable(false);
            area.setForeground(new Color(0x999999));
            panel.add(new JScrollPane(area), BorderLayout.CENTER); // 添加滚动面板
        }else {
            // 样式设置
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0x4A90E2)),
                    BorderFactory.createEmptyBorder(5,5,5,5)
            ));

            label.setFont(new Font("微软雅黑", Font.BOLD, 14));
            label.setForeground(new Color(0x2D3748));

            area.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            area.setLineWrap(true);

            panel.add(label, BorderLayout.NORTH);
            panel.add(new JScrollPane(area), BorderLayout.CENTER);
        }

        return panel;
    }

    // 新增更新Logo方法
    public void updateServiceLogo(int id, String service,String model) {
//        String model_company = SERVICE_ICONS.getOrDefault(service.toLowerCase(), SERVICE_ICONS.get("default"));
        String newTitle = "【"+service+"-"+model+"】"+playerLogos.get(id).getText();
        playerLogos.get(id).setText(newTitle);
//        playerLogos.get(id).setToolTipText("模型服务：" + service);
    }
    // 更新发言方法（需要适配新布局）
    // 修复后的updateSpeech方法（添加组件类型验证和事件线程调度）
    public void updateSpeech(int playerId, String message) {
        SwingUtilities.invokeLater(() -> {
            Container targetPanel = (playerId <= 5) ? leftPanel : rightPanel;

            for (Component comp : targetPanel.getComponents()) {
                if (comp instanceof JPanel) {
                    JPanel p = (JPanel) comp;
                    // 修复点1：直接获取标签组件
                    Component labelComp = p.getComponent(0);
                    if (labelComp instanceof JLabel) {
                        JLabel label = (JLabel) labelComp;
                        // 修复点2：使用contains匹配玩家编号
                        if (label.getText().contains(playerId + "号")) {
                            // 修复点3：确保组件结构正确
                            if (p.getComponentCount() > 1 && p.getComponent(1) instanceof JScrollPane) {
                                JScrollPane scrollPane = (JScrollPane) p.getComponent(1);
                                JViewport viewport = scrollPane.getViewport();
                                if (viewport.getView() instanceof JTextArea) {
                                    JTextArea area = (JTextArea) viewport.getView();
                                    area.append(message + "\n");
                                    area.setCaretPosition(area.getDocument().getLength());
                                    boolean isDead = GameData.isPlayerDead(playerId);
                                    if (isDead) {
                                        area.setEditable(false);
                                        area.setForeground(new Color(0x845B5B));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }
    public void updateRoleName(int playerId, String roleName) {
        Container targetPanel = (playerId <= 5) ? leftPanel : rightPanel;

        for (Component comp : targetPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel p = (JPanel) comp;
                Component labelComp = p.getComponent(0);
                if (labelComp instanceof JLabel) {
                    JLabel label = (JLabel) labelComp;
                    if (label.getText().startsWith(playerId + "号")) {
                      label.setText(playerId + "号玩家[" + roleName + "]");
                    }
                }
            }
        }
    }


    // 修改updateVoting方法实现样式
    public void updateVoting(String info) {
        SwingUtilities.invokeLater(() -> {
            votingArea.setForeground(VOTE_COLOR);
            votingArea.setFont(new Font("微软雅黑", Font.BOLD, 14));
            votingArea.append(info);
            votingArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            votingArea.append("\n");
            votingArea.setCaretPosition(votingArea.getDocument().getLength());
        });
    }


    public static void main(String[] args) {

    }
}





