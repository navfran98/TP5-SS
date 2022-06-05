import java.util.ArrayList;
import java.util.List;

public class Sylo {
    
    // Variables
    double l;
    double w;
    double d;
    double dt;
    double kn;
    double kt;
    double ktm;
    double floor;
    List<Particle> particles;
    List<Particle> prevParticles;
    List<Particle> borders;

    // Constructor
    public Sylo(double l, double w, double d, double dt, double ktm) {
        this.l = l;
        this.w = w;
        this.d = d;
        this.dt = dt;
        this.kn = Math.pow(10, 5);
        this.kt = ktm * kn;
        this.floor = (w-d)/2;
        this.particles = new ArrayList<>();
        this.prevParticles = new ArrayList<>();
        this.borders = generateBorderParticles();
    }

    // Metodos

    private List<Particle> generateBorderParticles(){
        List<Particle> list = new ArrayList<>();
        list.add(new Particle(0,0,0,0,0.0001,0));
        list.add(new Particle(0, l,0,0,0.0001,0));
        list.add(new Particle(w,0,0,0,0.0001,0));
        list.add(new Particle(w, l,0,0,0.0001,0));
        list.add(new Particle(floor,0,0,0, 0.0001,0));
        list.add(new Particle(w-floor,0,0,0,0.0001,0));
        return list;
    }

    public Particle nextEuler(int index) {

        Particle p = particles.get(index);

        Pair<Double, Double> force = Force.calculateForce(p, this, true, index);

        // Calculamos la posicion en (t - Dt)

        double newVx = p.vx - (dt / p.m) * force.first;
        double newVy = p.vy - (dt / p.m) * force.second;

        // Calculamos la velocidad en (t - dt)

        double newx = p.x - dt * newVx + (dt*dt) * force.first / (2*p.m);
        double newy = p.y - dt * newVy + (dt*dt) * force.second / (2*p.m);

        return new Particle(newx, newy, newVx, newVy, p.r, p.m);

    }

    public Particle nextBeeman(int index) {

        // Nos traemos las particulas ...

        Particle current = particles.get(index);
        Particle prev = prevParticles.get(index);

        // Calculamos las fuerzas ...

        Pair<Double, Double> currentForce = Force.calculateForce(current, this, true, index);
        Pair<Double, Double> prevForce = Force.calculateForce(prev, this, false, index);

        // Datos a calcular ...

        double rx = 0, ry = 0, newVx = 0, newVy = 0;

        // Condiciones iniciales

        double r0x = current.x, r0y = current.y, v0x = current.vx, v0y = current.vy;

        // Algoritmo de beeman para un paso ...

        // Posiciones
        rx = r0x + (v0x * dt) + (2.0/3.0) * (currentForce.first/current.m) * (dt * dt) - (1.0/6.0) * (prevForce.first/current.m) * (dt * dt);
        ry = r0y + (v0y * dt) + (2.0/3.0) * (currentForce.second/current.m) * (dt * dt) - (1.0/6.0) * (prevForce.second/current.m) * (dt * dt);

        // Predicciones
        double predVx = v0x + (3.0/2.0) * (currentForce.first/current.m) * dt - 0.5 * (prevForce.first/current.m) * dt;
        double predVy = v0y + (3.0/2.0) * (currentForce.second/current.m) * dt - 0.5 * (prevForce.second/current.m) * dt;

        // Correcciones
        Particle newp = new Particle(rx, ry, predVx, predVy, current.r, current.m);
        Pair<Double, Double> newForce = Force.calculateForce(newp, this,true, index);

        newVx= v0x + (1.0/3.0) * (newForce.first/current.m) * dt + (5.0/6.0) * (currentForce.first/current.m) * dt - (1.0/6.0) * (prevForce.first/current.m) * dt;
        newVy= v0y + (1.0/3.0) * (newForce.second/current.m) * dt + (5.0/6.0) * (currentForce.second/current.m) * dt - (1.0/6.0) * (prevForce.second/current.m) * dt; 

        return new Particle(rx, ry, newVx, newVy, current.r, current.m);
    }

