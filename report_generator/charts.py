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
OUTPUT_DIR='out/generated_tex'


class MarkerSpec:
    def __init__(self, sign: str, color: str):
        self.color = color
        self.sign = sign


ALG_MARKERS = {
    "Greedy": MarkerSpec("s", "blue"),
    "Steepest": MarkerSpec("o", "orange"),
    "Heuristic": MarkerSpec("*", "green"),
    "Random": MarkerSpec("p", "red")
}


def generate(name, title, instances, chart_type, alg_types=None, instance=None, xlabel=None, ylabel=None, map_alg_name=True, legend_outside=True):
    COMPARE_TYPES = [CType.MAX, CType.MIN, CType.AVG, CType.TIME, CType.TIME_EFF, CType.AVG_STEPS]

    if chart_type in COMPARE_TYPES:
        return CompareChart(name, title, instances, chart_type, alg_types, xlabel=xlabel, ylabel=ylabel, map_alg_name=map_alg_name, legend_outside=legend_outside).generate()
    else:
        return SingleInstanceChart(name, instance, title, instances, chart_type, alg_types, xlabel=xlabel, ylabel=ylabel, map_alg_name=map_alg_name, legend_outside=legend_outside).generate()


class CType(Enum):
        MAX = 'max'
        MIN = 'min'
        AVG = 'avg'
        TIME = 'averageTime'
        TIME_EFF = 'efficiencyInTime'
        AVG_STEPS = 'avg_steps'
        PROGRESS_AVG = 'progress_avg'
        PROGRESS_BEST = 'progress_best'
        BEG_END = 'beg_end'
        SIMILARITY = 'similarity'


class DefaultChart:
    def __init__(self, name, title, json_data, chart_type='default', save=True, ylabel=None, xlabel=None, map_alg_name=True, legend_outside=True):
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
        self.map_alg_name = map_alg_name
        self.legend_outside = legend_outside

    def get_mapped_alg_name(self, name: str):
        return name.split('-')[0] if self.map_alg_name else name

    def create_plt(self):
        raise NotImplementedError

    def generate(self):
        out_path = os.path.join('out', self.file_path)

        if not os.path.exists(os.path.join('out', self.dir_path)):
            os.makedirs(os.path.join('out', self.dir_path))
        
        self.plt.savefig(os.path.join('out', self.file_path), bbox_inches='tight')

        tex = R'''
\begin{figure}[H]
\includegraphics[width=\columnwidth]{../''' + self.file_path + '''}
\caption{''' + self.title + '''}
\label{fig:''' + self.name +'''}
\end{figure}
'''
        print(tex, file=self.output)

        if self.save:
            if not os.path.exists(OUTPUT_DIR):
                os.makedirs(OUTPUT_DIR)
            with open(os.path.join(OUTPUT_DIR, '{}.tex').format(self.name), 'w') as fd:
                self.output.seek(0)
                shutil.copyfileobj(self.output, fd)

        return tex


