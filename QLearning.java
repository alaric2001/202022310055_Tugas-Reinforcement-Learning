/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rl;

/**
 *
 * @author Alaric
 */
import java.text.DecimalFormat;
import java.util.Random;

public class QLearning {
    final DecimalFormat df = new DecimalFormat("#.##"); //deklarasi df

    final double alpha = 0.1; //Mendeklarasikan nilai alpha, yang merupakan faktor pembelajaran pada algoritma Q-learning
    final double gamma = 0.9; //deklarasi faktor diskon pada algoritma Q-learning

    // Definisikan indeks keadaan untuk setiap kotak
    final int stateA = 0;
    final int stateB = 1;
    final int stateC = 2;
    final int stateD = 3;
    final int stateE = 4;
    final int stateF = 5;
    final int stateG = 6;
    final int stateH = 7;
    final int stateI = 8;
    final int stateJ = 9;
    final int stateK = 10;
    final int stateL = 11;

    // Jumlah total keadaan
    final int statesCount = 12;

    // Array yang berisi indeks keadaan untuk setiap kotak
    final int[] states = new int[]{stateA, stateB, stateC, stateD, stateE, stateF, stateG, stateH, stateI, stateJ, stateK, stateL};

    // Matriks yang menunjukkan reward untuk setiap transisi dari satu keadaan ke keadaan lainnya
    int[][] R = new int[statesCount][statesCount]; // Reward lookup

    // Matriks Q yang akan dipelajari oleh algoritma Q-learning
    double[][] Q = new double[statesCount][statesCount]; // Q learning

    // Array yang menunjukkan tindakan yang mungkin dari setiap keadaan
    int[][] actions = new int[][]{
            {stateB, stateE},      // A
            {stateA, stateC, stateF}, // B
            {stateB, stateD, stateG}, // C
            {stateC},               // D (goal)
            {stateA, stateF, stateI}, // E
            {},                     // F (tidak dapat dilalui)
            {stateC, stateG, stateK}, // G
            {},                     // H (tidak dapat dilalui)
            {stateE, stateI},      // I
            {stateJ, stateE, stateK}, // J
            {stateG, stateL, stateJ}, // K
            {stateH, stateK}        // L
    };

    // Array yang berisi nama kotak untuk keperluan output
    String[] stateNames = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"};

    public QLearning() { //konstruktor dari kelas QLearning
        init();
    }

    public void init() {
        // Inisialisasi matriks reward
        for (int i = 0; i < statesCount; i++) {
            for (int j = 0; j < statesCount; j++) {
                // Kotak H dan F tidak dapat dilalui
                if (i == stateH || i == stateF || j == stateH || j == stateF) {
                    R[i][j] = -4; // Jika mencoba untuk melintasinya, rewardnya sangat rendah
                } else if (j == stateD) {
                    R[i][j] = 1; // Kotak D adalah tujuan, rewardnya tinggi
                } else {
                    R[i][j] = 0; // Selain itu, reward adalah 0
                }
            }
        }
    }

    //Metode ini bertujuan untuk memeriksa apakah key terdapat dalam array array
    boolean contains(int[] array, int key) {
        for (int i : array) { //loop foreach
            if (i == key) { //Jika nilai i sama dengan key, itu berarti key terdapat dalam array array
                return true;
            }
        }
        return false; // jika tidak ada maka return false
    }

    public static void main(String[] args) {
        long BEGIN = System.currentTimeMillis(); //menyimpan waktu awal eksekusi program.

        QLearning obj = new QLearning(); //// Membuat objek QLearning

        obj.run(); // Memulai proses Q-learning
        obj.printResult(); // Mencetak hasil Q-learning
        obj.showPolicy(); // Menampilkan kebijakan yang dipelajari

        long END = System.currentTimeMillis(); //menyimpan waktu akhir eksekusi program.
        // Menghitung dan mencetak waktu yang diperlukan untuk menjalankan program
        System.out.println("Time: " + (END - BEGIN) / 1000.0 + " sec.");
    }

