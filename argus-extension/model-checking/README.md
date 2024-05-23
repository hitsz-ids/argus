[![License](https://img.shields.io/badge/license-Apache%202-686FFF.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![CN doc](https://img.shields.io/badge/文档-中文版-686FFF.svg)](README_zh_CN.md)
[![Slack](https://img.shields.io/badge/slack-Join%20Argus-686FFF.svg?logo=slack)](https://join.slack.com/t/hitsz-ids/shared_invite/zt-2395mt6x2-dwf0j_423QkAgGvlNA5E1g)
## 🚀 Introduction

model-checking is an open source project for model scanning

## Getting started

### Config
* execute the following script
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

### Install
* You should execute the `model-checking-config.sh` script to install the model scanning project

### use
#### Add configuration to plugin center
* modify the file `src/main/resources/argus-center.properties`
```text
modules=module://demo?version=1.0.0;2.0.0,module://modelchecking?version=1.0.0;
```

#### Upload model file
1. Download the sample model file below
* [onnx](https://media.githubusercontent.com/media/onnx/models/main/vision/classification/squeezenet/model/squeezenet1.0-3.onnx)
* [TensorFlow Lite](https://huggingface.co/thelou1s/yamnet/resolve/main/lite-model_yamnet_tflite_1.tflite)
* [Core ML](https://raw.githubusercontent.com/Lausbert/Exermote/master/ExermoteInference/ExermoteCoreML/ExermoteCoreML/Model/Exermote.mlmodel)
* [Darknet](https://raw.githubusercontent.com/AlexeyAB/darknet/master/cfg/yolo.cfg)
2. Call HTTP request to upload model file
* url: http://127.0.0.1:9000/argus/upload
* method: PUT
* body:
```json
{
    "module": "modelchecking",
    "directory":"model",
    "fileName": "candy.onnx",
    "file": "candy.onnx" // file content
}
```

#### scan model
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

#### View model scan result
* url: http://127.0.0.1:9000/argus/execute
* method: POST
* body:
```json
{
  "path": "modelchecking/1.0.0/model-checking/status",
  "params": {"seq":"seq-id"}
}
```
* Find the seq-id from the database and view the model scan results based on the seq-id.

## 🤝 Join Community

Join our data sandbox community to explore related technologies and grow together. We welcome organizations, teams, and individuals who share our commitment to data protection and security through open source.
