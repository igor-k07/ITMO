import html


def to_xml(name, value, indent=0):
    space = " " * indent

    if value is None:
        return f"{space}<{name} />"
    if isinstance(value, (int, float, bool)):
        text = html.escape(str(value))
        return f"{space}<{name}>{text}</{name}>"
    if isinstance(value, str):
        text = html.escape(value)
        return f"{space}<{name}>{text}</{name}>"

    if isinstance(value, list):
        parts = []
        for item in value:
            parts.append(to_xml(name[:-1] if name.endswith("s") else "item",
                                item, indent))
        return "\n".join(parts)

    if isinstance(value, dict):
        lines = [f"{space}<{name}>"]
        for k, v in value.items():
            lines.append(to_xml(k, v, indent + 2))
        lines.append(f"{space}</{name}>")
        return "\n".join(lines)

    text = html.escape(str(value))
    return f"{space}<{name}>{text}</{name}>"


def write_xml(data):
    with open("schedule.xml", "w", encoding="utf-8") as f:
        xml_text = '<?xml version="1.0" encoding="UTF-8"?>\n' + to_xml("schedule", data)
        f.write(xml_text)