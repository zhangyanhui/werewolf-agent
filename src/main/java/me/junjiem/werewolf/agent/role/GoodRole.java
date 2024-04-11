package me.junjiem.werewolf.agent.role;

/**
 * 好人阵营
 *
 * @Author JunjieM
 * @Date 2024/4/9
 */
public interface GoodRole extends Role {
    /**
     * 发言
     *
     * @return
     */
    String speak(int id, int index, String gameInformation);
}
