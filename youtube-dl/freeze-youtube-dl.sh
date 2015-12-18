#!/usr/bin/env bash

base_dir=$(pwd)
parent_dir=$base_dir/../
exec_dir="youtube-dl-exec"
module_dir="source/youtube-dl/youtube_dl"
source_dir="source"
clone_dir="youtube-dl"
clone_exec_dir="youtube_dl"
freeze_setup="freeze-setup"

echo "Cleaning directories..."
rm -rf $parent_dir/$exec_dir/
rm -rf $source_dir/$clone_dir

echo "Entered "$(pwd)

echo "Cloning latest youtube-dl..."
cd $source_dir
echo "Entered "$(pwd)
git clone https://github.com/rg3/youtube-dl.git

echo "Copying freeze-setup files..."
cp $freeze_setup/* $clone_dir/$clone_exec_dir/

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

cd $clone_dir/$clone_exec_dir/
if [ $? -eq 0 ]; then
    clean_freeze
fi

if [ $? -eq 0 ]; then
    move_build_dir
    echo "Cleaning cloned sources..."
    cd $base_dir
    rm -rf $source_dir/$clone_dir
    echo "Done."
else
    echo "Freeze failed"
fi