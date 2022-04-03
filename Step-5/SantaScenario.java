import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class SantaScenario {
    public Santa santa;
    public List<Elf> elves;
    public List<Reindeer> reindeers;
    public boolean isDecember;

    public static void main(String[] args) {
        Semaphore elfSemaphore = new Semaphore(3);
        Semaphore reindeerSemaphore = new Semaphore(1);

        SantaScenario scenario = new SantaScenario();
        scenario.isDecember = false;

        // create the participants
        // Santa
        scenario.santa = new Santa(scenario, elfSemaphore, reindeerSemaphore);
        Thread th = new Thread(scenario.santa);
        th.start();

        // The elves: in this case: 10
        scenario.elves = new ArrayList<>();

        for (int i = 0; i != 10; i++) {
            Elf elf = new Elf(i + 1, scenario, elfSemaphore);
            scenario.elves.add(elf);
            th = new Thread(elf);
            th.start();
        }

        // The reindeer: in this case: 9
        scenario.reindeers = new ArrayList<>();

        for (int i = 0; i != 9; i++) {
            Reindeer reindeer = new Reindeer(i + 1, scenario, reindeerSemaphore);
            scenario.reindeers.add(reindeer);
            th = new Thread(reindeer);
            th.start();
        }

        // now, start the passing of time
        for (int day = 1; day < 500; day++) {
            // wait a day
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // turn on December
            if (day > (365 - 31)) {
                scenario.isDecember = true;
            }

            // print out the state:
            System.out.println("***********  Day " + day + " *************************");

            if (day == 370) {
                scenario.santa.stopRunning();

                for (Elf elf : scenario.elves) {
                    elf.stopRunning();
                }

                for (Reindeer reindeer : scenario.reindeers) {
                    reindeer.setRunning(false);
                }
            }

            scenario.santa.report();

            for (Elf elf : scenario.elves) {
                elf.report();
            }

            for (Reindeer reindeer : scenario.reindeers) {
                reindeer.report();
            }
        }
    }
}
