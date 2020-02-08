package barriers.tasks;

import java.util.Random;
import java.util.concurrent.*;

public class Friend implements Callable<String> {

    private CyclicBarrier barrier;

    public Friend(CyclicBarrier _barrier) {
        barrier = _barrier;
    }

    @Override
    public String call() throws TimeoutException {
        try {
            Random rand = new Random();
            Thread.sleep((rand.nextInt(20)*100 + 100));
            System.out.println("Here.. I was waiting for the others...");

            barrier.await(5, TimeUnit.SECONDS);

            System.out.println("Let's hang out!");

            return "OK";
        } catch (InterruptedException e) {
            System.out.println("Interrupted thread.");
        } catch (BrokenBarrierException e) {
            System.out.println("Error in thread " + Thread.currentThread().getName() +": "+ e.getMessage());
        }

        return "NOT OK";
    }
}
