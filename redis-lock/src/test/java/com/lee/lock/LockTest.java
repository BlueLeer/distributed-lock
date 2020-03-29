package com.lee.lock;

import ch.qos.logback.core.util.TimeUtil;
import com.lee.distributed.RedisLockApplication;
import com.lee.distributed.lock.RedisLock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * @author lee
 * @date 2020/3/29 15:50
 */
@SpringBootTest(classes = RedisLockApplication.class)
@RunWith(SpringRunner.class)
public class LockTest {
    public int count = 0;

    @Autowired
    private RedisLock redisLock;

    @Test
    public void test() throws InterruptedException {
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(() -> {

                while (!lock()) {
                }
                System.out.println(Thread.currentThread().getId() + ":拿到锁了...");
                for (int j = 0; j < 1000000; j++) {
                    count++;
                }
                boolean unlock = unlock();
                System.out.println(Thread.currentThread().getId() + ":释放锁了..." + unlock);

            }, "Thread-" + i);
        }

        Stream.of(threads).forEach(thread -> thread.start());
//        for (Thread thread : threads) {
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

        TimeUnit.SECONDS.sleep(30);
        System.out.println("count: " + count);

        // 再睡眠3秒中,保证上面5个线程已经执行完毕,保证count打印出的结果是最终结果
        TimeUnit.SECONDS.sleep(3);
        System.out.println("count: " + count);
    }

    private boolean unlock() {
        boolean unlock_result = redisLock.unlock("count_lock", Thread.currentThread().getId() + "");
        return unlock_result;
    }

    private boolean lock() {
        boolean success = redisLock.tryLock("count_lock", Thread.currentThread().getId() + "", 3);
        return success;
    }
}
