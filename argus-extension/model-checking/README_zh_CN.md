[![License](https://img.shields.io/badge/license-Apache%202-5BB9BC.svg)](https://www.apache.org/licenses/LICENSE-2.0.html) [![EN doc](https://img.shields.io/badge/document-English-5BB9BC.svg)](README.md)

## 🚀 简介

model-checking 是一个用于模型扫描的开源项目

## 入门

### 配置
您应该将资源目录中的 `argus-module.properties.template` 文件剪切到 `argus-module.properties` 文件中，并将其更改为您的本地路径

### 安装
您应该执行 `init.sh` 脚本以安装模型扫描脚本

### 使用
#### 保存模型文件
将模型文件存储在 `model-files` 目录中，例如：
* [onnx](https://media.githubusercontent.com/media/onnx/models/main/vision/classification/squeezenet/model/squeezenet1.0-3.onnx)
* [TensorFlow Lite](https://huggingface.co/thelou1s/yamnet/resolve/main/lite-model_yamnet_tflite_1.tflite)
* [Core ML](https://raw.githubusercontent.com/Lausbert/Exermote/master/ExermoteInference/ExermoteCoreML/ExermoteCoreML/Model/Exermote.mlmodel)
* [Darknet](https://raw.githubusercontent.com/AlexeyAB/darknet/master/cfg/yolo.cfg)

#### 发送 POST 请求以验证模型
* url: http://127.0.0.1:9000/argus/execute
* body:
```json
{
    "path": "modelchecking/1.0.0/model-checking/validate",
    "params":{
        "path":"candy.onnx"
    }
}
```

#### 查看模型结果
目前，查看模型结果的功能尚未完成，但您可以通过将文件名转换为 base64 编码，在 `scan-result` 目录中找到扫描结果文件。

## 🤝 加入社区
加入我们的数据沙盒社区，共同探索相关技术并共同成长。我们欢迎致力于通过开源保护数据和安全且与我们志同道合的组织、团队和个人。


