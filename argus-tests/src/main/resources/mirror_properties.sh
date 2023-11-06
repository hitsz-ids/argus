
# back to root
cd ../../../../

# argus-module.properties
if [ -f "argus-extension/demo/src/main/resources/argus-module.properties" ]; then
    echo "argus-module.properties already exists."
else
    touch "argus-extension/demo/src/main/resources/argus-module.properties"
    cp "argus-extension/demo/src/main/resources/argus-module.properties" "argus-extension/demo/target/classes/"
    echo "empty argus-module.properties generated."
fi

# argus-center.properties
if [ -f "argus-platform/argus-http-server/src/main/resources/argus-center.properties" ]; then
    echo "argus-center.properties already exists."
else
    touch "argus-platform/argus-http-server/src/main/resources/argus-center.properties"
    cp "argus-platform/argus-http-server/src/main/resources/argus-center.properties" "argus-platform/argus-http-server/target/classes/"
    echo "empty argus-center.properties generated."
fi
