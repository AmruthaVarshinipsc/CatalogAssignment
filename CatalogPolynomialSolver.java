import java.io.*;
import java.util.*;
import java.math.BigInteger;
import org.json.simple.*;
import org.json.simple.parser.*;

public class CatalogPolynomialSolver {

    // Gaussian elimination to solve Ax = B
    public static double[] solve(double[][] A, double[] B) {
        int n = A.length;
        for (int i = 0; i < n; i++) {
            // Pivot
            int max = i;
            for (int j = i + 1; j < n; j++)
                if (Math.abs(A[j][i]) > Math.abs(A[max][i])) max = j;

            // Swap rows
            double[] temp = A[i]; A[i] = A[max]; A[max] = temp;
            double t = B[i]; B[i] = B[max]; B[max] = t;

            // Normalize pivot row
            double pivot = A[i][i];
            for (int j = i; j < n; j++) A[i][j] /= pivot;
            B[i] /= pivot;

            // Eliminate
            for (int j = 0; j < n; j++) {
                if (j != i) {
                    double factor = A[j][i];
                    for (int k = i; k < n; k++) A[j][k] -= factor * A[i][k];
                    B[j] -= factor * B[i];
                }
            }
        }
        return B;
    }

    public static void main(String[] args) {
        try {
            // Read JSON from file
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(new FileReader("input.json"));

            JSONObject keys = (JSONObject) json.get("keys");
            int n = Integer.parseInt(keys.get("n").toString());
            int k = Integer.parseInt(keys.get("k").toString()); // degree + 1

            List<Integer> xs = new ArrayList<>();
            List<Double> ys = new ArrayList<>();

            for (Object keyObj : json.keySet()) {
                String key = keyObj.toString();
                if (key.equals("keys")) continue;
                int x = Integer.parseInt(key);
                JSONObject valObj = (JSONObject) json.get(key);
                int base = Integer.parseInt(valObj.get("base").toString());
                String valueStr = valObj.get("value").toString();

                // Decode using BigInteger
                BigInteger bigY = new BigInteger(valueStr, base);
                xs.add(x);
                ys.add(bigY.doubleValue()); // convert to double for solving
            }

            // Use only first k points
            double[][] A = new double[k][k];
            double[] B = new double[k];
            for (int i = 0; i < k; i++) {
                int x = xs.get(i);
                double y = ys.get(i);
                for (int j = 0; j < k; j++) {
                    A[i][j] = Math.pow(x, k - j - 1); // highest power first
                }
                B[i] = y;
            }

            double[] coeffs = solve(A, B);
            double c = coeffs[k - 1]; // last coefficient = constant term

            System.out.println((long) Math.round(c));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
