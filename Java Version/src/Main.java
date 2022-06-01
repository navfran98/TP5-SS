import Models.*;

public class Main {
    static final double kn = 10e5;
    static final double m = 0.01;

    public static void main(String[] args) {
        double dt = (0.1 * Math.sqrt(m/kn));

        Sylo sylo = new Sylo(1, 0.4, 0.15, dt);

        System.out.println("Comenzando el populate");
        sylo.populate(0.03);
        sylo.simulate(100);
        System.out.println("Finalizo la simulacion");
    }
}