import yaml


def to_yaml(data, k=0):
    space = ' ' * k
    if data == None:
        return "null"
    if isinstance(data, bool):
        if data:
            return "true"
        else:
            return "false"
    if isinstance(data, (int, float)):
        return str(data)
    if isinstance(data, str):
        if ':' in data:
            return f'"{data}"'
        return data
    if isinstance(data, list):
        items = []
        for item in data:
            item_str = to_yaml(item, k)
            if '\n' in item_str:
                first_line, *rest = item_str.splitlines()
                items.append(f"- {first_line}")
                for line in rest:
                    items.append(' ' * (k+2) + line)
            else:
                items.append(f"- {item_str}")
        return "\n".join(items)
    if isinstance(data, dict):
        items = []
        for key, value in data.items():
            key_str = str(key)
            value_str = to_yaml(value, k)
            if isinstance(value, (list, dict)):
                items.append(f"{key_str}:")
                for line in value_str.splitlines():
                    items.append(" " * (k+2) + line)
            else:
                items.append(f"{key_str}: {value_str}")
        return "\n".join(items)

    return f'"{str(data)}"'


def write_yaml(data: str):
    with open("schedule.yml", "w", encoding="utf-8") as f:
        f.write(data)


def auto_write_yaml(data: dict):
    with open("schedule2.yml", "w", encoding="utf-8") as f:
        yaml.dump(data, f, allow_unicode=True, sort_keys=False)