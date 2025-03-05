# 基于LLM的狼人杀Agent

## 项目概述
本项目是一个基于大语言模型（LLM）的狼人杀Agent实现。通过集成多种LLM服务，模拟狼人杀游戏中不同角色的行为和决策过程，为玩家带来更丰富、更智能的游戏体验。

## 项目配置

### API密钥配置
为了正常使用本项目，你需要配置相应的LLM服务的API密钥。具体操作如下：

1. 打开 `config.yaml` 文件。
2. 找到 `llm.api_key` 字段。
3. 将其值修改为你自己的API密钥，支持的API密钥类型包括：
    - 阿里云DashScope的apiKey
    - 智谱AI的apiKey
    - OpenAi的apiKey

### 示例配置
以下是 `config.yaml` 文件的示例内容：
```yaml
llm:
  service: "dashscope"  # 可选择 dashscope、zhipuai、openai
  api_key: "your_api_key_here"
  model_name: "your_model_name"
  temperature: 0.7
```

## 项目依赖
本项目使用了多种开源库和工具，确保在运行项目前已正确安装和配置以下依赖：

- Java 开发环境（JDK 8 及以上）
- Maven（用于项目构建和依赖管理）

## 项目结构
项目的主要目录结构如下：
```
werewolf-agent/
├── src/
│   ├── main/
│   │   ├── java/  # 主代码目录
│   │   └── resources/  # 资源文件目录
│   └── test/
│       └── java/  # 测试代码目录
├── config.yaml  # 配置文件
├── pom.xml  # Maven项目配置文件
└── README.md  # 项目说明文档
```

## 主要功能
- **角色模拟**：模拟狼人杀游戏中的各种角色，如村民、狼人、预言家、女巫、猎人等，每个角色都有其独特的行为和决策逻辑。
- **游戏流程控制**：实现了狼人杀游戏的完整流程，包括角色分配、天黑行动（狼人猎杀、预言家查验、女巫用药等）、天亮发言、投票等环节。
- **LLM集成**：通过调用不同的LLM服务，为角色的发言和决策提供智能支持，使游戏更加真实和有趣。

## 使用方法
1. 克隆本项目到本地：
```bash
git clone <项目仓库地址>
cd werewolf-agent
```
2. 配置API密钥：按照上述步骤配置 `config.yaml` 文件。
3. 构建项目：
```bash
mvn clean install
```
4. 运行项目：
```bash
java -cp target/classes:target/dependency/* me.junjiem.werewolf.agent.Main
```

## 参考与借鉴
本项目在实现过程中参考了 [https://github.com/ailijian/LLM-Werewolf](https://github.com/ailijian/LLM-Werewolf) 的狼人杀提示词，在此表示感谢。
本项目是在 [https://github.com/junjiem/werewolf-agent.git](https://github.com/junjiem/werewolf-agent.git)的基础上进行的二创，增加了模型间对战和gui展示功能 ，在此表示感谢。


## 贡献指南
如果你对本项目感兴趣并希望贡献代码，可以按照以下步骤进行：

1.  Fork 本项目到你的个人仓库。
2.  创建一个新的分支，用于开发你的功能或修复bug。
3.  提交你的代码并推送至你的个人仓库。
4.  向本项目的主仓库提交一个Pull Request，详细描述你的更改内容和目的。

## 许可证
本项目使用 [许可证名称] 许可证，详细信息请查看 `LICENSE` 文件。

## 联系我们
如果你在使用过程中遇到任何问题或有任何建议，欢迎通过以下方式联系我们：

- 邮箱：[yohannzhang@qq.com]
- 公众号：[诗在盛唐]
  ![img.png](/assets/images/img.png)
- 小程序：[诗在盛唐]
  ![img_2.png](/assets/images/img_2.png)
- 小程序：[不用上班倒计时]
  ![img_3.png](/assets/images/img_3.png)
- 小程序：[模拟手机来电]
  ![img_5.png](/assets/images/img_5.png)
- 问题跟踪：[项目的Issue页面链接]
```

通过以上信息，你可以更方便地了解和使用本项目。祝你游戏愉快！