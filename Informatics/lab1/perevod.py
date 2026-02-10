def ss9(x):
    f = 0
    n = 9
    res = []
    a = ''
    if x < 0:
        x = -x
        f = 1
    while x:
        res.append(x % n)
        x //= n
    res = res[::-1]
    for i in range(len(res)-1, -1, -1):
        if res[i] > n / 2:
            res[i] -= n
            if i != 0:
                res[i-1] += 1
            else:
                res = [1] + res
        if f:
            res[i] = -int(res[i])
    for i in res:
        if i < 0:
            i = f"({i})"
        a += str(i)
    print(a)

ss9()

