import json
from sympy import symbols, expand

# Function to decode y-values from given base to decimal
def decode_value(base, value):
    return int(value, base)

# Function to perform Lagrange interpolation and find the constant term
def lagrange_interpolation(points):
    x = symbols('x')
    polynomial = 0
    
    for i in range(len(points)):
        xi, yi = points[i]
        term = yi
        
        for j in range(len(points)):
            if i != j:
                xj, _ = points[j]
                term *= (x - xj) / (xi - xj)
        
        polynomial += term
    
    expanded_poly = expand(polynomial)
    return expanded_poly

# Function to process JSON input and find the secret constant term
def find_secret(json_data):
    keys = json_data["keys"]
    n, k = keys["n"], keys["k"]
    points = []
    
    for key, value in json_data.items():
        if key == "keys":
            continue
        
        x = int(key)  # Convert key to integer (x-value)
        y = decode_value(int(value["base"]), value["value"])  # Decode y-value
        points.append((x, y))
    
    points = sorted(points)[:k]  # Select first k points for interpolation
    polynomial = lagrange_interpolation(points)
    
    secret = polynomial.subs(symbols('x'), 0)  # Find constant term (c)
    return int(secret)

# Given test cases as JSON strings
testcase1_json = """
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
"""

testcase2_json = """
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
"""

# Load JSON data
testcase1 = json.loads(testcase1_json)
testcase2 = json.loads(testcase2_json)

# Compute the secrets
secret1 = find_secret(testcase1)
secret2 = find_secret(testcase2)

print(f"Secret for Test Case 1: {secret1}")
print(f"Secret for Test Case 2: {secret2}")
