import java.util.Random;
import java.util.concurrent.Semaphore;

public class Reindeer implements Runnable {

    public enum ReindeerState {
        AT_BEACH,
        AT_WARMING_SHED,
        AT_THE_SLEIGH
    }

    private ReindeerState state;
    private SantaScenario scenario;
    private Random rand = new Random();
    private boolean running;
    private Semaphore semaphore;

    /**
     * The number associated with the reindeer
     */
    private int number;

    public Reindeer(int number, SantaScenario scenario, Semaphore semaphore) {
        this.number = number;
        this.scenario = scenario;
        this.state = ReindeerState.AT_BEACH;
        this.running = true;
        this.semaphore = semaphore;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setState(ReindeerState state) {
        this.state = state;
    }

    @Override
    public void run() {
        while (running) {
            // wait a day
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // see what we need to do:
            switch (state) {
                case AT_BEACH: {
                    // If it is December, the reindeer might think about returning from the beach
                    if (scenario.isDecember) {
                        if (rand.nextDouble() < 0.1) {
                            state = ReindeerState.AT_WARMING_SHED;
                        }
                    }
                    break;
                }
                case AT_WARMING_SHED:
                    boolean allAtShed = scenario.reindeers
                            .stream()
                            .allMatch(reindeer -> reindeer.state == ReindeerState.AT_WARMING_SHED);

                    // If all the reindeer are home, wake up santa
                    if (allAtShed && semaphore.availablePermits() == 1) {
                        try {
                            semaphore.acquire();
                            scenario.santa.wakeUp(this);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    break;
                case AT_THE_SLEIGH:
                    // keep pulling
                    break;
            }
        }
    }

    /**
     * Report about my state
     */
    public void report() {
        System.out.println("Reindeer " + number + " : " + state);
    }
}
