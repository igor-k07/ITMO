from Informatics_Lab3_Task1 import search_time
import unittest


class TestTask1(unittest.TestCase):

    # Тест 1. Время в начале строки
    def test_1(self):
        input_text = "08:30 - начало собрание в кабинете 5:не опаздывать!"
        result = search_time(input_text)
        correct = "(TBD) - начало собрание в кабинете 5:не опаздывать!"
        self.assertEqual(result, correct)
        print(f'1 тест пройден успешно!')

    # Тест 2. Формат похожий на врремя но не яввляющийся им
    def test_2(self):
        input_text = "со счетом 2:0 ведет 1 команда, время:15:32:04"
        result = search_time(input_text)
        correct = "со счетом 2:0 ведет 1 команда, время:(TBD)"
        self.assertEqual(result, correct)
        print(f'2 тест пройден успешно!')

    # Тест 3. Считывание только ликвидного времени
    def test_3(self):
        input_text = "32:24:23:90:00:87:00:15:30:20:23:90"
        result = search_time(input_text)
        correct = "32:24:23:90:00:87:(TBD):(TBD):90"
        self.assertEqual(result, correct)
        print(f'3 тест пройден успешно!')

    # Тест 4. Буквы с цифрами через двоеточие
    def test_4(self):
        input_text = "С 23:59:59 не работает профиль ID:1342 на версии 23:0"
        result = search_time(input_text)
        correct = "С (TBD) не работает профиль ID:1342 на версии 23:0"
        self.assertEqual(result, correct)
        print(f'4 тест пройден успешно!')

    # Тест 5. Слитный текст с временем без пробелов
    def test_5(self):
        input_text = ("пробелыбылиудаленыв16:45потомучтов03:10:15"
                      "хакерывзломаливерсиюкода5:ведутсявостановительныеработы,времени26:45:15несуществует")
        result = search_time(input_text)
        correct = ("пробелыбылиудаленыв(TBD)потомучтов(TBD)"
                   "хакерывзломаливерсиюкода5:ведутсявостановительныеработы,времени26:45:15несуществует")
        self.assertEqual(result, correct)
        print(f'5 тест пройден успешно!')


if __name__ == '__main__':
    unittest.main()
