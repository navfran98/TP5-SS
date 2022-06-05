import java.util.List;

public class Force {

    // Metodos
    public static double getFN(Particle p1, Particle p2, Sylo s) {
        return - s.kn * p1.calculateOverlap(p2);
    }

    public static double getFT(Particle p1, Particle p2, Sylo s) {

        double xDiff = p2.x - p1.x;
        double yDiff = p2.y - p1.y;
        double distance = Math.sqrt(xDiff*xDiff + yDiff*yDiff);
        double enx = xDiff/distance;
        double eny = yDiff/distance;

        // Calculamos los versores tangenciales y normales ...
        Pair<Double, Double> en = new Pair<Double,Double>(enx, eny);
        Pair<Double, Double> et = new Pair<Double,Double>(-eny, enx);

        // Nos traemos la velocidad relativa entre las particulas ...
        Pair<Double, Double> v = p1.getRV(p2);

        // Solo nos interesa la componente tangencial ...
        double term = ( v.first * et.first + v.second * et.second );

        // Retornamos
        return - s.kt * p1.calculateOverlap(p2) * term;

    }

    public static Pair<Double, Double> calculateForce(Particle p, Sylo s, boolean current, int index) {

        // Las componentes de la fuerza ...
        double fxT = 0, fyT = 0;
        double fn = 0, ft = 0;

        // En que array trabajamos

        List<Particle> array;

        if(current)
            array = s.particles;
        else
            array = s.prevParticles;

        // Comparo contra las particulas ...
        for (int i = 0; i < array.size(); i++) {
            if(i != index){
                if(p.calculateOverlap(array.get(i)) > 0) {
                    // Nos traemos FN y FT
                    fn = getFN(p, array.get(i), s);
                    ft = getFT(p, array.get(i), s);

                    // Calculamos las fuerzas totales en X e Y ...
                    double xDiff = array.get(i).x - p.x;
                    double yDiff = array.get(i).y - p.y;
                    double distance = Math.sqrt(xDiff*xDiff + yDiff*yDiff);
                    double enx = xDiff/distance;
                    double eny = yDiff/distance;

                    fxT += fn * enx + ft * (-eny);
                    fyT += fn * eny + ft * enx;
                }
            }
        }

        // Creamos las paredes que en este caso son particulas ...

        Particle sup = new Particle(p.x, s.l, 0, 0, 0, 0);
        Particle inf = new Particle(p.x, 0, 0, 0, 0, 0);
        Particle der = new Particle(s.w, p.y, 0, 0, 0, 0);
        Particle izq = new Particle(0, p.y, 0, 0, 0, 0);

        // Comparo contra las paredes ...

        if( p.calculateOverlap(sup) > 0 ) {
            // Nos traemos FN y FT
            fn = getFN(p, sup, s);
            ft = getFT(p, sup, s);

            // Calculamos las fuerzas totales en X e Y ...
            double xDiff = sup.x - p.x;
            double yDiff = sup.y - p.y;
            double distance = Math.sqrt(xDiff*xDiff + yDiff*yDiff);
            double enx = xDiff/distance;
            double eny = yDiff/distance;

            fxT += fn * enx + ft * (-eny);
            fyT += fn * eny + ft * enx;
        }

        if( ( p.x <= s.floor || p.x >= s.w - s.floor ) && p.calculateOverlap(inf) > 0 ) {

            // Nos traemos FN y FT
            fn = getFN(p, inf, s);
            ft = getFT(p, inf, s);

            // Calculamos las fuerzas totales en X e Y ...
            double xDiff = inf.x - p.x;
            double yDiff = inf.y - p.y;
            double distance = Math.sqrt(xDiff*xDiff + yDiff*yDiff);
            double enx = xDiff/distance;
            double eny = yDiff/distance;

            fxT += fn * enx + ft * (-eny);
            fyT += fn * eny + ft * enx;
        }

        if( p.calculateOverlap(izq) > 0 ) {

            // Nos traemos FN y FT
            fn = getFN(p, izq, s);
            ft = getFT(p, izq, s);

            // Calculamos las fuerzas totales en X e Y ...
            double xDiff = izq.x - p.x;
            double yDiff = izq.y - p.y;
            double distance = Math.sqrt(xDiff*xDiff + yDiff*yDiff);
            double enx = xDiff/distance;
            double eny = yDiff/distance;

            fxT += fn * enx + ft * (-eny);
            fyT += fn * eny + ft * enx;
        }

        if( p.calculateOverlap(der) > 0 ) {
            // Nos traemos FN y FT
            fn = getFN(p, der, s);
            ft = getFT(p, der, s);

            // Calculamos las fuerzas totales en X e Y ...
            double xDiff = der.x - p.x;
            double yDiff = der.y - p.y;
            double distance = Math.sqrt(xDiff*xDiff + yDiff*yDiff);
            double enx = xDiff/distance;
            double eny = yDiff/distance;

            fxT += fn * enx + ft * (-eny);
            fyT += fn * eny + ft * enx;
        }
        fyT -= 9.8 * (p.m);
        return new Pair<Double,Double>(fxT, fyT);
    }
    
}
