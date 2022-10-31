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
                inFunc = False
                returns = ""
                prev_returns = ""
                extra_returns = ""
                indentifier_count = 0
                indentifier_threshold = 2
                is_generic = False
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
                                indentifier_count = 0
                            modifiers.append(token.value)

                            if "{" in lines[funcLine-1]:
                                inFunc = True
                            if "=" in lines[funcLine-1] and inFunc:
                                redefining = True
                        is_primative = False
                        l, c = token.position
                        if token.value == "void":
                            is_primative = True
                        if token.value == "<":
                            is_generic = True
                            indentifier_count -= 1
                        if token.value == ">":
                            is_generic = False

                        if isinstance(token, javalang.tokenizer.Identifier) and inFunc and l == funcLine and not gotName:
                            if "<" in lines[l-1] and ">" in lines[l-1] and token.value in lines[l-1]:
                                if not is_generic:
                                    extra_returns += token.value
                                    indentifier_threshold += 1
                                    indentifier_count += 1
                                else:
                                    extra_returns += "<"+token.value+">"
                                    indentifier_threshold += 1
                                    indentifier_count += 1
                                print(extra_returns)

                        if isinstance(token, javalang.tokenizer.Identifier) or is_primative and not is_generic:
                            if "{" in lines[l-1] and not "=" in lines[l-1]:
                                indentifier_count += 1

                                if returns == "":
                                    returns = token.value + extra_returns
                                    prev_returns = returns
                                    gotName = False

                                if indentifier_count == indentifier_threshold and not gotName and l == funcLine:
                                    gotName = True
                                    name = token.value

                                    if not redefining and inFunc:
                                        try:
                                            docBlock: javalang.javadoc.DocBlock = javalang.javadoc.parse(prevDoc)
                                            out[name] = {
                                                "desc": docBlock.description,
                                                "modifiers": modifiers.copy(),
                                                "line": funcLine,
                                                "returns": prev_returns
                                            }
                                        except ValueError:
                                            out[name] = {
                                                "desc": "",
                                                "modifiers": modifiers.copy(),
                                                "line": funcLine,
                                                "returns": prev_returns
                                            }

                                        prevDoc = ""
                                        startOfFunc = False
                                        inFunc = False
                                        returns = ""
                                        indentifier_threshold = 2
                                        extra_returns = ""
                            modifiers.clear()
                            redefining = False
                        if doc is not None:
                            prevDoc = doc


print(out)
