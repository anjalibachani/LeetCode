import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;
import java.time.*;
// Uncomment below classes to send network request if needed.
// import java.net.HttpURLConnection;
// import java.net.URL;

// https://cloud.box.com/s/s8ofw042qxyakg1ztnxdnqmt5fgqajhe

// class LeakyBucket {
    
//     public int threshold;
//     public int leakRate;
//     Map<String, LeakyBucket> ipBuckets = new HashMap<String, LeakyBucket>();
    
//     private int lastCapacity = 0;
//     private long lastTimestamp = 0L;
    
//     public LeakyBucket(int threshold, int leakRate) {
//         if(threshold <= 0 || leakRate <= 0) {
//             throw new IllegalArgumentException("Invalid threshold or leakRate");
//         }
//         this.threshold = threshold;
//         this.leakRate = leakRate;
//     }
//     /**
//      * Returns true or false depending on whether the request should be allowed through
//      */
//     boolean shouldConsumeRequest() {
//         synchronized(this) {
//             long timestamp = Instant.now().getEpochSecond();
//             int capacity = (int) Math.max(0L, lastCapacity - leakRate * (timestamp - lastTimestamp));
//             if(capacity < threshold) {
//                 lastCapacity = capacity + 1;
//                 lastTimestamp = timestamp;
//                 return true;
//             }
//         return false;
//         }
//     }
    
//     public boolean shouldConsumeRequest(String ipAddress) {
//         if(!ipBuckets.containsKey(ipAddress)) {
//             ipBuckets.put(ipAddress, new LeakyBucket(threshold, leakRate));
//         }
//         return ipBuckets.get(ipAddress).shouldConsumeRequest();
//     }
// }




class Main {
    
//     public static void main(String[] args) {
//         LeakyBucket lb = new LeakyBucket(3,1);
//         System.out.println("Request 1: with ipAddress - " + lb.shouldConsumeRequest("1.1.1") + "(should be true)");
//         System.out.println("Request 2: with ipAddress - " + lb.shouldConsumeRequest("1.1.1") + "(should be true)");
//         System.out.println("Request 3: with ipAddress - " + lb.shouldConsumeRequest("1.1.2") + "(should be true)");
//         System.out.println("Request 4: with ipAddress - " + lb.shouldConsumeRequest("1.1.2") + "(should be true)");
//         System.out.println("Request 5: with ipAddress - " + lb.shouldConsumeRequest("1.1.2") + "(should be true)");
//         System.out.println("Request 6: with ipAddress - " + lb.shouldConsumeRequest("1.1.2") + "(should be false)");
//         // System.out.println("Request 5: " + lb.shouldConsumeRequest() + "(should be false)");
//         // System.out.println("Request 6: " + lb.shouldConsumeRequest() + "(should be false)");
//         // try{
//         //     Thread.sleep(2000);
//         // }
//         // catch(InterruptedException e){
//         //     System.out.println("Sleeping thread for 2 seconds");
//         // }
        
//         // System.out.println("Request 7: " + lb.shouldConsumeRequest() + "(should be true)");
//         // System.out.println("Request 8: " + lb.shouldConsumeRequest() + "(should be true)");
//         // System.out.println("Request 9: " + lb.shouldConsumeRequest() + "(should be false)");
//         // System.out.println("Request 10: " + lb.shouldConsumeRequest() + "(should be false)");
        
        
//     }
    
    public static void main (String argsp[]) {
        System.out.println("Test Case 1:" );
        testPart1();
    }

    
    public static boolean canAllServicesReachEachOther (String[][] rules) {
        if (rules == null || rules.length ==0) {
            return true;
        }
        Map<String, List<String>> graph = buildServiceGraph(rules); 
        
        if(graph.size() <= 1) {
            return true;
        }
        
        Set<String> visited = new HashSet<>();
        String start = rules[0][0];
        bfs(graph, start, visited);        //function to traverse the graph
        
        return visited.size() == graph.size();
    }
    
    private static Map<String, List<String>> buildServiceGraph(String[][] rules) {
        Map<String, List<String>> graph = new HashMap<>();
        for(String[] rule: rules) {
            String service1 = rule[0], service2 = rule[1];
            List<String> neighborA = graph.getOrDefault(service1, new LinkedList<>());
            neighborA.add(service2);
            graph.put(service1, neighborA);
            
            List<String> neighborB = graph.getOrDefault(service2, new LinkedList<>());
            neighborB.add(service1);
            graph.put(service2, neighborB);
        }
        return graph;
    }
    
    private static void bfs(Map<String, List<String>> graph, String start, Set<String> visited) {
        Queue<String> queue = new LinkedList<>();
        queue.add(start);
        visited.add(start);
        
        while(!queue.isEmpty()) {
            String current = queue.poll();
            List<String> neighbors = graph.get(current);
            
            if(neighbors != null) {
                for(String neighbor: neighbors) {
                    if(!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }
        }
    }
    
    public static String[][] getNecessaryConnections(String[][] allPairs) {
        
        if(!canAllServicesReachEachOther(allPairs)){
            return new String[][] {};
        }
        Map<String,List<String>> serviceConnections = buildServiceGraph(allPairs);
        
        String startPoint = allPairs[0][0];
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        List<String[]> necessaryConnections = new ArrayList<>();
        
        queue.add(startPoint);
        visited.add(startPoint);
        while(!queue.isEmpty()) {
            String service = queue.poll();
            for(String nextService: serviceConnections.get(service)) {
                if(!visited.contains(nextService)) {
                    visited.add(nextService);
                    queue.add(nextService);
                    necessaryConnections.add(new String[] {service, nextService});
                }
            }
        }
        return necessaryConnections.toArray(new String[0][0]);
    }
    
    
    public static void testPart1() {
        String[][] rules = {
            {"BoxSign", "Monolith"},
            {"Conversion", "BoxSign"},
            {"Conversion", "Monolith"}
        };
        System.out.println("All Services Connected: " + canAllServicesReachEachOther(rules));
        System.out.println("All Services Connected: " + getNecessaryConnections(rules));
    }
}