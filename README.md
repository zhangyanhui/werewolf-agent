# 基于LLM的狼人杀Agent

## 一、项目概述
本项目是一个创新的基于大语言模型（LLM）的狼人杀Agent实现。通过集成阿里云DashScope、智谱AI、OpenAI等多种主流LLM服务，模拟狼人杀游戏中各个角色的行为与决策过程，旨在为玩家打造更丰富、更具智能性的游戏体验。

## 二、项目配置

### API密钥配置
为保证项目正常运行，你需要配置对应的LLM服务API密钥。操作步骤如下：
1. 打开 `config.yaml` 文件。
2. 定位到 `llm.api_key` 字段。
3. 将其值替换为你自己的API密钥，支持的密钥类型有：
    - 阿里云DashScope的apiKey
    - 智谱AI的apiKey
    - OpenAI的apiKey

### 示例配置
以下是 `application.yaml` 文件的示例，可按需修改：
```yaml
llm:
  service: "dashscope"  # 可选择 dashscope、zhipuai、openai
  api_key: "your_api_key_here"
  model_name: "your_model_name"
  temperature: 0.7
```

## 三、项目依赖
运行此项目前，请确保已正确安装并配置以下依赖：
- Java开发环境（JDK 8及以上）
- Maven（用于项目构建和依赖管理）

## 四、项目结构
项目的主要目录结构如下：
```
werewolf-agent/
├── src/
│   ├── main/
│   │   ├── java/  # 主代码目录
│   │   └── resources/  # 资源文件目录
│   └── test/
│       └── java/  # 测试代码目录
├── application.yaml  # 配置文件
├── pom.xml  # Maven项目配置文件
└── README.md  # 项目说明文档
```

## 五、主要功能
1. **角色模拟**：精准模拟狼人杀游戏中的各类角色，如村民、狼人、预言家、女巫、猎人等。每个角色都具备独特的行为和决策逻辑，使游戏更具策略性和趣味性。
2. **游戏流程控制**：完整实现狼人杀游戏的各个环节，涵盖角色分配、天黑行动（狼人猎杀、预言家查验、女巫用药等）、天亮发言、投票等，确保游戏流程的顺畅进行。
3. **LLM集成**：借助调用不同的LLM服务，为角色的发言和决策提供智能支持，让游戏中的交互更加真实、自然，增强玩家的沉浸感。

## 六、使用方法
1. **克隆项目**：将项目克隆到本地
```bash
git clone https://github.com/zhangyanhui/werewolf-agent.git
cd werewolf-agent
```
2. **配置API密钥**：按照上述配置步骤，修改 `application.yaml` 文件。
3. **构建项目**：使用Maven构建项目
```bash
mvn clean install
```
4. **运行项目**：启动项目
```bash
java -cp target/classes:target/dependency/* me.junjiem.werewolf.agent.Main
```

## 七、参考与借鉴
本项目在开发过程中参考了以下项目：
- [https://github.com/ailijian/LLM-Werewolf](https://github.com/ailijian/LLM-Werewolf) 的狼人杀提示词，在此表示感谢。
- 本项目是在 [https://github.com/junjiem/werewolf-agent.git](https://github.com/junjiem/werewolf-agent.git) 的基础上进行二次创作，增加了模型间对战和GUI展示功能，感谢原作者的贡献。

## 八、贡献指南
如果你对本项目感兴趣并希望贡献代码，请按照以下步骤操作：
1. Fork本项目到你的个人仓库。
2. 创建一个新的分支，用于开发新功能或修复bug。
3. 提交代码并推送至你的个人仓库。
4. 向本项目的主仓库提交一个Pull Request，并详细描述你的更改内容和目的。

## 九、许可证
本项目采用 [许可证名称] 许可证，详细信息请查看 `LICENSE` 文件。


如果你在使用过程中遇到问题或有任何建议，欢迎通过以下方式联系我们：
- 邮箱：[yohannzhang@qq.com]
## 作者的其他项目，欢迎体验
- 公众号：[诗在盛唐]
  ![img.png](/assets/images/img.png)
- 小程序：[诗在盛唐]
  ![img_2.png](/assets/images/img_2.png)
- 小程序：[不用上班倒计时]
  ![img_3.png](/assets/images/img_3.png)
- 小程序：[模拟手机来电]
  ![img_5.png](/assets/images/img_5.png)
- 问题跟踪：[项目的Issue页面链接]

祝你游戏愉快！ 