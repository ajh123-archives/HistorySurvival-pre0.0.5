import argparse

parser = argparse.ArgumentParser()
parser.add_argument("sourceDir", help="Java source dir")
parser.add_argument("outDir", help="Directory for the documentation")
args = parser.parse_args()


