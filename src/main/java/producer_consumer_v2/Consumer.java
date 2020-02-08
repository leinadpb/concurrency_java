package producer_consumer_v2;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public class Consumer implements Callable<String> {

    BlockingQueue<String> queue;

    public Consumer(BlockingQueue<String> _queue) {
        queue = _queue;
    }

    @Override
    public String call() throws InterruptedException {
        int count = 0;
        while(count++ < 50) {
            queue.take();
        }
        return "Consumed: " + (count - 1);
    }
}
