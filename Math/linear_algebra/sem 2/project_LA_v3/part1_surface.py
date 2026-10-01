"""
ПРОЕКТНАЯ РАБОТА ПО ЛИНЕЙНОЙ АЛГЕБРЕ. Часть 1. Вариант 3.
Однополостный гиперболоид - "небоскреб будущего".

Исходное уравнение:
    4x^2 - y^2 + 4z^2 + 10xy + 10yz + 10x - 2y + 10z - 37 = 0
"""
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D  # noqa
import os

OUT = os.path.dirname(__file__)

# --- 1. Матрица квадратичной формы ---------------------------------------
A = np.array([
    [4, 5, 0],
    [5, -1, 5],
    [0, 5, 4],
], dtype=float)
# линейная часть (коэффициенты при x, y, z)
L = np.array([10.0, -2.0, 10.0])
c0 = -37.0

print("Матрица квадратичной формы A =")
print(A)
print("Линейный вектор L =", L)
print("Свободный член c =", c0)

# --- 2. Собственные числа и собственные векторы ---------------------------
# Аналитически:  λ1=9, λ2=4, λ3=-6
# Проверим численно
w, V = np.linalg.eigh(A)
print("\nСобственные числа (numpy):", w)
print("Собственные векторы (столбцы):\n", V)

# Аналитические собственные векторы (нормированные):
e1 = np.array([1, 1, 1]) / np.sqrt(3)      # λ = 9
e2 = np.array([-1, 0, 1]) / np.sqrt(2)     # λ = 4
e3 = np.array([1, -2, 1]) / np.sqrt(6)     # λ = -6

# Сформируем ортогональную матрицу Q (det = +1, правая тройка)
Q = np.column_stack([e1, e2, e3])
print("\nQ =\n", Q)
print("det(Q) =", np.linalg.det(Q))
print("Q^T A Q =\n", np.round(Q.T @ A @ Q, 10))

lam = np.array([9.0, 4.0, -6.0])

# --- 3. Преобразование линейной части ---------------------------------------
# x = Q x'   =>   L^T x = L^T Q x' = L'^T x'
Lp = Q.T @ L
print("\nЛинейная часть в собств. базисе L' =", Lp)
# Должно получиться: (6*sqrt(3), 0, 4*sqrt(6))
print("Проверка: 6*sqrt(3) =", 6*np.sqrt(3), "; 4*sqrt(6) =", 4*np.sqrt(6))

# Уравнение:  9 x'^2 + 4 y'^2 - 6 z'^2 + 6√3 x' + 4√6 z' - 37 = 0

# --- 4. Выделение полных квадратов -----------------------------------------
# 9(x' + 1/√3)^2 - 3 + 4 y'^2 - 6 (z' - √6/3)^2 + 4 - 37 = 0
# 9 X^2 + 4 Y^2 - 6 Z^2 = 36
# X^2/4 + Y^2/9 - Z^2/6 = 1
# где X = x' + 1/√3, Y = y', Z = z' - √6/3
shift_prime = np.array([-1/np.sqrt(3), 0.0, np.sqrt(6)/3])  # координаты центра в собств. базисе
center = Q @ shift_prime
print("\nЦентр (в исходных координатах) =", center)  # должно быть (0, -1, 0)

# Проверка: подставим центр в исходное уравнение
def F(x, y, z):
    return (4*x**2 - y**2 + 4*z**2 + 10*x*y + 10*y*z
            + 10*x - 2*y + 10*z - 37)
print("F(центр) =", F(*center))

# Параметры канонического вида
a2, b2, c2 = 4.0, 9.0, 6.0
a, b, cc = np.sqrt(a2), np.sqrt(b2), np.sqrt(c2)
print(f"\nКаноническое уравнение:  X^2/{a2} + Y^2/{b2} - Z^2/{c2} = 1")
print(f"a = {a},  b = {b},  c = {cc:.6f} (≈ √6)")
print("Тип: однополостный гиперболоид (signature (+,+,-)).")

