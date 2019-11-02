import os
import numpy as np
from matplotlib import pyplot as plt
import re
from enum import Enum
import statistics

MARKERS = ['s', 'o', '*', 'p', 'x', 'D']

class DefaultChart:
    def __init__(self, name, json_data):
        plt.clf()
        self.json_data = json_data
        self.name = name
        self.plt = self.create_plt()
        self.dir_path = re.sub(r'[ĘÓĄŚŁŻŹĆŃęóąśłżźćń ]', '', 'imgs/{}'.format(name))
        self.file_path = None

    def create_plt(self):
        raise NotImplementedError

    def generate(self):
        out_path = os.path.join('out', self.file_path)

        if not os.path.exists(os.path.join('out', self.dir_path)):
            os.makedirs(os.path.join('out', self.dir_path))
        
        self.plt.savefig(os.path.join('out', self.file_path))

        return R'''
\begin{figure}[H]
\includegraphics[width=\columnwidth]{''' + self.file_path + '''}
\caption{''' + self.name + '''}
\end{figure}
'''

class SingleInstanceChart(DefaultChart):
    def __init__(self, name, json_data):
        super().__init__(name, json_data)
        self.file_path = '{}/{}.png'.format(self.dir_path, self.json_data['name'])

class CType(Enum):
        MAX = 'max'
        MIN = 'min'
        AVG = 'avg'
        TIME = 'averageTime'
        AVG_STEPS = 'avg_steps'

class CompareChart(DefaultChart):
    def __init__(self, name, json_data, ctype, title, alg_types=None):
        self.title = title
        self.ctype = ctype
        self.alg_types = alg_types

        super().__init__(name, json_data)
        self.file_path = '{}/{}.png'.format(self.dir_path, 'img')


    def create_plt(self):
        labels = (self.json_data.keys())
        x = np.arange(len(labels))
        i = 0

        for alg in next(iter(self.json_data.values())):

            # plot only selected algorithms (default all)
            if self.alg_types is None or alg in self.alg_types:
                if self.ctype in ([CType.AVG, CType.MAX, CType.MIN]):
                    scores = [x[alg]['score'][self.ctype.value] for x in self.json_data.values()]
                    originals = [x[alg]['score']['original'] for x in self.json_data.values()]
                    
                    y = [x/y for x, y in zip(originals, scores)]

                    if self.ctype == CType.AVG:
                        attempts = [x[alg]['attempts'] for x in self.json_data.values()]
                        e = []
                        for a in attempts:
                            x = [x['score'] for x in a]
                            e.append(0 if len(x) < 2 else statistics.stdev(x))

                        e = [x/y for x, y in zip(e, originals)]

                        plt.errorbar(labels, y, capsize=4, capthick=1.2, yerr=e, xerr=None, ls='none', marker=MARKERS[i], label=alg) #, uplims=True, lolims=True)
                    else:
                        plt.scatter(labels, y, marker=MARKERS[i], label=alg)
                    plt.ylim(0.0, 1.2) # todo: erase 0.0 
                
                elif self.ctype == CType.TIME:
                    y = [x[alg]['averageTime'] for x in self.json_data.values()]
                    plt.scatter(labels, y, marker=MARKERS[i], label=alg)

                elif self.ctype == CType.AVG_STEPS:
                    attempts = [x[alg]['attempts'] for x in self.json_data.values()]
                    steps = []
                    for a in attempts:
                        x = [x['steps'][-1]['first'] for x in a]
                        steps.append(x)
                    y = [statistics.mean(x) for x in steps]
                    e = [0 if len(x) < 2 else statistics.stdev(x) for x in steps]
                    plt.errorbar(labels, y, capsize=4, capthick=1.2, yerr=e, xerr=None, ls='none', marker=MARKERS[i], label=alg)


                i += 1

        plt.xticks(rotation=90)
        plt.legend()
        plt.ylabel('Wynik')
        plt.title(self.title)
        plt.show()

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