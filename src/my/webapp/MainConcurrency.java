package my.webapp;

import java.text.SimpleDateFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainConcurrency {
    private static int THREADS_COUNT = 10000;
    private static int counter;
    private int counter1;

    // Используется для многопоточности в non-safe классах типа SimpleDateFormat
//    private static final ThreadLocal<SimpleDateFormat> threadLocal =
//            new ThreadLocal<SimpleDateFormat>(){
//                @Override
//                protected SimpleDateFormat initialValue() {
//                    return new SimpleDateFormat();
//                }
//            };

    private static final ThreadLocal<SimpleDateFormat> threadLocal =
            ThreadLocal.withInitial(SimpleDateFormat::new);

    private void inc(){
        MainConcurrency.counter++;
        this.counter1++;
    }

    public static void main(String[] args) {
        MainConcurrency mc = new MainConcurrency();
        CountDownLatch latch = new CountDownLatch(THREADS_COUNT);
        System.out.println(Runtime.getRuntime().availableProcessors());
        // Take a look at ExecutorCompletionService !!!
        ExecutorService es = Executors.newFixedThreadPool(400);


        for (int i = 0; i < THREADS_COUNT; i++){
            es.submit(() -> {
//            new Thread(()->{
                for (int j = 0; j < 100; j++){
                    mc.inc();
                }
//                System.out.printf("Thread = %-20s; Date = %s\n",
//                        Thread.currentThread().getName(),
//                        threadLocal.get().format(new Date()));
                latch.countDown();
            })
//            .start();
        ;
        }

        try {
            latch.await(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        es.shutdown();
        System.out.printf("counter = %d; counter1 = %s", counter, mc.counter1);
    }




}
