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
    double kn;
    double kt;
    List<Particle> particles;
    List<Particle> prevParticles;
    List<Particle> borders;

    // Constructor
    public Sylo(double l, double w, double d, double dt, double ktm) {
        this.kn = 10e5;
        this.kt = ktm * this.kn;
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
    public void simulateUniverse(int seconds) {

        List<Boolean> first = new ArrayList<>();

        for(Particle p : particles)
            first.add(true);

        int step = 1;
        int n = 50000;
        int tOutput = 1000;

        while((dt*step) < 3) {
            int index = 0;
            for(Particle p : particles) {
                if(first.get(index)) {
                    prevParticles.set(index, Euler.run(p, this));
                    first.set(index, false);
                }
                Particle aux = particles.get(index);
                Particle newParticle = Beeman.run(index, this);
                if(newParticle.getX() > w || newParticle.getX() < 0 || newParticle.getY() > l){
                    first.set(index, true);
                    Particle ret = placeNewParticle(seconds);
                    particles.set(index, ret);
                    // reincerciones no deseadas
                }
                else if(newParticle.y <= -l/10) {
                    first.set(index, true);
                    Particle ret = placeNewParticle(seconds);
                    particles.set(index, ret);
                } else {
                    particles.set(index, newParticle);
                    prevParticles.set(index, aux);
                }
                index ++;
                if(step % tOutput == 0) {
                    OutputParser.writeUniverse(particles, borders, step*dt);
                }
            }
            if((step) % n ==  0)
                System.out.println(step*dt);
            step++;
        }
    }

    public void simulateEj1(int seconds, String fn) {

        // long currentTime= System.currentTimeMillis();
        // long end = currentTime + (seconds * 1000);

        List<Boolean> first = new ArrayList<>();

        for(Particle p : particles)
            first.add(true);

        int step = 1;
        int n = 50000;
        int printcsv = 10000;
        int radius_sum = 0;

        int escape_counter = 0;
        OutputParser.parseEj1(0, 0, fn);
        while((dt*step) < 6) {
            int index = 0;
            for(Particle p : particles) {
                if(first.get(index)) {
                    prevParticles.set(index, Euler.run(p, this));
                    first.set(index, false);
                }
                Particle aux = particles.get(index);
                Particle newParticle = Beeman.run(index, this);
                if(newParticle.getX() > w || newParticle.getX() < 0 || newParticle.getY() > l){
                    first.set(index, true);
                    Particle ret = placeNewParticle(seconds);
                    particles.set(index, ret);
                    // reincerciones no deseadas
                } else if(newParticle.y <= -l/10) {
                    escape_counter++;
                    first.set(index, true);
                    Particle ret = placeNewParticle(seconds);
                    particles.set(index, ret);
                } else {
                    particles.set(index, newParticle);
                    prevParticles.set(index, aux);
                }
                index ++;
            }
            if(step % printcsv == 0){
                OutputParser.parseEj1(step*dt, escape_counter, fn);
                escape_counter = 0;
            }
            if((step) % n ==  0){
                System.out.println(step*dt);
            }
            step++;
        }
    }

    public void simulateEj2(int seconds, String fn) {

        List<Boolean> first = new ArrayList<>();

        for(Particle p : particles)
            first.add(true);

        int step = 1;
        int n = 50000;
        int printcsv = 1000;

        double ke = 0;
        OutputParser.parseEj2(0, 0, fn);
        while((dt*step) < 6) {
            int index = 0;
            for(Particle p : particles) {
                if(first.get(index)) {
                    prevParticles.set(index, Euler.run(p, this));
                    first.set(index, false);
                }
                Particle aux = particles.get(index);
                Particle newParticle = Beeman.run(index, this);
                if(newParticle.getX() > w || newParticle.getX() < 0 || newParticle.getY() > l){
                    first.set(index, true);
                    Particle ret = placeNewParticle(seconds);
                    particles.set(index, ret);
                    // reincerciones no deseadas
                } else if(newParticle.y <= -l/10) {
                    first.set(index, true);
                    Particle ret = placeNewParticle(seconds);
                    particles.set(index, ret);
                } else {
                    ke += calculateKE(newParticle);
                    particles.set(index, newParticle);
                    prevParticles.set(index, aux);
                }
                index ++;
            }
            if(step % printcsv == 0){
                OutputParser.parseEj2(step*dt, ke, fn);
            }
            ke = 0;
            if((step) % n ==  0){
                System.out.println(step*dt);
            }
            step++;
        }
    }

    private static double calculateKE(Particle p){
        return 0.5 * p.mass * Math.pow(p.getVelocity(), 2);
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
        // while((currentTime = System.currentTimeMillis()) < end) {
        while(i < 300) {

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
                i++;
                first = false;
            } else {
                boolean flag = true;
                for(Particle other : particles){
                    if(p.getOverlap(other) >= 0){
                        flag = false;
                    }
                }
                if(flag){
                    particles.add(p);
                    i++;
                }
            }
        }
        for(Particle part : particles)
            prevParticles.add(Euler.run(part, this));
        
        
        OutputParser.writeUniverse(particles, borders,0);
        System.out.println("Finalizo el populate - " + particles.size() + " Particulas");
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
