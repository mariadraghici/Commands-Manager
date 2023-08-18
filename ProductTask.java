import java.io.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductTask implements Runnable{
    // salvare task curent
    String task;
    ExecutorService tpe;
    AtomicInteger inQueue;
    ManageFiles manage;
    // bufferul pentru sincronizarea cu
    // thread nivel 1
    Buffer buffer;

    public ProductTask(String task, ExecutorService tpe, AtomicInteger inQueue,
                       ManageFiles manage, Buffer buffer) {
        this.task = task;
        this.tpe = tpe;
        this.inQueue = inQueue;
        this.manage = manage;
        this.buffer = buffer;
    }

    @Override
    public void run() {
        // Procesare comanda
        try {
            ShipProducts();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Daca s-au citit toti bytesi-i din fisierul
        // orders.txt, se inchide task pool-ul
        if (manage.getBytes() == manage.getFileSize()) {
            tpe.shutdown();
        }
    }

    public void ShipProducts() throws IOException, InterruptedException {
        // reader pentru citirea fisierului order_products.txt
        BufferedReader myReader = new BufferedReader(new FileReader(manage.getFile()));
        // preluare valoare de produse din cadrul comenzii
        int index = task.indexOf(',');
        String number = task.substring(index + 1);
        int value = Integer.parseInt(number);

        // daca mai exista linii de citit si produse de cautat
        while (myReader.ready() && value != 0) {
            // citesc linia
            String line = myReader.readLine();
            // preiau id-ul comenzii prelucrate acum
            index = task.indexOf(',');
            String order = task.substring(0, index);
            // daca linia curenta contine id-ul comenzii
            if (line.contains(order)) {
                // scad numarul de produse pe care le mai am de cautat
                value--;
                // scriu in fisierul order_products_out.txt produsul gasit
                Tema2.Sem.acquire();
                FileWriter write_products = new FileWriter(manage.getOrder_products_out().getAbsoluteFile(), true);
                write_products.write(line + ",shipped\n");
                write_products.close();
                Tema2.Sem.release();
            }
        }
        // Scriu in buffer valoarea 1 pentru a anunta threadul de nivel 1
        // ca toate produsele din comanda respectiva au fost livrate
        buffer.put(1);
        // inchid reader-ul
        myReader.close();
    }
}
