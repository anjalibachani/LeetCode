import java.util.*;

public class Anjali {
    public static void main(String args[]){
        Student[] arr = new Student[5];
        arr[0] = new Student(1, "Anjali");
        arr[1] = new Student(2, "Aman");
        arr[2] = new Student(3, "Amit");
        arr[3] = new Student(4, "Anjali");
        arr[4] = new Student(5, "Aman");
        for(int i = 0; i < arr.length; i++){
            System.out.println(arr[i].id + " " + arr[i].name);
        }
    }
}

class Student {
    public int id;
    public String name;

    Student(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
