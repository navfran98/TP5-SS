
from math import sqrt
from Sylo import Sylo

kn = 10**5
kt = 2 * kn
m = 0.01
timeStep = (0.1 * sqrt(m/kn))/3
s = Sylo(0.4, 1, 0.1)

print("entre al populate")
s.populate(timeStep)
print("sali del populate")

with open("log.txt", "w") as file:
    file.write("a")

s.simulate(timeStep)
