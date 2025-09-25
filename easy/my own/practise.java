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

  /* public void printDB() {
    System.out.println("Current DB State:");
    System.out.println("DB: " + db);
    System.out.println("Counts: " + valueCount);
    System.out.println("Transactions: " + printTrancations());
    System.out.println("=====================");
  }

  private String printTrancations() {
    StringBuilder sb = new StringBuilder();
    for (List<Change> changes : transactions) {
      sb.append("[");
      for (Change c : changes) {
        sb.append(String.format("(%s: %s -> %s), ", c.key, c.oldValue, c.newValue));
      }
      if (!changes.isEmpty()) sb.setLength(sb.length() - 2); // Remove last comma
      sb.append("] ");
    }
    return sb.toString().trim();
  } */

// class InMemoryDatabaseTest {

//   // private static void assertEquals(Object expected, Object actual) {
//   //   if (!Objects.equals(expected, actual)) {
//   //     throw new IllegalStateException("Assertion failed. Expected: " + expected + ", Actual: " + actual);
//   //   }
//   }

  public void testBasicSetGetDeleteCount() {
    // Basic SET/GET/DELETE/COUNT operations

    InMemoryDBA db = new InMemoryDBA();
    db.set("a", "10");

    // assertEquals("10", db.get("a"));
    System.out.println("SET a 10");
    System.out.println(db.get("a"));
    // db.printDB();

    db.delete("a");
    // assertEquals("NULL", db.get("a"));
    System.out.println(db.get("a"));
    System.out.println("DELETE a");
    db.printDB();

    db.set("a", "10");
    db.set("b", "10");
    // assertEquals(2, db.count("10"));
    System.out.println(db.count("10"));
    System.out.println("SET a 10");
    db.printDB();
    System.out.println("SET b 10");
    db.printDB();

    db.delete("a");
    // assertEquals(1, db.count("10"));
    System.out.println(db.count("10"));
    System.out.println("DELETE a");
    db.printDB();

    db.set("b", "30");
    assertEquals(0, db.count("10"));
    System.out.println("SET b 30");
    db.printDB();
  }

  // void testSimpleTransactionWithRollback() {
  //   // Simple transaction with rollback
  //   db.begin();
  //   db.set("a", "10");
  //   assertEquals("10", db.get("a"));
  //   System.out.println("BEGIN");
  //   System.out.println("SET a 10");
  //   db.printDB();

  //   db.rollback();
  //   assertEquals("NULL", db.get("a"));
  //   System.out.println("ROLLBACK");
  //   db.printDB();
  // }

  // void testNestedTransactionsWithRollback() {
  //   // Nested transactions with rollback
  //   db.begin();
  //   db.set("a", "10");
  //   assertEquals("10", db.get("a"));
  //   System.out.println("BEGIN");
  //   System.out.println("SET a 10");
  //   db.printDB();

  //   db.begin();
  //   System.out.println("BEGIN");
  //   db.set("a", "20");
  //   assertEquals("20", db.get("a"));
  //   System.out.println("SET a 20");
  //   db.printDB();

  //   db.set("a", "20");
  //   assertEquals("20", db.get("a"));
  //   System.out.println("SET a 20");
  //   db.printDB();

  //   db.rollback(); // should revert a -> 10
  //   assertEquals("10", db.get("a"));
  //   System.out.println("ROLLBACK");
  //   db.printDB();

  //   db.rollback(); // should remove a
  //   assertEquals("NULL", db.get("a"));
  //   System.out.println("ROLLBACK");
  //   db.printDB();
  // }

  // void testNestedTransactionWithCommit() {
  //   // Nested transaction with commit
  //   db.begin();
  //   db.set("a", "30");
  //   System.out.println("SET a 30");
  //   db.printDB();

  //   db.begin();
  //   db.set("a", "40");
  //   System.out.println("SET a 40");
  //   db.printDB();
  //   System.out.println("COMMIT");
  //   db.printDB();
  //   db.commit(); // commit both transactions
  //   assertEquals("40", db.get("a"));
  //   System.out.println("COMMIT");
  //   db.printDB();

  //   db.rollback(); // should print NO TRANSACTION
  //   // no change expected
  //   assertEquals("40", db.get("a"));
  //   System.out.println("ROLLBACK");
  //   db.printDB();
  // }

  // void testDeleteInsideTransaction() {
  //   // DELETE inside transaction
  //   db.set("a", "50");

  //   db.begin();
  //   assertEquals("50", db.get("a"));
  //   db.set("a", "60");
  //   System.out.println("SET a 60");
  //   db.printDB();

  //   db.begin();
  //   db.delete("a");
  //   assertEquals("NULL", db.get("a"));
  //   System.out.println("DELETE a");
  //   db.printDB();
  //   db.rollback(); // undo delete
  //   assertEquals("60", db.get("a"));
  //   System.out.println("ROLLBACK");
  //   db.printDB();
  //   db.commit(); // commit outer
  //   assertEquals("60", db.get("a"));
  // }

  // void testCountWithRollback() {
  //   // COUNT with rollback
  //   db.set("a", "10");

  //   db.begin();
  //   assertEquals(1, db.count("10"));
  //   System.out.println("BEGIN");
  //   db.printDB();
  //   System.out.println("SET a 10");
  //   db.printDB();
  //   db.begin();
  //   db.delete("a");
  //   assertEquals(0, db.count("10"));
  //   System.out.println("DELETE a");
  //   db.printDB();
  //   db.rollback();
  //   assertEquals(1, db.count("10"));
  //   System.out.println("ROLLBACK");
  //   db.printDB();
  // }
// }

// class InMemoryDBATestRunner {
  public static void main(String[] args) {
    InMemoryDBA t = new InMemoryDBA();
    t.testBasicSetGetDeleteCount();
    // t.setUp();
    // t.testSimpleTransactionWithRollback();
    // t.setUp();
    // t.testNestedTransactionsWithRollback();
    // t.setUp();
    // t.testNestedTransactionWithCommit();
    // t.setUp();
    // t.testDeleteInsideTransaction();
    // t.setUp();
    // t.testCountWithRollback();
    System.out.println("All tests passed.");
  }
}