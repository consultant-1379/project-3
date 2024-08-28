import sys
import os
import requests

BUILD_SCRIPT_DIR = os.path.realpath(os.path.dirname(sys.argv[0]))
OKO_DIR = BUILD_SCRIPT_DIR + "/../oko"

def clean():
    os.system("docker container prune -f")
    os.system("docker image rm oko/builder")
    os.system("docker image rm oko/runner")

def build_no_test():
    clean()
    os.chdir(OKO_DIR)
    os.system("docker build -f ../buildScripts/Dockerfile-build-notest -t oko/builder .")
    build_runner()

def build_test():
    clean()
    os.chdir(OKO_DIR)
    os.system("docker build -f ../buildScripts/Dockerfile-build -t oko/builder .")
    build_runner()

def build_runner():
    os.chdir(OKO_DIR)
    os.system("docker build -f ../buildScripts/Dockerfile-runner -t oko/runner .")

TARGETS = {
    "clean" : clean,
    "build_no_test" : build_no_test,
    "build_test" : build_test,
}

DEFAULT_TARGET = "notest"

def main():
    target = DEFAULT_TARGET
    try:
        target = sys.argv[1]
    except:
        print(f"Proceeding with default target: {target}")
    
    try:
        buildFunc = TARGETS[target]
    except:
        print(f"Invalid target {target} specified. Valid targets are {TARGETS.keys()}")
        return
    buildFunc()

if __name__ == "__main__":
    main()