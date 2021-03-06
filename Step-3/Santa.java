import java.util.ArrayList;

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

    public Santa(SantaScenario scenario) {
        this.state = SantaState.SLEEPING;
        this.running = true;
        this.elvesAtDoor = new ArrayList<>();
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
                    // Help the elves who are at the door and go back to sleep
                    for (int i = 0; i < elvesAtDoor.size(); i++) {
                        Elf elf = elvesAtDoor.get(i);

                        if (elf != null) {
                            elf.setState(Elf.ElfState.WORKING);
                        }

                        elvesAtDoor.remove(i);
                    }

                    if (elvesAtDoor.isEmpty()) {
                        state = SantaState.SLEEPING;
                    }
                    break;
                case WOKEN_UP_BY_REINDEER:
                    // FIXME: assemble the reindeer to the sleigh then change state to ready
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
