public class Main {
    public static void main(String[] args) {
        FSServer server = new FSServer(".");
        server.addClient(new Client());
        server.start();
    }
}