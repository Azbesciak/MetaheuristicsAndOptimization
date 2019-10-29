import sys
import os
import json
import argparse
from pathlib import Path
from glob import glob
import charts
from io import StringIO
import shutil
import templates
from zipfile import ZipFile

OUTPUT_DIR='out'

def generate():
    output = StringIO()

    # Put document intro
    print(templates.INTRO, file=output)

    # Generate charts
    for f in glob('{}/*.sum'.format(args.path), recursive=True):
        with open(f) as json_file:
            summary = json.load(json_file)
            print(summary)

            summary_report = charts.SummaryChart('summary', summary).generate()
            print(summary_report, file=output)

            seq_report = charts.SeqChart('Początkowe vs Końcowe', summary).generate()
            print(seq_report, file=output)

    # Put document ending
    print(templates.END, file=output)

    # Save output as Latex document
    with open(os.path.join(OUTPUT_DIR, 'report.tex'), 'w') as fd:
        output.seek(0)
        shutil.copyfileobj(output, fd)


parser = argparse.ArgumentParser()
parser.add_argument('--path', type=Path,
                    required=True,
                    help='Path for .sum files')

args = parser.parse_args()

if os.path.exists(OUTPUT_DIR):
    shutil.rmtree(OUTPUT_DIR)
os.makedirs(OUTPUT_DIR)

generate()

file_name = os.path.join("report.zip")

# Create zip file with latex sources
with ZipFile(file_name, 'w') as zipObj:
    for f in glob('{}/**'.format(OUTPUT_DIR), recursive=True):
        zipObj.write(f)