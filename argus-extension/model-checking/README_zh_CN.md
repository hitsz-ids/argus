[![License](https://img.shields.io/badge/license-Apache%202-5BB9BC.svg)](https://www.apache.org/licenses/LICENSE-2.0.html) [![EN doc](https://img.shields.io/badge/document-English-5BB9BC.svg)](README.md)

## 🚀 简介

model-checking 是一个用于模型扫描的开源项目

## 入门

### 配置
* 执行下面脚本
```shell
export ARGUS_MODEL_CHECKING_RESOURCES_PATH='argus-extension/model-checking/src/main/resources'

cp ${ARGUS_MODEL_CHECKING_RESOURCES_PATH}/argus-module.properties.template ${ARGUS_MODEL_CHECKING_RESOURCES_PATH}/argus-module.properties

echo '' > ${ARGUS_MODEL_CHECKING_RESOURCES_PATH}/argus-module.properties
echo "center.host=127.0.0.1" >> ${ARGUS_MODEL_CHECKING_RESOURCES_PATH}/argus-module.properties
echo "center.port=9999" >> ${ARGUS_MODEL_CHECKING_RESOURCES_PATH}/argus-module.properties
echo "module.name=modelchecking" >> ${ARGUS_MODEL_CHECKING_RESOURCES_PATH}/argus-module.properties
echo "module.version=1.0.0" >> ${ARGUS_MODEL_CHECKING_RESOURCES_PATH}/argus-module.properties
echo "module.public=${ARGUS_HOME}/output/module/module.pem" >> ${ARGUS_MODEL_CHECKING_RESOURCES_PATH}/argus-module.properties
echo "module.pkcs8=${ARGUS_HOME}/output/module/module-pkcs8.key" >> ${ARGUS_MODEL_CHECKING_RESOURCES_PATH}/argus-module.properties
echo "ca.public=${ARGUS_HOME}/output/ca/ca.pem" >> ${ARGUS_MODEL_CHECKING_RESOURCES_PATH}/argus-module.properties
```

### 安装
* 您应该执行 `model-checking-config.sh` 脚本以安装模型扫描工程

### 使用
#### 插件中心添加配置
* 修改文件 `src/main/resources/argus-center.properties`
```text
modules=module://demo?version=1.0.0;2.0.0,module://modelchecking?version=1.0.0;
```

#### 上传模型文件
1. 下载下面的样例模型文件
* [onnx](https://media.githubusercontent.com/media/onnx/models/main/vision/classification/squeezenet/model/squeezenet1.0-3.onnx)
* [TensorFlow Lite](https://huggingface.co/thelou1s/yamnet/resolve/main/lite-model_yamnet_tflite_1.tflite)
* [Core ML](https://raw.githubusercontent.com/Lausbert/Exermote/master/ExermoteInference/ExermoteCoreML/ExermoteCoreML/Model/Exermote.mlmodel)
* [Darknet](https://raw.githubusercontent.com/AlexeyAB/darknet/master/cfg/yolo.cfg)
2. 调用接口上传模型文件
* url: http://127.0.0.1:9000/argus/upload
* method: PUT
* body:
```json
{
    "module": "modelchecking",
    "directory":"model",
    "fileName": "candy.onnx",
    "file":"candy.onnx" // 文件内容
}
```

#### 扫描模型
* url: http://127.0.0.1:9000/argus/execute
* method: POST
* body:
```json
{
    "path": "modelchecking/1.0.0/model-checking/scan",
    "params":{
        "path":"candy.onnx"
    }
}
```

#### 查看模型扫描结果
* url: http://127.0.0.1:9000/argus/execute
* method: POST
* body:
```json
{
  "path": "modelchecking/1.0.0/model-checking/status",
  "params": {"seq":"seq-id"}
}
```
* 从数据库中查找到seq-id，根据seq-id查看模型扫描结果

## 🤝 加入社区
加入我们的数据沙盒社区，共同探索相关技术并共同成长。我们欢迎致力于通过开源保护数据和安全且与我们志同道合的组织、团队和个人。


