class practice {
    public static int flipByBit(int x, int k) {
        int mask = 1 << k;

        if((x & mask) == 0){
            x = x | mask;
        }
        else {
            x = x & (~mask);
        }
        return x;
    }

    public static void  main(String args[]){
        System.out.println("X = " + flipByBit(5,0));
    }
}

