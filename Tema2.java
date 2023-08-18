import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Tema2 {
    static int P;
    public static Semaphore Sem = new Semaphore(1);
    public static Semaphore Sem2 = new Semaphore(1);


    public static void main(String[] args) throws IOException {
        // Preiau din input numele fisierelor de intrare
        String ordersFilename = "./" + args[0] + "/" + "orders.txt";
        String orderProductsFilename = "./" + args[0] + "/" + "order_products.txt";
        // Preiau numarul maxim P de threaduri din input
        P = Integer.parseInt(args[1]);
        // Numar threaduri de nivel 1
        Thread[] t = new Thread[P];

        // Reader pentru threadurile de nivel 1
        BufferedReader ordersReader = new BufferedReader(new FileReader(ordersFilename));
        // File pentru threadurile de nivel 2
        File file = new File(orderProductsFilename);

        // Coada si executor service pentru task-uri (comenzi + nr produse)
        AtomicInteger inQueue = new AtomicInteger(0);
        // Executor service-ul are maxim P workeri
        ExecutorService tpe = Executors.newFixedThreadPool(P);

       // Fisiere de iesire
        File orders_out = new File("orders_out.txt");
        File order_products_out = new File("order_products_out.txt");
        FileWriter myWriter = new FileWriter("order_products_out.txt");
        myWriter.write("");
        myWriter.close();

        // Trimit parametrii pentru gestionarea fisierelor in program
        ManageFiles manage = new ManageFiles(orderProductsFilename, orders_out, order_products_out,
                ordersReader, file);

        // Aflare dimensiune in bytes a fisierului orders.txt
        long fileSize = manage.getFileSizeNIO(ordersFilename);
        manage.setFileSize(fileSize);

        // Impartire fisier orders.txt la threadurile de nivel 1 pentru a
        // paraleliza citirea
        double chunk = ((double) fileSize) / P;
        long start = 1;
        long stop = (int) Math.floor(chunk);
        int started = 0;
        boolean ok = false;
        for (int i = 0; i < P; ++i) {
            // Creare threaduri nivel 1
            long limit = stop - start + 1;
            t[i] = new OrderTask(limit, i, tpe, inQueue, manage);
            t[i].start();
            if (ok) {
                started = i;
                break;
            }
            start = stop + 1;
            stop = (int) Math.floor(start + chunk);

            // tratarea caz daca limita inferioara sau superioara
            // este mai mare decat dimensiunea fisierului
            if (start >= fileSize) {
                start = fileSize - 1;
            }

            if (stop >= fileSize) {
                stop = fileSize - 1;
                ok = true;
            }
        }

        // Join la threaduri de nivel 1
        for (int i = 0; i <= started; ++i) {
            try {
                t[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Inchidere reader pentru citire si scriere orders.txt
        manage.getOrdersReader().close();
        manage.getWrite_orders().close();
    }
}
