import os
import numpy as np
from matplotlib import pyplot as plt
import re
from enum import Enum
import statistics
from io import StringIO
import shutil
import copy

MARKERS = ['s', 'o', '*', 'p', 'x', 'D']
OUTPUT_DIR='out'

def generate(name, title, instances, chart_type, alg_types=None, instance=None, xlabel=None, ylabel=None):
    COMPARE_TYPES = [CType.MAX, CType.MIN, CType.AVG, CType.TIME, CType.TIME_EFF, CType.AVG_STEPS]

    if chart_type in COMPARE_TYPES:
        return CompareChart(name, title, instances, chart_type, alg_types, xlabel=xlabel, ylabel=ylabel).generate()
    else:
        return SingleInstanceChart(name, instance, title, instances, chart_type, alg_types, xlabel=xlabel, ylabel=ylabel).generate()


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
    def __init__(self, name, title, json_data, chart_type='default', save=True, ylabel=None, xlabel=None):
        plt.clf()

        self.xlabel = xlabel
        self.ylabel = ylabel
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
        plt.legend()

        out_path = os.path.join('out', self.file_path)

        if not os.path.exists(os.path.join('out', self.dir_path)):
            os.makedirs(os.path.join('out', self.dir_path))
        
        self.plt.savefig(os.path.join('out', self.file_path), bbox_inches='tight')

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
    def __init__(self, name, instance, title, json_data, chart_type="single", alg_types=None, xlabel=None, ylabel=None):
        self.instance = instance
        self.alg_types = alg_types
        super().__init__(name, title, json_data, chart_type, xlabel=xlabel, ylabel=ylabel)
        self.file_path = '{}/{}{}.png'.format(self.dir_path, self.instance, self.name)

    def __plot_restarts(self):
        # self.ylabel = 'Liczba restartów'
        # self.xlabel = 'Łączny koszt przejazdu'
        axs = self.axs[self.alg_types.index(self.summary['type'])]

        attempts = self.summary['attempts']
        attempts_score = [x['score'] for x in attempts]
        scores = self.summary['score']

        std = 0 if len(attempts_score) < 2 else statistics.stdev(attempts_score)
        x = range(len(attempts))

        axs.scatter(x, attempts_score)

        axs.axhline(y=scores['avg'], color='r', linestyle='-', label='Avg')

        axs.axhline(y=scores['avg']-std, color='orange', linestyle='--', label='Std')
        axs.axhline(y=scores['avg']+std, color='orange', linestyle='--')
        axs.set_title(self.summary['type'])
        axs.axhline(y=min(attempts_score), color='green', linestyle=':', label="Najlepszy wynik")

    def __plot_progress(self):
        # self.ylabel = 'Liczba powtórzeń'
        # self.xlabel = 'Łączny koszt przejazdu'
        axs = self.axs[self.alg_types.index(self.summary['type'])]

        attempts = self.summary['attempts']
        attempts_steps = [x['steps'] for x in attempts]

        scores = []
        ox = []

        for attempt_steps in attempts_steps:
            ox.append([x['first'] for x in attempt_steps])
            scores.append([x['second'] for x in attempt_steps])

        scores = zip(*scores)
        e = copy.deepcopy(scores)
        scores = [statistics.mean(x) for x in scores]

        e = [0 if len(x) < 2 else statistics.stdev(x) for x in e]

        ox = zip(*ox)
        ox = [x[0] for x in ox]

        axs.set_title(self.summary['type'])
        axs.errorbar(ox, scores, capsize=4, capthick=1.2, yerr=None, xerr=None, ls='none', marker='o')

    def create_plt(self):
        self.fig, self.axs = plt.subplots(1, len(self.alg_types), sharey=True)

        self.fig.suptitle(self.instance, fontsize="x-large")
        self.fig.text(0.5, 0.04, self.xlabel, ha='center')
        self.fig.text(0.04, 0.5, self.ylabel, va='center', rotation='vertical')

        for alg_type in self.json_data[self.instance].keys(): 
            if alg_type in self.alg_types:
                # plt.subplot(1, len(self.alg_types), self.alg_types.index(alg_type) + 1)
                # plt.title(alg_type)

                self.summary = self.json_data[self.instance][alg_type]

                if self.chart_type == CType.RESTARTS:
                # plt.errorbar(ox, scores, capsize=4, capth
                    self.__plot_restarts()
                if self.chart_type == CType.PROGRESS:
                    self.__plot_progress()

                            
        return plt


class CompareChart(DefaultChart):
    def __init__(self, name, title, json_data, ctype, alg_types=None, opacity=0.7, ylabel=None, xlabel=None):
        self.ctype = ctype
        self.alg_types = alg_types
        self.opacity = opacity

        super().__init__(name, title, json_data, ctype, xlabel=xlabel, ylabel=ylabel)
        self.file_path = '{}/{}{}.png'.format(self.dir_path, 'compare', self.ctype.value)

    def generate(self):
        plt.xlabel(self.ylabel)
        plt.ylabel(self.xlabel)
        return super().generate()

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
        y = [1/(y/x) for x, y in zip(self.originals, scores)]

        if self.ctype == CType.AVG:
            self.xlabel = 'Jakość (przypadek średni)'
            e = []
            for a in self.attempts:
                x = [x['score'] for x in a]
                e.append(0 if len(x) < 2 else statistics.stdev(x))

            e = [0 if std == 0 else avg - (1 / ((std + score)/org)) for std, org, avg, score in zip(e, self.originals, y, scores)]

            plt.errorbar(self.labels, y, alpha=self.opacity, capsize=4, capthick=1.2, yerr=e, xerr=None, ls='none', marker=marker, label=alg) #, uplims=True, lolims=True)
        else:
            plt.scatter(self.labels, y, marker=marker, label=alg)

        if self.ctype == CType.MAX:
            self.xlabel = 'Jakość (przypadek najgorszy)'
        elif self.ctype == CType.MIN:
            self.xlabel = 'Jakość (przypadek najlepszy)'
        
        plt.ylim(0.0, 1.2) # todo: erase 0.0 

    def __plot_times(self, alg, marker):
        y = self.times
        plt.scatter(self.labels, y, marker=marker, label=alg)
        self.xlabel = 'Średni czas działania'
        plt.yscale('log')

    def __plot_steps(self, alg, marker):
        steps = []
        for a in self.attempts:
            x = [x['steps'][-1]['first'] for x in a]
            steps.append(x)
        y = [statistics.mean(x) for x in steps]
        e = [0 if len(x) < 2 else statistics.stdev(x) for x in steps]
        plt.errorbar(self.labels, y, alpha=self.opacity, capsize=4, capthick=1.2, yerr=e, xerr=None, ls='none', marker=marker, label=alg)
        self.xlabel = 'Średnia liczba kroków algorytmu'
        plt.yscale('log')

    def __plot_efficiency(self, alg, marker):
        scores = [x[alg]['score']['avg'] for x in self.json_data.values()]
        efficiency = [(best/avg/(time/best_time)) for best, avg, time, best_time in zip(self.originals, scores, self.times, self.best_times)]
        plt.scatter(self.labels, efficiency, marker=marker, label=alg)
        self.xlabel = 'Efektywność algorytmu'

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
        self.ylabel = 'Nazwa wczytanej instancji'
        
        # plt.title(self.title)
        # plt.show()

        return plt
