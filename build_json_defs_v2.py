import os
import json
import hashlib
import re
import argparse
import datetime

parser = argparse.ArgumentParser()
parser.add_argument("outFile", help="JSON output file")
parser.add_argument("libDir", help="Directory for the libraries")
parser.add_argument("gameJar", help="Game Jar")
parser.add_argument("gameJarDir", help="Directory for game Jar")
parser.add_argument("ver", help="Game Version")
args = parser.parse_args()


def sha1sum(filename):
    h = hashlib.sha1()
    b = bytearray(128 * 1024)
    mv = memoryview(b)
    try:
        with open(filename, 'rb', buffering=0) as f:
            for n in iter(lambda: f.readinto(mv), 0):
                h.update(mv[:n])
        return h.hexdigest()
    except FileNotFoundError as e:
        return 0


lib_dir = os.path.abspath(args.libDir)
files = os.listdir(lib_dir)

out = {
    "arguments": {
        "game": [
            "--username",
            "${auth_player_name}",
            "--version",
            "${version_name}",
            "--gameDir",
            "${game_directory}",
            "--uuid",
            "${auth_uuid}",
            "--accessToken",
            "${auth_access_token}",
            "--assetsDir",
            "${assets_root}",
            {
                "rules": [{"action": "allow", "features": {"is_demo_user": True}}],
                "value": "--demo"
            },
            {
                "rules": [{"action": "allow", "features": {"has_custom_resolution": True}}],
                "value": ["--width", "${resolution_width}", "--height", "${resolution_height}"]
            }
        ],
        "jvm": [
            {
                "rules": [{"action": "allow", "os": {"name": "osx"}}],
                "value": ["-XstartOnFirstThread"]
            },
            {
                "rules": [{"action": "allow", "os": {"name": "windows", "version": "^10\\."}}],
                "value": ["-Dos.name=Windows 10", "-Dos.version=10.0"]
            },
            {
                "rules": [{"action": "allow", "os": {"arch": "x86"}}],
                "value": "-Xss1M"
            },
            "-Djava.library.path=${natives_directory}",
            "-Dlauncher.brand=${launcher_name}",
            "-Dlauncher.version=${launcher_version}",
            "-cp",
            "${classpath}"
        ]
    },
    "complianceLevel": 0,
    "downloads": {
        "client": {
            "sha1": sha1sum(os.path.join(args.gameJarDir, args.gameJar)),
            "size": os.stat(os.path.join(args.gameJarDir, args.gameJar)).st_size,
            "url": "https://minersonline.ddns.net/files/history_survival/packages/" + args.ver + "/" + args.gameJar
        }
    },
    "id": "hs-" + args.ver,
    "javaVersion": {"component": "java-runtime-beta", "majorVersion": 17},
    "libraries": [None] * len(files),
    "mainClass": "net.ddns.minersonline.HistorySurvival.Game",
    "minimumLauncherVersion": 21,
    "releaseTime": datetime.datetime.now().astimezone().replace(microsecond=0).isoformat(),
    "time": datetime.datetime.now().astimezone().replace(microsecond=0).isoformat(),
    "type": "release"
}


def build_lib(name: str, ver: str, file: str):
    if ver.endswith("."):
        ver = ver[:-1]

    lib = {
        "downloads": {},
        "name": "HistorySurvival:" + name + ":" + ver,
    }

    has_natives = False
    lib_os = ""

    if "osx" in file:
        has_natives = True
        lib_os = "osx"
    if "macos" in file:
        has_natives = True
        lib_os = "osx"
    if "linux" in file:
        has_natives = True
        lib_os = "linux"
    if "windows" in file:
        has_natives = True
        lib_os = "windows"

    if has_natives:
        lib = {
            "downloads": {},
            "name": "HistorySurvival:natives-" + lib_os + ":" + name + ":" + ver,
        }

    lib["downloads"]["artifact"] = {
        "path": "HistorySurvival/" + file,
        "sha1": sha1sum(os.path.join(lib_dir, file)),
        "size": os.stat(os.path.join(lib_dir, file)).st_size,
        "url": "https://minersonline.ddns.net/files/libs/" + file
    }

    if has_natives:
        lib["rules"] = [
            {
                "action": "allow",
                "os": {
                    "name": lib_os
                }
            }
        ]

    return lib


natives = {}

file_index = 0
for file in files:
    split_name = re.search(r'([ A-Za-z0-9-_]*?)-([0-9.]*)([ A-Za-z0-9-_]*?).jar', file)

    lib = split_name.group(1)
    ver = split_name.group(2)

    out["libraries"][file_index] = build_lib(lib, ver, file)
    file_index += 1

out["libraries"] = [x for x in out["libraries"] if x is not None]

with open(os.path.abspath(args.outFile), "w") as outfile:
    json.dump(out, outfile, indent=4)
