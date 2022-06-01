import Models.*;
import Parsers.OutputParser;
import java.util.*;

public class Main {
    static final double kn = 10e5;
    static final double m = 0.01;

    public static void main(String[] args) {
        double dt = (0.1 * Math.sqrt(m/kn));

        Sylo sylo = new Sylo(1, 0.4, 0.15, dt/7);

        System.out.println("Comenzando el populate");
        sylo.populate(0.03);
        
        sylo.simulate(100);
        System.out.println("Finalizo la simulacion");
        // Particle p1 = new Particle(2, 2, 0, 0, 0, 0);
        // Particle p2 = new Particle(1, 2, 0, 0, 0, 0);
        // System.out.println(p1.equals(p2));
    }
}