package CASing;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class RaceCondition {

    private static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) {

        class Increment implements Runnable {
            @Override
            public void run() {
               for(int i = 0; i < 1000000; i++) {
                   counter.incrementAndGet();
               }
            }
        }

        class Decrement implements Runnable {
            @Override
            public void run() {
                for(int i = 0; i < 1000000; i++) {
                    counter.decrementAndGet();
                }
            }
        }

        ExecutorService executorService = Executors.newFixedThreadPool(100);
        List<Future<?>> futures = new ArrayList<>();

        try {
            // increments
            for(int i = 0; i < 500; i++) {
                futures.add(executorService.submit(new Increment()));
            }
            // decrements
            for(int i = 0; i < 500; i++) {
                futures.add(executorService.submit(new Decrement()));
            }

            futures.forEach(r -> {
                try {
                    r.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });

            System.out.println("Counter: " + counter); // Should be 0

        } finally {
            executorService.shutdown();
        }


    }
}
