import java.util.ArrayList;
import java.util.List;

public class Room {
    private int roomNumber;
    private List<ClientHandler> clients = new ArrayList<>();

    public Room(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void addClient(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public List<ClientHandler> getClients() {
        return clients;
    }
}