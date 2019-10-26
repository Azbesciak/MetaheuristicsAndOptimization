import os
import numpy as np
from matplotlib import pyplot as plt



class DefaultChart:
    def __init__(self, name, json_data):
        self.json_data = json_data
        self.name = name
        self.plt = self.create_plt()

    def create_plt(self):
        raise NotImplementedError

    def generate(self):
        img_path = 'imgs/{}'.format(self.name)
        if not os.path.exists(img_path):
            os.mkdir(img_path)
        self.plt.savefig('{}/{}.png'.format(img_path, self.json_data['name']))

class SummaryReport(DefaultChart):
    def create_plt(self):
        labels = ('Avg', 'Min', 'Max', 'Origin')
        y_pos = np.arange(len(labels))
        scores = self.json_data['score']
        performance = [scores['avg'], scores['min'], scores['max'], scores['original']]

        plt.bar(y_pos, performance, align='center', alpha=0.5)
        plt.xticks(y_pos, labels)
        plt.ylabel('Score')
        plt.title('Scores')

        return plt