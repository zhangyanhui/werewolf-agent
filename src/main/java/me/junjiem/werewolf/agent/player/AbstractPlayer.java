package me.junjiem.werewolf.agent.player;

import lombok.Getter;
import me.junjiem.werewolf.agent.util.GameData;

import java.util.List;

/**
 * @Author JunjieM
 * @Date 2024/4/11
 */
@Getter
public abstract class AbstractPlayer implements Player {
    protected final String voice;
    private List<String> voices;
    /**
     * ID
     */
    protected final int id;

    protected final String roleName;

    public AbstractPlayer(int id, String roleName,String voice) {
        this.id = id;
        this.roleName = roleName;
        this.voice = voice;
    }

    @Override
    public boolean isAlive() {
        return !GameData.killIds.contains(id);
    }
//    public String getVoice() {
//        return voice;
//    }
//    public String getVoice() {
//        return VoiceSelector.getStableVoice(this.voices, this.id);
//    }
}
