package me.junjiem.werewolf.agent.player;

import me.junjiem.werewolf.agent.GameOverException;
import me.junjiem.werewolf.agent.util.GameData;

import javax.swing.*;
import java.util.List;
import java.util.Scanner;

public class HumanPlayer extends AbstractPlayer {

    private AbstractPlayer delegate;

    public HumanPlayer(AbstractPlayer delegate) {
        super(delegate.id, delegate.roleName, delegate.voice);
        this.delegate = delegate; }


    @Override
    public boolean isGoodGuys() {
        return delegate.isGoodGuys();
    }

    @Override
    public String speak(int index) {
        String input = JOptionPane.showInputDialog(
                null,
                String.format("人类玩家%d号 请输入发言内容：", id),
                "玩家发言",
                JOptionPane.QUESTION_MESSAGE
        );
        return input != null ? input : "";
    }

    @Override
    public int vote(List<Integer> votingIds) {
        String input = JOptionPane.showInputDialog(
                null,
                String.format("人类玩家%d号 请输入投票号码：", id),
                "玩家投票",
                JOptionPane.QUESTION_MESSAGE
        );
        return input != null ? Integer.parseInt(input) : -1;
    }

    @Override
    public String testament() throws GameOverException {
        String input = JOptionPane.showInputDialog(
                null,
                String.format("人类玩家%d号 请输入遗言内容：", id),
                "玩家遗言",
                JOptionPane.QUESTION_MESSAGE
        );
        return input != null ? input : "";
    }

    @Override
    public int skill(String info) {
        if (hasSkill()) {
            String input = JOptionPane.showInputDialog(
                    null,
                    String.format("人类玩家%d号 请输入技能目标：", id),
                    "技能使用",
                    JOptionPane.QUESTION_MESSAGE
            );
            return input != null ? Integer.parseInt(input) : -1;
        }
        return -1;
    }
}
