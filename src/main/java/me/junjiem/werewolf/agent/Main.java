package me.junjiem.werewolf.agent;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import me.junjiem.werewolf.agent.bean.KillResult;
import me.junjiem.werewolf.agent.player.*;
import me.junjiem.werewolf.agent.util.GameData;
import me.junjiem.werewolf.agent.util.TTSPlayer;
import me.junjiem.werewolf.agent.util.YamlEnvLoader;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author JunjieM
 * @Date 2024/4/8
 */
@Slf4j
public class Main {
    private static GameGUI gui; // 新增GUI实例

    private static final ExecutorService audioExecutor = Executors.newSingleThreadExecutor();
    // 移除原有静态变量
    private static final Map<String, Map<String, Object>> llmConfigs;


    static {

        try  {
            String active = YamlEnvLoader.loadActiveConfig("config.yaml");
            InputStream in = Main.class.getClassLoader().getResourceAsStream("application-"+active+".yaml");

            Map<String, Object> config = YamlEnvLoader.loadWithEnv(in);
            // 新配置结构读取
            llmConfigs = (Map<String, Map<String, Object>>) config.get("llm_services");
        } catch (IOException e) {
            throw new RuntimeException("Load config.yaml failed.", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static void init() {
        gui = new GameGUI();
        gui.setVisible(true);
        // 定义角色列表
        List<String> roles = new ArrayList<>();
        roles.add("预言家");
        roles.add("女巫");
        roles.add("猎人");
        for (int i = 0; i < 3; i++) {
            roles.add("村民");
        }
        for (int i = 0; i < 3; i++) {
            roles.add("狼人");
        }
        // 洗牌，打乱角色顺序
        Collections.shuffle(roles);
        // 分配角色给玩家
        for (int i = 0; i < roles.size(); i++) {
            int id = i + 1;
            String role = roles.get(i);
            log.info(id + "号玩家角色: " + role);
            gui.updateRoleName(id,role);
            GameData.players.add(createPlayer(id, role));
            GameData.gameInformations.put(id, null);
        }
    }

    public static void main(String[] args) {

        System.out.println("-------------------初始化游戏-------------------");
        init();
        System.out.println("-------------------开始游戏-------------------");
        try {
            for (int i = 1;; i++) {
                Set<Integer> killIds = new HashSet<>();
                System.out.println("==================== 第" + i + "天 ====================");

                System.out.println("========== 天黑请闭眼 ==========");
                TTSPlayer.playAudio("天黑请闭眼", "sambert-zhiqi-v1");
                Thread.sleep(500);
                System.out.println("++++++++狼人请睁眼++++++++");
                TTSPlayer.playAudio("狼人请睁眼", "sambert-zhiqi-v1");
//                Thread.sleep(500);
                System.out.println(">>>>>>狼人请选择猎杀目标<<<<<<");
                TTSPlayer.playAudio("狼人请选择猎杀目标", "sambert-zhiqi-v1");
//                Thread.sleep(500);
                int killId = collectiveKill(); // 狼人集体决定猎杀目标
//                Thread.sleep(1000);
                System.out.println("++++++++狼人请闭眼++++++++");
                TTSPlayer.playAudio("狼人请闭眼", "sambert-zhiqi-v1");
//                Thread.sleep(500); // 添加等待
                System.out.println("++++++++预言家请睁眼++++++++");
                TTSPlayer.playAudio("预言家请睁眼", "sambert-zhiqi-v1");
                Thread.sleep(500);
                System.out.println(">>>>>>预言家请选择查验目标<<<<<<");
                TTSPlayer.playAudio("预言家请选择查验目标", "sambert-zhiqi-v1");
                Thread.sleep(500);
                List<AbstractPlayer> prophetPlayers = GameData.getAliveProphetPlayers();
                if (!prophetPlayers.isEmpty()) {
                    ((ProphetPlayer) prophetPlayers.get(0)).skill();
                }
                System.out.println("++++++++预言家请闭眼++++++++");
                TTSPlayer.playAudio("预言家请闭眼", "sambert-zhiqi-v1");
                Thread.sleep(500); // 添加等待
                System.out.println("++++++++女巫请睁眼++++++++");
                TTSPlayer.playAudio("女巫请睁眼", "sambert-zhiqi-v1");
                List<AbstractPlayer> witchPlayers = GameData.getAliveWitchPlayers();
                if (!witchPlayers.isEmpty()) {
                    WitchPlayer witchPlayer = (WitchPlayer) witchPlayers.get(0);
                    System.out.println(">>>>>>" + killId + "号玩家被刀<<<<<<");
                    if (i == 1) { // 第一天
                        System.out.println(">>>>>>由于是第一晚女巫救活了他<<<<<<");
                        witchPlayer.save(killId);
                        killId = -1;
                    } else {
                        int poisonId = witchPlayer.skill(killId);
                        if (poisonId != -1) {
                            killIds.add(poisonId);
                        }
                    }
                }
                System.out.println("++++++++女巫请闭眼++++++++");
                TTSPlayer.playAudio("女巫请闭眼", "sambert-zhiqi-v1");
                Thread.sleep(500); // 添加等待
                if (killId != -1) {
                    killIds.add(killId);
                }

                System.out.println("========== 天亮请睁眼 ==========");
                TTSPlayer.playAudio("天亮请睁眼", "sambert-zhiqi-v1");
//                Thread.sleep(100); // 添加等待
                if (killIds.isEmpty()) {
                    System.out.println(">>>>>>昨晚是个平安夜<<<<<<");
                    TTSPlayer.playAudio("昨晚是个平安夜", "sambert-zhiqi-v1");
                } else {
                    System.out.println(">>>>>>昨晚" + killIds + "号玩家死亡<<<<<<");
                    String info = "昨晚" + killIds + "号玩家死亡";
                    TTSPlayer.playAudio(info, "sambert-zhiqi-v1");

                    for (int id : killIds) {
                        GameData.playerDead(id);
                        GameData.addPlayerInformation(id, "第" + i + "天晚上死亡");
                        gui.updateSpeech(id,"");
                        refreshPlayerPanels(); // 添加界面刷新
                    }
                }
                List<AbstractPlayer> alivePlayers = GameData.getAlivePlayers();
                System.out.println("++++++++开始依次发言++++++++");
                TTSPlayer.playAudio("开始依次发言", "sambert-zhiqi-v1");
                List<String> speekList = Lists.newArrayList();
                for (int j = 0; j < alivePlayers.size(); j++) {
                    AbstractPlayer player = alivePlayers.get(j);
                    String speak = player.speak(j);
                    String message = player.getId() + "号玩家 : " + speakInformation(i, speak);

                    // 修改原代码第162-163行的播放逻辑为：
                    audioExecutor.execute(() -> {
                        try {
                            gui.updateSpeech(player.getId(), message); // 替换原来的appendMessage
                            refreshPlayerPanels(); // 添加界面刷新
                            TTSPlayer.playAudio(message, player.getVoice());
                        } catch (Exception e) {
                            log.error("音频播放失败", e);
                        }
                    });
                    GameData.addPlayerInformation(player.getId(), message);
//                    Thread.sleep(3000);

                }

                System.out.println("++++++++开始进行投票++++++++");
                Map voteMap = startVote(i, 1, alivePlayers,
                        alivePlayers.stream().map(AbstractPlayer::getId).collect(Collectors.toList()));

                // 在需要判断的地方使用
                if (Main.isAudioBusy()) {
                    System.out.println("有音频正在播放，请稍候...");
                    audioExecutor.awaitTermination(30, TimeUnit.SECONDS);
                }
                TTSPlayer.playAudio("开始进行投票","sambert-zhiqi-v1");

                int voteId = collectiveVote(i, 1, alivePlayers,
                        alivePlayers.stream().map(AbstractPlayer::getId).collect(Collectors.toList()),voteMap);
                System.out.println(">>>>>>" + voteId + "号玩家被投票出局<<<<<<");


                GameData.playerDead(voteId);
                refreshPlayerPanels(); // 添加界面刷新
                System.out.println(">>>>>>" + voteId + "号玩家请发表遗言<<<<<<");
                TTSPlayer.playAudio(voteId + "号玩家请发表遗言","sambert-zhiqi-v1");
                testaments(i, voteId);

            }
        } catch (GameOverException e) {
            System.out.println("-------------------结束游戏-------------------");
            TTSPlayer.playAudio("结束游戏"+"获胜阵营：" + (GameData.goodGuysWin ? "好人阵营" : "狼人阵营"),"sambert-zhiqi-v1");

            System.out.println("---------------------------------------------");
            System.out.println("|              获胜阵营：" + (GameData.goodGuysWin ? "好人" : "坏人") + "阵营               |");
            System.out.println("---------------------------------------------");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (LineUnavailableException e) {
//            throw new RuntimeException(e);
            e.printStackTrace();
        }// 在main方法结束前添加关闭逻辑（在catch块最后）
        finally {
            audioExecutor.shutdown();
            try {
                if (!audioExecutor.awaitTermination(300000, TimeUnit.SECONDS)) {
                    audioExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                audioExecutor.shutdownNow();
            }
        }
    }
    // 判断音频系统是否在使用中
    public static boolean isAudioBusy() {
        return TTSPlayer.isAnyAudioPlaying(); // 需要TTSPlayer配合实现
    }
    private static Map startVote(int day, int times, List<AbstractPlayer> players, List<Integer> voteIds) {
        Map<Integer, Integer> map = new HashMap<>();
        for (AbstractPlayer player : players) {
            int voteId = player.vote(voteIds);
            String voteInfo = "第" + day + "天第" + times + "次投票: 弃票";
            if (voteId != -1) {
                voteInfo = "第" + day + "天第" + times + "次投票: 投给了" + voteId + "号玩家";
                map.put(player.getId(), voteId);
            }
            GameData.addPlayerInformation(player.getId(), voteInfo);
        }
        return map;
    }

    private static void testaments(int day, int killId) throws GameOverException, LineUnavailableException, InterruptedException {
        String testament = GameData.getPlayer(killId).testament();
        String message = killId + "号玩家 : " + testamentInformation(day, testament);

        GameData.addPlayerInformation(killId, testamentInformation(day, testament));
        System.out.println(message);
        gui.updateSpeech(killId, message); // 替换原来的appendMessage
        TTSPlayer.playAudio(message,GameData.getPlayer(killId).getVoice());
        refreshPlayerPanels();
    }

    private static String speakInformation(int day, String speak) {
        return "第" + day + "天发言: " + speak;
    }

    private static String testamentInformation(int day, String testament) {
        return "第" + day + "天被投票出局，发表遗言: " + testament;
    }

    private static AbstractPlayer createPlayer(int id, String roleName) {
        // 获取角色对应配置（含默认配置）
        Map<String, Object> config = Optional.ofNullable(llmConfigs.get(roleName))
                .orElse(llmConfigs.get("default"));
        List<String> voices = (List<String>) config.get("voice");
        String voice = "";
        if (voices instanceof List) {
            int roleCount = GameData.getPlayerCountByRole(roleName);
            voice =  voices.get(roleCount);
       }

        if (voice.isEmpty()) {
            throw new RuntimeException("voice is empty");
        }
        //打印角色配置
        System.out.println("角色配置: " + config);
        System.out.println("角色语音: " + voice);

        String service = (String) config.get("service");
        String apiKey = (String) config.get("api_key");
        String modelName = (String) config.get("model_name");
        Double temperature = (Double) config.get("temperature");

        String modelCompany = (String) config.get("model_company");

        gui.updateServiceLogo(id, modelCompany,modelName);
        switch (roleName) {
            case "村民":
                return new VillagerPlayer(id, roleName, service, apiKey, modelName, temperature,voice);
            case "狼人":
                return new WerewolfPlayer(id, roleName, service, apiKey, modelName, temperature,voice);
            case "预言家":
                return new ProphetPlayer(id, roleName, service, apiKey, modelName, temperature,voice);
            case "女巫":
                return new WitchPlayer(id, roleName, service, apiKey, modelName, temperature,voice);
            case "猎人":
                return new HunterPlayer(id, roleName, service, apiKey, modelName, temperature,voice);
            default:
                throw new IllegalArgumentException("不支持的角色: " + roleName);
        }
    }


    /**
     * 集体决定投票目标
     *
     * @return
     */
    public static int collectiveVote(int day, int times, List<AbstractPlayer> players, List<Integer> voteIds,Map<Integer, Integer> map) {



        System.out.println("第" + day + "天第" + times + "次投票结果：" + map.entrySet().stream()
                .map(e -> e.getKey() + "->" + e.getValue())
                .collect(Collectors.joining(", "))
        );
        // 添加GUI更新调用
        String voteResult = "【第" + day + "天第" + times + "次投票】\n" +
                map.entrySet().stream()
                        .map(e -> e.getKey() + "号 → " + e.getValue() + "号")
                        .collect(Collectors.joining("\n")) + "\n";
        gui.updateVoting(voteResult);
        Map<Integer, Long> frequencyMap = map.values().stream()
                .collect(Collectors.groupingBy(
                        Function.identity(), // 分组依据，这里表示元素本身
                        Collectors.counting()  // 计算每个元素的数量
                ));
        long maxFrequency = frequencyMap.values().stream().max(Long::compareTo).orElse(0L);
        List<Integer> maxFrequencyVoteIds = frequencyMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(maxFrequency))
                .map(Map.Entry::getKey).collect(Collectors.toList());
        if (maxFrequencyVoteIds.size() == 1) { // 只有一个出现次数最多的ID
            return maxFrequencyVoteIds.get(0);
        } else { // 有多个出现次数最多的ID
            System.out.println(maxFrequencyVoteIds.stream().map(id -> id + "")
                    .collect(Collectors.joining(", ")) + "都为" + maxFrequency + "票，请重新投票");
            List<AbstractPlayer> newPlayers = players.stream()
                    .filter(p -> !maxFrequencyVoteIds.contains(p.getId()))
                    .collect(Collectors.toList());
            List<Integer> newVoteIds = players.stream()
                    .map(AbstractPlayer::getId)
                    .filter(maxFrequencyVoteIds::contains)
                    .collect(Collectors.toList());
            Map<Integer, Integer> newMap = startVote(day, times + 1, newPlayers, newVoteIds);
            return collectiveVote(day, times + 1, newPlayers, newVoteIds, newMap);
        }
    }

    /**
     * 狼人集体决定猎杀目标
     *
     * @return
     */
    public static int collectiveKill() {
        Map<AbstractPlayer, KillResult> map = new HashMap<>();
        for (AbstractPlayer player : GameData.getAliveWerewolfPlayers()) {
            List<String> teamStrategies = new ArrayList<>();
            int index = 1;
            for (Map.Entry<AbstractPlayer, KillResult> e : map.entrySet()) {
                teamStrategies.add("team_strategy_" + (index++) + ":" + e.getKey().getId() + "号玩家想杀死的玩家是"
                        + e.getValue().getKillId() + "号，他的游戏策略如下：\n" + e.getValue().getMyStrategy());
            }
            KillResult result = ((WerewolfPlayer) player).skill(String.join("\n", teamStrategies));
            map.put(player, result);
        }
        Map<Integer, Long> frequencyMap = map.values().stream()
                .map(KillResult::getKillId)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        Function.identity(), // 分组依据，这里表示元素本身
                        Collectors.counting()  // 计算每个元素的数量
                ));
        long maxFrequency = frequencyMap.values().stream().max(Long::compareTo).orElse(0L);
        List<Integer> maxFrequencyKillIds = frequencyMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(maxFrequency))
                .map(Map.Entry::getKey).collect(Collectors.toList());
        if (maxFrequencyKillIds.size() == 1) { // 只有一个出现次数最多的ID
            return maxFrequencyKillIds.get(0);
        } else { // 有多个出现次数最多的ID
            return collectiveKill();
        }
    }
    // 在Main类中添加
    private static void refreshPlayerPanels() {
        SwingUtilities.invokeLater(() -> {
            gui.revalidate();
            gui.repaint();
        });
    }

}