# --- 5. Семейства прямолинейных образующих ------------------------------------
# X/a + Z/c = (1/u) (1 - Y/b)
# X/a - Z/c =  u    (1 + Y/b)
# и второе семейство со знаком минус перед Y.
# Запишем параметрически: точка на образующей в (X,Y,Z) системе
# параметризуем через v = Y/b ∈ [-1, 1]?  Лучше через параметр t = Y (любое число),
# и второй параметр u (выбор образующей).
def gen_family1(u, t):
    """Параметрическое представление семейства I в (X,Y,Z) канонических координатах.
    Параметр t = Y, u — выбор образующей.
    Прямая получается фиксацией u и вариацией t."""
    # X/a + Z/c = (1/u)(1 - t/b)
    # X/a - Z/c =  u   (1 + t/b)
    s1 = (1 - t/b) / u   # = X/a + Z/c
    s2 = u * (1 + t/b)   # = X/a - Z/c
    X = a * (s1 + s2) / 2
    Z = cc * (s1 - s2) / 2
    Y = t
    return X, Y, Z

def gen_family2(v, t):
    s1 = (1 + t/b) / v   # = X/a + Z/c
    s2 = v * (1 - t/b)   # = X/a - Z/c
    X = a * (s1 + s2) / 2
    Z = cc * (s1 - s2) / 2
    Y = t
    return X, Y, Z

# Проверка: точка на образующей лежит на гиперболоиде
for fam, params in [(gen_family1, (1.7, 0.5)), (gen_family2, (-0.8, -0.3))]:
    X, Y, Z = fam(*params)
    val = X**2/a2 + Y**2/b2 - Z**2/c2
    print(f"  {fam.__name__}{params}: X^2/a^2+Y^2/b^2-Z^2/c^2 = {val:.6f}")

# --- 6. Преобразование к исходным координатам ---------------------------------
# (X,Y,Z) -> (x',y',z'):  x' = X - 1/√3,  y' = Y,  z' = Z + √6/3
# (x',y',z') -> (x,y,z):  x = Q (x',y',z')^T
def canonical_to_original(X, Y, Z):
    xp = X - 1/np.sqrt(3)
    yp = Y
    zp = Z + np.sqrt(6)/3
    pts = np.stack([xp, yp, zp], axis=0)  # shape (3, ...)
    orig = np.tensordot(Q, pts, axes=([1], [0]))
    return orig[0], orig[1], orig[2]

# --- 7. Визуализация поверхности и прямолинейных образующих --------------------
fig = plt.figure(figsize=(14, 10))

# параметрическая поверхность: X = a cosh(u) cos(v), Y = b cosh(u) sin(v), Z = c sinh(u)
u = np.linspace(-1.2, 1.2, 60)
v = np.linspace(0, 2*np.pi, 80)
U, V = np.meshgrid(u, v)
Xc = a * np.cosh(U) * np.cos(V)
Yc = b * np.cosh(U) * np.sin(V)
Zc = cc * np.sinh(U)

ax1 = fig.add_subplot(2, 2, 1, projection="3d")
ax1.plot_surface(Xc, Yc, Zc, alpha=0.35, cmap="viridis", edgecolor="none")
ax1.set_title("Канонический вид:  X²/4 + Y²/9 − Z²/6 = 1")
ax1.set_xlabel("X"); ax1.set_ylabel("Y"); ax1.set_zlabel("Z")

# Семейства прямых
t_arr = np.linspace(-6, 6, 30)
us = np.linspace(0.3, 3.0, 8)
ax2 = fig.add_subplot(2, 2, 2, projection="3d")
ax2.plot_surface(Xc, Yc, Zc, alpha=0.18, color="lightgray", edgecolor="none")
for uu in us:
    X1, Y1, Z1 = gen_family1(uu, t_arr)
    ax2.plot(X1, Y1, Z1, color="crimson", lw=0.9)
    X2, Y2, Z2 = gen_family2(uu, t_arr)
    ax2.plot(X2, Y2, Z2, color="royalblue", lw=0.9)
ax2.set_title("Два семейства прямолинейных образующих\n(канонические координаты)")
ax2.set_xlabel("X"); ax2.set_ylabel("Y"); ax2.set_zlabel("Z")

