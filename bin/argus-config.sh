#!/usr/bin/env bash

# resolve links - $0 may be a softlink
this="${BASH_SOURCE-$0}"
common_bin=$(cd -P -- "$(dirname -- "${this}")" && pwd -P)
script="$(basename -- "${this}")"
this="${common_bin}/${script}"

# convert relative path to absolute path
config_bin=$(dirname "${this}")
script=$(basename "${this}")
config_bin=$(cd "${config_bin}"; pwd)
this="${config_bin}/${script}"

# Set Argus version from generated script
. ${config_bin}/version.sh

# This will set the default installation for a tarball installation while os distributors can
# set system installation locations.
ARGUS_HOME=$(dirname $(dirname "${this}"))
ARGUS_ASSEMBLY_SERVER_JAR="${ARGUS_HOME}/argus-assembly/argus-assembly-http-server/target/argus-assembly-http-server-${VERSION}-with-dependencies.jar"
ARGUS_ASSEMBLY_EXTENSION_DEMO_JAR="${ARGUS_HOME}/argus-assembly/argus-assembly-extension-demo/target/argus-assembly-extension-demo-${VERSION}-with-dependencies.jar"

# Check if java is found
if [[ -z "${JAVA}" ]]; then
  if [[ -n "${JAVA_HOME}" ]] && [[ -x "${JAVA_HOME}/bin/java" ]];  then
    JAVA="${JAVA_HOME}/bin/java"
  elif [[ -n "$(which java 2>/dev/null)" ]]; then
    JAVA=$(which java)
  else
    echo "Error: Cannot find 'java' on path or under \$JAVA_HOME/bin/. Please set JAVA_HOME in user bash profile."
    exit 1
  fi
fi

# Check Java version == 17
JAVA_VERSION=$(${JAVA} -version 2>&1 | awk -F '"' '/version/ {print $2}')
JAVA_MAJORMINOR=$(echo "${JAVA_VERSION}" | awk -F. '{printf("%03d%03d",$1,$2);}')
JAVA_MAJOR=$(echo "${JAVA_VERSION}" | awk -F. '{printf("%03d",$1);}')
if [[ ${JAVA_MAJORMINOR} != 017000 && ${JAVA_MAJOR} != 017 ]]; then
  echo "Error: Argus requires Java 17, currently Java $JAVA_VERSION found."
  exit 1
fi
