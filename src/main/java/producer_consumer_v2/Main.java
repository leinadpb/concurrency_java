package producer_consumer_v2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {


    /**
     * Producer Consumer implementation using Concurrent Queues.
     * @param args
     * @throws InterruptedException
     */

    public static void main(String[] args) throws InterruptedException {

        BlockingQueue<String> queue = new ArrayBlockingQueue<>(50);

        List<Callable<String>> producersAndConsumers = new ArrayList<>();

        for(int i = 0; i < 2; i ++) {
            producersAndConsumers.add(new Producer(queue));
        }

        for(int i = 0; i < 2; i ++) {
            producersAndConsumers.add(new Consumer(queue));
        }

        System.out.println("Producers and consumer launched!");

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        try {
            List<Future<String>> futures = executorService.invokeAll(producersAndConsumers);

            futures.forEach(f -> {
               try {
                   System.out.println(f.get());

               } catch (InterruptedException | ExecutionException e) {
                   e.printStackTrace();
               }
            });

        } finally {
            executorService.shutdown();
        }
    }
}
