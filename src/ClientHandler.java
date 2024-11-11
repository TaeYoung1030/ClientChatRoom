import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class ClientHandler implements Runnable {
    private static Map<String, Room> rooms;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private Room room;

    public ClientHandler(Socket socket, Map<String, Room> rooms) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.rooms = rooms;
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        try {
            // 방 번호 입력 받기

            String roomNumber = bufferedReader.readLine();

            // 유효한 방 번호인지 확인
            if (!rooms.containsKey(roomNumber)) {
                bufferedWriter.write("잘못된 방 번호입니다. 프로그램을 종료합니다.");
                bufferedWriter.newLine();
                bufferedWriter.flush();
                closeEverything(socket, bufferedReader, bufferedWriter);
                return;
            }

            room = rooms.get(roomNumber);
            room.addClient(this);

            // 이름 입력 받기

            clientUsername = bufferedReader.readLine();

            broadcastMessage(clientUsername + "님이 " + roomNumber + "번 방에 입장하였습니다.");

            // 클라이언트 메시지 수신 대기
            String messageFromClient;
            while (socket.isConnected()) {
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(clientUsername + ": " + messageFromClient);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : room.getClients()) {
            try {
                if (!clientHandler.clientUsername.equals(this.clientUsername)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        if (room != null) {
            room.removeClient(this);
        }
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
