public class Location {

    private double[] locDim;

    public Location(double[] locDim) {
        this.locDim = locDim;
    }

    public double getLocDimN(int n) {
        return locDim[n];
    }

    public void setLocDimN(double x, int n) {
        this.locDim[n] = x;
    }

    public double[] getLoc() {
        return locDim;
    }
}