# Та же поверхность и образующие в исходных координатах
ax3 = fig.add_subplot(2, 2, 3, projection="3d")
xo, yo, zo = canonical_to_original(Xc, Yc, Zc)
ax3.plot_surface(xo, yo, zo, alpha=0.25, cmap="plasma", edgecolor="none")
for uu in us:
    X1, Y1, Z1 = gen_family1(uu, t_arr)
    xx, yy, zz = canonical_to_original(X1, Y1, Z1)
    ax3.plot(xx, yy, zz, color="crimson", lw=0.7)
    X2, Y2, Z2 = gen_family2(uu, t_arr)
    xx, yy, zz = canonical_to_original(X2, Y2, Z2)
    ax3.plot(xx, yy, zz, color="royalblue", lw=0.7)
ax3.scatter(*center, color="black", s=40)
ax3.set_title("Поверхность в исходной системе координат\n(красные/синие — образующие)")
ax3.set_xlabel("x"); ax3.set_ylabel("y"); ax3.set_zlabel("z")

# Сечения горизонтальными плоскостями Z = const (в канонических координатах)
ax4 = fig.add_subplot(2, 2, 4)
for Z0 in np.linspace(-3, 3, 7):
    k2 = 1 + Z0**2 / c2
    th = np.linspace(0, 2*np.pi, 200)
    Xs = a*np.sqrt(k2)*np.cos(th)
    Ys = b*np.sqrt(k2)*np.sin(th)
    ax4.plot(Xs, Ys, label=f"Z={Z0:.1f}")
ax4.set_aspect("equal")
ax4.set_title("Сечения Z = const — эллипсы\n(гиперболоид сужается к Z = 0)")
ax4.set_xlabel("X"); ax4.set_ylabel("Y")
ax4.legend(fontsize=7, loc="upper right")
ax4.grid(True, alpha=0.3)

plt.tight_layout()
plt.savefig(os.path.join(OUT, "fig_part1_overview.png"), dpi=140)
plt.close(fig)

# --- 8. Сечения наклонными плоскостями: гипербола/прямые/эллипс ----------
fig2 = plt.figure(figsize=(12, 8))
# Сечение плоскостью Y = 0 -> гипербола X^2/4 - Z^2/6 = 1
axA = fig2.add_subplot(2, 2, 1)
th = np.linspace(-2, 2, 400)
axA.plot(a*np.cosh(th), cc*np.sinh(th), "r")
axA.plot(-a*np.cosh(th), cc*np.sinh(th), "r")
axA.set_title("Сечение Y = 0 (плоскость x'z' / XY-Z)\nГипербола  X²/4 − Z²/6 = 1")
axA.set_xlabel("X"); axA.set_ylabel("Z"); axA.grid(True, alpha=0.3); axA.set_aspect("equal")

# Сечение X = 0 -> Y^2/9 - Z^2/6 = 1
axB = fig2.add_subplot(2, 2, 2)
axB.plot(b*np.cosh(th), cc*np.sinh(th), "g")
axB.plot(-b*np.cosh(th), cc*np.sinh(th), "g")
axB.set_title("Сечение X = 0\nГипербола  Y²/9 − Z²/6 = 1")
axB.set_xlabel("Y"); axB.set_ylabel("Z"); axB.grid(True, alpha=0.3); axB.set_aspect("equal")

# Сечение Z = 0 -> Эллипс X^2/4 + Y^2/9 = 1 (горловой эллипс)
axC = fig2.add_subplot(2, 2, 3)
tt = np.linspace(0, 2*np.pi, 300)
axC.plot(a*np.cos(tt), b*np.sin(tt), "b")
axC.set_title("Сечение Z = 0\nГорловой эллипс  X²/4 + Y²/9 = 1")
axC.set_xlabel("X"); axC.set_ylabel("Y"); axC.grid(True, alpha=0.3); axC.set_aspect("equal")

# Сечение касательной плоскостью к асимптотическому конусу (Z = (c/a)X)
# Подставим Z = (cc/a) X в каноническое уравнение
# X^2/4 + Y^2/9 - (X^2 * 6/4)/6 = 1  =>  X^2/4 + Y^2/9 - X^2/4 = 1  =>  Y^2 = 9
# Получаем пару прямых Y = ±3 (вырожденная гипербола — две параллельные прямые)
axD = fig2.add_subplot(2, 2, 4)
xs = np.linspace(-5, 5, 100)
axD.plot(xs, 3*np.ones_like(xs), "m"); axD.plot(xs, -3*np.ones_like(xs), "m")
axD.set_title("Сечение наклонной плоскостью Z = (c/a)X\nПара параллельных прямых  Y = ±3")
axD.set_xlabel("X (или параметр вдоль плоскости)"); axD.set_ylabel("Y")
axD.grid(True, alpha=0.3); axD.set_ylim(-5, 5); axD.set_xlim(-5, 5)

