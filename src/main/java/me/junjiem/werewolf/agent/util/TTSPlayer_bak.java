package me.junjiem.werewolf.agent.util;

import com.alibaba.dashscope.audio.tts.SpeechSynthesisAudioFormat;
import com.alibaba.dashscope.audio.tts.SpeechSynthesisParam;
import com.alibaba.dashscope.audio.tts.SpeechSynthesizer;
import me.junjiem.werewolf.agent.Main;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TTSPlayer_bak {
    private static final Object playLock = new Object();
    private static final int BUFFER_SIZE = 4096;
    private static final Set<String> playingAudios = Collections.synchronizedSet(new HashSet<>());
    private static final String ttskey;
    static {

        try  {
            String active = YamlEnvLoader.loadActiveConfig();
            InputStream in = Main.class.getClassLoader().getResourceAsStream("application-"+active+".yaml");
            Map<String, Object> config = YamlEnvLoader.loadWithEnv(in);
            // 新配置结构读取
            ttskey = (String) config.get("ttskey");
        } catch (IOException e) {
            throw new RuntimeException("Load config.yaml failed.", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    static class AudioPlayer implements AutoCloseable {
        private final BlockingQueue<byte[]> audioQueue = new LinkedBlockingQueue<>(2000);
        private final SourceDataLine dataLine;
        private final Thread playbackThread;
        private volatile boolean running = true;
        private volatile boolean dataFinished = false;

        public AudioPlayer() throws LineUnavailableException {
            AudioFormat format = new AudioFormat(48000, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            dataLine = (SourceDataLine) AudioSystem.getLine(info);
            dataLine.open(format);
            dataLine.start();

            playbackThread = new Thread(() -> {
                byte[] silence = new byte[BUFFER_SIZE];
                while (running || (!audioQueue.isEmpty() && !dataFinished)) { // 修改循环条件
                    try {
                        byte[] chunk = audioQueue.poll(100, TimeUnit.MILLISECONDS);
                        if (chunk != null) {
                            dataLine.write(chunk, 0, chunk.length);
                        } else if (!running) {
                            dataLine.write(silence, 0, silence.length);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                dataLine.drain();
                dataLine.close();
            });
            playbackThread.start();
        }

        public void play(byte[] audioData) {
            if (running) {
                audioQueue.offer(audioData);
            }
        }

        public void markDataFinished() {
            dataFinished = true;
        }

        public void waitCompletion() throws InterruptedException {
            // 等待队列清空或超时
            long start = System.currentTimeMillis();
            while (!dataFinished || !audioQueue.isEmpty()) {
                if (System.currentTimeMillis() - start > 58000) { // 5秒超时
                    break;
                }
                Thread.sleep(100);
            }
            running = false;
            playbackThread.join(58000); // 最多等待2秒
        }

        @Override
        public void close() {
            running = false;
            dataFinished = true;
            audioQueue.clear();
            try {
                playbackThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void playAudio(String text, String model) {
        synchronized (playLock) {
            playingAudios.add(text);
            try (AudioPlayer player = new AudioPlayer()) {
                SpeechSynthesizer synthesizer = new SpeechSynthesizer();
                SpeechSynthesisParam param = SpeechSynthesisParam.builder()
                        .model(model)
                        .text(text)
                        .sampleRate(48000)
                        .format(SpeechSynthesisAudioFormat.PCM)
                        .apiKey(ttskey)
                        .build();

                synthesizer.streamCall(param)
                        .blockingForEach(msg -> {
                            if (msg.getAudioFrame() != null) {
                                player.play(msg.getAudioFrame().array());
                            }
                        });

                player.markDataFinished(); // 标记数据发送完成
                player.waitCompletion();   // 等待播放完成

            } catch (Exception e) {
                System.err.println("语音播放异常: " + e.getMessage());
            }finally {
                playingAudios.remove(text);
            }
        }
    }
    public static boolean isAnyAudioPlaying() {
        return !playingAudios.isEmpty();
    }
    public static void main(String[] args) {
        new Thread(() -> playAudio("第1天发言: 9号预言家，查杀5号。为什么摸5号呢，因为我作为9号，肯定是隔着几个位置去摸，才能有更好的视角去判断狼人。作为预言家，我的责任就是找出狼人，带领好人走向胜利，现在，第一匹狼已经找出来了，就是5号，请大家死票下5。1号玩家，如果你是好人，我建议你第二天发言的时候，多提供一些信息，不要像今天这样什么都没聊，你需要给我们好人开开视野，今天，请你把票投到5号身上，我需要你的支持。2号玩家我觉得你的发言很好，如果你是好人，也请你相信我，把票投到5号身上。对话其他好人好玩，认真发言，第一匹狼已经给你们找出来了，剩下2匹找出来只是时间问题，只要我们好人团结一心，肯定能取得胜利。好了，我的发言完毕，再重复一次，请大家死票下5。", "sambert-zhijia-v1")).start();
        new Thread(() -> playAudio("第2天发言: 9号预言家，查杀5号。为什么摸5号呢，因为我作为9号，肯定是隔着几个位置去摸，才能有更好的视角去判断狼人。作为预言家，我的责任就是找出狼人，带领好人走向胜利，现在，第一匹狼已经找出来了，就是5号，请大家死票下5。1号玩家，如果你是好人，我建议你第二天发言的时候，多提供一些信息，不要像今天这样什么都没聊，你需要给我们好人开开视野，今天，请你把票投到5号身上，我需要你的支持。2号玩家我觉得你的发言很好，如果你是好人，也请你相信我，把票投到5号身上。对话其他好人好玩，认真发言，第一匹狼已经给你们找出来了，剩下2匹找出来只是时间问题，只要我们好人团结一心，肯定能取得胜利。好了，我的发言完毕，再重复一次，请大家死票下5。", "sambert-zhijia-v1")).start();
    }
}




