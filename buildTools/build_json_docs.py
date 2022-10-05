import argparse
import json
from operator import mod
import os
import javalang

parser = argparse.ArgumentParser()
parser.add_argument("projects", help="Java projects")
parser.add_argument("outDir", help="Directory for the documentation")
args = parser.parse_args()

projectsList = list(args.projects)
projectsList[0] = "{"
projectsList[len(projectsList) - 1] = "}"
projectsStr = "".join(projectsList)

projects = json.loads(projectsStr)
out = {}

for name in projects:
    project = projects[name]
    pathSep = "/"
    if os.name == 'nt':
        pathSep = "\\"
    if name == "game":
        project["packageName"] = project["packageName"][:-4] + "HistorySurvival"
    packagePath = project["packageName"].replace(".", pathSep)
    sources = project["sources"]
    for sourceDir in sources:
        realPath = os.path.join(sourceDir, packagePath)
        if name == "game":
            file = os.path.join(realPath, "Game.java")
            with open(file) as javaCode:
                lines = javaCode.readlines()
                data = "".join(lines)
                tokens = javalang.tokenizer.tokenize(data)
                isInClass = False
                isReadyForClass = True
                gotName = False
                name = ""
                modifiers = []
                funcLine = -1
                prevFuncLine = -1
                prevDoc = ""
                redefining = False
                startOfFunc = False
                for token in tokens:
                    doc = token.javadoc
                    if not isInClass and isinstance(token, javalang.tokenizer.Modifier):
                        isReadyForClass = True
                    if isReadyForClass and token.value == "class":
                        isInClass = True
                        isReadyForClass = False
                    if isInClass:
                        if isinstance(token, javalang.tokenizer.Modifier):
                            gotName = False
                            l, c = token.position

                            prevFuncLine = funcLine
                            funcLine = l
                            if not startOfFunc:
                                startOfFunc = True
                            modifiers.append(token.value)
                                
                            if "=" in lines[funcLine-1]:
                                if "{" in lines[funcLine-1]:
                                    redefining = True
                        if not gotName and isinstance(token, javalang.tokenizer.Identifier):
                            l, c = token.position
                            if "{" in lines[l-1]:
                                if l == funcLine:
                                    gotName = True
                                    name = token.value
                                    if not redefining:
                                        try:
                                            docBlock: javalang.javadoc.DocBlock = javalang.javadoc.parse(prevDoc)
                                            out[name] = {
                                                "desc": docBlock.description,
                                                "modifiers": modifiers.copy(),
                                                "line": funcLine
                                            }
                                        except ValueError:
                                            out[name] = {
                                                "desc": "",
                                                "modifiers": modifiers.copy(),
                                                "line": funcLine
                                            }

                                        prevDoc = ""
                                        startOfFunc = False
                            modifiers.clear()
                        if doc is not None:
                            prevDoc = doc

print(out)
