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
projectsStr = projectsStr.replace("\'", "\"")

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
                got_returns = False
                indentifier_count = 0
                indentifier_threshold = 0
                is_generic = False
                found = []
                className = ""
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
                                modifiers.clear()
                                got_returns = False

                            if "{" in lines[funcLine-1]:
                                inFunc = True
                            if "=" in lines[funcLine-1] and inFunc:
                                redefining = True

                            if inFunc:
                                modifiers.append(token.value)
                        is_primative = False
                        l, c = token.position
                        if token.value == "void":
                            is_primative = True
                            indentifier_count += 1
                            indentifier_threshold += 1

                        if is_primative:
                            indentifier_threshold += 1

                        if isinstance(token, javalang.tokenizer.Identifier) or is_primative and not is_generic:
                            if "{" in lines[l-1] and not "=" in lines[l-1]:
                                indentifier_count += 1
                                data = lines[l-1].split("(")
                                beforeParams = data[0]
                                splitAt = lines[l-1].find("(")
                                is_generic = False
                                if "<" in beforeParams and ">" in beforeParams and token.value in beforeParams:
                                    is_generic = True
                                    indentifier_threshold += 1
                                    indentifier_count -= 1
                                    if indentifier_threshold == 3:
                                        indentifier_count = 3

                                if returns == "" and startOfFunc and not gotName:
                                    returns = token.value + extra_returns
                                    prev_returns = returns
                                    gotName = False
                                    got_returns = True
                                    if not is_generic:
                                        if not is_primative:
                                            indentifier_count -= 1
                                        indentifier_threshold += 1

                                if c < splitAt:
                                    if indentifier_count == 3 and indentifier_threshold == 4:
                                        indentifier_threshold -= 1

                                if indentifier_count == indentifier_threshold and not gotName and l == funcLine:
                                    gotName = True
                                    name = token.value
                                    modifiersStr = " ".join(modifiers)

                                    realReturns = beforeParams.replace(name, "")
                                    realReturns = realReturns.replace(modifiersStr, "")
                                    realReturns = "".join(realReturns.split())

                                    afterData = data[1]
                                    afterData = afterData.replace(")", "")
                                    afterData = afterData.replace("{", "")
                                    afterData = afterData.rstrip()
                                    afterData = afterData.split("throws")
                                    params = afterData[0].split(",")
                                    params = [s.strip() for s in params]
                                    params = [" ".join(s.split()) for s in params]
                                    throws = ""
                                    if len(afterData) > 1:
                                        throws = afterData[1]
                                        throws = throws.lstrip()
                                        throws = throws.rstrip()

                                    if not redefining and inFunc and name not in found:
                                        try:
                                            docBlock: javalang.javadoc.DocBlock = javalang.javadoc.parse(prevDoc)
                                            out[name] = {
                                                "desc": docBlock.description,
                                                "modifiers": modifiers.copy(),
                                                "line": funcLine,
                                                "returns": realReturns,
                                                "params": params,
                                                "throws": throws
                                            }
                                        except ValueError:
                                            out[name] = {
                                                "desc": "",
                                                "modifiers": modifiers.copy(),
                                                "line": funcLine,
                                                "returns": realReturns,
                                                "params": params,
                                                "throws": throws
                                            }

                                        prevDoc = ""
                                        startOfFunc = False
                                        inFunc = False
                                        returns = ""
                                        indentifier_threshold = 0
                                        indentifier_count = 0
                                        extra_returns = ""
                                        is_generic = False
                                        found.append(name)
                            redefining = False
                        if doc is not None:
                            prevDoc = doc


print(out)
