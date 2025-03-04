package me.junjiem.werewolf.agent.util;

import com.alibaba.dashscope.audio.tts.SpeechSynthesisResult;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesisAudioFormat;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesisParam;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesizer;
import com.alibaba.dashscope.common.ResultCallback;
import me.junjiem.werewolf.agent.Main;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;



public class TTSPlayer {
    private static final Set<String> playingAudios = Collections.synchronizedSet(new HashSet<>());
    private static final String MODEL = "cosyvoice-v1";
    private static final int CHUNK_SIZE = 10; // 流式分块大小
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
    // 实例级音频设备管理
    private SourceDataLine dataLine;
    private final AudioFormat audioFormat = new AudioFormat(
            22050, 16, 1, true, false
    );

    public void streamAudioDataToSpeaker(String text, String voice) throws LineUnavailableException, InterruptedException {
        // 初始化音频设备
        initAudioDevice();

        CountDownLatch latch = new CountDownLatch(1);
        SpeechSynthesizer synthesizer = createSynthesizer(voice, latch);

        // 分块发送文本
        sendTextChunks(text, synthesizer);

        // 等待合成完成
        latch.await();

        // 安全关闭资源
        closeResources();
    }

    private void initAudioDevice() throws LineUnavailableException {
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        dataLine = (SourceDataLine) AudioSystem.getLine(info);
        dataLine.open(audioFormat);
        dataLine.start();
    }

    private SpeechSynthesizer createSynthesizer(String voice, CountDownLatch latch) {
        SpeechSynthesisParam param = SpeechSynthesisParam.builder()
                .apiKey(ttskey)
                .model(MODEL)
                .voice(voice)
                .format(SpeechSynthesisAudioFormat.PCM_22050HZ_MONO_16BIT)
                .build();

        return new SpeechSynthesizer(param, new ResultCallback<SpeechSynthesisResult>() {
            @Override
            public void onEvent(SpeechSynthesisResult result) {
                if (result.getAudioFrame() != null) {
                    byte[] audioData = result.getAudioFrame().array();
                    dataLine.write(audioData, 0, audioData.length);
                }
            }

            @Override
            public void onComplete() {
                dataLine.drain();
                latch.countDown();
            }

            @Override
            public void onError(Exception e) {
                System.err.println("TTS Error: " + e.getMessage());
                latch.countDown();
            }
        });
    }

    private void sendTextChunks(String text, SpeechSynthesizer synthesizer) {
        int index = 0;
        while (index < text.length()) {
            int endIndex = Math.min(index + CHUNK_SIZE, text.length());
            String chunk = text.substring(index, endIndex);
            synthesizer.streamingCall(chunk);
            index = endIndex;
        }
        synthesizer.streamingComplete();
    }

    private void closeResources() {
        if (dataLine != null) {
            dataLine.stop();
            dataLine.close();
        }
    }

    public static void playAudio(String text, String voice) {
        playingAudios.add(text);
        try {
            new TTSPlayer().streamAudioDataToSpeaker(text, voice);
        } catch (Exception e) {
            System.err.println("播放失败: " + e.getMessage());
        } finally {
            playingAudios.remove(text);
        }
    }

    public static boolean isAnyAudioPlaying() {
        return !playingAudios.isEmpty();
    }

    public static void main(String[] args) {
        // 测试连续播放
        new Thread(() -> playAudio("第一段长文本测试...", "longxiaochun")).start();
        new Thread(() -> playAudio("第二段文本内容...", "longxiaochun")).start();
    }
}





