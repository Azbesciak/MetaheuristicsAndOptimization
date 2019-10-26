import sys
import os
import json
import argparse
from pathlib import Path
from glob import glob
import charts

def generate():
    for f in glob('{}/*.sum'.format(args.path), recursive=True):
        with open(f) as json_file:
            summary = json.load(json_file)
            print(summary)
            charts.SummaryReport('summary', summary).generate()


parser = argparse.ArgumentParser()
parser.add_argument('--path', type=Path,
                    required=True,
                    help='Path for .sum files')

args = parser.parse_args()

generate()