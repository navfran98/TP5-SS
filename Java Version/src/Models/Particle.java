package Models;

public class Particle {

    // Variables
    double x;
    double y;
    double vx;
    double vy;
    double radius;
    double mass;

    // Constructor
    public Particle( double x, double y, double vx, double vy, double radius, double mass ) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.radius = radius;
        this.mass = mass;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRadius() {
        return radius;
    }

    // Metodos
    public double getVelocity() {
        return Math.sqrt(this.vx * this.vx + this.vy * this.vy);
    }

    public Pair<Double, Double> getRelativeVelocity(Particle other) {
        Pair<Double, Double> res = new Pair<>(this.vx - other.vx, this.vy - other.vy);
        return res;
    }

    public double getOverlap(Particle other) {
        double xDiff = this.x - other.x;
        double yDiff = this.y - other.y;
        double distance = Math.sqrt(xDiff*xDiff + yDiff*yDiff);
        return this.radius + other.radius - distance;
    }

    public Pair<Pair<Double, Double>, Pair<Double, Double>> getVersors(Particle other){
        double term1 = this.x - other.x;
        double term2 = this.y - other.y;

        double divider = Math.sqrt(term1 * term1 + term2 * term2);

        double enx = (other.x - this.x)/divider;
        double eny = (other.y - this.y)/divider;

        Pair<Double, Double> p1 = new Pair<>(enx, eny);
        Pair<Double, Double> p2 = new Pair<>(-eny, enx);

        Pair<Pair<Double, Double>, Pair<Double, Double>> ret = new Pair<>(p1, p2);
        return ret;
    }

    // Overriding equals() to compare two Complex objects
    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true 
        if (o == this) { return true; }
 
        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Particle)) { return false; }

        // typecast o to Complex so that we can compare data members
        Particle p = (Particle) o;

        // Compare the data members and return accordingly
        return Double.compare(this.x, p.x) == 0
                && Double.compare(this.y, p.y) == 0
                && Double.compare(this.vx, p.vx) == 0
                && Double.compare(this.vy, p.vy) == 0
                && Double.compare(this.radius, p.radius) == 0
                && Double.compare(this.mass, p.mass) == 0;
    }
}
