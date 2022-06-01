package Models;

public class Beeman {

    public static Particle run(int index, Sylo sylo){
        // Particulas
        Particle p = sylo.particles.get(index);
        Particle prevp = sylo.prevParticles.get(index);

        // Forces
        Pair<Double, Double> currentForce = Force.calculateForce(p, sylo, 1, index);
        Pair<Double, Double> prevForce = Force.calculateForce(prevp, sylo, 0, index);
        
        // Datos a calcular
        double rx = 0, ry = 0, newVx = 0, newVy = 0;

        // Condiciones iniciales
        double r0x = p.x, r0y = p.y, v0x = p.vx, v0y = p.vy;
        
        // Algoritmo de Beeman para un paso
        // Posiciones
        rx = r0x + (v0x * sylo.dt) + (2.0/3.0) * (currentForce.first/p.mass) * (sylo.dt * sylo.dt) - (1.0/6.0) * (prevForce.first/p.mass) * (sylo.dt * sylo.dt);
        ry = r0y + (v0y * sylo.dt) + (2.0/3.0) * (currentForce.second/p.mass) * (sylo.dt * sylo.dt) - (1.0/6.0) * (prevForce.second/p.mass) * (sylo.dt * sylo.dt);
        
        // Predicciones
        double predVx = v0x + (3.0/2.0) * (currentForce.first/p.mass) * sylo.dt - 0.5 * (prevForce.first/p.mass) * sylo.dt;
        double predVy = v0y + (3.0/2.0) * (currentForce.second/p.mass) * sylo.dt - 0.5 * (prevForce.second/p.mass) * sylo.dt;

        // Correcciones
        Particle newp = new Particle(rx, ry, predVx, predVy, p.radius, p.mass);
        Pair<Double, Double> newForce = Force.calculateForce(newp, sylo, 2, index);
        
        newVx= v0x + (1.0/3.0) * (newForce.first/p.mass) * sylo.dt + (5.0/6.0) * (currentForce.first/p.mass) * sylo.dt - (1.0/6.0) * (prevForce.first/p.mass) * sylo.dt;
        newVy= v0y + (1.0/3.0) * (newForce.second/p.mass) * sylo.dt + (5.0/6.0) * (currentForce.second/p.mass) * sylo.dt - (1.0/6.0) * (prevForce.second/p.mass) * sylo.dt; 

        return new Particle(rx, ry, newVx, newVy, p.radius, p.mass);
    }
}