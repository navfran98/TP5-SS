
from math import sqrt
from Sylo import Sylo

kn = 10**5
kt = 2 * kn
m = 0.01
timeStep = 0.1 * sqrt(m/kn)
s = Sylo(0.4, 1, 0.2)

print("entre al populate")
s.populate(timeStep)
print("sali del populate")

s.simulate(timeStep)