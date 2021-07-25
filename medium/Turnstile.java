import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class Turnstile {
    public static int[] getTimes(int[] time, int[] direction) {
        Queue<Integer> enters = new LinkedList<Integer>();
        Queue<Integer> exits = new LinkedList<Integer>();
        int n = time.length;
        for(int i = 0; i < n; i++) {
            Queue<Integer> q = direction[i] == 1 ? exits : enters;
            q.offer(i);
            System.out.println(i + " - " + direction[i]);
            // System.out.println(i)
        }

        int[] result = new int[n];
        int lastTime = -2;
        Queue<Integer> lastQ = exits;
        while(enters.size() > 0 && exits.size() > 0) {
            int currentTime = lastTime + 1;
            int peekEnterTime = time[enters.peek()];
            int peekExitTime = time[exits.peek()];
            Queue<Integer> q;
            if (currentTime < peekEnterTime && currentTime < peekExitTime) {  // case 1: when no one has used the turnstile in the last sec. 
                // The turnstile was not used
                // Take whoever has the earliest time or
                // if enter == exit, take exit
                q = (peekEnterTime < peekExitTime) ? enters : exits;   // either person with less time goes first or if same time then exit goes first
                int personIdx = q.poll();
                result[personIdx] = time[personIdx];
                lastTime = time[personIdx]; // time
                lastQ = q;
            } else {                        // case 2 and 3: when turnstile has been used in the last sec
                // Turnstile was used last second
                if (currentTime >= peekEnterTime && currentTime >= peekExitTime) {      //  case 2: turnstile used, people waiting on both side of entry and exit so choose exit at same time
                    // Have people waiting at both ends
                    // Prioritize last direction
                    q = lastQ;
                } else {                                                                // case 3: turnstile used, but only one person waiting to enter or exit
                    // current >= enters.peek() || current >= exits.peek()
                    q = currentTime >= peekEnterTime ? enters : exits; // take whatever that's queuing
                }
                sint personIdx = q.poll();
                result[personIdx] = currentTime;
                lastTime = currentTime; // time
                lastQ = q;
            }
        }

        Queue<Integer> q = enters.size() > 0 ? enters : exits; // so we dont check for rest of the elements and just add them to the result.
        while(q.size() > 0) {
            int currentTime = lastTime + 1;
            int personIdx = q.poll();
            if (currentTime < time[personIdx]) {
                // The turnstile was not used
                currentTime = time[personIdx];
            }

            result[personIdx] = currentTime;
            lastTime = currentTime; // time
        }

        return result;
    }

    public static void test(int[] time, int[] direction, int[] expected) {
        int[] result = getTimes(time, direction);
        System.out.println("ACTUAL: " + Arrays.toString(result));
        System.out.println("EXPECTED: " + Arrays.toString(expected));
    }

    public static void main(String args[]) {
        System.out.println("Turnstile");
        test(new int[] {0, 0, 1, 5}, new int[] {0,1,1,0}, new int[] {2, 0, 1, 5});
        // test(new int[] {0, 0, 5, 5}, new int[] {0,1,1,0}, new int[] {1, 0, 5, 6});
        // test(new int[] {0, 0, 1, 1}, new int[] {0,1,1,0}, new int[] {2, 0, 1, 3});
        // test(new int[] {0, 0, 0, 0}, new int[] {0,1,1,0}, new int[] {2, 0, 1, 3});
        // test(new int[] {0, 0, 0, 0}, new int[] {0,0,0,0}, new int[] {0, 1, 2, 3});
        // test(new int[] {0, 0, 1, 3, 10}, new int[] {0,1,1,1,0}, new int[] {2, 0, 1, 3, 10});
        // test(new int[] {0, 1, 1, 3, 3}, new int[] {0, 1, 0, 0, 1}, new int[] {0,2,1,4,3});
    }
}