    void run() { //Method untuk menjalankan proses pembelajaran Q-learning
        Random rand = new Random(); //Menghasilkan bilangan acak
        for (int i = 0; i < 1000; i++) {// train episodes pembelajaran
            int state = rand.nextInt(statesCount); //inisialisasi variabel state dengan sebuah nilai acak
            while (state != stateD) { // loop yang terus berjalan sampai mencapai goal
                int[] actionsFromState = actions[state]; //mendapatkan array tindakan yang mungkin dari keadaan saat ini (state) dari matriks actions
                // Cek apakah ada tindakan yang mungkin dari keadaan saat ini
                if (actionsFromState.length == 0) {
                    break; // Jika tidak ada tindakan yang mungkin, keluar dari loop
                }
                //pembuatan indeks acak untuk memilih tindakan dari array
                int index = rand.nextInt(actionsFromState.length);
                int action = actionsFromState[index]; //pemilihan tindakan acak dari array
                int nextState = action; //menetapkan nilai action sebagai keadaan berikutnya (nextState)
                double q = Q(state, action); //mendapatkan nilai Q dari keadaan saat ini dan tindakan yang dipilih
                double maxQ = maxQ(nextState); //mendapatkan nilai maksimum Q dari keadaan berikutnya (nextState)
                // Mendapatkan reward untuk pasangan keadaan-tindakan saat ini
                int r = R(state, action);
                // Menghitung nilai Q baru berdasarkan formula Q-learning
                double value = q + alpha * (r + gamma * maxQ - q);
                // Memperbarui nilai Q untuk pasangan keadaan-tindakan saat ini
                setQ(state, action, value);
                // Memperbarui keadaan saat ini ke keadaan berikutnya
                state = nextState;
            }
        }
    }

    double maxQ(int s) { //mencari nilai maksimum dari semua nilai Q yang mungkin dari keadaan s
        int[] actionsFromState = actions[s]; //mendapatkan daftar tindakan yang mungkin dari keadaan s dari array actions
        //Nilai awal maxValue diatur sangat kecil untuk memastikan setiap nilai Q yang lebih besar akan menggantikannya
        double maxValue = Double.MIN_VALUE;
        //Looping setiap keadaan yang mungkin dari keadaan state
        for (int nextState : actionsFromState) {
            double value = Q[s][nextState]; //mendapatkan nilai Q dari matriks Q untuk keadaan-tindakan saat ini
            if (value > maxValue) //memeriksa apakah nilai Q saat ini lebih besar dari maxValue
                maxValue = value; //Jika nilai Q saat ini lebih besar dari maxValue, maka maxValue akan diperbarui dengan nilai Q
        }
        return maxValue; //kembalikan nilai maksimum dari semua nilai Q
    }

    int policy(int state) { //menentukan kebijakan (policy) dari suatu keadaan state
        int[] actionsFromState = actions[state]; //mendapatkan daftar tindakan yang mungkin dari keadaan state dari array actions
        //Nilai awal maxValue diatur sangat kecil untuk memastikan setiap nilai Q yang lebih besar akan menggantikannya
        double maxValue = Double.MIN_VALUE;
        //mengatur keadaan default yang akan dipilih jika tidak ada keadaan lain yang memiliki nilai Q yang lebih besar
        int policyGotoState = state; 
        for (int nextState : actionsFromState) { //Looping setiap keadaan yang mungkin berikutnya dari keadaan state
            double value = Q[state][nextState]; //mendapatkan nilai Q dari matriks Q untuk keadaan-tindakan saat ini
            if (value > maxValue) {
                //maxValue akan diperbarui dengan nilai Q saat ini dan policyGotoState akan diatur ke keadaan saat ini
                maxValue = value;
                policyGotoState = nextState;
            }
        }
        return policyGotoState;
    }

    double Q(int s, int a) { //metode getter untuk mendapatkan nilai Q
        return Q[s][a];
    }

    //metode setter untuk mengatur nilai Q untuk keadaan-tindakan tertentu (s, a) dengan nilai value
    void setQ(int s, int a, double value) {
        Q[s][a] = value;
    }

    //metode getter untuk mendapatkan nilai reward untuk pasangan keadaan-tindakan tertentu (s, a)
    int R(int s, int a) {
        return R[s][a];
    }

    void printResult() { //Metode untuk mencetak hasil dari pembelajaran Q
        System.out.println("Print result");
        for (int i = 0; i < Q.length; i++) { //loop untuk setiap baris dalam matriks Q
            System.out.print("out from " + stateNames[i] + ": ");
            for (int j = 0; j < Q[i].length; j++) { //loop untuk setiap baris dalam matriks Q
            //Mencetak nilai Q untuk keadaan-tindakan saat ini dalam format yang diformat menggunakan objek DecimalFormat df
                System.out.print(df.format(Q[i][j]) + " ");
            }
            System.out.println(); //Mencetak baris baru
        }
    }

    //Metode ini menampilkan kebijakan yang dipelajari setelah pembelajaran Q
    void showPolicy() { //
        System.out.println("\nIn show policy");
        for (int i = 0; i < states.length; i++) { //loop untuk setiap keadaan dalam array states
            int from = states[i]; //Menetapkan nilai keadaan saat ini sebagai keadaan asal (from)
            int to = policy(from); //Mendapatkan keadaan tujuan (to) dengan memanggil metode policy(from)
            //Mencetak kebijakan dari keadaan asal ke keadaan tujuan"
            System.out.println("from " + stateNames[from] + " goto " + stateNames[to]);
        }
    }
}
