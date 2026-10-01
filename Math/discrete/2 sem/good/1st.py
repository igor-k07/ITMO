# по матрице смежности при помощи алгоритма, который использует упорядочивание
# вершин, красит граф в правильную раскраску, поясняет каждый шаг

matrix = [[1 if c.isdigit() and c != '0'
           else 0 for c in line.split(';')]
          for line in open('graph.csv').read().strip().split('\n')]



graph = {v + 1: {u + 1: matrix[v][u] for u in range(len(matrix[v]))}
         for v in range(len(matrix))}
# для отладки выведем сам граф:
#for v in graph:
    #print(f'{v}: {graph[v]}')
print('-' * 30)

# graph[v][u] == 1 - смежные вершины, 0 - несмежные
colors = {}  # colors[j] - вершины, покрашенные в j-тый цве3т
j = 1  # текущий цвет
while graph:
    print(f'j={j}')
    colors[j] = list()
    vs = sorted(graph, key=lambda v: (-sum(graph[v].values()), v))
    for v in graph:
        print(f'v={v}, r({v})={sum(graph[v].values())}')
    print('Упорядоченные в порядке невозрастания r вершины:')
    print(vs)
    del_vs = list()
    for v in vs:
        fl = True
        for u in colors[j]:
            if graph[v][u] == 1:
                fl = False
        if fl:
            colors[j].append(v)
            del_vs.append(v)
    print(f'Красим в цвет j={j} вершины:')
    print(*del_vs)
    for v in del_vs:
        for u in graph:
            del graph[u][v]
        del graph[v]
    j += 1
    print('-' * 30)
print('Ответ:')
print(f'Задействовано цветов: {len(colors)}')
print(colors)


print('Матрица смежности:')
print("e1 e2 e3 e4 e5 e6 e7 e8 e9 10 11 12")
source = [line.split(';') for line in open('graph.csv').read().strip().split('\n')]

for i in range(len(source)):
    for j in range(len(source[i])):
        elem = "-" if source[i][j] == '' else source[i][j]
        print(f'{elem:>2}', end=' ')
    print()