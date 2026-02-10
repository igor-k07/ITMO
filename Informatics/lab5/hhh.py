import re

pattern = re.compile(r'Людовик(?!X(IX|IV|V?I{,3}))')

lines = [
    'ЛюдовикXVI',
    'Людовик',
    'ЛюдовикLXVII',
    'ЛюдовикXXL',
    'ЛюдовикXV',
    'ЛюдовикXVIII',
]

for s in lines:
    if pattern.search(s):
        print('match:', s)
