"""
Лабораторная работа: численное интегрирование.
Вариант 1: f(x) = x^2, [a, b] = [1, 2].
Точное значение интеграла: I = 7/3.
"""

import argparse
import math
import os

import numpy as np
import matplotlib.pyplot as plt


def f(x):
    return x ** 2


A, B = 1.0, 2.0
EXACT = (B ** 3 - A ** 3) / 3.0  # 7/3


def integral_sum(n: int, mode: str):
    """Возвращает (значение суммы, координаты ступенек для отрисовки)."""
    xs = np.linspace(A, B, n + 1)
    h = (B - A) / n

    if mode == "left":
        heights = f(xs[:-1])
        s = h * np.sum(heights)
    elif mode == "right":
        heights = f(xs[1:])
        s = h * np.sum(heights)
    elif mode == "mid":
        mids = 0.5 * (xs[:-1] + xs[1:])
        heights = f(mids)
        s = h * np.sum(heights)
    elif mode == "trap":
        heights = 0.5 * (f(xs[:-1]) + f(xs[1:]))
        s = h * np.sum(heights)
    else:
        raise ValueError(f"unknown mode: {mode}")

    return s, xs, heights


def theoretical_error(n: int, mode: str) -> float:
    """Теоретическая верхняя оценка погрешности на [1,2] для f=x^2.
    f'(x)=2x, max|f'|=4 на [1,2]. f''(x)=2, max|f''|=2.
    (b-a)=1.
    """
    if mode in ("left", "right"):
        return 4.0 * (B - A) ** 2 / (2 * n)
    if mode == "mid":
        return 2.0 * (B - A) ** 3 / (24 * n ** 2)
    if mode == "trap":
        return 2.0 * (B - A) ** 3 / (12 * n ** 2)
    raise ValueError(mode)


def plot_sum(n: int, mode: str, save_path: str | None = None):
    s, xs, heights = integral_sum(n, mode)
    err = abs(s - EXACT)
    theo = theoretical_error(n, mode)

    fig, ax = plt.subplots(figsize=(9, 5.5))

    # гладкая f
    xx = np.linspace(A, B, 400)
    ax.plot(xx, f(xx), "k-", lw=2, label="f(x) = x²")

    h = (B - A) / n
    titles = {
        "left": "Левые прямоугольники",
        "right": "Правые прямоугольники",
        "mid": "Средние прямоугольники",
        "trap": "Трапеции",
    }

    if mode == "trap":
        # рисуем трапеции
        for i in range(n):
            x0, x1 = xs[i], xs[i + 1]
            y0, y1 = f(x0), f(x1)
            ax.fill([x0, x0, x1, x1], [0, y0, y1, 0],
                    color="tab:orange", alpha=0.35,
                    edgecolor="tab:orange", linewidth=0.8)
    else:
        # прямоугольники
        if mode == "left":
            lefts = xs[:-1]
        elif mode == "right":
            lefts = xs[:-1]  # сами прямоугольники начинаются от x_{i-1}
        else:  # mid
            lefts = xs[:-1]
        for i in range(n):
            ax.fill([lefts[i], lefts[i], lefts[i] + h, lefts[i] + h],
                    [0, heights[i], heights[i], 0],
                    color="tab:blue", alpha=0.3,
                    edgecolor="tab:blue", linewidth=0.8)

    ax.set_xlim(A - 0.05, B + 0.05)
    ax.set_ylim(0, f(B) * 1.15)
    ax.set_xlabel("x")
    ax.set_ylabel("f(x)")
    ax.set_title(
        f"{titles[mode]}, n={n}\n"
        f"S = {s:.6f},  точно = {EXACT:.6f},  |Δ| = {err:.2e},  "
        f"теор. оценка = {theo:.2e}"
    )
    ax.grid(alpha=0.3)
    ax.legend(loc="upper left")

    if save_path:
        fig.tight_layout()
        fig.savefig(save_path, dpi=130)
        plt.close(fig)
    else:
        plt.show()
    return s, err, theo


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("-n", type=int, default=10, help="число разбиений")
    parser.add_argument(
        "-m", "--mode",
        choices=["left", "right", "mid", "trap", "all"],
        default="all",
    )
    parser.add_argument("--save-dir", default=None,
                        help="папка для сохранения PNG (если задана — без show)")
    args = parser.parse_args()

    modes = ["left", "right", "mid", "trap"] if args.mode == "all" else [args.mode]

    print(f"f(x) = x^2,  [a,b] = [{A},{B}],  точное I = 7/3 = {EXACT:.10f}")
    print(f"n = {args.n}")
    print(f"{'метод':<10}{'сумма':>14}{'|Δ|':>14}{'теор.':>14}")
    for m in modes:
        path = None
        if args.save_dir:
            os.makedirs(args.save_dir, exist_ok=True)
            path = os.path.join(args.save_dir, f"n{args.n}_{m}.png")
        s, err, theo = plot_sum(args.n, m, save_path=path)
        print(f"{m:<10}{s:>14.8f}{err:>14.2e}{theo:>14.2e}")


if __name__ == "__main__":
    main()
