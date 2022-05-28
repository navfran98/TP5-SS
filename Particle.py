from math import sqrt


class Particle:

    def __init__(self, x, y, vx, vy, radius, mass):
        self.x = x
        self.y = y
        self.vx = vx
        self.vy = vy
        self.radius = radius
        self.mass = mass

    def getVelocity(self):
        return sqrt(self.vx**2 + self.vy**2)

    # Para comparar contra paredes crear una "particula" de
    # velocidad 0 en el lugar que queramos
    def getRelVelocity(self, other):
        return abs(self.getVelocity() - other.getVelocity())

    # Para comparar contra paredes crear una "particula" de
    # radio 0 en el lugar que queramos
    def overlap(self, other):
        term1 = self.x - other.x
        term2 = self.y - other.y
        return self.radius + other.radius - sqrt(term1**2 + term2**2)

    # Para comparar contra paredes crear una "particula" de
    # radio 0 en el lugar que queramos. En nuestro caso no hay
    # que preocuparnos por el angulo que muestra el ppt pq el
    # silo es un rectangulo
    def colForces(self, other):

        # Distancia entre centros
        term1 = other.x - self.x
        term2 = other.y - self.y
        divider = sqrt(term1**2 + term2**2)

        enx = (other.x - self.x) / divider
        eny = (other.y - self.y) / divider

        en = [ enx, eny ]
        et = [ -eny, enx ]

        return en, et
