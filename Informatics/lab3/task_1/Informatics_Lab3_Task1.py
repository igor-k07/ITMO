from re import *


def search_time(text):
    pattern = r"([0-1][0-9]|2[0-3]):[0-5][0-9](:[0-5][0-9])?"
    text = sub(pattern, '(TBD)', text)
    return text
