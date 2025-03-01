package me.junjiem.werewolf.agent.role;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

// 在role包下创建HumanRole.java
public class HumanRole implements GoodRole{
    private final Scanner scanner = new Scanner(System.in);



    public String speak(int playerId, int index, Map<Integer, String> gameInfo) {
        System.out.printf("\n[人类玩家%d号] 请输入你的发言内容：", playerId);
        return scanner.nextLine();
    }

    public int vote(int playerId, Map<Integer, String> gameInfo, List<Integer> candidateIds) {
        System.out.printf("\n[人类玩家%d号] 可投票目标：%s（输入0弃票）：", playerId, candidateIds);
        while (true) {
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice == 0 || candidateIds.contains(choice)) {
                    return choice;
                }
                System.out.print("输入无效，请重新输入：");
            } catch (NumberFormatException e) {
                System.out.print("请输入有效数字：");
            }
        }
    }


}