plt.tight_layout()
plt.savefig(os.path.join(OUT, "fig_part1_sections.png"), dpi=140)
plt.close(fig2)

# --- 9. Инженерная задача: небоскрёб-гиперболоид вращения ----------------
# Высота 30 этажей, площадь основания (z=0) = 30000 м^2, площадь 15-го этажа = 20000 м^2.
# Гиперболоид вращения:  X^2/A^2 + Y^2/A^2 - Z^2/C^2 = 1   (a=b=A)
# Радиус на высоте Z:  R(Z) = A*sqrt(1 + Z^2/C^2)
# S(0) = π A^2 = 30000  =>  A = sqrt(30000/π)
# Высота этажа h = 4 м (стандарт), 15 этаж -> Z = 15*4 = 60 м (середина здания при высоте 30*4=120 м)
# НО! Минимальное сечение - это z=0. Если ставить здание основанием при z=0,
# то горловое сечение совпадает с основанием. Поставим горловое сечение посередине здания:
# центр здания на высоте H/2 = 60 м. Тогда основание соответствует Z = -60, 15-й этаж = Z = 0 (горло), верх Z=+60.
# Но тогда площадь 15-го этажа МЕНЬШЕ площади основания — соответствует условию (20000 < 30000).
import math
h_floor = 4.0          # высота этажа, м
N_floors = 30
H = N_floors * h_floor # 120 м
S_base = 30000.0       # м²
S_mid  = 20000.0       # м²
# Сместим начало канонических координат: основание здания при Z = -H/2 = -60,
# верх при Z = +60, горло (15-й этаж) при Z = 0.
A_base_R = math.sqrt(S_base / math.pi)        # радиус основания
A_mid_R  = math.sqrt(S_mid  / math.pi)        # радиус 15-го (горло)
A_param  = A_mid_R                             # это и есть параметр a гиперболоида вращения
# Из R_base^2 = A^2 (1 + (H/2)^2 / C^2):
C_param = (H/2) / math.sqrt( (A_base_R**2 - A_param**2) / A_param**2 )
print("\n--- Инженерная задача: небоскреб-гиперболоид вращения ---")
print(f"H = {H} м, A (горловой радиус) = {A_param:.3f} м, C = {C_param:.3f} м")
print(f"Каноническое:   X²/{A_param**2:.2f} + Y²/{A_param**2:.2f} − Z²/{C_param**2:.2f} = 1")
print(f"Радиус основания (Z=-60):  {A_param*math.sqrt(1+(H/2)**2/C_param**2):.3f}"
      f"  (ожидался {A_base_R:.3f})")
# Полезная площадь — сумма площадей этажей.
total_S = 0.0
for k in range(N_floors):
    z_floor = -H/2 + (k + 0.5) * h_floor   # центр этажа
    R_k = A_param * math.sqrt(1 + z_floor**2 / C_param**2)
    total_S += math.pi * R_k**2
print(f"Общая полезная (рабочая) площадь по всем этажам: {total_S:.0f} м²")

# Лифт — движется по прямой образующей.
# Семейство I в (X,Y,Z) канонических: для гиперболоида вращения с параметрами A, C
#    X/A + Z/C = (1/u)(1 - Y/A),    X/A - Z/C = u (1 + Y/A)
# Берём u = 1 (произвольно):
#    X/A + Z/C = 1 - Y/A
#    X/A - Z/C = 1 + Y/A
# Складывая/вычитая:  X/A = 1,    Z/C = -Y/A
# => X = A,  Y = -(A/C) Z  (прямая, лежит на поверхности!)
# Лифт движется снизу вверх с постоянной скоростью v_lift по этой прямой.
v_lift = 1.0  # м/с (примем 1 м/с по вертикали — реалистично для медленного лифта)
# Время на прохождение всего здания: T = H / v_lift
T_lift = H / v_lift
print(f"Уравнение лифтовой образующей в (X,Y,Z):  X = {A_param:.3f}, "
      f"Y = -(A/C) Z = {(-A_param/C_param):.4f} * Z,  Z ∈ [-{H/2:.0f}, +{H/2:.0f}]")
