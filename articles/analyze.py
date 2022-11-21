"""
This script analyzes the given database for word frequency.
The generated data may be useful for determining stop words and
analyzing the usefulness of frequency data within individual articles.
"""

import sys
import re
from collections import defaultdict
from nltk.stem import PorterStemmer

if len(sys.argv) != 2:
    print("Improper usage: python3 analyze.py <name of database file>")
    exit(-1)

_, filename = sys.argv

corpus = defaultdict(lambda: 0)
porter = PorterStemmer()

with open(filename, 'r') as f:
    for i in range(18595):
        title, body = (f.readline()[:-1], f.readline()[:-1])
        # parse the body
        body = body.lower()
        # remove all references
        body = re.sub(r"\[[a-zA-Z0-9]+\]", "", body)
        # remove all punctuation
        body = re.sub(r"[^a-z0-9 ]", "", body)
        # now tokenize
        tokens = body.split(' ')
        for t in tokens:
            t = porter.stem(t)
            corpus[t]+=1
print(corpus)
# write the corpus to a csv file
sorted_keys = list(corpus.keys())
sorted_keys.sort(key=lambda x: corpus[x], reverse=True)

with open("corpus.csv", "w") as f:
    for k in sorted_keys:
        f.write(f'{k}, {corpus[k]}\n')
    



