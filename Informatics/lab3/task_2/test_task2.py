from Informatics_Lab3_Task2 import del_students
import unittest


class TestTask2(unittest.TestCase):
    # Тест 1. Двойная фамилия подходящего студента
    def test_1(self):
        with open('spisok1.txt', 'r', encoding='utf8') as students:
            group_number = 'P0000'
            new_spisok = del_students(students, group_number)
            self.assertIn('Смирнова А.А. P33113', new_spisok)
            self.assertIn('Пелих Е.В. P0000', new_spisok)
            self.assertNotIn('Бых А.А. P0000', new_spisok)
            self.assertNotIn('Петров-Смирнов П.П. P0000', new_spisok)
            print(f'{new_spisok}\n1 тест пройден успешно!\n')

    # Тест 2. Номер группы подходящих студентов совпадает с началом номеров пододящих студентов из других групп
    def test_2(self):
        with open('spisok2.txt', 'r', encoding='utf8') as students:
            group_number = 'P3122'
            new_spisok = del_students(students, group_number)
            self.assertIn('Бых А.А. P0000', new_spisok)
            self.assertIn('Пелих Е.В. P312254', new_spisok)
            self.assertIn('Симонов В.Л. P3122', new_spisok)
            self.assertNotIn('Смирнова А.А. P3122', new_spisok)
            self.assertNotIn('Петров П.П. P3122', new_spisok)
            print(f'{new_spisok}\n2 тест пройден успешно!\n')

    # Тест 3. Списки на Английском
    def test_3(self):
        with open('spisok3.txt', 'r', encoding='utf8') as students:
            group_number = 'P4567'
            new_spisok = del_students(students, group_number)
            self.assertIn('Rublex F.D P4567', new_spisok)
            self.assertIn('Lee A.R.  P31111', new_spisok)
            self.assertIn('Small P.P P3122', new_spisok)
            self.assertNotIn('Jun D.D. P4567', new_spisok)
            self.assertNotIn('Big J.J. P4567', new_spisok)
            print(f'{new_spisok}\n3 тест пройден успешно!\n')

    # Тест 4. Неправильный формат строк списка(!Тройная фамилия)
    def test_4(self):
        with open('spisok4.txt', 'r', encoding='utf8') as students:
            group_number = 'P0000'
            new_spisok = del_students(students, group_number)
            self.assertIn('Бых А А P0000', new_spisok)
            self.assertIn('Смирнова А P0000', new_spisok)
            self.assertIn('П Е.Е. P0000', new_spisok)
            self.assertIn('Петров-Иванов-Сидоров П.П. P0000', new_spisok)
            self.assertIn('Симонов P0000', new_spisok)
            self.assertNotIn('Кипарисов Л.Л. P0000', new_spisok)
            print(f'{new_spisok}\n4 тест пройден успешно!\n')

    # Тест 5. Смешанный список с Русскими и Английскими учениками
    def test_5(self):
        with open('spisok5.txt', 'r', encoding='utf8') as students:
            group_number = 'P1'
            new_spisok = del_students(students, group_number)
            self.assertIn('Сидоров А.К. P1', new_spisok)
            self.assertIn('Joey P.D. P1', new_spisok)
            self.assertIn('Симонов Н.Е P1', new_spisok)
            self.assertNotIn('Ли А.А. P1', new_spisok)
            self.assertNotIn('Pupkin В.В. P1', new_spisok)
            print(f'{new_spisok}\n5 тест пройден успешно!\n')


if __name__ == '__main__':
    unittest.main()
