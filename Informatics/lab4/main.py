from parser_ron import parse_ron_object, read_ron, auto_parse_ron
from bin_to_yaml import to_yaml, write_yaml, auto_write_yaml
from to_xml import write_xml

# Свой парсер RON-файла (десериализация)
text_ron = read_ron("schedule.ron")
bi_obj = parse_ron_object(text_ron)
print(bi_obj, '\n')

# Парсер RON с готовыми библеотеками (десериализация)
bi_obj2 = auto_parse_ron("schedule.ron")
print(bi_obj, '\n')

# ручная сериализация бинарного файла в Yaml-файл
yaml_text = to_yaml(bi_obj)
write_yaml(yaml_text)


# сериализация бинарного файла в Yaml-файл с помощью готовых библиотек
auto_write_yaml(bi_obj2)
#

# Сериализация в XML-файл
write_xml(bi_obj)