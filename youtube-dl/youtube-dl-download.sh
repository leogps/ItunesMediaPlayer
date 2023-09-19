#!/usr/bin/env bash

echo "detecting os..."
detected_os="win"
if [[ "$OSTYPE" == "linux-gnu" ]]; then
  detected_os="linux"
elif [[ "$OSTYPE" == "darwin"* ]]; then
  detected_os="macos"
fi
echo "detected os: $detected_os"

base_dir=$(pwd)
parent_dir=$base_dir/../
exec_dir="youtube-dl-exec"

downloadable_file="yt-dlp_$detected_os"
url="https://github.com/yt-dlp/yt-dlp/releases/latest/download/$downloadable_file"
echo "downloading from $downloadable_file..."

mkdir -p "$parent_dir"/$exec_dir/
cd $parent_dir/$exec_dir/
echo "Entered "$(pwd)
curl -L $url -o youtube-dl
chmod a+x youtube-dl
./youtube-dl --help