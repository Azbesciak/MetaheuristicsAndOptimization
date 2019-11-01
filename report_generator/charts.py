import os
import numpy as np
from matplotlib import pyplot as plt
import re
from enum import Enum
import statistics

MARKERS = ['o', '^', 's', '*', 'p', 'x', 'D']

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

class CompareChart(DefaultChart):
    def __init__(self, name, json_data, ctype, title):
        self.title = title
        self.ctype = ctype

        super().__init__(name, json_data)
        self.file_path = '{}/{}.png'.format(self.dir_path, 'img')


    def create_plt(self):
        labels = (self.json_data.keys())
        x = np.arange(len(labels))
        i = 0

        for alg in next(iter(self.json_data.values())):
            y = [x[alg]['score']['original'] / x[alg]['score'][self.ctype.value] for x in self.json_data.values()]
            plt.scatter(labels, y, marker=MARKERS[i], label=alg)
            i += 1

        plt.ylim(0.0, 1.2) # todo: erase 0.0 
        plt.xticks(rotation=90)
        plt.legend()
        plt.ylabel('Wynik')
        plt.title(self.title)

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