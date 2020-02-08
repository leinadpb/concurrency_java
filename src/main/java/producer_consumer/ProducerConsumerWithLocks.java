package producer_consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumerWithLocks {

    public static void main(String[] args) {

        List<Integer> buffer = new ArrayList<>();

        Lock lock = new ReentrantLock();
        Condition isEmpty = lock.newCondition();
        Condition isFull = lock.newCondition();

        class Consumer implements Callable<String> {

            public String call() throws InterruptedException, TimeoutException {
                int count = 0;
                 while(count++ < 50) {
                     try {

                         lock.lock();

                         while(isEmpty(buffer)) {
                             // wait
                             if (!isEmpty.await(10, TimeUnit.MILLISECONDS)) {
                                 throw new TimeoutException("Consumer time out.");
                             }
                         }

                         buffer.remove(buffer.size() - 1);

                         //signal
                         isFull.signalAll();

                     } finally {
                         lock.unlock();
                     }
                 }

                 return "Consumed: " + (count - 1);
            }

        }

        class Producer implements Callable<String> {
            public String call() throws InterruptedException {
                int count = 0;
                while (count++ < 50) {
                    try {

                        lock.lock();

                        // force
//                        int t = 9/0;

                        while(isFull(buffer)) {
                            // wait
                            isFull.await();
                        }
                        buffer.add(1);

                        //signal
                        isEmpty.signalAll();

                    } finally {
                        lock.unlock();
                    }
                }
                return "Produced: " + (count - 1);
            }
        }

        // generate producers
        List<Producer> producers = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            producers.add(new Producer());
        }

        // generate consumers
        List<Consumer> consumers = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            consumers.add(new Consumer());
        }

        System.out.println("Producers and consumers launched!");

        List<Callable<String>> producersAndConsumers = new ArrayList<>();
        producersAndConsumers.addAll(producers);
        producersAndConsumers.addAll(consumers);

        // Executor
        ExecutorService executor = Executors.newFixedThreadPool(8);
        try {
            List<Future<String>> futures = executor.invokeAll(producersAndConsumers);

            futures.forEach(future -> {
                try {
                    System.out.println(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
            System.out.println("Executor shutdown.");
        }

    }


    public static boolean isEmpty(List<Integer> buffer) {
        return buffer.size() == 0;
    }

    public static boolean isFull(List<Integer> buffer) {
        return buffer.size() == 10;
    }

}
