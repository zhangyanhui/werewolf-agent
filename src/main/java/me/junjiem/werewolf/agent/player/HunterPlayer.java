package me.junjiem.werewolf.agent.player;

import lombok.NonNull;
import me.junjiem.werewolf.agent.GameOverException;
import me.junjiem.werewolf.agent.bean.ShotResult;
import me.junjiem.werewolf.agent.role.HunterRole;
import me.junjiem.werewolf.agent.util.GameData;

import java.util.List;

/**
 * 猎人角色
 * @Author JunjieM
 * @Date 2024/4/11
 */
public class HunterPlayer extends AbstractPlayer {

    private final HunterRole role;



    public HunterPlayer(int id, String roleName, @NonNull String service, @NonNull String apiKey, String modelName, Double temperature, String voice) {
        super(id, roleName,voice);
        this.role = new HunterRole(service, apiKey, modelName, temperature);
    }

    @Override
    public boolean isGoodGuys() {
        return true;
    }

    @Override
    public String speak(int index) {
        return role.speak(id, index, GameData.getGameInformation());
    }

    @Override
    public int vote(List<Integer> voteIds) {
        return role.vote(id, GameData.getGameInformation(), voteIds);
    }

    @Override
    public String testament() throws GameOverException {
//        ShotResult result = role.skill(id, GameData.getGameInformation());
//        if (result.isShot()) {
//            int shotId = result.getShotId();
//            System.out.println(">>>>>>" + shotId + "号被开枪带走<<<<<<");
//            GameData.playerDead(shotId);
//        }
        return "我是猎人";
    }

    @Override
    public int skill(String info) throws GameOverException {
        ShotResult result = role.skill(id, GameData.getGameInformation());
        if (result.isShot()) {
            int shotId = result.getShotId();
            System.out.println(">>>>>>" + shotId + "号被开枪带走<<<<<<");
            GameData.playerDead(shotId);
            this.setShootCause(result.getShotCause());
        }

        return result.getShotId();
    }
}
