import matplotlib.pyplot as plt
import networkx as nx
import numpy as np

# Порядок вершин согласно Гамильтонову циклу
cycle_nodes = ["x1", "x2", "x3", "x4", "x9", "x5", "x6", "x8", "x11", "x12", "x10", "x7"]
n = len(cycle_nodes)

G = nx.Graph()
G.add_nodes_from(cycle_nodes)

# Ребра Гамильтонова цикла
cycle_edges = [(cycle_nodes[i], cycle_nodes[(i + 1) % n]) for i in range(n)]

# Внутренние хорды (Psi_4) — всегда идут от x3 (веер)
psi_4 = [("x3", "x1"), ("x3", "x9"), ("x3", "x8"), ("x3", "x11"), ("x3", "x12"), ("x3", "x10")]
# Внешние хорды (Psi_9)
psi_9 = [("x2", "x10"), ("x2", "x8"), ("x2", "x7")]

fig, ax = plt.subplots(figsize=(10, 10))

# Позиции вершин на окружности
pos = {}
for i, node in enumerate(cycle_nodes):
    angle = 2 * np.pi * i / n
    pos[node] = np.array([np.cos(angle), np.sin(angle)])

# Рисуем вершины
nx.draw_networkx_nodes(G, pos, node_color="#2dd4bf", node_size=700, ax=ax)
nx.draw_networkx_labels(G, pos, font_size=11, font_weight="bold", ax=ax)

# 1. Цикл — черные прямые линии по периметру
nx.draw_networkx_edges(G, pos, edgelist=cycle_edges, width=2, edge_color="black", ax=ax)

# 2. Внутренние хорды (Psi_4) — СТРОГО ПРЯМЫЕ линии внутри круга (веер не пересекается!)
nx.draw_networkx_edges(G, pos, edgelist=psi_4, width=2, edge_color="#2563eb", ax=ax)

# 3. Внешние хорды (Psi_9) — строго внешние дуги
for u, v in psi_9:
    p1 = pos[u]
    p2 = pos[v]
    
    # Считаем расстояние по циклу, чтобы подобрать оптимальный изгиб
    idx_u = cycle_nodes.index(u)
    idx_v = cycle_nodes.index(v)
    dist = min(abs(idx_u - idx_v), n - abs(idx_u - idx_v))
    
    # Знак радиуса определяет, в какую сторону выгибается дуга. 
    # Ставим отрицательный, чтобы они гарантированно уходили во внешнюю сторону
    rad = -0.2 * dist 
    
    ax.annotate(
        "", xy=p2, xycoords='data',
        xytext=p1, textcoords='data',
        arrowprops=dict(
            arrowstyle="-",
            color="#dc2626",
            linewidth=2,
            linestyle="--",
            connectionstyle=f"arc3,rad={rad}",
        )
    )

plt.title("Планаризованный плоский граф (Вариант 28)", fontsize=14, fontweight="bold")
plt.axis("off")

# Немного расширим границы отображения, чтобы внешние дуги не обрезались по краям
ax.set_xlim(-1.4, 1.4)
ax.set_ylim(-1.4, 1.4)

plt.tight_layout()
plt.show()