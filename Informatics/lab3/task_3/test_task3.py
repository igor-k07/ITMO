from Informatics_Lab3_Task3 import except_cron
import unittest


class TestTask2(unittest.TestCase):
    # Тест 1. Корректное выражение со специальными символами
    def test_1(self):
        text = '1-5 2,5 6/4 * 1'
        res = except_cron(text)
        self.assertTrue(res)
        print('1 тест пройден успешно!')


    # Тест 2. Корректное выражение с незначащими нулями
    def test_2(self):
        text = '0004 5,0003 11/0005 4-0006 01-02'
        res = except_cron(text)
        self.assertTrue(res)
        print('2 тест пройден успешно!')

    # Тест 3. Неккоректное выражение с "/"
    def test_3(self):
        text = '11/60 7 11 7 2'
        res = except_cron(text)
        self.assertFalse(res)
        print('3 тест пройден успешно!')

    # Тест 4. Корректное выражение с "*"
    def test_4(self):
        text = '1* 1* * */3 *'
        res = except_cron(text)
        self.assertTrue(res)
        print('4 тест пройден успешно!')

    # Тест 5. Корректное выражение с большим количествомм ","
    def test_5(self):
        text = '1,2,3,4,5,6,7,8,9 * 11 * 1,2,4,5'
        res = except_cron(text)
        self.assertTrue(res)
        print('5 тест пройден успешно!')


if __name__ == '__main__':
    unittest.main()
