import pyron


def read_ron(path: str):
    with open(path, 'r', encoding="utf-8") as ron_file:
        data = ron_file.read()
        data = data[1:-1].strip()
        return data


def split_top_data(data, sep=','):
    res, buf = [], []
    depth_obj, depth_list = 0, 0
    for char in data:
        if char == '(':
            depth_obj += 1
        if char == ')':
            depth_obj -= 1
        if char == '[':
            depth_list += 1
        if char == ']':
            depth_list -= 1

        if char == sep and depth_list == 0 and depth_obj == 0:
            st = ''.join(buf)
            res.append(st)
            buf = []
        else:
            buf.append(char)

    return res


def parse_ron_value(value):
    value = value.strip()
    if value == None:
        return None
    if value.isalnum():
        return value
    if value[0] == '"' and value[-1] == '"':
        return value[1:-1]
    if value[0] == '(' and value[-1] == ')':
        return parse_ron_object(value[1:-1].strip())
    if value[0] == '[' and value[-1] == ']':
        return parse_ron_list(value[1:-1].strip())


def parse_ron_object(ron):
    obj = {}
    parts = split_top_data(ron)
    for part in parts:
        part = part.strip()
        key, value = part.split(':', 1)
        obj[key.strip()] = parse_ron_value(value.strip())
    return obj


def parse_ron_list(ron):
    l = []
    parts = split_top_data(ron)
    for part in parts:
        part = part.strip()
        l.append(parse_ron_value(part))
    return l


def auto_parse_ron(path: str):
    data = pyron.load(path)
    return data


if __name__ == '__main__':
    ron = read_ron("schedule.ron")
    bin_obj = parse_ron_object(ron)
    print(bin_obj)