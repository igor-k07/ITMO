from re import *

text = ("Уважаемые студенты! В эту субботу в "
        "15:00 планируется доп. занятие на 2 "
        "часа. То есть в 17:00:01 оно уже "
        "точно кончится.")
pattern = r"\d{2}:\d{2}(:\d{2})?"
for match in finditer(pattern, text):
    start = match.start()
    end = match.end()
    text = text[:start] + '(TBD)' + text[end:]
print(text)