import java.util.*;;

public class hashMapExamples {
    public static void main(String[] args) {
        // Example 1: Basic HashMap operations
        Map<String, Integer> map = new HashMap<>();
        map.put("one", 1);
        map.put("two", 2);
        System.out.println("Value for 'one': " + map.get("one")); // Output: 1
        System.out.println("Map size: " + map.size()); // Output: 2
        map.remove("two");
        System.out.println("Map size after removal: " + map.size()); // Output: 1

        // Example 2: Iterating over a HashMap
        // Using Map.entrySet() to get key-value pairs
        map.put("three", 3);
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        // Using Map.keySet() to get keys
        for (String key : map.keySet()) {
            System.out.println("Key: " + key + ", Value: " + map.get(key));
        }
        
        // Example 3: Checking for existence of a key
        if (map.containsKey("one")) {
            System.out.println("'one' exists in the map.");
        } else {
            System.out.println("'one' does not exist in the map.");
        }

        // Example 4: Using getOrDefault
        int value = map.getOrDefault("four", 0); // Returns 0 if "four" is not found
        System.out.println("Value for 'four': " + value); // Output: 0

        // Example 5: Adding to map using getOrDefault
        map.put("five", map.getOrDefault("five", 0) + 1);
        System.out.println("Value for 'five': " + map.get("five")); // Output: 1

    }
}
