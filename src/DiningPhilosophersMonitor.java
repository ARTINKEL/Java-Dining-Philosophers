import java.util.Arrays;
import java.util.Random;

public class DiningPhilosophersMonitor {

    public static void main(String args[]) {
        Philosopher[] philosophers = new Philosopher[5];
        Monitor monitors = new Monitor(5);

        for (int i = 0; i < 5; i++) {
            philosophers[i] = new Philosopher(i, monitors);
            new Thread(philosophers[i]).start();
        }
    }
}

class Philosopher implements Runnable {
    private Random random = new Random();
    private int id;
    private Monitor monitor;

    Philosopher(int id, Monitor monitor) {
        this.id = id;
        this.monitor = monitor;
    }

    @Override
    public void run() {
        try {
            try {
                while (monitor.getEats() < 500) {
                    think();
                    monitor.pickUpFork(id);
                    eat();
                    monitor.putDownFork(id);
                }
            } catch (InterruptedException ignored) {}
        } finally {
            System.out.println("Number of eats for each philosopher: " + Arrays.toString(monitor.getEatsArray()));
        }
    }

    private void think() throws InterruptedException {
        System.out.println("Philosopher " + id + " thinking...");
        Thread.sleep(random.nextInt(3));
    }

    private void eat() throws InterruptedException {
        Thread.sleep(random.nextInt(3));
    }
}

class Monitor {
    private enum State { THINKING, HUNGRY, EATING }
    private State[] states;
    private int eats;
    private int[] eatsArray = new int[5];

    public Monitor(int x) {
        states = new State[x];
        for (int i = 0; i < x; i++) {
            states[i] = State.THINKING;
        }
    }

    public synchronized void pickUpFork(int id) throws InterruptedException {
        states[id] = State.HUNGRY;
        System.out.println("Philosopher " + id + " is hungry...");
        while(leftRightEating(id)) {
            wait();
        }
        states[id] = State.EATING;
        System.out.println("Philosopher " + id + " eating...");
        eats++;
        eatsArray[id]++;
    }

    public synchronized void putDownFork(int id) {
        states[id] = State.THINKING;
        notifyAll();
    }

    private boolean leftRightEating(int id) {
        return states[(id + 1) % 5] == State.EATING || states[(id + 4) % 5] == State.EATING;
    }

    public int getEats() {
        return eats;
    }

    public int[] getEatsArray() {
        return eatsArray;
    }
}