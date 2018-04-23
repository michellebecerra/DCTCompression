import numpy as np
import matplotlib.pyplot as plt
num = [5.8, 6.2, 6.2, 7.2, 7.3, 7.3, 6.5, 6.8, 6.8, 6.8, 5.5, 5.0, 5.2, 5.2, 5.8, 6.2, 6.2, 6.2, 5.9, 6.3, 5.2, 4.2, 2.8, 2.8, 2.3, 2.9, 1.8, 2.5, 2.5, 3.3, 4.1, 4.9]
x = [None]*len(num)
ind = 0
for i in num:
	x[ind] = round( ((i - 0.25)/8)*32)
	ind = ind + 1
print x
dic = {}
for i in range(1, len(x)):
	key =  x[i] - x[i-1];
	if key in dic:
		dic[key] = dic[key] + 1
	else:
		dic[key] = 1
print dic