class SingleInstanceChart(DefaultChart):
    def __init__(self, name, instance, title, json_data, chart_type="single", alg_types=None, xlabel=None, ylabel=None, map_alg_name=True, legend_outside=True):
        self.instance = instance
        self.alg_types = alg_types
        self.map_alg_name = map_alg_name
        super().__init__(name, title, json_data, chart_type, xlabel=xlabel, ylabel=ylabel, map_alg_name=map_alg_name, legend_outside=legend_outside)
        self.file_path = '{}/{}{}.png'.format(self.dir_path, self.instance, self.name)

    def __plot_begend(self):
        alg_name = self.summary['type']
        axs = self.axs[self.alg_types.index(alg_name)]
        attempts = self.summary['attempts']
        attempts_steps = [x['steps'] for x in attempts]

        end = []
        beg = []

        for attempt_steps in attempts_steps:
            end.append(attempt_steps[0]['second'])
            beg.append(attempt_steps[1]['second'])

        axs.set_title(self.get_mapped_alg_name(alg_name))
        axs.scatter(beg, end, s=0.5, color=self.marker.color)

    def __plot_restarts(self, only_best=False):
        # self.ylabel = 'Liczba powtórzeń'
        # self.xlabel = 'Łączny koszt przejazdu'
        alg_name = self.summary['type']
        axs = self.axs[self.alg_types.index(alg_name)]
        attempts = self.summary['attempts']
        ox = [range(len(attempts))]
        y = []

        scores = [x['score'] for x in attempts]
        y = [min(scores[0:x]) if only_best else statistics.mean(scores[0:x]) for x in range(1, len(scores)+1)]

        axs.set_title(self.get_mapped_alg_name(alg_name))
        axs.scatter(ox, y, s=0.8, color=self.marker.color)

        axs.set_xlabel(self.xlabel)
        axs.set_ylabel(self.ylabel)

    def __plot_similarity(self):
        axs = self.axs[self.alg_types.index(self.summary['type'])]
        original = self.summary['score']['original']
        attempts = self.summary['attempts']
        y = self.summary['similarity']

        scores = [x['score'] for x in attempts]
        ox = [1/(x/original) for x in scores]

        axs.set_title(self.get_mapped_alg_name(self.summary['type']))
        axs.scatter(ox, y, s=0.5)

        axs.set_xlabel(self.xlabel)
        axs.set_ylabel(self.ylabel)

    def create_plt(self):
        self.fig, self.axs = plt.subplots(len(self.alg_types), 1)

        for axs in self.axs:
            axs.set_xlabel(self.xlabel)
            axs.set_ylabel(self.ylabel)

        # self.fig.suptitle(self.instance, fontsize="x-large")
        # self.fig.text(0.5, 0.04, self.xlabel, ha='center')
        # self.fig.text(0.04, 0.5, self.ylabel, va='center', rotation='vertical')

        for alg_type in self.json_data[self.instance].keys(): 
            if alg_type in self.alg_types:
                self.marker=ALG_MARKERS.get(self.get_mapped_alg_name(alg_type))
                # plt.subplot(1, len(self.alg_types), self.alg_types.index(alg_type) + 1)
                # plt.title(alg_type)

                self.summary = self.json_data[self.instance][alg_type]

                if self.chart_type == CType.BEG_END:
                # plt.errorbar(ox, scores, capsize=4, capth
                    self.__plot_begend()
                elif self.chart_type == CType.PROGRESS_AVG:
                    self.__plot_restarts()
                elif self.chart_type == CType.PROGRESS_BEST:
                    self.__plot_restarts(only_best=True)
                elif self.chart_type == CType.SIMILARITY:
                    self.__plot_similarity()

        # plt.tight_layout()
        plt.tight_layout()
        return plt

bar_width = 0.1


