import javax.annotation.processing.SupportedOptions;

class main {
    public static void main(String[] args) {
        // double dt = 0.00001;
        // System.out.println(dt);
        // Sylo s = new Sylo(1, 0.4, 0, dt, 2);
        // s.populate(0.005);
        // s.simulateUniverse(50);
        runEj1_2();
        // runEj4();
        // runEj3();
        // runUniverse();
    }

    public static void runUniverse(){
        double dt = 0.00001;
        Sylo sylo = new Sylo(1, 0.4,0, dt, 0.25);
        OutputParser.createCleanUniverseFile("XYZ/output.xyz");
        sylo.populate(0.005);
        sylo.simulateUniverse(50);
    }

    public static void runEj1_2(){
        double d1 = 0.15;
        double d2 = 0.17;
        double d3 = 0.19;
        double d4 = 0.21;
        double[] ds = {d1,d2,d3,d4};
        // double dt = (0.1 * Math.sqrt(0.01/100000));
        double dt = 0.00001;

        for (int i = 1; i < 5; i++) {
            String fn = "FilesEj1/OutputEj1_" + i + ".csv";
            Sylo sylo = new Sylo(1, 0.3, ds[i-1], dt, 2);
            OutputParser.createCleanCSVFile(fn);
            sylo.populate(0.005);
            sylo.simulateEJ1_2(50, fn);
        }
    }

    public static void runEj3(){
        double d1 = 0.15;
        double d2 = 0.17;
        double d3 = 0.19;
        double d4 = 0.21;
        double[] ds = {d1,d2,d3,d4};
        // double dt = (0.1 * Math.sqrt(m/kn));
        double dt = 0.00001;

        for (int i = 1; i <5 ; i++) {
            String fn = "FilesEj3/OutputEj3_" + i + ".csv";
            Sylo sylo = new Sylo(1, 0.3, ds[i-1], dt, 2);
            OutputParser.createCleanCSVFile(fn);
            sylo.populate(0.005);
            sylo.simulateEJ3_4(50, fn);
        }
    }

    public static void runEj4(){
        double dt = 0.00001;
        double ktm2 = 1;
        double ktm3 = 2;
        double ktm4 = 3;
        double[] ktms = {ktm2,ktm3,ktm4};

        for (int i = 1; i < 4; i++) {
            String fn = "FilesEj4/OutputEj4_" + i + ".csv";
            Sylo sylo = new Sylo(1, 0.3, 0, dt, ktms[i-1]);
            OutputParser.createCleanCSVFile(fn);
            sylo.populate(0.005);
            sylo.simulateEJ3_4(50, fn);
        }
    }
}