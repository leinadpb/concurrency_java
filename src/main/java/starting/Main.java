package starting;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class Main {

    public static void main(String args[]) {

        // Executor pattern

        // Build an executor
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Task
        Callable<String> task1 = () -> {
            String result;
            try {
                Thread.sleep(2000);
//                System.out.println("Hello from inside the thread: " + Thread.currentThread().getName());
                result = "OK";
            } catch (InterruptedException e) { result = e.getMessage(); }
            return result;
        };
        // Task
        Runnable task2 = () -> {
            try {
                Thread.sleep(2000);
//                System.out.println("Hello from inside the thread: " + Thread.currentThread().getName());
            } catch (InterruptedException e) { e.printStackTrace(); }
        };

        List<Future<String>> taskResults = new ArrayList<>();

        try {
            // Send tasks to executor
            for(int i = 0; i < 10; i++) {
                taskResults.add(executor.submit(task1));
            }
            System.out.println("All task submitted.");

            // Try get result of 3th task

            System.out.println("Trying to get 7th task result...");
            String result = taskResults.get(7).get(100, TimeUnit.MILLISECONDS);
            System.out.println("7th task result: " + result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();

        } catch (TimeoutException e) {
            System.out.println("Timeout!! waited for too long.");
        } finally {
            // Shutdown !
            executor.shutdown();
        }

    }
}
