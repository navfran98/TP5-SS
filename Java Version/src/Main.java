import Models.Sylo;
import Parsers.OutputParser;

import java.util.*;

public class Main {
    static final double kn = 10e5;
    static final double m = 0.01;

    public static void main(String[] args) {
        double dt = (0.1 * Math.sqrt(m/kn));
        OutputParser.createCleanUniverseFile("output.xyz");
        Sylo sylo = new Sylo(1, 0.4, 0.15, dt);

        sylo.populate(1);
        sylo.simulate(50);
    }
}