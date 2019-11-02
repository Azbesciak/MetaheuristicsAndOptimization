import sys
import os
import json
import argparse
from pathlib import Path
from glob import glob
import charts
from io import StringIO
import shutil
from zipfile import ZipFile

OUTPUT_DIR='out'

def generate():
    instances = dict()

    # Load summary files to dict
    for f in sorted(glob('{}/*.sum'.format(args.path), recursive=True)):
            with open(f) as json_file:
                summary = json.load(json_file)
                if summary['name'] not in instances:
                    instances[summary['name']] = dict()
                instances[summary['name']][summary['type']] = summary

    # Generate global instances charts
    try:
        output = StringIO()

        print('\\section{{Porównanie wyników}}', file=output)

        max_scores = charts.CompareChart('Porównanie najlepszych wyników', instances, charts.CType.MIN, "Najlepsze wyniki").generate()
        print(max_scores, file=output)

        min_scores = charts.CompareChart('Porównanie najgorszych wyników', instances, charts.CType.MAX, "Najgorsze wyniki").generate()
        print(min_scores, file=output)

        avg_scores = charts.CompareChart('Porównanie średnich wyników', instances, charts.CType.AVG, "Średnie wyniki").generate()
        print(avg_scores, file=output)
        
        avg_times = charts.CompareChart('Porównanie czasów działania', instances, charts.CType.TIME, "Czasy").generate()
        print(avg_times, file=output)

        efiiciency = charts.CompareChart('Efektywność', instances, charts.CType.TIME_EFF, "Efektywność", alg_types=['Greedy', "Steepest"]).generate()
        print(efiiciency, file=output)

        avg_steps = charts.CompareChart('Średnia liczba kroków dla GS', instances, charts.CType.AVG_STEPS, "Kroki", alg_types=['Greedy', "Steepest"]).generate()
        print(avg_steps, file=output)

        # Save output as Latex document
        with open(os.path.join(OUTPUT_DIR, 'summary.tex'), 'w') as fd:
            output.seek(0)
            shutil.copyfileobj(output, fd)
    except Exception as e:
        print("Error: Generacja nie powiodła się \n\t{}: {}".format(type(e), e))

    # Generate single instances charts
    for instance in instances.keys():
        output = StringIO()
        print(instance)

        try:
            print('\\section{{{}}}'.format(instance), file=output)

            for alg_type in instances[instance].keys(): 
                summary = instances[instance][alg_type]
                # print(summary)
                print('\\subsection{{{}}}'.format(alg_type), file=output)

                summary_report = charts.SummaryChart('summary', summary).generate()
                print(summary_report, file=output)

                seq_report = charts.SeqChart('Wyniki ({} uruchomień)'.format(len(summary['attempts'])), summary).generate()
                print(seq_report, file=output)

            # Save output as Latex document
            with open(os.path.join(OUTPUT_DIR, '{}.tex'.format(instance)), 'w') as fd:
                output.seek(0)
                shutil.copyfileobj(output, fd)
        except Exception as e:
            print("Error: Generacja {} nie powiodła się \n\t{}: {}".format(instance, type(e), e))


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