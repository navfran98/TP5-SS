from random import random
import time
from Particle import Particle
from Integrators import Euler, Beeman
import numpy as np


class Sylo:

    def __init__(self, w, l, d):
        self.w = w # Ancho del silo
        self.l = l # Alto del silo
        self.d = d # Apertura de salida
        self.particles = []
        self.prevParticles = []
        # Calculamos el tamanio del piso
        floor = (w-d)/2
        self.borders = [(0,0), (0, self.l), (self.w, 0), (self.w, self.l), (floor, self.l), (w-floor, self.l)]
    
    # |------- W -------|
    # |-----------------| -|
    # |                 |  |
    # |                 |  |
    # |                 |  |
    # |                 |  |
    # |                 |  L
    # |                 |  |
    # |                 |  |
    # |                 |  |
    # |                 |  |
    # |-----|     |-----| -|
    #       |--D--|


    def simulate(self, timeStep):

        # Primero tenemos que hacer euler de todas las particulas del sistema
        # entonces...
        first = [False for i in range(len(self.particles))]

        # Habria que preguntar esto, no se cuanto tiene que durar la sim
        # le mando que ande por 30 segundos
        sim_duration = timeStep * 150
        step = 1
        while (timeStep * step) < sim_duration:
            index = 0
            for p in self.particles:
                if(first[index]):
                    self.prevParticles[index] = Euler.run(p, timeStep, self.particles, self.l, self.w, self.d)
                    first[index] = False
                # Nos guardamos la actual para dsp dejarla en el prev
                aux = self.particles[index]

                # Hacemos Beeman
                new_part = Beeman.run(p, self.prevParticles[index], timeStep, self.particles, self.prevParticles, self.l, self.w, self.d)
                if(new_part.y <= self.l+(self.l/10)):
                    # Ponemos first en True para que haga euler de nuevo
                    first[index] = True
                    # Ubicamos la particula arriba de nuevo donde entre
                    self.particles[index] = self.placeNewParticle()
                else:
                    # Updateamos el current
                    self.particles[index] = new_part
                    # Cambiamos la anterior
                    self.prevParticles[index] = aux

                index += 1

                with open("Output.xyz", "a") as file:
                    dump = ""
                    dump += f"{len(self.particles)+6}\n"
                    dump += f"Time={timeStep*step}\n"
                    for bp in self.borders:
                        dump += f"{100} {bp[0]} {bp[1]} {0} {0.02}\n"
                    for p in self.particles:
                        #color, x, y, radio
                        dump += f"{200} {p.x} {p.y} {0} {p.radius}\n"
                    file.write(dump)
            
            step += 1


    # Tienen que entrar la maxima cantidad posible de 
    # particulas que entre en el area del silo

    def populate(self, timeStep):

        # Queremos probar llenar todo lo posible en 15 segundos
        t_end = time.time() + timeStep*50

        first = True

        while time.time() < t_end:

            # Generamos un radio random
            rand_r = np.random.uniform(0.02, 0.03)

            # Generamos posiciones random
            rand_x = np.random.uniform(0, self.w)
            rand_y = np.random.uniform(0, self.l)

            p = Particle(rand_x, rand_y, 0, 0, rand_r/2, 0.01)

            # Chequeamos que no se choque con paredes
            while True:
                 # Paredes
                sup = Particle(p.x, self.l, 0, 0, 0, 0)
                inf = Particle(p.x, 0, 0, 0, 0, 0)
                izq = Particle(0, p.y, 0, 0, 0, 0)
                der = Particle(self.w, p.y, 0, 0, 0, 0)
                if(p.overlap(sup) > 0 and p.overlap(inf) > 0 and p.overlap(izq) > 0 and p.overlap(der) > 0):
                    rand_x = np.random.uniform(0, self.w)
                    rand_y = np.random.uniform(0, self.l)
                    p = Particle(rand_x, rand_y, 0, 0, rand_r/2, 0.01)
                else:
                    break
            
            if(first):
                self.particles.append(p)
                first = False
                
            # Chequeamos que no se choque con otras particulas
            for other in self.particles:
                if(p.overlap(other) <= 0):
                    # Si llegamos aca la particula entra
                    # entonces la metemos
                    self.particles.append(p)
                    break
        
        with open("Output.xyz", "w") as file:
            dump = ""
            dump += f"{len(self.particles)+6}\n"
            dump += "Time=0\n"
            for bp in self.borders:
                dump += f"{100} {bp[0]} {bp[1]} {0} {0.02}\n"
            for p in self.particles:
                #color, x, y, radio
                dump += f"{200} {p.x} {p.y} {0} {p.radius}\n"
            file.write(dump)
            
        for p in self.particles:
            self.prevParticles.append(Euler.run(p, timeStep, self.particles, self.l, self.w, self.d))
        
            # Si llegamos aca o la particula entro o tenemos 
            # que hacer todo de nuevo

            # Repetimos esto por 15 segundos
    
    def placeNewParticle(self):

        # Queremos ubicarla dentro de todo rapido
        t_end = time.time() + 1

        while time.time() < t_end:
            # Generamos un radio random
            rand_r = np.random.uniform(0.02, 0.03)

            # Generamos posiciones random
            rand_x = np.random.uniform(0, self.w)
            rand_y = np.random.uniform(0, self.l)

            p = Particle(rand_x, rand_y, 0, 0, rand_r/2, 0.01)

            # Chequeamos que no se choque con paredes
            while True:
                 # Paredes
                sup = Particle(p.x, self.l, 0, 0, 0, 0)
                inf = Particle(p.x, 0, 0, 0, 0, 0)
                izq = Particle(0, p.y, 0, 0, 0, 0)
                der = Particle(self.w, p.y, 0, 0, 0, 0)
                if(p.overlap(sup) > 0 and p.overlap(inf) > 0 and p.overlap(izq) > 0 and p.overlap(der) > 0):
                    rand_x = np.random.uniform(0, self.w)
                    rand_y = np.random.uniform(0, self.l)
                    p = Particle(rand_x, rand_y, 0, 0, rand_r/2, 0.01)
                else:
                    break

            # Chequeamos que no se choque con otras particulas
            for other in self.particles:
                if(p.overlap(other) <= 0):
                    # Si llegamos aca la particula entra
                    # entonces la metemos
                    return p
