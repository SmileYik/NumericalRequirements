import org.eu.smileyik.numericalrequirements.core.element.data.singlenumber.DoubleElementValue;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ElementAtomicTest {

    public static final class ElementValue extends DoubleElementValue {

        @Override
        public long period() {
            return 1;
        }

        @Override
        protected void updateValue(double second) {
            Double value = getValue();
            setValue(value + 1);
            System.out.println(Thread.currentThread().getName() + ": Change before: " + value + ", Change after: " + getValue());
        }
    }

    class MyRunnable implements Runnable {
        ScheduledFuture<?> scheduledFuture = null;

        private final ElementValue bar;

        public MyRunnable(ElementValue bar) {
            this.bar = bar;
        }

        private int count = 0;

        @Override
        public void run() {
            if (scheduledFuture != null) {
                bar.update();
                if (++count == 1000) {
                    scheduledFuture.cancel(true);
                }
            }
        }

        public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
            this.scheduledFuture = scheduledFuture;
        }
    }

    @Test
    public void test1() {
        ElementValue bar = new ElementValue();
        bar.setValue(0D);
        ScheduledExecutorService service = Executors.newScheduledThreadPool(10);
        List<Runnable> runnables = new ArrayList<>();
        for (int i = 0; i < 2; ++i) {
            MyRunnable myRunnable = new MyRunnable(bar);
            myRunnable.scheduledFuture = service.scheduleWithFixedDelay(myRunnable, 0, 1, TimeUnit.MILLISECONDS);
        }

        try {
            boolean b = service.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {

        }

    }
}
