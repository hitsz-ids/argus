#!/bin/bash

output=output

rm -rf "$output"
mkdir "$output"
mkdir "$output"/ca
mkdir "$output"/center
mkdir "$output"/module
subj="/C=CN/ST=China/L=Shenzhen/O=argus.ids.io/OU=argus.ids.io/CN=argus/emailAddress=argus@ids.io"
openssl genrsa -out "$output"/ca/ca.key 2048
openssl req -new -x509 -key "$output"/ca/ca.key -out "$output"/ca/ca.pem -subj $subj

openssl genrsa -out "$output"/module/module.key 2048
openssl req -new -key "$output"/module/module.key -out "$output"/module/module.csr -subj $subj
openssl x509 -req -sha256 -CA "$output"/ca/ca.pem -CAkey "$output"/ca/ca.key -CAcreateserial -days 3650 -in "$output"/module/module.csr -out "$output"/module/module.pem
openssl pkcs8 -topk8 -nocrypt -in "$output"/module/module.key -out "$output"/module/module-pkcs8.key

openssl genrsa -out "$output"/center/center.key 2048
openssl req -new -key "$output"/center/center.key -out "$output"/center/center.csr -subj $subj

openssl x509 -req -sha256 -CA "$output"/ca/ca.pem -CAkey "$output"/ca/ca.key -CAcreateserial -days 3650 -in "$output"/center/center.csr -out "$output"/center/center.pem
openssl pkcs8 -topk8 -nocrypt -in "$output"/center/center.key -out "$output"/center/center-pkcs8.key