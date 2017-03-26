/*
Credit: https://gandhim.wordpress.com/2010/04/04/particle-swarm-optimization-pso-sample-code-using-java/
*/

public class PSOUtility {
    public static int getMaxPos(double[] list) {
        int pos = 0;
        double maxValue = list[0];

        for(int i=0; i<list.length; i++) {
            if(list[i] > maxValue) {
                pos = i;
                maxValue = list[i];
            }
        }

        return pos;
    }
}