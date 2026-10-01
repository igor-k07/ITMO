"""
ПРОЕКТНАЯ РАБОТА. Часть 2. Вариант 3.
"Управление бионическим протезом — сглаживание кинематики".
Аппроксимация 21 значения тригонометрическим многочленом 5-й степени
через ортогональное проектирование в R^21.
"""
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import os

OUT = os.path.dirname(__file__)

# --- Исходные данные (вариант 3) -----------------------------------------
y = np.array([
    -1.24, -1.17, -1.08, -0.96, -0.84, -0.79, -0.80, -0.90, -1.10, -1.21, -1.02,
    -1.28, -1.32, -1.34, -1.36, -1.37, -1.37, -1.36, -1.35, -1.33, -1.30
])
assert y.size == 21
N = 21
i = np.arange(1, N+1)
theta = 2*np.pi*(i - 1) / N

# --- Базис подпространства U размерности 11 ------------------------------
ones = np.ones(N)
basis = [ones]
for k in range(1, 6):
    basis.append(np.cos(k*theta))
    basis.append(np.sin(k*theta))
basis = np.array(basis)  # (11, 21)
names = ["1", "c1", "s1", "c2", "s2", "c3", "s3", "c4", "s4", "c5", "s5"]

# --- Проверка ортогональности (численно) ---------------------------------
G = basis @ basis.T  # матрица Грама
print("Матрица Грама (ожидаем диагональ: 21, 21/2, 21/2, ...):")
with np.printoptions(precision=3, suppress=True):
    print(G)
off_max = np.max(np.abs(G - np.diag(np.diag(G))))
print(f"Макс. модуль внедиагонального элемента: {off_max:.2e}  (≈ 0  ⇒  базис ортогонален)")

# --- Коэффициенты через скалярные произведения --------------------------
a0 = (2/N) * np.dot(y, ones)
a = np.zeros(6)
b = np.zeros(6)
a[0] = a0
for k in range(1, 6):
    a[k] = (2/N) * np.dot(y, np.cos(k*theta))
    b[k] = (2/N) * np.dot(y, np.sin(k*theta))

print("\nКоэффициенты:")
print(f"  a0 = {a0:+.6f}")
for k in range(1, 6):
    print(f"  a{k} = {a[k]:+.6f},  b{k} = {b[k]:+.6f}")

# --- Значения многочлена в точках выборки -------------------------------
def trig_poly(th):
    s = a0/2 + np.zeros_like(th)
    for k in range(1, 6):
        s = s + a[k]*np.cos(k*th) + b[k]*np.sin(k*th)
    return s

f_vec = trig_poly(theta)
e_vec = y - f_vec

eps_abs = np.linalg.norm(e_vec)
eps_rel = eps_abs / np.linalg.norm(y)
R2 = (1 - eps_rel**2) * 100
print(f"\nε_abs = {eps_abs:.6f}")
print(f"ε_rel = {eps_rel:.6f}")
print(f"R² = {R2:.3f} %")

# --- Проверка идемпотентности проектора P² = P --------------------------
# Применим тот же проектор к f_vec — должны получить тот же f_vec
ap0 = (2/N) * np.dot(f_vec, ones)
ap = [ap0/2 + np.zeros_like(theta)]
for k in range(1, 6):
    ak = (2/N) * np.dot(f_vec, np.cos(k*theta))
    bk = (2/N) * np.dot(f_vec, np.sin(k*theta))
Pf = ap0/2 + sum(
    (2/N)*np.dot(f_vec, np.cos(k*theta))*np.cos(k*theta)
  + (2/N)*np.dot(f_vec, np.sin(k*theta))*np.sin(k*theta)
    for k in range(1, 6)
)
print(f"\nПроверка P²=P:  ||P(f) − f|| = {np.linalg.norm(Pf - f_vec):.3e}  (должно быть ≈ 0)")

# Проверка ортогональности ошибки базису U
print("Проверка ортогональности e ⊥ U  (⟨e, базисный⟩ должно быть ≈ 0):")
for nm, bv in zip(names, basis):
    print(f"  ⟨e, {nm:>3}⟩ = {np.dot(e_vec, bv):+.3e}")

# --- Визуализация --------------------------------------------------------
fig = plt.figure(figsize=(13, 9))

# График 1: исходные данные и сглаженный сигнал
ax1 = fig.add_subplot(2, 2, 1)
ax1.plot(theta, y, "o", color="black", label="данные y_i", markersize=6)
th_fine = np.linspace(0, 2*np.pi, 400)
ax1.plot(th_fine, trig_poly(th_fine), "-", color="crimson",
         label="тригон. многочлен f(θ)", lw=2)
ax1.plot(theta, f_vec, "s", color="crimson", markersize=4, alpha=0.7,
         label="f(θ_i) — проекция")
