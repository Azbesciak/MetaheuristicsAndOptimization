import os
import numpy as np
from matplotlib import pyplot as plt
import re



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

class SummaryReport(DefaultChart):
    def create_plt(self):
        labels = ('Avg', 'Min', 'Max', 'Origin')
        y_pos = np.arange(len(labels))
        scores = self.json_data['score']
        performance = [scores['avg'], scores['min'], scores['max'], scores['original']]

        plt.bar(y_pos, performance, align='center', alpha=0.5)
        plt.xticks(y_pos, labels)
        plt.ylabel('Wynik')
        plt.title('Scores')

        return plt

class SeqReport(DefaultChart):
    def create_plt(self):
        attempts = self.json_data['attempts']
        x = range(len(attempts))
        plt.ylabel('Wynik')
        plt.title('Jakość rozwiązania początkowego vs końcowego')
        plt.scatter(x, attempts)

        return plt