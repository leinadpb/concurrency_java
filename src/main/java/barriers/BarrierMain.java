package barriers;

import barriers.tasks.Friend;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class BarrierMain {

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        CyclicBarrier barrier = new CyclicBarrier(4, () -> System.out.println("Barrier opening... :D"));
        List<Future<String>> futures = new ArrayList<>();

        try {
            // Produce friends
            for(int i = 0; i < 4; i++) {
                Friend friend = new Friend(barrier);
                futures.add(executorService.submit(friend));
            }

            // Get friends results
            futures.forEach(r -> {
                try {
                    System.out.println("Friend result: " + r.get(1, TimeUnit.SECONDS));

                } catch (InterruptedException | ExecutionException e) {

                    System.out.println("Error executing task: " + e.getMessage());

                } catch (TimeoutException e) {

                    r.cancel(true);
                }
            });
        } finally {
            executorService.shutdown();
        }


    }
}
