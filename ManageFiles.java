import java.io.*;

public class ManageFiles {
    private String products;
    private File orders_out;
    private File order_products_out;
    private FileWriter write_orders;
    private BufferedReader ordersReader;
    private File file;
    long bytes = 0;
    long fileSize;
    public ManageFiles(String products, File orders_out, File order_products_out,
                       BufferedReader ordersReader, File file)
            throws IOException {
        // Nume fisier order_products.txt
        this.products = products;
        // Fisier orders_out.txt
        this.orders_out = orders_out;
        // Fisier order_products_out.txt
        this.order_products_out = order_products_out;
        // Creare writer orders_out.txt
        write_orders = new FileWriter(orders_out);
        // Salvare reader pentru orders.txt
        this.ordersReader = ordersReader;
        // Salvare file pentru order_products.txt
        this.file = file;
    }

    public File getOrder_products_out() {
        return order_products_out;
    }
    public FileWriter getWrite_orders() {
        return write_orders;
    }

    public BufferedReader getOrdersReader() {
        return ordersReader;
    }

    public File getFile() {
        return file;
    }

    // Determinarea dimensiunii in bytes a unui fisier
    public long getFileSizeNIO(String fileName) throws IOException {

        System.out.println(fileName);
        File file = new File(fileName);
        long bytes = 0;

        if (file.exists()) {
            bytes = file.length();
        } else {
            System.out.println("File does not exist!");
        }

        return bytes;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getBytes() {
        return bytes;
    }

    public void incrementBytes(long a) {
        this.bytes += a;
    }
}
