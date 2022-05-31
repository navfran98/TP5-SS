package Models;

import Parsers.OutputParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Sylo {

    // Varibles
    double l;
    double w;
    double d;
    double floor;
    double dt;
    double mass;
    List<Particle> particles;
    List<Particle> prevParticles;
    List<Particle> borders;

    // Constructor
    public Sylo(double l, double w, double d, double dt) {
        this.l = l;
        this.w = w;
        this.d = d;
        this.floor = (w-d)/2;
        this.dt = dt;
        this.mass = 0.01;
        this.particles = new ArrayList<>();
        this.prevParticles = new ArrayList<>();
        this.borders = generateBorderParticles();
    }

    private List<Particle> generateBorderParticles(){
        List<Particle> list = new ArrayList<>();
        list.add(new Particle(0,0,0,0,0,0));
        list.add(new Particle(0, l,0,0,0,0));
        list.add(new Particle(w,0,0,0,0,0));
        list.add(new Particle(w, l,0,0,0,0));
        list.add(new Particle(floor,0,0,0,0,0));
        list.add(new Particle(w-floor,0,0,0,0,0));
        return list;
    }

    // Metodos
    public void simulate(int seconds) {

        long currentTime= System.currentTimeMillis();
        long end = currentTime + (seconds * 1000);

        List<Boolean> first = new ArrayList<>();

        for(Particle p : particles)
            first.add(true);

        int step = 1;
        int tOutput = 1000 ;

        while(currentTime < end) {
            int index = 0;
            for(Particle p : particles) {
                if(first.get(index)) {
                    prevParticles.set(index, Euler.run(p, this));
                    first.set(index, false);
                }
                Particle aux = particles.get(index);
                Particle newParticle = Beeman.run(index, this);
                if(newParticle.y <= -l/10 && newParticle.x > floor && newParticle.x < w - floor) {
                    first.set(index, true);
                    particles.set(index, placeNewParticle(seconds));
                } else {
                    particles.set(index, newParticle);
                    prevParticles.set(index, aux);
                }
                index ++;
                currentTime = System.currentTimeMillis();
                if(step % tOutput == 0) {
                    OutputParser.writeUniverse(particles, borders, currentTime);
                }
            }
            step++;
        }
    }

    public void populate(int seconds) {

        long currentTime= System.currentTimeMillis();
        long end = currentTime + (seconds * 1000);

        double radiusLow = 0.02/2;
        double radiusHigh = 0.03/2;

        double y_high = l;
        double x_high = w;
        boolean first = true;
        int i = 0;
        while(i < 10) {

            double rand_r = (Math.random() * (radiusHigh-radiusLow)) + radiusLow;
            double rand_x = Math.random() * x_high;
            double rand_y = Math.random() * y_high;

            Particle p = new Particle(rand_x, rand_y, 0, 0, rand_r, mass);

            while(true) {
                
                Particle sup = new Particle(p.x, l, 0, 0, 0, 0);
                Particle inf = new Particle(p.x, 0, 0, 0, 0, 0);
                Particle izq = new Particle(0, p.y, 0, 0, 0, 0);
                Particle der = new Particle(w, p.y, 0, 0, 0, 0);

                if(p.getOverlap(sup) >= 0 || p.getOverlap(inf) >= 0 || p.getOverlap(izq) >= 0 || p.getOverlap(der) >= 0){
                    rand_x = Math.random() * x_high;
                    rand_y = Math.random() * y_high;
                    p = new Particle(rand_x, rand_y, 0, 0, rand_r, mass);
                } else {
                    break;
                }
                
            }

            if(first) {
                particles.add(p);
                first = false;
            } else {
                boolean flag = true;
                for(Particle other : particles){
                    if(p.getOverlap(other) >= 0){
                        flag = false;
                    }
                }
                if(flag)
                    particles.add(p);
            }
            i++;
//            currentTime = System.currentTimeMillis();
            
        }
        for(Particle part : particles)
            prevParticles.add(Euler.run(part, this));
        

        //TODO: Parsear el estado inicial en t=0 en el xyz
        OutputParser.writeUniverse(particles, borders,0);
        
    }

    public Particle placeNewParticle(int seconds) {

        long currentTime= System.currentTimeMillis();
        long end = currentTime + (seconds * 1000);

        double radiusLow = 0.02/2;
        double radiusHigh = 0.03/2;

        double y_high = l;
        double x_high = w;
        boolean first = true;
        
        while(currentTime < end) {

            double rand_r = (Math.random() * (radiusHigh-radiusLow)) + radiusLow;
            double rand_x = Math.random() * x_high;
            double rand_y = Math.random() * y_high;

            Particle p = new Particle(rand_x, rand_y, 0, 0, rand_r, mass);

            while(true) {
                
                Particle sup = new Particle(p.x, l, 0, 0, 0, 0);
                Particle inf = new Particle(p.x, 0, 0, 0, 0, 0);
                Particle izq = new Particle(0, p.y, 0, 0, 0, 0);
                Particle der = new Particle(w, p.y, 0, 0, 0, 0);

                if(p.getOverlap(sup) >= 0 || p.getOverlap(inf) >= 0 || p.getOverlap(izq) >= 0 || p.getOverlap(der) >= 0){
                    rand_x = Math.random() * x_high;
                    rand_y = Math.random() * y_high;
                    p = new Particle(rand_x, rand_y, 0, 0, rand_r, mass);
                } else {
                    break;
                }
                
            }

            boolean flag = true;
            for(Particle other : particles){
                if(p.getOverlap(other) >= 0){
                    flag = false;
                }
            }
            if(flag)
                return p;
        }
        return null;
    }
}
