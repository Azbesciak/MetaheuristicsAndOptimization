import os
import numpy as np
from matplotlib import pyplot as plt
import re
from enum import Enum
import statistics
from io import StringIO
import shutil

MARKERS = ['s', 'o', '*', 'p', 'x', 'D']
OUTPUT_DIR='out'

def generate(name, title, instances, chart_type, alg_types=None):
    COMPARE_TYPES = [CType.MAX, CType.MIN, CType.AVG, CType.TIME, CType.TIME_EFF, CType.AVG_STEPS]

    if chart_type in COMPARE_TYPES:
        return CompareChart(name, title, instances, chart_type, alg_types).generate()
    else:
        return SingleInstanceChart(name, title, instances, chart_type, alg_types).generate()


class CType(Enum):
        MAX = 'max'
        MIN = 'min'
        AVG = 'avg'
        TIME = 'averageTime'
        TIME_EFF = 'efficiencyInTime'
        AVG_STEPS = 'avg_steps'
        PROGRESS = 'progress'
        RESTARTS = 'restarts'


class DefaultChart:
    def __init__(self, name, title, json_data, chart_type='default', save=True):
        plt.clf()
        self.title = title
        self.chart_type = chart_type
        self.json_data = json_data
        self.name = name
        self.plt = self.create_plt()
        self.dir_path = re.sub(r'[ĘÓĄŚŁŻŹĆŃęóąśłżźćń ]', '', 'pics/')
        self.file_path = None
        self.output = StringIO()
        self.save = save

    def create_plt(self):
        raise NotImplementedError

    def generate(self):
        out_path = os.path.join('out', self.file_path)

        if not os.path.exists(os.path.join('out', self.dir_path)):
            os.makedirs(os.path.join('out', self.dir_path))
        
        self.plt.savefig(os.path.join('out', self.file_path))

        tex = R'''
\begin{figure}[H]
\includegraphics[width=\columnwidth]{''' + self.file_path + '''}
\caption{''' + self.title + '''}
\end{figure}
'''
        print(tex, file=self.output)

        if self.save:
            with open(os.path.join(OUTPUT_DIR, '{}.tex').format(self.name), 'w') as fd:
                self.output.seek(0)
                shutil.copyfileobj(self.output, fd)

        return tex


class SingleInstanceChart(DefaultChart):
    def __init__(self, name, title, json_data, chart_type="single", alg_types=None):
        super().__init__(name, title, json_data, chart_type)
        self.file_path = '{}/{}{}.png'.format(self.dir_path, self.json_data['name'], self.chart_type)

    def __plot_restarts(self):
        attempts = self.json_data['attempts']
        attempts_score = [x['score'] for x in attempts]
        scores = self.json_data['score']

        std = 0 if len(attempts_score) < 2 else statistics.stdev(attempts_score)
        x = range(len(attempts))

        plt.ylabel('Wynik')
        plt.scatter(x, attempts_score)

        plt.axhline(y=scores['avg'], color='r', linestyle='-', label='avg')

        plt.axhline(y=scores['avg']-std, color='orange', linestyle='--', label='avg')
        plt.axhline(y=scores['avg']+std, color='orange', linestyle='--', label='avg')

    def create_plt(self):
        if self.chart_type == CType.RESTARTS:
            self.__plot_restarts()
        
        return plt


