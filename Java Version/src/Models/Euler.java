package Models;

public class Euler {

    public static Particle run(Particle p, Sylo sylo) {
        int index = sylo.particles.indexOf(p);
        Pair<Double, Double> forces = Force.calculateForce(p, sylo, 1, index);

        double newVx = p.vx + (sylo.dt/p.mass)* forces.first;
        double newVy = p.vy + (sylo.dt/p.mass)* forces.second;
        double newX = p.x + sylo.dt * newVx + ((sylo.dt*sylo.dt) * forces.first)/(2*p.mass);
        double newY = p.y + sylo.dt * newVy + ((sylo.dt*sylo.dt) * forces.second)/(2*p.mass);

        return new Particle(newX, newY, newVx, newVy, p.radius, p.mass);
    }
    
}