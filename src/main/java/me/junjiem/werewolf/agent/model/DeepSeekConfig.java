package me.junjiem.werewolf.agent.model;

public class DeepSeekConfig {
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";
    private final String apiKey;

    public DeepSeekConfig(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiUrl() { return API_URL; }
    public String getApiKey() { return apiKey; }
}
