<div align="center">
<br />
<img src="doc/argus-logo.jpg" width="365" >
<br />

[![License](https://img.shields.io/badge/license-Apache%202-686FFF.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![CN doc](https://img.shields.io/badge/文档-中文版-686FFF.svg)](README_zh_CN.md)
[![Slack](https://img.shields.io/badge/slack-Join%20Argus-686FFF.svg?logo=slack)](https://join.slack.com/t/hitsz-ids/shared_invite/zt-2395mt6x2-dwf0j_423QkAgGvlNA5E1g)
</div>


## 🚀 Introduction

Argus is a result review engine that prevents data leakage and ensures out-of-domain result traceability with functions such as sensitive data identification, data and AI model watermarking, and AI model-entrapped data scanning. It's the last line of defense for data leaving the domain, providing users with a strong guarantee.

Check out our website for detailed information: https://argus.idslab.io/

## 💻 Architecture Overview

<img src="doc/argus-architecture-overview.png">

## 🎉 Features

- Ease of use - Visualizing AI model structures and out-of-domain data, empowering users to accurately analyze and make informed decisions.
- Data traceability - By using data watermarking, Argus identifies data sources and tracks data flow to ensure the legality and traceability of data.
- Model traceability - Argus uses AI model watermarking to track model usage and authorization, ensuring legality and controllability.
- Strong scalability - Pluggable plug-in technology allows users to customize data detection tools for different extensions, providing scalability.
- Rule customization -  Users can customize data outbound rules to detect and process data according to their own requirements.
- Cloud-native - Argus supports Kubernetes deployment and provides powerful automation capabilities.

## 🤝 Join Community

Join argus community to explore related technologies and grow together. We welcome organizations, teams, and individuals who share our commitment to data protection and security through open source.

This is our QQ Group:  
<img src="doc/argus-qq-group.jpg" width="256">




会议纪要：
1、ModelScanJob封装多一个commandJob；统一处理runtime,stop,fail等方法
2、插件代码写完后还需要写好单测一起提
3、文件存储在store里面统一做，存储目录固定写在Argus项目根目录下就好了
4、存储目录下的插件目录名称需要有一个规范，例如 插件名称@版本号； model-checking-extension@0-0-1, 并且插件名称校验
5、在io/ids/argus/entry/controller/HttpController.java 添加一个上传文件接口 put, 存储到固定目录（根据参数判断extension）
6、在io/ids/argus/entry/controller/HttpController.java 添加一个下载文件接口 get
7、后续可以变为每个扫描job变为一次docker容器来扫描
8、代码质量优先


除了extension，还会有什么模块会需要用到上传下载接口吗


待确认问题
1、storage这个名称，在store-server使用配置文件存储，然后store-client请求获取目录名称的方式去做？
或者干脆把整个系统的配置文件一起改了，做到一起可以吗？
2、~~extensions 这个名称，也配置成常量？怎么做比较好？~~
3、module name validate
4、日志还没有处理，很多地方还是使用new Exception这样的写法
5、函数还没写注释
6、保存的结果文件名称也叫模型文件名称是否可以？
7、目前没有做分片上传，限制上传100MB大小的模型 (grpc的是)
8、~~目前的grpc的下载还有问题，没有实现分片~~
9、客户端和服务端 报错打印问题 - 测试&开发
10、分片上传下载的单测代码，还有高并发的测试代码
3MB改为4MB试一下
如果全部使用计时器 condition, 那么初始值应该文件分片数量
11、60s配置
