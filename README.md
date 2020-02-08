## Notes taking in the course

Java Advanced

Pool of threads: Instance of:
1. Executor interface
2. ExecutorService (extends from Executor). Has about 10 more methods.
3. A Factory class Executors is available with about 20 methods to create executors.

Example:

1. Build a pool of thread with only one thread in it:
ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

Note: The thread inside the pool is also created and will be kept alive as long as this pool is. If a task is sent to this executor, will be executed in that thread, and the thread will maintain alive when the task is finished.

The most used executors:
1. Single thread executor.
2. Fixed thread pool executor.

Single Thread Executor:
Has a waiting queue to handle multiple tasks.
Tasks are executed in order of their submission.
We cannot know if a task is done or not.
We can cancel the execution of a task, if it hasn’t started yet.

Comparisions Executor vs Runnable pattern
1. Building an executor is mor efficient than creating threads on demand.
2. The Executor has a waiting queue to handle multiple requests.
3. A task can be removed from the waiting queue.

Runnable Task:
1. Does not returns anything.
2. No exception can be raised.

The Callable Interface

public interface Callable<V> {
 V call() throws Exeception;
}

Executor interface does not handle Callable, but only Runnable tasks. The ExecutorService does take a Callable in its submit() method. Return a Future object.

How Future Object Works ??
1. Create a Callable task
2. Create a ExecutorService
3. Submit the task to the executor. It will immediately return a Future<?>. Then the .get() method of the Future can be called to produced the result the task is supposed to return.
4. When you call .get() on a future, TWO things can happen:
    1. The object produced by the task is available: the object will be returned immediately.
    2. The object is NOT YET available: it will block the thread until it’s available.
5. If things go wrong… An InterruptedException is thrown:
    1. If the thread that’s executing the task has been interrupted. It’s possible to interrupted by sending a shutdown command to the executor.
6. The task itself has thrown an exception:
    1. It’s wrapped in a ExecutionException and re-thrown.

Behavior of Future.get()
If the task has completed, then the .get() call will return the produced result immediately.
Else, it will block until it’s ready.

WE CAN ALSO, pass a timeout to a Future.get() to avoid being block indefinitely.  :)
If this timeout is passed an InterruptedException is raised.


———

Synchronization


Intrinsic synchronization

Synchronized -> prevents more than one thread execute the code at the same time. Its the basic synchronization in Java. If more than one thread tries to access the synchronized block of code, only one is allowed, the others wait.

But.. what happens if the thread is blocked inside the synchronized block of code? 
Then, all others threads are also block, and there is no way in the JDK or JVM to release them.
The only way is to reboot the JVM. We need to avoid this at all cost.

The Lock Pattern solves this issue.
Interruptible pattern
The thread will be waiting until it can enter the guarded block of code
Another thread can interrupted by calling .interrupt() -> This can be costly or hard to achieve though

Time Lock acquisition —

use tryLock instead of lock
If a thread is already executing the guarded block of code, the tryLock will return false immediately. Will not enter the guarded block of code and will be able to do something else.
Can also pass a timeOut in the tryLock

Fair Lock acquisition —

When severals threads are waiting for a lock (intrinsic or explicit) the first one to enter the guarded block of code is chosen randomly.
Fairness -> Means that the first to enter the wait line, will the the first to enter the guarded block of code.

ReentrantLock() is not Fair by default.
To make it Fair, pass “true” in the argument -> ReentrantLock(true);  ——>  A Fair lock IS COSTLY



——
Implementing Producer / Consumer pattern
Intrinsic lock: Producer, Consumer
Pattern is implemented with wait / notify. But this does not works with the Explicit lock pattern.. So, we need something else:

Condition Object
A condition object is used to park and awake threads.
A Lock object can have any number of conditions.

The await() call is blocking, can be interrupted.

Five version of await():
1. await()
2. await(time, timeUnit)
3. awaitNanos(nanosTimeout)
4. awaitUntil(date)
5. awaitUnnterruptibly()

— > This are ways to prevent the blocking of waiting threads with the Condition API.

Lock And Condition
Is another implementation of the wait / notify pattern
It gives wiggle room to build better concurrent systems
Controlled uninterruptible, timeout, fairness


————
Read / Write Blocks

Lock when we write to a variable, a collection for example.
Allow Parallel reads.

ReadWriteLock is an interface with only two methods:
1. readLock(): Get a read lock
2. writeLock(): Get a write lock. Only one thread can hold the writeLock.
When the WriteLock is held, none-one can hold the readLock.
As many threads are needed can hold the read lock.

Can be used to create a thread safe Cache. Ex:

Map<Long, User> cache = new HashMap<>();

// To read the map -> Allows concurrent reads to the map.
Try {
	readLock.lock();
	return cache.get(key);
} finally {
	readLock.unlock();
}

// To modify the map -> One thread at a time
Try {
	writeLock.lock();
	cache.put(key, value);
} finally {
	writeLock.unlock();
}

This prevents concurrent reads, that could read corrupted value.

Can also be achieved with a ConcurrentHashMap.


Semaphores ——
It looks like a Lock, but instead of allowing one thread it allows several in the same block of code.

