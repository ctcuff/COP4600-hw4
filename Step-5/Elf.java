import java.util.Random;
import java.util.concurrent.Semaphore;

public class Elf implements Runnable {

    enum ElfState {
        WORKING,
        TROUBLE,
        AT_SANTAS_DOOR
    }

    private ElfState state;
    /**
     * The number associated with the Elf
     */
    private int number;
    private Random rand = new Random();
    private SantaScenario scenario;
    private boolean running;
    private boolean isInTrouble;
    private Semaphore semaphore;

    public Elf(int number, SantaScenario scenario, Semaphore semaphore) {
        this.number = number;
        this.scenario = scenario;
        this.semaphore = semaphore;
        this.state = ElfState.WORKING;
        this.running = true;
        this.isInTrouble = false;
    }

    public ElfState getState() {
        return state;
    }

    /**
     * Santa might call this function to fix the trouble
     *
     * @param state
     */
    public void setState(ElfState state) {
        this.state = state;
    }

    public void stopRunning() {
        this.running = false;
    }

    @Override
    public void run() {
        while (running) {
            // wait a day
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            switch (state) {
                case WORKING: {
                    isInTrouble = false;
                    // at each day, there is a 1% chance that an elf runs into
                    // trouble.
                    if (rand.nextDouble() < 0.01) {
                        state = ElfState.TROUBLE;

                        try {
                            semaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                case TROUBLE:
                    if (semaphore.availablePermits() == 0) {
                        for (Elf elf : scenario.elves) {
                            if (elf.state == ElfState.TROUBLE) {
                                elf.setState(Elf.ElfState.AT_SANTAS_DOOR);
                            }
                        }
                    }
                    break;
                case AT_SANTAS_DOOR:
                    // In this case, Santa will only be woken up if 3 elves have
                    // acquired the semaphore
                    scenario.santa.wakeUp(this);
                    break;
            }
        }
    }

    /**
     * Report about my state
     */
    public void report() {
        System.out.println("Elf " + number + " : " + state);
    }
}
