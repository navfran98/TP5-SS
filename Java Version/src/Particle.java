public class Particle {
    
    // Variables
    double x;
    double y;
    double vx;
    double vy;
    double r;
    double m;

    // Constructor
    public Particle (double x, double y, double vx, double vy, double r, double m) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.r = r;
        this.m = m;
    }

    // Metodos
    public double getV() { return Math.sqrt(vx*vx + vy*vy); }

    public double getD(Particle other) { return Math.sqrt((x-other.x)*(x-other.x) + (y-other.y)*(y-other.y)); }

    public Pair<Double, Double> getRV(Particle other) {
        double nvx = this.vx - other.vx;
        double nvy = this.vy - other.vy;
        Pair<Double, Double> ret = new Pair<Double,Double>(nvx, nvy);
        return ret;
    }

    public double calculateOverlap(Particle other) {
        double xDiff = x - other.x;
        double yDiff = y - other.y;
        return r + other.r - Math.sqrt( xDiff*xDiff + yDiff*yDiff );
    }

}
