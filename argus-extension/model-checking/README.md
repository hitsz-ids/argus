[![License](https://img.shields.io/badge/license-Apache%202-686FFF.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![CN doc](https://img.shields.io/badge/文档-中文版-686FFF.svg)](README_zh_CN.md)
[![Slack](https://img.shields.io/badge/slack-Join%20Argus-686FFF.svg?logo=slack)](https://join.slack.com/t/hitsz-ids/shared_invite/zt-2395mt6x2-dwf0j_423QkAgGvlNA5E1g)
## 🚀 Introduction

model-checking is an open source project for model scanning

## Getting started

### config
You should cut the `argus-module.properties.template` file in the resources directory into the `argus-module.properties` file and change it to your local path

### install
You should execute the `init.sh` script to install the model scanning script

### use
#### save a model file
Store model files in the `model-files` directory, for example
* [onnx](https://media.githubusercontent.com/media/onnx/models/main/vision/classification/squeezenet/model/squeezenet1.0-3.onnx)
* [TensorFlow Lite](https://huggingface.co/thelou1s/yamnet/resolve/main/lite-model_yamnet_tflite_1.tflite)
* [Core ML](https://raw.githubusercontent.com/Lausbert/Exermote/master/ExermoteInference/ExermoteCoreML/ExermoteCoreML/Model/Exermote.mlmodel)
* [Darknet](https://raw.githubusercontent.com/AlexeyAB/darknet/master/cfg/yolo.cfg)

#### send a post request to validate model
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

#### view model result
Currently, the function of viewing model results has not been completed, but you can find the scan result file in the `scan-result` directory by converting the file name to base64 encoding.

## 🤝 Join Community

Join our data sandbox community to explore related technologies and grow together. We welcome organizations, teams, and individuals who share our commitment to data protection and security through open source.