    // Metodos
    public void simulateUniverse(int seconds) {

        List<Boolean> first = new ArrayList<>();

        for(Particle p : particles)
            first.add(true);

        int step = 1;
        int n = 50000;
        int tOutput = 1000;

        while((dt*step) < 5) {
            int index = 0;
            for(Particle p : particles) {
                if(first.get(index)) {
                    prevParticles.set(index, nextEuler(index));
                    first.set(index, false);
                }
                Particle aux = particles.get(index);
                Particle newParticle = nextBeeman(index);
                if(newParticle.x > w || newParticle.x < 0 || newParticle.y > l){
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

    public void simulateEJ1_2(int seconds, String fn) {

        List<Boolean> first = new ArrayList<>();

        for(Particle p : particles)
            first.add(true);

        int step = 1;
        int n = 50000;
        int printcsv = 1000;
        int escape_counter = 0;
        while((dt*step) < 5) {
            int index = 0;
            for(Particle p : particles) {
                if(first.get(index)) {
                    prevParticles.set(index, nextEuler(index));
                    first.set(index, false);
                }
                Particle aux = particles.get(index);
                Particle newParticle = nextBeeman(index);
                if(newParticle.x > w || newParticle.x < 0 || newParticle.y > l){
                    first.set(index, true);
                    Particle ret = placeNewParticle(seconds);
                    particles.set(index, ret);
                    // reincerciones no deseadas
                }
                else if(newParticle.y <= -l/10) {
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
                OutputParser.parseEj1_2(step*dt, escape_counter, fn);
                escape_counter = 0;
            }
            if((step) % n ==  0)
                System.out.println(step*dt);
            step++;
        }
    }

    public void simulateEJ3_4(int seconds, String fn) {

        List<Boolean> first = new ArrayList<>();

        for(Particle p : particles)
            first.add(true);

        int step = 1;
        int n = 50000;
        int printcsv = 1000;
        double ke = 0;
        OutputParser.parseEj3_4(0, 0, fn);
        while((dt*step) < 5) {
            int index = 0;
            for(Particle p : particles) {
                if(first.get(index)) {
                    prevParticles.set(index, nextEuler(index));
                    first.set(index, false);
                }
                Particle aux = particles.get(index);
                Particle newParticle = nextBeeman(index);
                if(newParticle.x > w || newParticle.x < 0 || newParticle.y > l){
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
                    ke += 0.5 * newParticle.m * Math.pow(newParticle.getV(),2);
                    particles.set(index, newParticle);
                    prevParticles.set(index, aux);
                }
                index ++;
            }
            if(step % printcsv == 0){
                OutputParser.parseEj3_4(step*dt, ke, fn);
            }
            ke = 0;
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
        // while((currentTime = System.currentTimeMillis()) < end) {
        while(i < 300) {

            double rand_r = (Math.random() * (radiusHigh-radiusLow)) + radiusLow;
            double rand_x = Math.random() * x_high;
            double rand_y = Math.random() * y_high;

            Particle p = new Particle(rand_x, rand_y, 0, 0, rand_r, 0.01);

            while(true) {
                
                Particle sup = new Particle(p.x, l, 0, 0, 0, 0);
                Particle inf = new Particle(p.x, 0, 0, 0, 0, 0);
                Particle izq = new Particle(0, p.y, 0, 0, 0, 0);
                Particle der = new Particle(w, p.y, 0, 0, 0, 0);

                if(p.calculateOverlap(sup) >= 0 || p.calculateOverlap(inf) >= 0 || p.calculateOverlap(izq) >= 0 || p.calculateOverlap(der) >= 0){
                    rand_x = Math.random() * x_high;
                    rand_y = Math.random() * y_high;
                    p = new Particle(rand_x, rand_y, 0, 0, rand_r, p.m);
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
                    if(p.calculateOverlap(other) >= 0){
                        flag = false;
                    }
                }
                if(flag){
                    particles.add(p);
                    i++;
                }
            }
        }

        int in = 0;
        for(Particle part : particles){
            prevParticles.add(nextEuler(in));
            in++;
        }
        
        
        OutputParser.writeUniverse(particles, borders, 0);
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

            Particle p = new Particle(rand_x, rand_y, 0, 0, rand_r, 0.01);

            while(true) {
                
                Particle sup = new Particle(p.x, l, 0, 0, 0, 0);
                Particle inf = new Particle(p.x, 0, 0, 0, 0, 0);
                Particle izq = new Particle(0, p.y, 0, 0, 0, 0);
                Particle der = new Particle(w, p.y, 0, 0, 0, 0);

                if(p.calculateOverlap(sup) >= 0 || p.calculateOverlap(izq) >= 0 || p.calculateOverlap(der) >= 0){
                    rand_x = Math.random() * x_high;
                    rand_y = Math.random() * (y_high - y_low) + y_low;
                    p = new Particle(rand_x, rand_y, 0, 0, rand_r, p.m);
                } else {
                    break;
                }
                
            }

            boolean flag = true;
            for(Particle other : particles){
                if(p.calculateOverlap(other) >= 0){
                    flag = false;
                }
            }
            if(flag)
                return p;
        }
        return null;
    }

}
