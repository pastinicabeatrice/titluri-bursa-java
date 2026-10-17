import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerTitluri implements AutoCloseable {
    private final ServerSocket serverSocket;
    private final Map<String, TitluBursa> titluri;
    private final ExecutorService clienti = Executors.newCachedThreadPool();

    public ServerTitluri(int port, Map<String, TitluBursa> titluri) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.titluri = titluri;
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void porneste() {
        Thread accept = new Thread(() -> {
            while (!serverSocket.isClosed()) {
                try {
                    Socket socket = serverSocket.accept();
                    clienti.submit(() -> serveste(socket));
                } catch (SocketException e) {
                    return;
                } catch (IOException e) {
                    System.out.println("Eroare server: " + e.getMessage());
                }
            }
        });
        accept.setDaemon(true);
        accept.start();
    }

    private void serveste(Socket socket) {
        try (socket;
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            String simbol = in.readLine();
            TitluBursa t = simbol == null ? null : titluri.get(simbol.trim().toUpperCase());
            if (t == null) {
                out.println("Simbol necunoscut: " + simbol);
            } else {
                out.printf("%s, inchidere %.4f, volum %d%n", t.getDenumire(), t.getInchidere(), t.getVolum());
            }
        } catch (IOException e) {
            System.out.println("Eroare la deservirea clientului: " + e.getMessage());
        }
    }

    @Override
    public void close() throws IOException {
        serverSocket.close();
        clienti.shutdown();
    }
}
