import java.util.Random;

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

    public Elf(int number, SantaScenario scenario) {
        this.number = number;
        this.scenario = scenario;
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
                    }
                    break;
                }
                case TROUBLE:
                    if (!isInTrouble) {
                        scenario.elvesInTrouble.add(this);
                        isInTrouble = true;
                    }

                    if (scenario.elvesInTrouble.size() == 3) {
                        for (Elf elf : scenario.elvesInTrouble) {
                            elf.setState(ElfState.AT_SANTAS_DOOR);
                        }
                        
                        scenario.santa.elvesAtDoor.addAll(scenario.elvesInTrouble);
                        scenario.elvesInTrouble.clear();
                        scenario.santa.wakeUp(this);
                    }
                    break;
                case AT_SANTAS_DOOR:
                    // FIXME: if feasible, wake up Santa
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
