import java.util.*;

public class InMemoryDB {

    // Main database: variable -> value
    private Map<String, String> db = new HashMap<>();
    // Value counts: value -> frequency
    private Map<String, Integer> valueCount = new HashMap<>();
    // Transaction stack
    private Stack<List<Change>> transactions = new Stack<>(); //transactions are ordered lifo

    // Record of a change (for rollback)
    private static class Change {
        String key, oldValue, newValue;
        Change(String key, String oldValue, String newValue) {
            this.key = key;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }
    }

    // ---------- DATA COMMANDS ----------
    public void set(String key, String value) {
        String oldValue = db.get(key);

        // Log change if inside transaction
        if (!transactions.isEmpty()) {
            transactions.peek().add(new Change(key, oldValue, value));
        }

        // Update db and counts
        if (oldValue != null) {
            decrementCount(oldValue);
        }
        db.put(key, value);
        incrementCount(value);
    }

    public String get(String key) {
        return db.getOrDefault(key, "NULL");
    }

    public void delete(String key) {
        String oldValue = db.get(key);
        if (oldValue == null) return;

        // Log change
        if (!transactions.isEmpty()) {
            transactions.peek().add(new Change(key, oldValue, null));
        }

        db.remove(key);
        decrementCount(oldValue);
    }

    public int count(String value) {
        return valueCount.getOrDefault(value, 0);
    }

    // ---------- TRANSACTION COMMANDS ----------
    public void begin() {
        transactions.push(new ArrayList<>());
    }

    public void rollback() {
        if (transactions.isEmpty()) {
            System.out.println("NO TRANSACTION");
            return;
        }
        List<Change> changes = transactions.pop();
        // Undo all changes in reverse order
        for (int i = changes.size() - 1; i >= 0; i--) {
            Change c = changes.get(i);
            if (c.oldValue == null) {
                // Variable did not exist before
                db.remove(c.key);
                decrementCount(c.newValue);
            } else {
                // Restore old value
                if (c.newValue != null) decrementCount(c.newValue);
                db.put(c.key, c.oldValue);
                incrementCount(c.oldValue);
            }
        }
    }

    public void commit() {
        if (transactions.isEmpty()) {
            System.out.println("NO TRANSACTION");
            return;
        }
        transactions.clear(); // All changes are permanent
    }

    // ---------- HELPERS ----------
    private void incrementCount(String value) {
        valueCount.put(value, valueCount.getOrDefault(value, 0) + 1);
    }

    private void decrementCount(String value) {
        int cnt = valueCount.get(value);
        if (cnt == 1) {
            valueCount.remove(value);
        } else {
            valueCount.put(value, cnt - 1);
        }
    }

    // ---------- INPUT DRIVER ----------
    public static void main(String[] args) {
        InMemoryDB db = new InMemoryDB();
        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String[] cmd = sc.nextLine().trim().split(" ");
            if (cmd.length == 0) continue;

            switch (cmd[0]) {
                case "SET": db.set(cmd[1], cmd[2]); break;
                case "GET": System.out.println(db.get(cmd[1])); break;
                case "DELETE": db.delete(cmd[1]); break;
                case "COUNT": System.out.println(db.count(cmd[1])); break;
                case "BEGIN": db.begin(); break;
                case "ROLLBACK": db.rollback(); break;
                case "COMMIT": db.commit(); break;
                case "END": return; // exit program
                default: System.out.println("Unknown command");
            }
        }
    }

    public class InMemoryDatabaseTest {
        public static void main(String[] args) {
            InMemoryDB db = new InMemoryDB();
    
            // ----------- TEST 1: Basic SET/GET/DELETE/COUNT -----------
            db.set("a", "10");
            assert db.get("a").equals("10");
            db.delete("a");
            assert db.get("a").equals("NULL");
            System.out.print("anjali = " + db.get("a" + "hello" + "NULL"));
    
            db.set("a", "10");
            db.set("b", "10");
            assert db.count("10") == 2;
            db.delete("a");
            assert db.count("10") == 1;
            db.set("b", "30");
            assert db.count("10") == 0;
    
            // ----------- TEST 2: Simple transaction with rollback -----------
            db = new InMemoryDB(); // reset
            db.begin();
            db.set("a", "10");
            assert db.get("a").equals("10");
            db.rollback();
            assert db.get("a").equals("NULL");
    
            // ----------- TEST 3: Nested transactions with rollback -----------
            db = new InMemoryDB(); // reset
            db.begin();
            db.set("a", "10");
            assert db.get("a").equals("10");
    
            db.begin();
            db.set("a", "20");
            assert db.get("a").equals("20");
    
            db.rollback(); // should revert a -> 10
            assert db.get("a").equals("10");
    
            db.rollback(); // should remove a
            assert db.get("a").equals("NULL");
    
            // ----------- TEST 4: Nested transaction with commit -----------
            db = new InMemoryDB(); // reset
            db.begin();
            db.set("a", "30");
    
            db.begin();
            db.set("a", "40");
            db.commit(); // commit both transactions
            assert db.get("a").equals("40");
    
            db.rollback(); // should print NO TRANSACTION
            // no change expected
            assert db.get("a").equals("40");
    
            // ----------- TEST 5: DELETE inside transaction -----------
            db = new InMemoryDB(); // reset
            db.set("a", "50");
            db.begin();
            assert db.get("a").equals("50");
    
            db.set("a", "60");
            db.begin();
            db.delete("a");
            assert db.get("a").equals("NULL");
    
            db.rollback(); // undo delete
            assert db.get("a").equals("60");
    
            db.commit(); // commit outer
            assert db.get("a").equals("60");
    
            // ----------- TEST 6: COUNT with rollback -----------
            db = new InMemoryDB(); // reset
            db.set("a", "10");
            db.begin();
            assert db.count("10") == 1;
    
            db.begin();
            db.delete("a");
            assert db.count("10") == 0;
    
            db.rollback();
            assert db.count("10") == 1;
    
            System.out.println("✅ All tests passed!");
        }
    }    
}
