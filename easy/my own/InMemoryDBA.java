import java.util.*;

public class InMemoryDBA {

  // Main database: variable -> value
  private Map<String, String> db = new HashMap<>();
  // Value counts: value -> frequency
  private Map<String, Integer> valueCount = new HashMap<>();
  // Transaction stack
  private Stack<List<Change>> transactions = new Stack<>();

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

  public static void main(String[] args) {
    InMemoryDBA t = new InMemoryDBA();
    t.testBasicSetGetDeleteCount();
    System.out.println("\n--- Test 2: Simple Transaction with Rollback ---");
    t.testSimpleTransactionWithRollback();
    System.out.println("\n--- Test 3: Nested Transactions with Rollback ---");
    t.testNestedTransactionsWithRollback();
    System.out.println("\n--- Test 4: Nested Transaction with Commit ---");
    t.testNestedTransactionWithCommit();
    System.out.println("\n--- Test 5: Delete Inside Transaction ---");
    t.testDeleteInsideTransaction();
    System.out.println("\n--- Test 6: Count with Rollback ---");
    t.testCountWithRollback();
    System.out.println("\nAll tests completed successfully!");
  }

  public void testBasicSetGetDeleteCount() {
    // Basic SET/GET/DELETE/COUNT operations

    InMemoryDBA db = new InMemoryDBA();
    db.set("a", "10");

    System.out.println("SET a 10");
    System.out.println(db.get("a"));

    db.delete("a");
    System.out.println("DELETE a");
    System.out.println(db.get("a"));

    db.set("a", "10");
    db.set("b", "10");
    System.out.println("SET a 10");
    System.out.println(db.get("a"));
    System.out.println("SET b 10");
    System.out.println(db.get("b"));

    db.delete("a");
    System.out.println("DELETE a");
    System.out.println(db.get("a"));

    db.set("b", "30");
    System.out.println("SET b 30");
    System.out.println(db.get("b"));
  }

  public void testSimpleTransactionWithRollback() {
    // Simple transaction with rollback
    InMemoryDBA db = new InMemoryDBA();
    db.begin();
    db.set("a", "10");
    System.out.println("BEGIN");
    System.out.println("SET a 10");
    System.out.println(db.get("a"));

    db.rollback();
    System.out.println("ROLLBACK");
    System.out.println(db.get("a"));
  }

  public void testNestedTransactionsWithRollback() {
    // Nested transactions with rollback
    InMemoryDBA db = new InMemoryDBA();
    db.begin();
    db.set("a", "10");
    System.out.println("BEGIN");
    System.out.println("SET a 10");
    System.out.println(db.get("a"));

    db.begin();
    System.out.println("BEGIN");
    db.set("a", "20");
    System.out.println("SET a 20");
    System.out.println(db.get("a"));

    db.set("a", "20");
    System.out.println("SET a 20");
    System.out.println(db.get("a"));

    db.rollback(); // should revert a -> 10
    System.out.println("ROLLBACK");
    System.out.println(db.get("a"));

    db.rollback(); // should remove a
    System.out.println("ROLLBACK");
    System.out.println(db.get("a"));
  }

  public void testNestedTransactionWithCommit() {
    // Nested transaction with commit
    InMemoryDBA db = new InMemoryDBA();
    db.begin();
    db.set("a", "30");
    System.out.println("SET a 30");
    System.out.println(db.get("a"));

    db.begin();
    db.set("a", "40");
    System.out.println("SET a 40");
    System.out.println(db.get("a"));
    
    System.out.println("COMMIT");
    db.commit(); // commit both transactions
    System.out.println("COMMIT");
    System.out.println(db.get("a"));

    db.rollback(); // should print NO TRANSACTION
    // no change expected
    System.out.println("ROLLBACK");
    System.out.println(db.get("a"));
  }

  public void testDeleteInsideTransaction() {
    // DELETE inside transaction
    InMemoryDBA db = new InMemoryDBA();
    db.set("a", "50");
    System.out.println("SET a 50");
    System.out.println(db.get("a"));

    db.begin();
    db.set("a", "60");
    System.out.println("SET a 60");
    System.out.println(db.get("a"));

    db.begin();
    db.delete("a");
    System.out.println("DELETE a");
    System.out.println(db.get("a"));
    
    db.rollback(); // undo delete
    System.out.println("ROLLBACK");
    System.out.println(db.get("a"));
    
    db.commit(); // commit outer
    System.out.println("COMMIT");
    System.out.println(db.get("a"));
  }

  public void testCountWithRollback() {
    // COUNT with rollback
    InMemoryDBA db = new InMemoryDBA();
    db.set("a", "10");
    System.out.println("SET a 10");
    System.out.println(db.get("a"));

    db.begin();
    System.out.println("BEGIN");
    System.out.println("Count of 10: " + db.count("10"));
    
    db.begin();
    db.delete("a");
    System.out.println("DELETE a");
    System.out.println("Count of 10: " + db.count("10"));
    
    db.rollback();
    System.out.println("ROLLBACK");
    System.out.println("Count of 10: " + db.count("10"));
  }

  
}