Semaphore semaphore = new Semaphore(5); // permits
Try {
	semaphore.acquire();
	// do something -> guarded block of code

} finally {
	semaphore.release();
}

It’s non-fair by default.
.acquire() is blocking until a permit is available.
At most 5 threads can execute the guarded code at the same time.
Can be fair if you pass the “true” as the second argument.
.acquire() can ask for more than one permit. ——> AWARE!! The release call MUST release them.

Check the waiting threads:
- Are there any waiting threads?
- How many?
- Get the collection of the waiting threads.

Summary
Semaphore: It has a number of permits, with can be acquired in different ways, and released.



Barriers and Latchers
Barriers: To have several task to wait for each other
Latch: To count down operations and let the a task start.

Barriers -> CyclicBarrier
Multiple threads execute several sub-tasks. Then when they’re all done, a merging process is run.
We need a way to distribute the computation among several threads.
We need to know when all the threads has finished their tasks.
We need to launch a post-processing at that moment.
It’s like a gateway, where your tasks are hold. When all the taks hit .await() in the barrier (lets say 4) and the barrier was created with 4, then this gateway is open, letting the threads continue their way. After open, it’s closed again to handle Cyclic operations.
You can introduce a Timeout to .await()

Latches
Its a kind of Barrier, that once opened, cannot be closed. This is the CountDown latch.
Use .countDown() to decrease the number of services you specified in the initialization.
Check that different threads did their tasks
Once open-end, cannot be closed.

CyclicBarrier -> Useful for parallel apps
CountDownLatch -> Useful for App startup for initialize different services before the App.



CASing!  ->   compare and swap
Properties of the CPU available in the JDK.
What is in the JDK to implement CASing?
How and when to use it?

CASing “Compare And Swap”

The problem in concurrent programming is the concurrent access to Shared Memory.
We use Synchronization to handle that.
It prevents several threads to modify the same part of the memory at the same time.

But…

Synchronization has a cost.
We sure it to be sure that our code was correct.

Though protection by lock is essential to avoid race conditions: when two threads are accessing the same shared memory at the same time.

But, in fact, there’s no real concurrency at runtime, this is the case where CASing can be used:

CASing works with three parameters:
1. A location in memory (address)
2. An existing value at that location
3. A new value to replace this existing value

If the current value at that location is the same as the expected value, then it’s replaced by the new value and returns true.
If not, returns false

—> All in a single, atomic assembly instruction.

When concurrency is very high, it can be very inefficient, causing CPU and memory loads. Though, it can be more performant than synchronization.
The load in CPU in memory, when concurrency is very high, is due to how it works: Multiple threads will try to access the atomic variable at the same time, but only one will win the race, the other will keep trying and trying… Until the memory is available again.
For cases like this, use LongAdder and LongAccumulator to handle this situations. Only available in Java 8+


Concurrent collections and maps

copyOnWriteArrayList or copyOnWriteArraySet
Very good when there are lots of reads and very few writes. Because when writing a copy of array is made (this is costly) and the reference of the old is moved to the new in a synchronized way.

Good situations: Application initialization.

Queues and Stacks
Queue and Deque: interfaces

ArrayBlockingQueue: A bounded blocking queue built on an array.
Bounded means, that we create a BlockingQueue with a certain amount of cells (size of the array). Once full, adding element is not possible.

ConcurrentBlockingQueue: An unbounded blocking queue.

Two types of queue: FIFIO (queue) and LIFO (stack)

Queue: Queue
Deque: both a queue and a stack

Producer produces to the queue. Consumer consumes from the queue.
There can be any number of producers and consumers, each of them in its own thread.

A thread does not know how many elements are in the thread.

Two questions:
1. What happens if the queue / stack is full, and we need to add more elements to it?
2. What happens if the queue / stack is empty, and we need to get an element from it?

Adding element to a Full Queue / BlockingQueue:
1. Boolean add(E e); // fail: throws IllegalArgumentException
2. Boolean offer(E e); // returns false
3. Boolean offer(E e, timeOut); // returns false after timeOut
4. Void put(E e); // blocking. Waits until there is some available space. -> For BlockingQueue


Concurrent Maps
1. ConcurrentHashMap: JDK 7+
2. ConcurrentSkipHashMap: No synchronization. JDK 6

Concurrent Maps atomic operations:
1. putIfAbsent(key, value);
2. remove(key, value);
3. replace(key, value);
4. replace(key, existingValue, newValue);

ConcurrentMap implementations:

Thread-safe maps
Efficient up to a certain number fo threads
A number of efficient, parallel special operators.

ConcurrentHashMap FROM JDK 8+
Has been made to handle millions of <key, value> pairs.
A fully concurrent map.
With built-in parallel operations
Can be used very efficient and very large concurrent Sets.

Concurrent Skip List
Smart Structure to create Linked lists.
Provide fast random access to any of its elements.
Relies on atomic reference operations, no synchronization.
Can be used to create Maps and Sets.

Skip List
Used to implement a map
Keys are sorted
Is not an array-based structure

ConcurrentSkipListMap
 -> All implementations are using AtomicReference
So, its a thread-safe map with no synchronization.

ConcurrentSkipListSet
-> using the same structure as ConcurrentSkipListMap

Both can be used for high concurrency environments, as long as there is enough elements in it.
































































