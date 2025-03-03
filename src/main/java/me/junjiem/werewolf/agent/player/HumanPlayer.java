package me.junjiem.werewolf.agent.player;

import me.junjiem.werewolf.agent.GameOverException;
import me.junjiem.werewolf.agent.util.GameData;

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
        //通过键盘输入，并返回
        System.out.printf("\n[人类玩家%d号] 请输入你的发言内容：", id);
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();

    }

    @Override
    public int vote(List<Integer> votingIds) {
        //通过键盘输入，并返回
        System.out.printf("\n[人类玩家%d号] 请输入你的投票内容：", id);

        Scanner scanner = new Scanner(System.in);
        return Integer.parseInt(scanner.nextLine());
    }

    @Override
    public String testament() throws GameOverException {
        //通过键盘输入，并返回
        System.out.printf("\n[人类玩家%d号] 请输入你的发言内容：", id);
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();

    }


    @Override
    public int skill(String info) {
        if(hasSkill()){
            System.out.printf("\n[人类玩家%d号] 请输入你的技能内容：", id);
            Scanner scanner = new Scanner(System.in);
            return Integer.parseInt(scanner.nextLine());

        }else {
            return -1;
        }

    }
}
