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
    with open(filename, 'rb', buffering=0) as f:
        for n in iter(lambda: f.readinto(mv), 0):
            h.update(mv[:n])
    return h.hexdigest()


lib_dir = args.libDir
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
            "url": "https://minersonline.ddns.net/files/history_survival/packages/"+args.ver+"/"+args.gameJar
        }
    },
    "id": "hs-"+args.ver,
    "javaVersion": {"component": "java-runtime-beta", "majorVersion": 17},
    "libraries": [None] * len(files),
    "mainClass": "net.ddns.minersonline.HistorySurvival.Game",
    "minimumLauncherVersion": 21,
    "releaseTime": datetime.datetime.now().astimezone().replace(microsecond=0).isoformat(),
    "time": datetime.datetime.now().astimezone().replace(microsecond=0).isoformat(),
    "type": "release"
}
natives = {}

file_index = 0
for file in files:
    if "natives" not in file:
        has_natives = False
        split_name = re.search(r'([ A-Za-z-]*?)-([0-9.]*?)([ A-Za-z-]*?).jar', file)
        lib = split_name.group(1)
        ver = split_name.group(2)

        if lib+"-"+ver+"-natives-linux.jar" in files:
            has_natives = True
        if lib+"-"+ver+"-natives-macos.jar" in files:
            has_natives = True
        if lib+"-"+ver+"-natives-windows.jar" in files:
            has_natives = True

        if has_natives:
            lib_os = "all"
            natives[file.replace(".jar", "")]["natives"][lib_os] = file
            continue

        out["libraries"][file_index] = {
            "downloads": {
                "artifact": {
                    "path": "HistorySurvival/" + file,
                    "sha1": sha1sum(os.path.join(lib_dir, file)),
                    "size": os.stat(os.path.join(lib_dir, file)).st_size,
                    "url": "https://minersonline.ddns.net/files/libs/" + file
                }
            },
            "name": "HistorySurvival:"+lib+":"+ver
        }
        file_index += 1
    else:
        native = file.split("-natives-")
        lib_os = native[1].replace(".jar", "")
        lib = native[0]
        if lib not in natives:
            natives[lib] = {"natives": {}}

        natives[lib]["natives"][lib_os] = file

for native in natives:
    print(native, natives[native])

    linux = natives[native]["natives"]["linux"]
    macos = natives[native]["natives"]["macos"]
    windows = natives[native]["natives"]["windows"]
    lib_all = natives[native]["natives"]["all"]

    name, ver = native.rsplit('-', 1)

    lib = {
        "downloads": {
            "artifact": {
                "path": "HistorySurvival/"+lib_all,
                "sha1": sha1sum(os.path.join(lib_dir, lib_all)),
                "size": os.stat(os.path.join(lib_dir, lib_all)).st_size,
                "url": "https://minersonline.ddns.net/files/libs/"+lib_all
            },
            "classifiers": {
                "natives-linux": {
                    "path": "HistorySurvival/"+linux,
                    "sha1": sha1sum(os.path.join(lib_dir, linux)),
                    "size": os.stat(os.path.join(lib_dir, linux)).st_size,
                    "url": "https://minersonline.ddns.net/files/libs/"+linux
                },
                "natives-osx": {
                    "path": "HistorySurvival/"+macos,
                    "sha1": sha1sum(os.path.join(lib_dir, macos)),
                    "size": os.stat(os.path.join(lib_dir, macos)).st_size,
                    "url": "https://minersonline.ddns.net/files/libs/"+macos
                },
                "natives-windows": {
                    "path": "HistorySurvival/"+windows,
                    "sha1": sha1sum(os.path.join(lib_dir, windows)),
                    "size": os.stat(os.path.join(lib_dir, windows)).st_size,
                    "url": "https://minersonline.ddns.net/files/libs/"+windows
                }
            }
        },
        "name": "HistorySurvival:"+name+":"+ver,
        "natives": {
            "linux": "natives-linux",
            "osx": "natives-osx",
            "windows": "natives-windows"
        }
    }
    out["libraries"][file_index] = lib
    file_index += 1

out["libraries"] = [x for x in out["libraries"] if x is not None]

with open(args.outFile, "w") as outfile:
    json.dump(out, outfile, indent=4)
