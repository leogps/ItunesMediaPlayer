#!/usr/bin/env bash

base_dir=$(pwd)
parent_dir=$base_dir/../
exec_dir="youtube-dl-exec"
module_dir="source/youtube-dl/youtube_dl"

rm -rf $parent_dir/$exec_dir/

echo "TODO: git clone if source not available."
echo "Freezing youtube-dl..."

clean_freeze() {
    rm -rf build
    python setup-youtube-dl-frozen.py build
}

move_build_dir() {
    mv build/exe.* $parent_dir/$exec_dir/
    cd $parent_dir/$exec_dir/
    chmod +x youtube-dl
    ./youtube-dl --help
    if [ $? -eq 0 ]; then
        echo "Freeze successful. Available at: " $parent_dir/$exec_dir/
    fi
}

cd $module_dir
if [ $? -eq 0 ]; then
    clean_freeze
fi

if [ $? -eq 0 ]; then
    move_build_dir
else
    echo "Freeze failed"
fi