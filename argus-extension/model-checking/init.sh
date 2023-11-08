if [ ! -d "model-files" ]; then
  mkdir model-files
fi

if [ ! -d "scan-result" ]; then
  mkdir scan-result
fi

# whether nodejs has been downloaded
type node > /dev/null 2>&1
if [[ $? -ne 0 ]]; then
  echo "nodejs not install"
  echo "nodejs installing ..."
  curl -sL https://deb.nodesource.com/setup_18.x | sudo bash -
  sudo apt install -y nodejs
fi

if [ ! -d "model-checking" ]; then
  mkdir model-checking
fi
# work folder
cd model-checking
# init project
if [ ! -f "package.json" ]; then
  npm init -y --quiet
fi

npm install model-checking@0.0.2

cp -r node_modules/model-checking/dist .