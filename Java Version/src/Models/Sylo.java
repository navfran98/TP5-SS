package Models;

import java.util.ArrayList;
import java.util.List;
import Parsers.OutputParser;
import Models.*;
import java.util.Random;

import javax.swing.plaf.metal.MetalBorders.Flush3DBorder;

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
        list.add(new Particle(floor,0,0,0, 0,0));
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
        int n = 500000;
        int tOutput = 20000;

        while((dt*step) < 5) {
            int index = 0;
            for(Particle p : particles) {
                if(first.get(index)) {
                    prevParticles.set(index, Euler.run(p, this));
                    first.set(index, false);
                }
                Particle aux = particles.get(index);
                Particle newParticle = Beeman.run(index, this);
                double retu = 0;
                if(newParticle.getX() > w || newParticle.getX() < 0 || newParticle.getY() > l){
                    System.out.println("Otro error");
                    System.out.println(newParticle);
                    System.out.println(aux);
                    for (int i = 0; i < particles.size(); i++) {
                        if(index != i){
                            double over = aux.getOverlap(particles.get(i));
                            if(over > 0){
                                System.out.println(particles.get(i) + " - " + over);
                            }
                        }
                    }
                    retu = 1;
                
                }
                if(newParticle.getY() < 0 && newParticle.getX() < floor && newParticle.getX() > w-floor){
                    System.out.println("Y negativa");
                    System.out.println(newParticle);
                    System.out.println(aux);
                    for (int i = 0; i < particles.size(); i++) {
                        if(index != i){
                            double over = aux.getOverlap(particles.get(i));
                            if(over > 0){
                                System.out.println(particles.get(i) + " - " + over);
                            }
                        }
                    }
                    retu = 1;
                }
                if(retu == 1)
                    return; 
                if(newParticle.y <= -l/10 && newParticle.x > floor && newParticle.x < w - floor) {
                    first.set(index, true);
                    Particle ret = placeNewParticle(seconds);
                    particles.set(index, ret);
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
            if((step) % n ==  0)
                System.out.println(step*dt);
            step++;
        }
    }

    public void populate(double seconds) {

        long currentTime= System.currentTimeMillis();
        long end = currentTime + (int)(seconds * 1000);

        double radiusLow = 0.02/2;
        double radiusHigh = 0.03/2;

        double y_high = l;
        double x_high = w;
        boolean first = true;
        int i = 0;
        while((currentTime = System.currentTimeMillis()) < end) {

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
        
        
        OutputParser.writeUniverse(particles, borders,0);
        System.out.println("Finalizo el populate - " + particles.size() + " Particulas");
        // double r = 0.01;
        // Particle p1 = new Particle(w - 2 * r, r, 0, 0, r, mass);
        // Particle p2 = new Particle(w - 2 * 0.03, l- 10*r, 0, 0, 0.04, mass);
        // Particle p3 = new Particle(w - 2 * r, l- 25*r, 0, 0, r, mass);
        // Particle p4 = new Particle(w -  4 *r, l-20*r, 0, 0, r, mass);
        // Particle p5 = new Particle(w - 4 * r , 2*r, 0, 0, r, mass);
        // Particle p6 = new Particle(w - 2 * r, 4*r, 0, 0, r, mass);

        // particles.add(p1);
        // particles.add(p2);
        // particles.add(p3);
        // particles.add(p4);
        // particles.add(p5);
        // particles.add(p6);

        
        // prevParticles.add(Euler.run(p1, this));
        // prevParticles.add(Euler.run(p2, this));
        // prevParticles.add(Euler.run(p3, this));
        // prevParticles.add(Euler.run(p4, this));
        // prevParticles.add(Euler.run(p5, this));
        // prevParticles.add(Euler.run(p6, this));

        // OutputParser.writeUniverse(particles, borders,0);
    }

    public Particle placeNewParticle(int seconds) {

        long currentTime= System.currentTimeMillis();
        long end = currentTime + (seconds * 1000);

        double radiusLow = 0.02/2;
        double radiusHigh = 0.03/2;

        double y_high = l;
        double y_low = (3 * l)/5;
        double x_high = w;
        
        while((currentTime = System.currentTimeMillis()) < end) {

            double rand_r = (Math.random() * (radiusHigh-radiusLow)) + radiusLow;
            double rand_x = Math.random() * x_high;
            double rand_y = Math.random() * (y_high - y_low) + y_low;

            Particle p = new Particle(rand_x, rand_y, 0, 0, rand_r, mass);

            while(true) {
                
                Particle sup = new Particle(p.x, l, 0, 0, 0, 0);
                Particle inf = new Particle(p.x, 0, 0, 0, 0, 0);
                Particle izq = new Particle(0, p.y, 0, 0, 0, 0);
                Particle der = new Particle(w, p.y, 0, 0, 0, 0);

                if(p.getOverlap(sup) >= 0 || p.getOverlap(izq) >= 0 || p.getOverlap(der) >= 0){
                    rand_x = Math.random() * x_high;
                    rand_y = Math.random() * (y_high - y_low) + y_low;
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
