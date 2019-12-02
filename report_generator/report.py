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
    'Greedy-RandomNBStart-HeuristicInit',
    'Greedy-ZeroNBStart-HeuristicInit',
    'Greedy-ZeroNBStart-RandomInit',
    ]

STEEPEST_ALGS = [
    'Steepest-ContinuousNBStart-HeuristicInit',
    'Steepest-ContinuousNBStart-RandomInit',
    'Steepest-RandomNBStart-RandomInit',
    'Steepest-RandomNBStart-HeuristicInit',
    'Steepest-ZeroNBStart-HeuristicInit',
    'Steepest-ZeroNBStart-RandomInit'
    ]

SUMMARY_ALGS_ALL = [
    'Random', 
    'Heuristic', 
    'Steepest-ZeroNBStart-HeuristicInit', 
    'Greedy-ZeroNBStart-HeuristicInit', 
    'SimulatedAnnealing-HeuristicInit_increaseRatio1', 
    'TabuSearch-HeuristicInit'
    ]
SUMMARY_ALGS_SELECTED = [
    'Steepest-ZeroNBStart-HeuristicInit', 
    'Greedy-ZeroNBStart-HeuristicInit', 
    'SimulatedAnnealing-HeuristicInit_increaseRatio1', 
    'TabuSearch-HeuristicInit'
    ]
RANDOM_ALGS_SELECTED = ['Greedy-RandomNBStart-RandomInit', 'Steepest-RandomNBStart-RandomInit']
SUMMARY_ALGS_SA = [
    'SimulatedAnnealing-RandomInit_increaseRatio3', 
    'SimulatedAnnealing-HeuristicInit_increaseRatio4', 
    'SimulatedAnnealing-HeuristicInit_increaseRatio3', 
    'SimulatedAnnealing-HeuristicInit_increaseRatio2',
    'SimulatedAnnealing-HeuristicInit_increaseRatio1'
    ]
SUMMARY_ALGS_TS = [
    'TabuSearch-RandomInit',
    'TabuSearch-HeuristicInit',
    'TabuSearch-HeuristicInit-BreakTabu',
    'TabuSearch-HeuristicInit-NoCollisions',
    'TabuSearch-HeuristicInit-NoCollisions_BreakTabu'
]

def test_ts(instances):
    efiiciency = charts.generate('efficiency_SA', 
'{{Porównanie efektywności algorytmów SA}}',
instances, charts.CType.TIME_EFF, alg_types=SUMMARY_ALGS_SA, map_alg_name=False)

def test_sa(instances):
    efiiciency = charts.generate('efficiency_TS', 
'{{Porównanie efektywności algorytmów TS}}',
instances, charts.CType.TIME_EFF, alg_types=SUMMARY_ALGS_TS, map_alg_name=False)

