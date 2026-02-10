import numpy
from pandas import *
from matplotlib import pyplot as plt


def calculate_column_widths(df, min_width=0.08, max_width=2):

    widths = []
    for col in df.columns:
        max_len = max(
            len(str(col)),  # Длина заголовка
            df[col].astype(str).str.len().max()  # Макс. длина данных
        )
        # Нормализуем ширину (простая эвристика)
        width = min_width + (max_len * 0.03)
        width = min(width, max_width)  # Ограничиваем максимальную ширину

        widths.append(width)

    # Нормализуем, чтобы сумма была примерно 1
    total = sum(widths)
    if total > 1:
        widths = [w / total * 0.95 for w in widths]

    return widths


df = read_excel("lab5.xlsm")

rows = []
df = df.drop(df.columns[5], axis=1)
# df = df.drop(df.columns[3], axis=1)
df = df.fillna("")
df = df.iloc[2:14]
df = df.iloc[:, :24].astype('int64', errors='ignore')
for row in df.values:
    rows.append(row[0:24])
data = DataFrame(rows)

print(data.to_string())

widths = calculate_column_widths(data)
fig, ax = plt.subplots(figsize=(10, 4))
ax.axis('off')
ax.axis('tight')
table = ax.table(
    cellText=data.values,  # Данные
    loc='center',  # Положение
    cellLoc='center'  # Выравнивание в ячейках
)

table.auto_set_font_size(False)
cells = table.get_celld()
for key in cells.keys():
    cell = cells[key]
    row, col = key
    cell.set_width(widths[col])
    if col == 0:
        cell.set_color('#bf1d2a')
    elif col == 1:
        cell.set_color('#804080')
    elif col == 2:
        cell.set_color('#404580')
    elif col == 3 or col == 4:
        cell.set_color('#5a8040')
    else:
        cell.set_color('#408080')
    cell.set_edgecolor('black')

    # bbox = cell.get_bbox()
    # x1, y1, x2, y2 = bbox.xmin, bbox.ymin, bbox.xmax, bbox.ymax
    # print(x1, x2)
    # if col == 0:
    #     ax.plot([0.007, 0.007], [0, 10], color='blue', linewidth=2)
    # if row == 0:
    #     ax.plot([x1, x2], [y1, y1], color='red', linewidth=3)


table.set_fontsize(10)
table.scale(1.2, 1.5)
plt.show()