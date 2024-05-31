# Анализ кода с помощью thread sanitizer

В качестве проекта была выбрана реализация алгоритма Дейкстры для Relaxed-Multi-Queue на C++. [Работа производилась в форке](https://github.com/Kuarni/multiqueue).

## Репозиторий до

Следует упомянуть, что в репозитории до - не собирались тесты, что было поправлено. Также судя по всему автор не использовал какие-либо инструменты для анализа корректности параллельного исполнения, но использовал address sanitizier. Поэтому был добавлен флаг `-fsanitize=thread -g`.

Запуск производился со следующими параметрами: `./mq 1 params.txt 10000 1 run`

Где:
- `1` - это имя датасета, он был добавлен в форк под именем `1.in`
- `params.txt` - параметры, со значением `6 4`, где первая цифра - это количество потоков, а вторая - параметр K, отвечающий за кол-во очередей в Multi-Queue (также закоммичен в форке)
- `10000` - превыделенная память для очередей
- `1` - запуск параллельного алгоритма
- `run` - обычный запуск, без бенчмарка

Thread sanitizier выдал одно предупреждение о data race, но при изучение предупреждения, был сделан вывод, что оно ложноположительное.

[отчет thread sanitizier](report-before.txt)

### Почему ложноположительное

Sanitizier написал о том, что гонка возникла при операции `push` в одном потоке и операции `pop` в другом, в данном случае оба потока стучались по одному адресу, НО в секции с вызовом этих операций, на объект навешивается `lock` (было проверено, что код лока корректный), 
так что выполнение этих операций могло быть только последовательным.

## Внесение гонки данных

Гонка данных была внесена простым способом, были удалены блокировки вокруг операции `pop`. После этого thread sanitizier уже выдовал не одно предупреждение о гонке данных, а в больших количествах. Также SEGV на atomic переменной элемента очереди. 

[отчет thread sanitizier](report-after.txt)