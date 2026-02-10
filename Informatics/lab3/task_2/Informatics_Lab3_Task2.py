from re import *


def del_students(spisok, group_number):
    spisok = spisok.read()
    pattern = (r"(\n[A-ZА-ЯЁ][a-zа-яё]+(-[A-Za-zА-Яа-яёЁ]{2,})?\s([A-ZА-ЯЁ]\.)\3" + fr"\s{group_number}\b)" +
               r"|" +
               r"^([A-ZА-ЯЁ][a-zа-яё]+(-[A-Za-zА-Яа-яёЁ]{2,})?\s([A-ZА-ЯЁ]\.)\6" + fr"\s{group_number}\b\n)" +
               r"|" +
               r"(\n[A-ZА-ЯЁ][a-zа-яё]+(-[A-Za-zА-Яа-яёЁ]{2,})?\s([A-ZА-ЯЁ]\.)\9" + fr"\s{group_number}\b\n)")
    new_spisok = sub(pattern, '', spisok)
    return new_spisok