print(f"Параметризация по времени:  Z(t) = -H/2 + v_lift * t = -{H/2:.0f} + {v_lift}*t,  "
      f"t ∈ [0, {T_lift:.1f}] c")

# --- 10. Алгоритм 3D-печати шахты лифта (узлы / рёбра) -------------------
# Печатаем "коробку" из 4 прямых образующих, симметрично расположенных вокруг шахты,
# плюс кольцевые связи каждые dh = 4 м (на каждом этаже).
# Шахта пусть имеет квадратное сечение со стороной s = 1.5 м (внутр. размер кабины + зазор)
# Возьмём 4 образующие, лежащие на гиперболоиде, повёрнутые на 0, 90, 180, 270 градусов
# вокруг оси Z. У каждой образующей: точка касания X=A, Y_угла=0.
# При повороте на угол φ: образующая через точку (A cos φ, A sin φ, 0) с направлением
# d = (-A/C * sin φ, A/C * cos φ, 1) — это другое представление,
# но нам нужны 4 прямые именно лифтовой шахты, не на гиперболоиде. Алгоритм описан в отчёте.
print("\nАлгоритм 3D-печати шахты лифта см. в отчёте (fig сгенерированы).")

# --- Вывод ключевых результатов в файл -----------------------------------
with open(os.path.join(OUT, "part1_results.txt"), "w", encoding="utf-8") as fh:
    fh.write("ПРОЕКТНАЯ РАБОТА. Часть 1. Вариант 3. Однополостный гиперболоид.\n")
    fh.write("="*72 + "\n\n")
    fh.write("Исходное уравнение:\n  4x² − y² + 4z² + 10xy + 10yz + 10x − 2y + 10z − 37 = 0\n\n")
    fh.write("Матрица квадратичной формы A:\n")
    fh.write(str(A) + "\n\n")
    fh.write(f"Собственные числа: λ₁=9, λ₂=4, λ₃=−6  (signature (+,+,−))\n")
    fh.write(f"Ортонормированный базис из собственных векторов:\n")
    fh.write(f"  e₁ = (1,1,1)/√3        (λ=9)\n")
    fh.write(f"  e₂ = (−1,0,1)/√2       (λ=4)\n")
    fh.write(f"  e₃ = (1,−2,1)/√6       (λ=−6)\n")
    fh.write(f"det Q = +1 (правая ориентация)\n\n")
    fh.write(f"Линейная часть в собственном базисе: L' = (6√3, 0, 4√6)\n")
    fh.write(f"После выделения полных квадратов и переноса:\n")
    fh.write(f"  X²/4 + Y²/9 − Z²/6 = 1\n")
    fh.write(f"Центр поверхности в исходных координатах: (0, −1, 0)\n")
    fh.write(f"Полуоси: a=2, b=3, c=√6 ≈ {np.sqrt(6):.4f}\n")
    fh.write(f"Тип: ОДНОПОЛОСТНЫЙ ГИПЕРБОЛОИД (общий, не вращения, т.к. a≠b).\n\n")
    fh.write("Инженерная задача (небоскреб 30 этажей):\n")
    fh.write(f"  Высота этажа h = {h_floor} м, общая высота H = {H} м.\n")
    fh.write(f"  Каноническое уравнение гиперболоида вращения:\n")
    fh.write(f"    X²/{A_param**2:.2f} + Y²/{A_param**2:.2f} − Z²/{C_param**2:.2f} = 1,   Z ∈ [−60, 60] м\n")
    fh.write(f"  A (радиус горла, 15-й этаж) = {A_param:.3f} м\n")
    fh.write(f"  C = {C_param:.3f} м\n")
    fh.write(f"  Общая рабочая площадь (сумма площадей этажей): ≈ {total_S:.0f} м²\n")
    fh.write(f"  Лифт: прямая X=A, Y=−(A/C)Z, t∈[0,{T_lift:.0f}] c при v=1 м/с\n")

print("\nГотово. Файлы в", OUT)
