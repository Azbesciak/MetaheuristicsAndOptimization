import os
import numpy as np
from matplotlib import pyplot as plt
import re
import statistics



class DefaultChart:
    def __init__(self, name, json_data):
        plt.clf()
        self.json_data = json_data
        self.name = name
        self.plt = self.create_plt()

    def create_plt(self):
        raise NotImplementedError

    def generate(self):
        dir_path = re.sub(r'[ĘÓĄŚŁŻŹĆŃęóąśłżźćń ]', '', 'imgs/{}'.format(self.name))
        file_path = '{}/{}.png'.format(dir_path, self.json_data['name'])
        out_path = os.path.join('out', file_path)

        if not os.path.exists(os.path.join('out', dir_path)):
            os.makedirs(os.path.join('out', dir_path))
        
        self.plt.savefig(os.path.join('out', file_path))

        return R'''
\begin{figure}[H]
\includegraphics[width=\columnwidth]{''' + file_path + '''}
\caption{''' + self.name + '''}
\end{figure}

Wnioski: TODO
'''

class SummaryChart(DefaultChart):
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

class SeqChart(DefaultChart):
    def create_plt(self):
        attempts = self.json_data['attempts']
        scores = self.json_data['score']
        std = statistics.stdev(attempts)

        x = range(len(attempts))
        plt.ylabel('Wynik')
        plt.title('Wyniki znalezionych rozwiązań')
        plt.scatter(x, attempts)

        plt.axhline(y=scores['avg'], color='r', linestyle='-', label='avg')

        plt.axhline(y=scores['avg']-std, color='orange', linestyle='--', label='avg')
        plt.axhline(y=scores['avg']+std, color='orange', linestyle='--', label='avg')

        plt.show()

        return plt