# Подсветим аномалию (индекс 11 — резкий выброс 7.422 в варианте 1, но в нашем варианте 3
# плавная кинематика; точка 11 (i=11): -1.02 — небольшой всплеск)
idx_outlier = 10  # i = 11 (0-индексация)
ax1.axvspan(theta[idx_outlier]-0.1, theta[idx_outlier]+0.1, color="yellow", alpha=0.3,
            label="артефакт (i=11)")
ax1.set_xlabel("θ"); ax1.set_ylabel("угол сустава, рад")
ax1.set_title("Кинематика сустава: данные и сглаживающая проекция")
ax1.legend(fontsize=8); ax1.grid(True, alpha=0.3)

# График 2: ошибка
ax2 = fig.add_subplot(2, 2, 2)
ax2.stem(theta, e_vec, basefmt=" ")
ax2.axhline(0, color="gray", lw=0.5)
ax2.set_xlabel("θ"); ax2.set_ylabel("e_i = y_i − f(θ_i)")
ax2.set_title(f"Ортогональная составляющая e ∈ U⊥\n||e||={eps_abs:.4f},  R² = {R2:.2f} %")
ax2.grid(True, alpha=0.3)

# График 3: спектр гармоник
ax3 = fig.add_subplot(2, 2, 3)
ks = np.arange(0, 6)
amp_cos = np.abs(a.copy()); amp_cos[0] = abs(a0/2)  # для a0 рисуем a0/2
amp_sin = np.abs(b)
width = 0.35
ax3.bar(ks - width/2, amp_cos, width, label="|a_k|", color="steelblue")
ax3.bar(ks + width/2, amp_sin, width, label="|b_k|", color="orange")
ax3.set_xlabel("k"); ax3.set_ylabel("амплитуда")
ax3.set_title("Спектр коэффициентов")
ax3.set_xticks(ks); ax3.legend(); ax3.grid(True, alpha=0.3)

# График 4: ортогональность базиса (heatmap матрицы Грама)
ax4 = fig.add_subplot(2, 2, 4)
im = ax4.imshow(G, cmap="RdBu_r", vmin=-21, vmax=21)
ax4.set_xticks(range(11)); ax4.set_yticks(range(11))
ax4.set_xticklabels(names, fontsize=8); ax4.set_yticklabels(names, fontsize=8)
ax4.set_title("Матрица Грама базиса U\n(чисто диагональная ⇒ базис ортогонален)")
plt.colorbar(im, ax=ax4, shrink=0.8)

plt.tight_layout()
plt.savefig(os.path.join(OUT, "fig_part2_overview.png"), dpi=140)
plt.close(fig)

# --- Сохраним числовые результаты ----------------------------------------
with open(os.path.join(OUT, "part2_results.txt"), "w", encoding="utf-8") as fh:
    fh.write("ПРОЕКТНАЯ РАБОТА. Часть 2. Вариант 3.\n")
    fh.write("Управление бионическим протезом — сглаживание кинематики.\n")
    fh.write("="*70 + "\n\n")
    fh.write("Исходные данные (21 значение угла сустава, рад):\n")
    fh.write(", ".join(f"{v:+.3f}" for v in y) + "\n\n")
    fh.write("Точки выборки:  θ_i = 2π(i−1)/21,  i = 1..21\n\n")
    fh.write("Базис подпространства U ⊂ R²¹ (dim U = 11):\n")
    fh.write("  {1, cos(kθ), sin(kθ) : k=1..5}\n\n")
    fh.write("Численная проверка ортогональности базиса:\n")
    fh.write(f"  max |G_{{ij}}|_{{i≠j}} = {off_max:.2e}\n")
    fh.write(f"  diag(G) = {np.diag(G)}  (теоретически 21, 21/2 = 10.5, ...)\n\n")
    fh.write("КОЭФФИЦИЕНТЫ ФУРЬЕ:\n")
    fh.write(f"  a0 = {a0:+.6f}\n")
    for k in range(1, 6):
        fh.write(f"  a{k} = {a[k]:+.6f}    b{k} = {b[k]:+.6f}\n")
    fh.write("\nМЕТРИКИ:\n")
    fh.write(f"  абсолютная ошибка ε_abs = ||y − f|| = {eps_abs:.6f}\n")
    fh.write(f"  относительная ошибка ε_rel       = {eps_rel:.6f}\n")
    fh.write(f"  коэффициент детерминации R²      = {R2:.3f} %\n\n")
    fh.write(f"Проверка идемпотентности проектора: ||P(f) − f|| = "
             f"{np.linalg.norm(Pf - f_vec):.2e}\n\n")
    fh.write("ВЕКТОР ОШИБКИ e = y − f:\n")
    fh.write(", ".join(f"{v:+.4f}" for v in e_vec) + "\n")
print("Готово. Результаты в", OUT)
