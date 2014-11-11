/**
 * Created by Scadgek on 11/3/2014.
 */
public class Node {
    private double[] weights;
    private double localError;

    public double[] getWeights() {
        return weights;
    }

    public void setWeights(double[] weights) {
        this.weights = weights;
    }

    public double getLocalError() {
        return localError;
    }

    public void setLocalError(double localError) {
        this.localError = localError;
    }

    public double distanceTo(double[] x) {
        double retVal = 0;
        for (int i = 0; i < x.length; i++) {
            retVal += Math.pow(x[i] - weights[i], 2);
        }
        return Math.sqrt(retVal);
    }
}