def summary_charts(instances):
    charts.generate('avg_cmp_greedy', 
"Wpływ przyjętej strategi przeglądania oraz początkowego rozwiązania na \\textbf{{jakość}} rozwiązania końcowego dla heurystyki \\textit{{Local Search}} typu \\textbf{{\\textit{{Greedy}}}}", 
instances, charts.CType.AVG, GREEDY_ALGS, map_alg_name=False)
    charts.generate('avg_cmp_steepest',
"Wpływ przyjętej strategi przeglądania oraz początkowego rozwiązania na \\textbf{{jakość}} rozwiązania końcowego dla heurystyki \\textit{{Local Search}} typu \\textbf{{\\textit{{Steepest}}}}",
instances, charts.CType.AVG, STEEPEST_ALGS, map_alg_name=False)
    charts.generate('efficiency_cmp_greedy', 
"Wpływ przyjętej strategi przeglądania oraz początkowego rozwiązania na \\textbf{{efektywność}} rozwiązania końcowego dla heurystyki \\textit{{Local Search}} typu \\textbf{{\\textit{{Greedy}}}}", 
instances, charts.CType.TIME_EFF, GREEDY_ALGS, map_alg_name=False)
    charts.generate('efficiency_cmp_steepest',
"Wpływ przyjętej strategi przeglądania oraz początkowego rozwiązania na \\textbf{{efektywność}} rozwiązania końcowego dla heurystyki \\textit{{Local Search}} typu \\textbf{{\\textit{{Steepest}}}}", 
instances, charts.CType.TIME_EFF, STEEPEST_ALGS, map_alg_name=False)

    max_scores = charts.generate('best_cmp', 
"Porównanie badanych algorytmów pod względem miary jakości (wzór \\ref{{eq:quality}}) dla \\textbf{{najlepszego}} ostatecznie uzyskanego wyniku", 
instances, charts.CType.MIN, SUMMARY_ALGS_ALL)

    min_scores = charts.generate('worst_cmp',  
"Porównanie badanych algorytmów pod względem miary jakości (wzór \\ref{{eq:quality}}) dla \\textbf{{najgorszego}} ostatecznie uzyskanego wyniku", 
instances, charts.CType.MAX, SUMMARY_ALGS_ALL)

    avg_scores = charts.generate('avg_cmp', 
"Porównanie badanych algorytmów pod względem miary jakości (wzór \\ref{{eq:quality}}) dla \\textbf{{średniej}} z ostatecznie uzyskanych wyników", 
instances, charts.CType.AVG, SUMMARY_ALGS_ALL)
    
    avg_times = charts.generate('times_cmp', 
"Uśredniony czas przetwarzania badanych algorytmów do momentu spełnienia indywidualnych warunków stopu dla próbki co najmniej 10 uruchomień oraz minimalnego trwania poszczególnej serii wynoszącego 1 sekundę", 
instances, charts.CType.TIME, SUMMARY_ALGS_ALL)

    efiiciency = charts.generate('efficiency_cmp', 
'{{Porównanie efektywności algorytmów dla wszystkich instancji}}',
instances, charts.CType.TIME_EFF, alg_types=SUMMARY_ALGS_ALL)

    avg_steps = charts.generate('steps_cmp', 
"Porównanie średniej liczby kroków algorytmów \\textit{{Greedy}} i \\textit{{Steepest}} dla wszystkich instancji", 
instances, charts.CType.AVG_STEPS, alg_types=SUMMARY_ALGS_SELECTED)

def single_charts(instances, instance):
    charts.generate("{}_progress_avg".format(instance), 
"Wpływ liczby restartów w konfiguracji \\textit{{multi-random start}} na \\textbf{{średnie}} znalezione rozwiązanie dla algorytmów \\textit{{Greedy}} i \\textit{{Steepest}} oraz instancji \\textbf{{{}}}".format(instance), 
instances, charts.CType.PROGRESS_AVG, alg_types=RANDOM_ALGS_SELECTED, instance=instance,
        xlabel='Liczba restartów', ylabel='Średnie rozwiązanie')

    charts.generate("{}_progress_best".format(instance), 
"Wpływ liczby restartów w konfiguracji \\textit{{multi-random start}} na \\textbf{{najlepsze}} znalezione rozwiązanie dla algorytmów \\textit{{Greedy}} i \\textit{{Steepest}} oraz instancji \\textbf{{{}}}".format(instance), 
instances, charts.CType.PROGRESS_BEST, alg_types=RANDOM_ALGS_SELECTED, instance=instance,
        xlabel='Liczba restartów', ylabel='Najlepsze rozwiązanie')

    charts.generate("{}_begend".format(instance), 
"Jakość rozwiązania początkowego i końcowego dla instancji \\textbf{{{}}}".format(instance),
 instances, charts.CType.BEG_END, alg_types=RANDOM_ALGS_SELECTED, instance=instance,
        ylabel='Początkowe rozwiązanie', xlabel='Końcowe rozwiązanie')

    charts.generate("{}_similaritygi".format(instance), 
"Wpływ jakości na podobieństwo znajdowanych rozwiązań dla instancji \\textbf{{{}}}".format(instance), 
instances, charts.CType.SIMILARITY, alg_types=RANDOM_ALGS_SELECTED, instance=instance,
        xlabel='Jakość', ylabel='Podobieństwo')


def generate():
    instances = dict()

    # Load summary files to dict
    for f in sorted(glob('{}/*.sum'.format(args.path), recursive=True)):
            with open(f) as json_file:
                summary = json.load(json_file)
                if summary['name'] not in instances:
                    instances[summary['name']] = dict()
                instances[summary['name']][summary['type']] = summary

# SA, TS
    test_ts(instances)
    test_sa(instances)
    
    # Generate global instances charts
    summary_charts(instances)

    # Generate single instances charts
    for instance in instances.keys():
        print(instance)

        # single_charts(instances, instance)

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