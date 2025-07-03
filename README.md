# SpringAI 学习测试 Demo 项目

本项目是一个基于 Spring Boot 和 SpringAI 的学习与测试 Demo。
旨在帮助开发者快速了解和实践 SpringAI 在实际业务场景中的应用。
项目涵盖了`AI 聊天、嵌入向量、多模态、多文档阅读器、提示词、向量数据库、RAG 检索增强生成、ETL技术、结构化输出、文档读取、音频处理、图片生成、ToolCalling、MCP`等多种样例代码。

各位可以根据目前给出的样例代码结合英文文档学习，当然我也提供了中文文档：

中文文档参考（目前正在手操中文文档）：https://hcnxcb6dq341.feishu.cn/wiki/TghiwB6vai9KcRkb7t5cxjdjnBY?from=from_copylink

---

## 目录结构

```
AIDevelop/
├── pom.xml                # 父项目配置
├── AI/
│   ├── pom.xml            # 子模块配置
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/springai/ai/
│   │   │   │   ├── model/         # 业务模型与接口
│   │   │   │   ├── config/        # 配置类
│   │   │   │   ├── reader/        # 文档读取与处理
│   │   │   │   ├── VectorSQL/     # 向量数据库相关
│   │   │   │   ├── multimodal/    # 多模态相关
│   │   │   │   ├── structuredOutput/  # 结构化输出
│   │   │   │   ├── RAG/           # RAG 相关
│   │   │   │   ├── Prompt/        # Prompt 相关
│   │   │   │   ├── toolCall/        # toolCall 相关
│   │   │   │   ├── ChatMemory/        # ChatMemory 相关
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── test/
└── ...
```

## 主要功能模块

- **AI 聊天**  
  集成 ChatClient，支持上下文记忆、系统提示、Advisor 拦截器等功能。示例代码见 `model/`。
- **向量数据库**  
  支持 Neo4j、MongoDB、ElasticSearch 等多种向量存储，提供文档嵌入、相似度检索等能力。示例代码见 `VectorSQL/` 目录。
- **RAG 检索增强生成 与 ETL技术**  
  实现多种 QueryTransformer、Retriever、Advisor 组合，支持复杂的检索增强生成流程。示例代码见 `RAG/`。
- **文档读取与处理**  
  支持 TXT、PDF、Markdown、HTML、Tika 等多种文档格式的读取与向量化。示例代码见 `reader/` 目录。
- **音频处理**  
  集成 OpenAI 语音模型，支持语音转文本、文本转语音。示例代码见 `model/`。
- **图片生成与处理**  
  支持图片生成、图片消息等。示例代码见 `model/ImageTest.java`。
- **多模态**  
    多模态相关示例代码。见 `multimodal/`。
- **多文档阅读器**
    MD、HTML、PDF、TXT 等，见 `reader/` 目录。
- **提示词**
    提示词模板，见 `Prompt/` 目录。
- **结构化输出**
    三个实现类，List、Map、Bean实体类，见 `model/`。
- **函数调用**
    工具调用，见 `toolCall/`。
- **ChatMemory**
    聊天记忆，见 `ChatMemory/`。

## 环境依赖

- JDK 17 及以上
- Spring Boot 3.4.3
- SpringAI 1.0.0-M8 及相关依赖（Neo4j、MongoDB、PDF、Markdown、Tika、Jsoup 等）
- Maven

## 快速开始

1. **配置依赖**  
   参考 `pom.xml` 和 `AI/pom.xml`，已集成 SpringAI 及相关依赖。
2. **配置 application.yml**  
   在 `AI/src/main/resources/application.yml` 配置 Neo4j、OpenAI 等相关参数。
3. **接口测试**  
   通过 Postman 或其他工具访问 `/api/chat`、`/api/EmbeddingTest`、`/api/Audio` 等接口，体验各类 AI 能力。

## 参考文档

- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [SpringAI 官方文档](https://docs.spring.io/spring-ai/reference/)
- [OpenAI Chat API](https://docs.spring.io/spring-ai/reference/api/chat/openai-chat.html)
- [Spring Web](https://docs.spring.io/spring-boot/3.4.3/reference/web/servlet.html)
- [Maven 官方文档](https://maven.apache.org/guides/index.html)

## 常见问题

- **Maven 父 POM 继承问题**  
  由于 Maven 的设计，项目 POM 会继承父 POM 的元素（如 `<license>` 和 `<developers>`）。如需自定义这些元素，可在项目 POM 中进行空覆盖。如果切换父 POM且需要继承，需移除这些覆盖。

---

如需详细功能说明或代码解读，请查阅各模块源码或接口注释。
