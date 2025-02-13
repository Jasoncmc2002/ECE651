def add(l: list):
    a = 0
    for i in l:
        a += i
    return a

str = input()
# print(str[1:-1].split(','))
# print(int('   1    '))
l = list(map(int, str[1:-1].split(',')))
print(add(l))