import argparse
import json
import os
import javalang

parser = argparse.ArgumentParser()
parser.add_argument("projects", help="Java projects")
parser.add_argument("outDir", help="Directory for the documentation")
args = parser.parse_args()

projectsList = list(args.projects)
projectsList[0] = "{"
del projectsList[1]
del projectsList[-1]
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
                identifier_count = 0
                indentifier_threshold = 0
                is_generic = False
                found = []
                className = ""
                out["classes"] = {}
                for token in tokens:
                    doc = token.javadoc
                    line, column = token.position
                    if not isInClass and isinstance(token, javalang.tokenizer.Modifier):
                        isReadyForClass = True
                    if isReadyForClass and token.value == "class":
                        isInClass = True
                        isReadyForClass = False
                    if isInClass:
                        if isinstance(token, javalang.tokenizer.Modifier):
                            gotName = False

                            prevFuncLine = funcLine
                            funcLine = line
                            if not startOfFunc:
                                startOfFunc = True
                                identifier_count = 0
                                modifiers.clear()
                                got_returns = False

                            if "{" in lines[funcLine - 1]:
                                inFunc = True
                            if "=" in lines[funcLine - 1] and inFunc:
                                redefining = True

                            if inFunc:
                                modifiers.append(token.value)

                        is_primitive = False
                        splitAt = -1
                        if className != "":
                            if inFunc:
                                splitAt = lines[line - 1].find("(")
                                if column < splitAt and (
                                        token.value == "void" or isinstance(token, javalang.tokenizer.BasicType)
                                ):
                                    is_primitive = True
                                    identifier_count += 1
                                    indentifier_threshold += 1

                            if is_primitive:
                                indentifier_threshold += 1

                        if isinstance(token, javalang.tokenizer.Identifier) and className == "":
                            className = token.value
                            classData = lines[line - 1].split(className, 1)
                            left = " ".join(classData[0].split()).replace("{", "").replace("}", "").split(" ")
                            right = " ".join(classData[1].split()).replace("{", "").replace("}", "").split(" ")

                            classModifiers = left.copy()
                            del classModifiers[-1]

                            out["classes"][className] = {
                                "line": line,
                                "type": left[-1],
                                "modifers": classModifiers,
                                "extends": "",
                                "implements": "",
                                "functions": {}
                            }

                        if isinstance(token, javalang.tokenizer.Identifier) or is_primitive and not is_generic:
                            if "{" in lines[line - 1] and "=" not in lines[line - 1]:
                                identifier_count += 1
                                data = lines[line - 1].split("(")
                                beforeParams = data[0]
                                is_generic = False
                                if "<" in beforeParams and ">" in beforeParams and token.value in beforeParams:
                                    is_generic = True
                                    indentifier_threshold += 1
                                    identifier_count -= 1
                                    if indentifier_threshold == 3:
                                        identifier_count = 3

                                if returns == "" and startOfFunc and not gotName:
                                    returns = token.value + extra_returns
                                    prev_returns = returns
                                    gotName = False
                                    got_returns = True
                                    if not is_generic:
                                        if not is_primitive:
                                            identifier_count -= 1
                                        indentifier_threshold += 1

                                if column < splitAt:
                                    if identifier_count == 3 and indentifier_threshold == 4:
                                        indentifier_threshold -= 1

                                if identifier_count == indentifier_threshold and not gotName and line == funcLine:
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
                                            out["classes"][className]["functions"][name] = {
                                                "desc": docBlock.description,
                                                "modifiers": modifiers.copy(),
                                                "line": funcLine,
                                                "returns": realReturns,
                                                "params": params,
                                                "throws": throws
                                            }
                                        except ValueError:
                                            out["classes"][className]["functions"][name] = {
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
                                        identifier_count = 0
                                        extra_returns = ""
                                        is_generic = False
                                        found.append(name)
                            redefining = False
                        if doc is not None:
                            prevDoc = doc

print(out)
