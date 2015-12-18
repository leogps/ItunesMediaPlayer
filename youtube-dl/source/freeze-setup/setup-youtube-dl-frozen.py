#!/usr/bin/env python

import sys
from cx_Freeze import setup, Executable

import os.path

path = os.path.realpath(os.path.abspath(__file__))
sys.path.insert(0, os.path.dirname(os.path.dirname(path)))

import youtube_dl

setup(  name = "youtube_dl_frozen",
        version = "0.1",
        description = "youtube_dl_frozen",
        executables = [Executable("youtube-dl.py", includes=youtube_dl)])