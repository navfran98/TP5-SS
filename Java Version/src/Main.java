import Models.*;
import Parsers.OutputParser;

public class Main {
    static final double kn = 10e5;
    static final double m = 0.01;
    static final int l = 1;
    static final double w = 0.4;

    public static void main(String[] args) {
        // runUniverse();
        // runEj1_2();
        runEj3();
        // runEj4();
    }

    public static void runUniverse(){
        double dt = (0.1 * Math.sqrt(m/kn));
        double d = 0.15;
        Sylo sylo = new Sylo(l, w, d, dt, 2);
        OutputParser.createCleanUniverseFile("XYZ/output.xyz");
        sylo.populate(0.005);
        sylo.simulateUniverse(50);
    }

    public static void runEj1_2(){
        double d1 = 0.15;
        double d2 = 0.18;
        double d3 = 0.21;
        double d4 = 0.24;
        double[] ds = {d1,d2,d3,d4};
        double dt = (0.1 * Math.sqrt(m/kn));

        for (int i = 1; i < 5; i++) {
            String fn = "FilesEj1/OutputEj1_" + i + ".csv";
            Sylo sylo = new Sylo(l, w, ds[i-1], dt, 2);
            OutputParser.createCleanCSVFile(fn);
            sylo.populate(0.005);
            sylo.simulateEj1(50, fn);
        }
    }

    public static void runEj3(){
        double d1 = 0.15;
        double d2 = 0.18;
        double d3 = 0.21;
        double d4 = 0.24;
        double[] ds = {d1,d2,d3,d4};
        double dt = (0.1 * Math.sqrt(m/kn));

        for (int i = 1; i < 5; i++) {
            String fn = "FilesEj2/OutputEj2_" + i + ".csv";
            Sylo sylo = new Sylo(l, w, ds[i-1], dt, 2);
            OutputParser.createCleanCSVFile(fn);
            sylo.populate(0.005);
            sylo.simulateEj2(50, fn);
        }
    }

    public static void runEj4(){
        double dt = (0.1 * Math.sqrt(m/kn));
        double ktm1 = 0.5;
        double ktm2 = 1;
        double ktm3 = 2;
        double ktm4 = 3;
        double[] ktms = {ktm1,ktm2,ktm3,ktm4};

        for (int i = 1; i < 5; i++) {
            String fn = "FilesEj4/OutputEj4_" + i + ".csv";
            Sylo sylo = new Sylo(l, w, 0, dt, ktms[i-1]);
            OutputParser.createCleanCSVFile(fn);
            sylo.populate(0.005);
            sylo.simulateEj2(50, fn);
        }
    }
    
}