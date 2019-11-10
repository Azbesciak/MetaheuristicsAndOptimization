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

OUTPUT_DIR = 'out'

GREEDY_ALGS = [
    'Greedy-ContinuousNBStart-HeuristicInit',
    'Greedy-ContinuousNBStart-RandomInit',
    'Greedy-RandomNBStart-RandomInit',
    'Greedy-ZeroNBStart-HeuristicInit',
    'Greedy-ZeroNBStart-RandomInit'
    ]

STEEPEST_ALGS = [
    'Steepest-ContinuousNBStart-HeuristicInit',
    'Steepest-ContinuousNBStart-RandomInit',
    'Steepest-RandomNBStart-RandomInit',
    'Steepest-ZeroNBStart-HeuristicInit',
    'Steepest-ZeroNBStart-RandomInit'
    ]

SUMMARY_ALGS_ALL = ['Random', 'Heuristic', 'Steepest-ZeroNBStart-HeuristicInit', 'Greedy-ZeroNBStart-HeuristicInit']
SUMMARY_ALGS_SELECTED = ['Steepest-ZeroNBStart-HeuristicInit', 'Greedy-ZeroNBStart-HeuristicInit']
RANDOM_ALGS_SELECTED = ['Greedy-RandomNBStart-RandomInit', 'Steepest-RandomNBStart-RandomInit']

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
        charts.generate('avg_cmp_greedy', "Wpływ rodzaju startu dla \"Greedy\"", instances, charts.CType.AVG, GREEDY_ALGS, map_alg_name=False)

        charts.generate('avg_cmp_steepest', "Wpływ rodzaju startu dla \"Steepest\"", instances, charts.CType.AVG, STEEPEST_ALGS, map_alg_name=False)

        max_scores = charts.generate('best_cmp', "Najlepsze wyniki", instances, charts.CType.MIN, SUMMARY_ALGS_ALL)

        min_scores = charts.generate('worst_cmp',  "Najgorsze wyniki", instances, charts.CType.MAX, SUMMARY_ALGS_ALL)

        avg_scores = charts.generate('avg_cmp', "Średnie wyniki", instances, charts.CType.AVG, SUMMARY_ALGS_ALL)
        
        avg_times = charts.generate('times_cmp', "Czasy", instances, charts.CType.TIME, SUMMARY_ALGS_ALL)

        efiiciency = charts.generate('efficiency_cmp', "Efektywność", instances, charts.CType.TIME_EFF, alg_types=SUMMARY_ALGS_SELECTED)

        avg_steps = charts.generate('steps_cmp', "Kroki", instances, charts.CType.AVG_STEPS, alg_types=SUMMARY_ALGS_SELECTED)

    except Exception as e:
        print("Error: Generacja nie powiodła się \n\t{}: {}".format(type(e), e))

    # Generate single instances charts
    for instance in instances.keys():
        print(instance)

        charts.generate("{}_progress_avg".format(instance), "Postępy AVG", instances, charts.CType.PROGRESS_AVG, alg_types=RANDOM_ALGS_SELECTED, instance=instance,
        xlabel='Liczba restartów', ylabel='Średnie rozwiązanie')

        charts.generate("{}_progress_best".format(instance), "Postępy BEST", instances, charts.CType.PROGRESS_BEST, alg_types=RANDOM_ALGS_SELECTED, instance=instance,
        xlabel='Liczba restartów', ylabel='Najlepsze rozwiązanie')

        charts.generate("{}_begend".format(instance), "Początkowe/Końcowe", instances, charts.CType.BEG_END, alg_types=RANDOM_ALGS_SELECTED, instance=instance,
        xlabel='Początkowe rozwiązanie', ylabel='Końcowe rozwiązanie')

        charts.generate("{}_similaritygi".format(instance), "Podobieństwo", instances, charts.CType.SIMILARITY, alg_types=RANDOM_ALGS_SELECTED, instance=instance,
        xlabel='Jakość', ylabel='Podobieństwo')



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