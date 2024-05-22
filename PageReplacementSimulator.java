import java.util.*;

public class PageReplacementSimulator {

    public static void main(String[] args) {
        int[] pages = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3};
        int numFrames = 3;

        System.out.println("Metodo FIFO - " + simulateFIFO(pages, numFrames) + " faltas de página");
        System.out.println("Metodo LRU - " + simulateLRU(pages, numFrames) + " faltas de página");
        System.out.println("Metodo Clock - " + simulateClock(pages, numFrames) + " faltas de página");
        System.out.println("Metodo NFU - " + simulateNFU(pages, numFrames) + " faltas de página");
    }
// ======================================================================================================
// FIFO - Para cada página:
// 	* Se a página não estiver no conjunto frameSet, ocorre uma falta de página.
// 	* Se a fila estiver cheia (frameQueue.size() == numFrames), remove a página mais antiga.
// 	* Adiciona a nova página à fila e ao conjunto.
    public static int simulateFIFO(int[] pages, int numFrames) {
        Queue<Integer> frameQueue = new LinkedList<>();
        Set<Integer> frameSet = new HashSet<>();
        int pageFaults = 0;

        for (int page : pages) {
            if (!frameSet.contains(page)) {
                if (frameQueue.size() == numFrames) {
                    int oldest = frameQueue.poll();
                    frameSet.remove(oldest);
                }
                frameQueue.offer(page);
                frameSet.add(page);
                pageFaults++;
            }
        }

        return pageFaults;
    }
// ======================================================================================================
// LRU - Para cada página:
// 	* Se a página não estiver no conjunto frameSet, ocorre uma falta de página.
// 	* Se a lista estiver cheia (frameList.size() == numFrames), remove a página menos recentemente usada (primeiro elemento da lista).
// 	* Adiciona a nova página ao final da lista e ao conjunto.
// 	* Se a página já estiver no conjunto, atualiza a posição dela na lista para o final (marcando-a como recentemente usada).
    public static int simulateLRU(int[] pages, int numFrames) {
        LinkedList<Integer> frameList = new LinkedList<>();
        Set<Integer> frameSet = new HashSet<>();
        int pageFaults = 0;

        for (int page : pages) {
            if (!frameSet.contains(page)) {
                if (frameList.size() == numFrames) {
                    int leastRecent = frameList.pollFirst();
                    frameSet.remove(leastRecent);
                }
                frameList.offerLast(page);
                frameSet.add(page);
                pageFaults++;
            } else {
                frameList.remove((Integer) page);
                frameList.offerLast(page);
            }
        }

        return pageFaults;
    }
// ======================================================================================================
// Clock - Para cada página:
// 	* Verifica se a página já está no array de quadros (frames). Se sim, marca como usada (used[i] = true).
// 	* Se a página não estiver presente:
// 	* Move o ponteiro até encontrar um quadro não utilizado (used[pointer] = false).
// 	* Substitui a página nesse quadro e atualiza o ponteiro e o array used.
// 	* Incrementa o contador de faltas de página.
    public static int simulateClock(int[] pages, int numFrames) {
        int[] frames = new int[numFrames];
        boolean[] used = new boolean[numFrames];
        Arrays.fill(frames, -1);
        int pointer = 0;
        int pageFaults = 0;

        for (int page : pages) {
            boolean found = false;
            for (int i = 0; i < numFrames; i++) {
                if (frames[i] == page) {
                    used[i] = true;
                    found = true;
                    break;
                }
            }

            if (!found) {
                while (used[pointer]) {
                    used[pointer] = false;
                    pointer = (pointer + 1) % numFrames;
                }
                frames[pointer] = page;
                used[pointer] = true;
                pointer = (pointer + 1) % numFrames;
                pageFaults++;
            }
        }

        return pageFaults;
    }
// ======================================================================================================
// NFU - Para cada página:
// 	* Se a página não estiver no conjunto frameSet, ocorre uma falta de página.
// 	* Se o conjunto estiver cheio (frameSet.size() == numFrames), remove a página menos frequentemente usada (usando pageCounter para encontrar).
// 	* Adiciona a nova página ao conjunto e atualiza o contador de uso no mapa pageCounter.
// 	* Incrementa o contador de uso da página atual no mapa.
    public static int simulateNFU(int[] pages, int numFrames) {
        Map<Integer, Integer> pageCounter = new HashMap<>();
        Set<Integer> frameSet = new HashSet<>();
        int pageFaults = 0;

        for (int page : pages) {
            if (!frameSet.contains(page)) {
                if (frameSet.size() == numFrames) {
                    int leastUsed = Collections.min(frameSet, Comparator.comparing(pageCounter::get));
                    frameSet.remove(leastUsed);
                }
                frameSet.add(page);
                pageFaults++;
            }
            pageCounter.put(page, pageCounter.getOrDefault(page, 0) + 1);
        }

        return pageFaults;
    }
}
