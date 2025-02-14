import org.json.JSONObject;
import org.json.JSONTokener;
import java.math.BigInteger;
import java.util.*;

public class ShamirSecretSharing {
    
    // Function to decode a value from its given base
    private static BigInteger decodeValue(int base, String value) {
        return new BigInteger(value, base);
    }

    // Function to compute the Lagrange interpolation and get the constant term
    private static BigInteger lagrangeInterpolation(List<int[]> points) {
        int k = points.size();
        BigInteger secret = BigInteger.ZERO;

        for (int i = 0; i < k; i++) {
            int xi = points.get(i)[0];
            BigInteger yi = BigInteger.valueOf(points.get(i)[1]);

            BigInteger term = yi;
            BigInteger denominator = BigInteger.ONE;
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    int xj = points.get(j)[0];
                    term = term.multiply(BigInteger.valueOf(-xj));
                    denominator = denominator.multiply(BigInteger.valueOf(xi - xj));
                }
            }
            term = term.divide(denominator);  // Divide term by denominator
            secret = secret.add(term);
        }
        
        return secret;
    }

    // Function to read JSON input, decode y values, and find the secret constant
    public static BigInteger findSecret(JSONObject jsonData) {
        int n = jsonData.getJSONObject("keys").getInt("n");
        int k = jsonData.getJSONObject("keys").getInt("k");
        List<int[]> points = new ArrayList<>();

        for (String key : jsonData.keySet()) {
            if (key.equals("keys")) continue;

            int x = Integer.parseInt(key);
            JSONObject valueObj = jsonData.getJSONObject(key);
            int base = valueObj.getInt("base");
            BigInteger y = decodeValue(base, valueObj.getString("value"));

            points.add(new int[]{x, y.intValue()});
        }

        points.sort(Comparator.comparingInt(p -> p[0])); // Sort points by x
        return lagrangeInterpolation(points.subList(0, k));
    }

    public static void main(String[] args) {
        String testcase1 = """
        {
            "keys": {
                "n": 4,
                "k": 3
            },
            "1": {
                "base": "10",
                "value": "4"
            },
            "2": {
                "base": "2",
                "value": "111"
            },
            "3": {
                "base": "10",
                "value": "12"
            },
            "6": {
                "base": "4",
                "value": "213"
            }
        }
        """;

        String testcase2 = """
        {
            "keys": {
                "n": 10,
                "k": 7
            },
            "1": {
                "base": "6",
                "value": "13444211440455345511"
            },
            "2": {
                "base": "15",
                "value": "aed7015a346d63"
            },
            "3": {
                "base": "15",
                "value": "6aeeb69631c227c"
            },
            "4": {
                "base": "16",
                "value": "e1b5e05623d881f"
            },
            "5": {
                "base": "8",
                "value": "316034514573652620673"
            },
            "6": {
                "base": "3",
                "value": "2122212201122002221120200210011020220200"
            },
            "7": {
                "base": "3",
                "value": "20120221122211000100210021102001201112121"
            },
            "8": {
                "base": "6",
                "value": "20220554335330240002224253"
            },
            "9": {
                "base": "12",
                "value": "45153788322a1255483"
            },
            "10": {
                "base": "7",
                "value": "1101613130313526312514143"
            }
        }
        """;

        JSONObject json1 = new JSONObject(new JSONTokener(testcase1));
        JSONObject json2 = new JSONObject(new JSONTokener(testcase2));

        System.out.println("Secret for Test Case 1: " + findSecret(json1));
        System.out.println("Secret for Test Case 2: " + findSecret(json2));
    }
}