class CompareChart(DefaultChart):
    def __init__(self, name, title, json_data, ctype, alg_types=None, opacity=0.7):
        self.ctype = ctype
        self.alg_types = alg_types
        self.opacity = opacity

        super().__init__(name, title, json_data, ctype)
        self.file_path = '{}/{}{}.png'.format(self.dir_path, 'compare', self.ctype.value)


    def __get_best_times(self):
        best_times = []
        for alg in next(iter(self.json_data.values())):
            if self.alg_types is None or alg in self.alg_types:
                times = [x[alg]['averageTime'] for x in self.json_data.values()]
                best_times.append(times)
        best_times = zip(*best_times)
        best_times = [min(x) for x in best_times]
        return best_times

    def __plot_scores(self, alg, marker):
        scores = [x[alg]['score'][self.ctype.value] for x in self.json_data.values()]
        y = [x/y for x, y in zip(self.originals, scores)]

        if self.ctype == CType.AVG:
            e = []
            for a in self.attempts:
                x = [x['score'] for x in a]
                e.append(0 if len(x) < 2 else statistics.stdev(x))

            e = [x/y for x, y in zip(e, self.originals)]

            plt.errorbar(self.labels, y, alpha=self.opacity, capsize=4, capthick=1.2, yerr=e, xerr=None, ls='none', marker=marker, label=alg) #, uplims=True, lolims=True)
        else:
            plt.scatter(self.labels, y, marker=marker, label=alg)
        plt.ylim(0.0, 1.2) # todo: erase 0.0 

    def __plot_times(self, alg, marker):
        y = self.times
        plt.scatter(self.labels, y, marker=marker, label=alg)

    def __plot_steps(self, alg, marker):
        steps = []
        for a in self.attempts:
            x = [x['steps'][-1]['first'] for x in a]
            steps.append(x)
        y = [statistics.mean(x) for x in steps]
        e = [0 if len(x) < 2 else statistics.stdev(x) for x in steps]
        plt.errorbar(self.labels, y, alpha=self.opacity, capsize=4, capthick=1.2, yerr=e, xerr=None, ls='none', marker=marker, label=alg)

    def __plot_efficiency(self, alg, marker):
        scores = [x[alg]['score']['avg'] for x in self.json_data.values()]
        efficiency = [(best/avg/(time/best_time)) for best, avg, time, best_time in zip(self.originals, scores, self.times, self.best_times)]
        plt.scatter(self.labels, efficiency, marker=marker, label=alg)

    def create_plt(self):
        self.labels = (self.json_data.keys())
        self.best_times = self.__get_best_times()

        x = np.arange(len(self.labels))
        i = 0

        for alg in next(iter(self.json_data.values())):
            # plot only selected algorithms (default all)
            if self.alg_types is None or alg in self.alg_types:
                self.originals = [x[alg]['score']['original'] for x in self.json_data.values()]
                self.attempts = [x[alg]['attempts'] for x in self.json_data.values()]
                self.times = [x[alg]['averageTime'] for x in self.json_data.values()]

                if self.ctype in ([CType.AVG, CType.MAX, CType.MIN]): 
                    self.__plot_scores(alg, MARKERS[i])
                
                elif self.ctype == CType.TIME:
                    self.__plot_times(alg, MARKERS[i])

                elif self.ctype == CType.AVG_STEPS:
                    self.__plot_steps(alg, MARKERS[i])

                elif self.ctype == CType.TIME_EFF:
                    self.__plot_efficiency(alg, MARKERS[i])

                i += 1

        plt.xticks(rotation=90)
        plt.legend()
        plt.ylabel('Wynik')
        # plt.title(self.title)
        # plt.show()

        return plt


class SummaryChart(SingleInstanceChart):
    def create_plt(self):
        labels = ('Avg', 'Min', 'Max', 'Origin')
        attempts = self.json_data['attempts']
        y_pos = np.arange(len(labels))
        scores = self.json_data['score']
        performance = [scores['avg'], scores['min'], scores['max'], scores['original']]

        plt.bar(y_pos, performance, align='center', alpha=0.5)
        plt.xticks(y_pos, labels)
        plt.ylabel('Wynik')
        plt.title('Scores')

        return plt


class SeqChart(SingleInstanceChart):
    def create_plt(self):
        attempts = self.json_data['attempts']
        attempts_score = [x['score'] for x in attempts]
        scores = self.json_data['score']
        
        std = statistics.stdev(attempts_score)
        x = range(len(attempts))

        plt.ylabel('Wynik')
        plt.title('Wyniki znalezionych rozwiązań')
        plt.scatter(x, attempts_score)

        plt.axhline(y=scores['avg'], color='r', linestyle='-', label='avg')

        plt.axhline(y=scores['avg']-std, color='orange', linestyle='--', label='avg')
        plt.axhline(y=scores['avg']+std, color='orange', linestyle='--', label='avg')

        return plt