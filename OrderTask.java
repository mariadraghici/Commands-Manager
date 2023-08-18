import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderTask extends Thread {
    // id thread
    private int id;
    // limita de bytes cititi
    private long chunk;
    // acelasi executor service
    ExecutorService tpe;
    // aceeasi coada cu taskuri
    AtomicInteger inQueue;
    // manage-ul pentru a accesa fisierele
    ManageFiles manage;

    public OrderTask(long chunk, int id, ExecutorService tpe,
                     AtomicInteger inQueue, ManageFiles manage) {
        this.chunk = chunk;
        this.id = id;
        this.tpe = tpe;
        this.inQueue = inQueue;
        this.manage = manage;
    }

    @Override
    public void run() {
        try {
            readMultipleChars();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void readMultipleChars() throws IOException, InterruptedException {
        // cat timp are ce sa citeasca si nu intrece limita de bytes asignati
        while (manage.getOrdersReader().ready() && chunk > 0) {
            // salvez linia in res
            String res = manage.getOrdersReader().readLine();
            // scad numarul de bytes cititi din limita
            chunk -= res.length();

            // preiau numarul de produse din comanda
            int index = res.indexOf(',');
            String number = res.substring(index + 1);
            int app = Integer.parseInt(number);
            // incrementez numarul de bytes cititi pana acum per total
            Tema2.Sem2.acquire();
            manage.incrementBytes(res.length() + 1);
            Tema2.Sem2.release();
            // daca numarul de produse din comanda e mai mare decat 0
            if (app != 0) {
                // adaug task-ul in task pool
                inQueue.incrementAndGet();
                // creez un buffer pentru sincronizarea intre
                // acest thread de nivel 1 si threadul de nivel 2
                // care o sa prelucreze comanda
                Buffer buffer = new Buffer(1);
                // dau submit la task
                tpe.submit(new ProductTask(res, tpe, inQueue, manage, buffer));
                // daca threadul de nivel 2 a terminat de procesat
                // comanda, scriu 'shipped' in fisierul orders_out.txt
                if (buffer.get() == 1) {
                    manage.getWrite_orders().write(res + ",shipped\n");
                }
            }
        }
    }
}
