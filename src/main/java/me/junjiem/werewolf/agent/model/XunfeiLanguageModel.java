package me.junjiem.werewolf.agent.model;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public class XunfeiLanguageModel implements ChatLanguageModel {

    private final String apiKey;
    private final String modelName;
    private final Double temperature;
    private static final String API_URL = "https://spark-api-open.xf-yun.com/v1/chat/completions";

//    public static void main(String[] args) {
////        System.out.println(new DoubaoLanguageModel("sk-06e306e994a84064893afe35dcf77229", "ddoubao-1-5-pro-32k-250115", 0.7).generate(List.of(UserMessage.userMessage("你好"))).content());
//    UserMessage userMessage = new UserMessage("你好");
//    List list = Lists.newArrayList();
//        XunfeiLanguageModel XunfeiLanguageModel = new XunfeiLanguageModel("HOpgisHzycqDSssrFnva:APujOEECxFCXAuWWDolD","generalv3",0.7);
//    list.add(userMessage);
//        XunfeiLanguageModel.generate(list);
//    }
    @Override
    public Response<AiMessage> generate(List<ChatMessage> messages) {
        try {
            String responseBody = HttpRequest.post(API_URL)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .body(buildRequestBody(messages))
                    .execute()
                    .body();

            JSONObject responseJson = JSONUtil.parseObj(responseBody);
            System.out.println("doubao-> "+responseJson.toString());
            String content = responseJson.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getStr("content");

            return Response.from(AiMessage.from(content));
        } catch (Exception e) {
            throw new RuntimeException("doubao API调用失败", e);
        }
    }

    private String buildRequestBody(List<ChatMessage> messages) {
        JSONObject body = new JSONObject();
        body.put("model", modelName);
        body.put("temperature", temperature);
        body.put("messages", messages.stream()
                .map(msg -> new JSONObject()
                        .put("role", msg instanceof UserMessage ? "user" : "assistant")
                        .put("content", msg.text()))
                .collect(Collectors.toList()));
        return body.toString();
    }

    // 保持其他方法默认实现
    @Override
    public Response<AiMessage> generate(List<ChatMessage> messages, List<ToolSpecification> toolSpecifications) {
        return ChatLanguageModel.super.generate(messages, toolSpecifications);
    }

    @Override
    public Response<AiMessage> generate(List<ChatMessage> messages, ToolSpecification toolSpecification) {
        return ChatLanguageModel.super.generate(messages, toolSpecification);
    }
}

