import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Santa implements Runnable {

    enum SantaState {
        SLEEPING,
        READY_FOR_CHRISTMAS,
        WOKEN_UP_BY_ELVES,
        WOKEN_UP_BY_REINDEER
    }

    private SantaState state;
    private boolean running;
    public ArrayList<Elf> elvesAtDoor;
    private Semaphore elfSemaphore;
    private Semaphore reindeerSemaphore;
    private SantaScenario scenario;

    public Santa(SantaScenario scenario, Semaphore elfSemaphore, Semaphore reindeerSemaphore) {
        this.state = SantaState.SLEEPING;
        this.running = true;
        this.elvesAtDoor = new ArrayList<>();
        this.elfSemaphore = elfSemaphore;
        this.reindeerSemaphore = reindeerSemaphore;
        this.scenario = scenario;
    }

    public void stopRunning() {
        this.running = false;
    }

    public void addElfToDoor(Elf elf) {
        elvesAtDoor.add(elf);
    }

    public void wakeUp(Runnable waker) {
        if (waker instanceof Elf) {
            state = SantaState.WOKEN_UP_BY_ELVES;
        } else if (waker instanceof Reindeer) {
            state = SantaState.WOKEN_UP_BY_REINDEER;
        }
    }

    @Override
    public void run() {
        while (running) {
            // wait a day...
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            switch (state) {
                case SLEEPING: // if sleeping, continue to sleep
                    break;
                case WOKEN_UP_BY_ELVES:
                    for (Elf elf : scenario.elves) {
                        // Help the elves who are at the door and go back to sleep
                        if (elf.getState() == Elf.ElfState.AT_SANTAS_DOOR) {
                            elf.setState(Elf.ElfState.WORKING);
                            elfSemaphore.release();
                        }
                    }

                    state = SantaState.SLEEPING;
                    break;
                case WOKEN_UP_BY_REINDEER:
                    // Assemble the reindeer to the sleigh then change state to ready
                    for (Reindeer reindeer : scenario.reindeers) {
                        reindeer.setState(Reindeer.ReindeerState.AT_THE_SLEIGH);
                        reindeer.setRunning(true);
                    }

                    reindeerSemaphore.release();
                    state = SantaState.READY_FOR_CHRISTMAS;
                    break;
                case READY_FOR_CHRISTMAS: // nothing more to be done
                    break;
            }
        }
    }

    /**
     * Report about my state
     */
    public void report() {
        System.out.println("Santa : " + state);
    }
}
