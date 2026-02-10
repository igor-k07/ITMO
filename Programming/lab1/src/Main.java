public class Main {
    public static void main(String[] args) {
        long[] w = new long[6];
        int indx = 0;
        for (int i = 17; i>=7; i -=2){
            w[indx] = i;
            indx++;
        }

        double[] x = new double[16];
        for (int i = 0; i < x.length; i++){
            x[i] = Math.random() * (11.0 + 2.0) - 2.0;
        }

        double[][] s = new double[6][16];
        for (int i = 0; i < 6; i++){
            for (int j = 0; j < 16; j++){
                s[i][j] = elemCount(w[i], x[j]);
            }
        }
        printMatrix(s);
        System.out.printf("%o", 16737);

    }
    public static void f(Object... args){

    }
    public static double elemCount(long a, double b){
        double res;
        switch ((int) a){
            case 13:
                res = Math.pow(Math.pow(3. / (Math.pow(b, b/3./4.) +
                        1.0), 2), 1./3);
                break;
            case 9, 11, 17:
                res = Math.atan(Math.cos(Math.tan(Math.cos(b))));
                break;
            default:
                res = Math.exp(Math.pow(Math.pow(Math.sin(b), 2),
                        Math.sin(Math.atan((b + 4.5) / 13.)) + Math.PI));
        }
        return res;
    }

    public static void printMatrix(double[][] m){
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 16; j++) {
                System.out.printf("%7.4f", m[i][j]);
            }
            System.out.println();
        }
    }

}
