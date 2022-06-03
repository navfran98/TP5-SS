package Models;

import java.util.List;

public class Force {

    // Variables
    static double kn;
    static double kt;
    static double g = 9.8;

    // Metodos
    public static double calculateFn( Particle p1, Particle p2 ){
        return - kn * p1.getOverlap(p2);
    }

    public static double calculateFt(Particle p1, Particle p2){
        Pair<Pair<Double, Double>, Pair<Double, Double>> versors = p1.getVersors(p2);
        Pair<Double, Double> velocities = p1.getRelativeVelocity(p2);

        double term = velocities.first * versors.second.first + velocities.second * versors.second.second;
        double ret = -kt * p1.getOverlap(p2) * term;

        return ret;
    }

    public static Pair<Double, Double> singleParticleContactForce(Particle p1, Particle p2) {
        // Nos traemos los versores tangenciales y normales
        Pair<Pair<Double, Double>, Pair<Double, Double>> versors = p1.getVersors(p2);
        double Fn = calculateFn(p1, p2);
        double Ft = calculateFt(p1, p2);

        double fx = Fn * versors.first.first + Ft * versors.second.first;
        double fy = Fn * versors.first.second + Ft * versors.second.second;

        Pair<Double, Double> ret = new Pair<>(fx, fy);
        return ret;
    }

    public static Pair<Double, Double> calculateForce(Particle current, Sylo sylo, int flag, int index){
        // Creamos las paredes
        kt = sylo.kt;
        kn = sylo.kn;
        Particle sup = new Particle(current.x, sylo.l, 0, 0, 0, 0);
        Particle inf = new Particle(current.x, 0, 0, 0, 0, 0);
        Particle izq = new Particle(0, current.y, 0, 0, 0, 0);
        Particle der = new Particle(sylo.w, current.y, 0, 0, 0, 0);

        double Fx_tot = 0, Fy_tot = 0;
        Pair<Double, Double> forces;
        List<Particle> list;
        if(flag == 0)
            list = sylo.prevParticles;
        else
            list = sylo.particles;

        for (int i = 0; i < list.size(); i++) {
            Particle other = list.get(i);
            if(current.getOverlap(other) > 0){
                if(index != i){
                    forces = singleParticleContactForce(current, other);
                    Fx_tot += forces.first;
                    Fy_tot += forces.second;
                }
            }
        }

        if(current.x <= sylo.floor + current.radius || current.x >= sylo.w-sylo.floor-current.radius){
            if(current.getOverlap(inf) > 0){
                forces = singleParticleContactForce(current, inf);
                Fx_tot += forces.first;
                Fy_tot += forces.second;
            }
        }

        if(current.getOverlap(sup) > 0){
            forces = singleParticleContactForce(current, sup);
            Fx_tot += forces.first;
            Fy_tot += forces.second;
        }

        if(current.getOverlap(izq) > 0){
            forces = singleParticleContactForce(current, izq);
            Fx_tot += forces.first;
            Fy_tot += forces.second;
        }

        if(current.getOverlap(der) > 0){
            forces = singleParticleContactForce(current, der);
            Fx_tot += forces.first;
            Fy_tot += forces.second;
        }

        Fy_tot -= current.mass * g;

        Pair<Double, Double> ret = new Pair<>(Fx_tot, Fy_tot);
        return ret;
    }
}