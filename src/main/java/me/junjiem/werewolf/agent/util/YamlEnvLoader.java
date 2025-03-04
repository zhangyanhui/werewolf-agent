package me.junjiem.werewolf.agent.util;


import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
// 需要添加的import
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
public class YamlEnvLoader {
    private static final Pattern ENV_PATTERN = Pattern.compile("\\$\\{(?<name>\\w+)(?::(?<default>.*?))?\\}");

    // 修改main方法测试
    public static void main(String[] args) {
        InputStream input = YamlEnvLoader.class.getClassLoader()
                .getResourceAsStream("application.yaml");
        Map<String, Object> config = loadWithEnv(input);

        // 打印完整配置结构
        System.out.println("==== 解析后的配置结构 ====");
        new Yaml().dump(config, new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
        // 详细提取路径
        Map<?,?> llmServices = (Map<?,?>) config.get("spring");
        Map<?,?> profiles = (Map<?,?>) llmServices.get("profiles");

        String active = (String) profiles.get("active");


        System.out.println("\nactive: " +active);
    }
    public static String loadActiveConfig() throws Exception {
        InputStream input = YamlEnvLoader.class.getClassLoader()
                .getResourceAsStream("application.yaml");
        Map<String, Object> config = loadWithEnv(input);

        // 打印完整配置结构
//        System.out.println("==== 解析后的配置结构 ====");
        new Yaml().dump(config, new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
        // 详细提取路径
        Map<?,?> llmServices = (Map<?,?>) config.get("spring");
        Map<?,?> profiles = (Map<?,?>) llmServices.get("profiles");

        String active = (String) profiles.get("active");


//        System.out.println("\nactive: " +active);
        return active;



    }

    public static Map<String, Object> loadWithEnv(InputStream input) {
        Yaml yaml = new Yaml();
        Map<String, Object> config = yaml.load(input);
        processMap(config);
        return config;
    }

    private static void processMap(Map<String, Object> map) {
        map.replaceAll((key, value) -> {
            try {
                return processValue(value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // 修改List处理部分的代码
    private static Object processValue(Object value) throws Exception {
        if (value instanceof String) {
            return replacePlaceholders((String) value);
        } else if (value instanceof Map) {
            processMap((Map<String, Object>) value);
            return value;
        } else if (value instanceof List) {
            processList((List<Object>) value); // 添加类型转换
            return value;
        }
        return value;
    }
    // 新增专门处理List的方法
    @SuppressWarnings("unchecked")
    private static void processList(List<Object> list) throws Exception {
        ListIterator<Object> iterator = list.listIterator();
        while (iterator.hasNext()) {
            Object element = iterator.next();
            iterator.set(processValue(element));
        }
    }
    // 修改replacePlaceholders方法
    private static String replacePlaceholders(String value) {
        Matcher matcher = ENV_PATTERN.matcher(value);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String varName = matcher.group("name");
            String defaultValue = matcher.group("default");
            String envValue = System.getenv(varName);

            if (envValue == null) {
                if (defaultValue != null) {
                    envValue = defaultValue;
                } else {
                    throw new ConfigException("[配置错误] 缺少必需环境变量: " + varName
                            + "\n💡 解决方案：请设置环境变量或添加默认值（示例）："
                            + "\n    export " + varName + "=your_api_key_here");
                }
            }
            matcher.appendReplacement(buffer, envValue.replace("\\", "\\\\"));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
