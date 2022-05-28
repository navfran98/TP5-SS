from math import sqrt
import numpy as np
from Particle import Particle

# ---------------------------------------------- #
#             < VARIABLES GLOBALES >
# ---------------------------------------------- #

kn = 10**5
kt = 2 * kn
m = 0.01

# ---------------------------------------------- #
#            < ALGORITMO DE EULER >
# ---------------------------------------------- #

class Euler:
    
    def run(particle, timeStep, particles, l , w, d):
        # Calculamos la fuerza ... 
        force_x, force_y = forceCalc(particle, particles, l, w, d)

        # Calculamos la velocidad en X ...
        newVelocityX = particle.vx + (timeStep / m) * force_x

        # Calculamos la posicion en X ...
        newPositionX = particle.x + (timeStep * newVelocityX) + (timeStep**2) * force_x / (2 * m)

        # Calculamos la velocidad en Y ...
        newVelocityY = particle.vy + (timeStep / m) * force_y

        # Calculamos la posicion en Y ...
        newPositionY = particle.y + (timeStep * newVelocityY) + (timeStep**2) * force_y / (2 * m)

        return Particle(newPositionX, newPositionY, newVelocityX, newVelocityY, particle.radius, m)

# ---------------------------------------------- #
#            < ALGORITMO DE BEEMAN >
# ---------------------------------------------- #

class Beeman:

    def run(particle, prevParticle, timeStep, particles, prevParticles, l , w, d):

        # Primero creamos lo que vamos a devolver
        r_x = 0
        v_x = 0
        r_y = 0
        v_y = 0

        # Seteamos las condiciones iniciales...
        r0_x = particle.x
        v0_x = particle.vx
        r0_y = particle.y
        v0_y = particle.vy

        # Calculamos las fuerzas
        c_fx, c_fy = forceCalc(particle, particles, l, w, d)
        p_fx, p_fy = forceCalc(prevParticle, prevParticles, l, w, d)

        # Pos
        r_x = r0_x + (v0_x * timeStep) + (2/3) * (c_fx/m) * (timeStep**2) - (1/6) * (p_fx/m) * (timeStep**2)
        r_y = r0_y + (v0_y * timeStep) + (2/3) * (c_fy/m) * (timeStep**2) - (1/6) * (p_fy/m) * (timeStep**2)
        
        # Pred
        predVx = v0_x + (3/2) * (c_fx/m) * timeStep - 0.5 * (p_fx/m) * timeStep
        predVy = v0_y + (3/2) * (c_fy/m) * timeStep - 0.5 * (p_fy/m) * timeStep    
        
        # Correc
        # CHEQUEAR ESTO
        P = Particle(r_x, r_y, predVx, predVy, particle.radius, m)
        n_fx, n_fy = forceCalc(P, particles, l, w, d)
        v_x = v0_x + (1/3) * (n_fx/m) * timeStep + (5/6) * (c_fx/m) * timeStep - (1/6) * (p_fx/m) * timeStep
        v_y = v0_y + (1/3) * (n_fy/m) * timeStep + (5/6) * (c_fy/m) * timeStep - (1/6) * (p_fy/m) * timeStep
        
        return Particle(r_x, r_y, v_x, v_y, particle.radius, m)

# ---------------------------------------------- #
#             < Calculo de fuerzas >
# ---------------------------------------------- #

def calculateFN(particle1, particle2):
    return -kn * particle1.overlap(particle2)

def calculateFT(particle1, particle2):

    # Necesitamos la componente tangencial de la velocidad relativa
    # entonces buscamos los versores tangencial y normal:
    en, et = particle1.colForces(particle2)

    # Nos traemos el modulo de la velocidad relativa
    rel_v = particle1.getRelVelocity(particle2)

    # Como solo nos interesa la componente tangencial hacemos
    # PROYECCION ECALAR que es que:
    # Vector x Versor = Componente del Vector sobre ese versor
    # Pero esto nos da otro vector y nosotros queremos que
    # Ft sea un numero no un vector ==> sacamos modulo de
    # la proyeccion que hicimos
    rel_v_t = sqrt((rel_v * et[0])**2 + (rel_v * et[1])**2)

    return -kt * particle1.overlap(particle2) * rel_v_t

def SPContactForce(particle1, particle2):

    # Nos traemos los versores
    en, et = particle1.colForces(particle2)

    Fn = calculateFN(particle1, particle2)
    Ft = calculateFT(particle1, particle2)

    # Proyectamos la fuerza sobre el eje X
    Fx = Fn * en[0] + Ft * et[0]

    # Proyectamos la fuerza sobre el eje y
    Fy = Fn * en[1] + Ft * et[1]

    return Fx, Fy

def forceCalc(current, particles, l, w, d):

    # Calculamos el tamanio del piso
    floor = (w-d)/2

    # Paredes
    sup = Particle(current.x, l, 0, 0, 0, 0)
    inf = Particle(current.x, 0, 0, 0, 0, 0)
    izq = Particle(0, current.y, 0, 0, 0, 0)
    der = Particle(w, current.y, 0, 0, 0, 0)

    Fx_tot = 0
    Fy_tot = 0

    for other in particles:
        if(current.overlap(other) >= 0):
            if(current.x != other.x and current.y != other.y and current.radius != other.radius):
                Fx, Fy = SPContactForce(current, other)
                Fx_tot += Fx
                Fy_tot += Fy

    if(current.x <= floor or current.x >= w-floor):
        if(current.overlap(sup) >= 0):
            #print(f"{current.x} - {sup.x} ----- {current.y} - {sup.y}")
            Fx, Fy = SPContactForce(current, sup)
            Fx_tot += Fx
            Fy_tot += Fy
    if(current.overlap(inf) >= 0):
        #print(f"{current.x} - {inf.x} ----- {current.y} - {inf.y}")
        Fx, Fy = SPContactForce(current, inf)
        Fx_tot += Fx
        Fy_tot += Fy
    if(current.overlap(der) >= 0):
        #print(f"{current.x} - {der.x} ----- {current.y} - {der.y}")
        Fx, Fy = SPContactForce(current, der)
        Fx_tot += Fx
        Fy_tot += Fy
    if(current.overlap(izq) >= 0):
        #print(f"{current.x} - {izq.x} ----- {current.y} - {izq.y}")
        Fx, Fy = SPContactForce(current, izq)
        Fx_tot += Fx
        Fy_tot += Fy
    
    
    # En el eje y hay que incluir la gravedad
    Fy_tot += m * 9.8
    # Devolvemos el modulo de la fuerza
    return Fx_tot, Fy_tot


