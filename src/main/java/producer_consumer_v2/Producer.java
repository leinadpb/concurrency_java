package producer_consumer_v2;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public class Producer implements Callable<String> {

    private BlockingQueue<String> queue;

    public Producer(BlockingQueue<String> _queue) {
        queue = _queue;
    }

    @Override
    public String call() throws InterruptedException {
        int count = 0;
        while(count++ < 50) {
            queue.put(Integer.toString(count));
        }
        return "Count: " + (count - 1);
    }
}