class CompareChart(DefaultChart):
    def __init__(self, name, title, json_data, ctype, alg_types=None, opacity=0.7, ylabel=None, xlabel=None, map_alg_name=True, legend_outside=True):
        self.ctype = ctype
        self.alg_types = alg_types
        self.opacity = opacity
        self.marker = MarkerSpec(None, None)
        self.map_alg_name = map_alg_name
        super().__init__(name, title, json_data, ctype, xlabel=xlabel, ylabel=ylabel, map_alg_name=map_alg_name, legend_outside=legend_outside)
        self.file_path = '{}/{}{}.png'.format(self.dir_path, 'compare', self.name)

    def generate(self):
        plt.xlabel(self.ylabel)
        plt.ylabel(self.xlabel)
        if self.legend_outside:
            plt.legend(loc='lower left', bbox_to_anchor=(0.0, 1.02))
        else:
            plt.legend(loc='best')
        
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

    def __plot_scores(self, alg, positions: [float]):
        scores = [x[alg]['score'][self.ctype.value] for x in self.json_data.values()]

        y = [1/(y/x) for x, y in zip(self.originals, scores)]
        if self.ctype == CType.AVG:
            self.xlabel = 'Jakość (przypadek średni)'
            e = []
            for a in self.attempts:
                x = [x['score'] for x in a]
                e.append(0 if len(x) < 2 else statistics.stdev(x))

            e = [0 if std == 0 else avg - (1 / ((std + score)/org)) for std, org, avg, score in zip(e, self.originals, y, scores)]
            plt.errorbar(positions, y, alpha=self.opacity, capsize=4, capthick=1.2, yerr=e, xerr=None,
                         ls='none', marker=self.marker.sign, label=self.get_mapped_alg_name(alg), color=self.marker.color) #, uplims=True, lolims=True)
        else:
            plt.scatter(positions, y, marker=self.marker.sign, label=self.get_mapped_alg_name(alg), color=self.marker.color)

        if self.ctype == CType.MAX:
            self.xlabel = 'Jakość (przypadek najgorszy)'
        elif self.ctype == CType.MIN:
            self.xlabel = 'Jakość (przypadek najlepszy)'
        plt.ylim(0.0, 1.2) # todo: erase 0.0 

    def __plot_times(self, alg, positions: [float]):
        y = self.times
        plt.scatter(positions, y, marker=self.marker.sign, color=self.marker.color, label=self.get_mapped_alg_name(alg))
        self.xlabel = 'Średni czas działania'
        plt.yscale('log')

    def __plot_steps(self, alg, positions: [float]):
        steps = []
        for a in self.attempts:
            x = [x['steps'][-1]['first'] for x in a]
            steps.append(x)
        y = [statistics.mean(x) for x in steps]
        e = [0 if len(x) < 2 else statistics.stdev(x) for x in steps]
        plt.errorbar(positions, y, alpha=self.opacity, capsize=4, capthick=1.2, yerr=e, xerr=None, ls='none', marker=self.marker.sign, label=self.get_mapped_alg_name(alg), color=self.marker.color)
        self.xlabel = 'Średnia liczba kroków algorytmu'
        plt.yscale('log')

    def __plot_efficiency(self, alg, positions: [float]):
        scores = [x[alg]['score']['avg'] for x in self.json_data.values()]
        efficiency = [(best/avg/(time/best_time)) for best, avg, time, best_time in zip(self.originals, scores, self.times, self.best_times)]
        plt.scatter(positions, efficiency, marker=self.marker.sign, label=self.get_mapped_alg_name(alg), color=self.marker.color)
        self.xlabel = 'Efektywność algorytmu'

    def create_plt(self):
        self.labels = (self.json_data.keys())
        self.best_times = self.__get_best_times()
        plt.grid(True, linestyle='--')
        total = len(next(iter(self.json_data.values())))
        i = 0
        index = np.arange(len(list(self.labels)))

        for alg in next(iter(self.json_data.values())):
            positions = index + (i - total / 2) * bar_width
            # plot only selected algorithms (default all)
            if self.alg_types is None or alg in self.alg_types:
                self.originals = [x[alg]['score']['original'] for x in self.json_data.values()]
                self.attempts = [x[alg]['attempts'] for x in self.json_data.values()]
                self.times = [x[alg]['averageTime'] for x in self.json_data.values()]
                self.marker=ALG_MARKERS.get(alg, MarkerSpec(MARKERS[i%len(MARKERS)], None))

                if self.ctype in ([CType.AVG, CType.MAX, CType.MIN]): 
                    self.__plot_scores(alg, positions)
                
                elif self.ctype == CType.TIME:
                    self.__plot_times(alg, positions)

                elif self.ctype == CType.AVG_STEPS:
                    self.__plot_steps(alg, positions)

                elif self.ctype == CType.TIME_EFF:
                    self.__plot_efficiency(alg, positions)
                plt.xticks(index, self.labels)

                i += 1

        plt.xticks(rotation=60, ha='right')
        self.ylabel = 'Nazwa wczytanej instancji'
        
        # plt.title(self.title)
        # plt.show()

        return plt
