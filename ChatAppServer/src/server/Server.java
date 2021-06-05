package server;

import app.Message;
import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Yunus Emre Atik (yunusemre.atik@stu.fsm.edu.tr)
 */
public class Server {

    public static ServerSocket server_Socket;                                           // Create server socket    
    public static ServerThread server_Thread;                                           // Create server thread

    public static ArrayList<SClient> Clients = new ArrayList<>();                       // All Connected Clients
    public static ArrayList Rooms_Names = new ArrayList<>();                            // All Rooms_Names
    public static ArrayList Rooms_IDs = new ArrayList<>();                              // All Rooms_IDs
    public static ArrayList Rooms_Sahip_Ids = new ArrayList<>();                        // All Rooms_IDs
    
    public static int Id_Client = 0;                                // Clients ID
    public static int Id_Room = 0;                                  // Room ID
    public static int port = 0;                                     // Server Port Number

    public static Semaphore lock = new Semaphore(1, true);          // For Pairing Clients
    public static boolean is_Open = false;                          // Checking Server is Open/Close

    public static void Start(int ServerPort) {                      // Starting to Server Func.
        try {
            Server.port = ServerPort;
            Server.server_Socket = new ServerSocket(Server.port);   // Create Server Socket on entry
            Server.is_Open = true;

            Server.server_Thread = new ServerThread();              // Create Server Thread in Start
            Server.server_Thread.start();                           // Start Server's Listen Thread

        } catch (IOException ex) {
            System.out.println("Error on Server Start");
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void Send(SClient client, Message msg) {          // Send Message to Client1->Server->Client2
        try {
            client.sc_Output.writeObject(msg);                      // Writing message to Client2 output

        } catch (IOException ex) {
            System.out.println("Error on Server Send");
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void SendBroadCastMessage(Message msg) {

        for (SClient client : Clients) {
            try {
                client.sc_Output.writeObject(msg);
            } catch (IOException ex) {
                System.out.println("Server->SendBroadCastMessage Hata!");
            }
        }

    }

    public static void Deneme(Message msg) {
        ArrayList abc = (ArrayList) msg.content;
        ArrayList de = (ArrayList) abc.get(0);
        for (SClient client : Clients) {
            try {
                System.out.print(client.name + " Kisisine: ");
                for (Object object : de) {
                    System.out.print(object.toString() + ",");
                }
                System.out.println(" Gonderildi");
                client.sc_Output.writeObject(msg);
            } catch (IOException ex) {
                System.out.println("Server->SendBroadCastMessage Hata!");
            }
        }

    }

    public static void SendRoomList(Message msg) {
        String[] names = new String[Rooms_Names.size()];
        Integer[] ids = new Integer[Rooms_IDs.size()];
        Integer[] ids1 = new Integer[Rooms_Sahip_Ids.size()];

        int i = 0;
        for (Object r1 : Rooms_Names) {
            names[i] = (String) r1;
            i++;
        }
        i = 0;
        for (Object i1 : Rooms_IDs) {
            ids[i] = (Integer) i1;
            i++;
        }
        i = 0;
        for (Object i2 : Rooms_Sahip_Ids) {
            ids1[i] = (Integer) i2;
            i++;
        }
        ArrayList RoomList = new ArrayList<>();
        RoomList.add(names);
        RoomList.add(ids);
        RoomList.add(ids1);
        msg.content = RoomList;

        for (SClient client : Clients) {
            try {
                client.sc_Output.writeObject(msg);
            } catch (IOException ex) {
                System.out.println("Server->SendRoomList Hata!");
            }
        }
    }

    public static void SendClientList(Message msg) {
        String[] names = new String[Clients.size()];
        Integer[] ids = new Integer[Clients.size()];

        int i = 0;
        for (SClient cl : Clients) {
            names[i] = cl.name;
            ids[i] = cl.Id_SClient;
            i++;
        }

        ArrayList List = new ArrayList<>();
        List.add(names);
        List.add(ids);
        msg.content = List;

        for (SClient client : Clients) {
            try {
                client.sc_Output.writeObject(msg);
            } catch (IOException ex) {
                System.out.println("Server->SendDeneme Hata!");
            }
        }

    }

    public static void SendToClients(Message msg) { //path,data,myname,showID,friendnames
        //Private->Enter : isim,color,girdi,emoji,friend(string ARR),showID 
        ArrayList receive = (ArrayList) msg.content;

        ArrayList names = (ArrayList) receive.get(4);

        for (SClient client : Clients) {
            for (Object name : names) {
                if (client.name.equals((String) name)) {
                    System.out.println(client.name + "," + client.Id_SClient + " kisine gitti.");
                    try {
                        client.sc_Output.writeObject(msg);  //Join Mesajini Herkese Gonder
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                }
            }
        }
    }

    public static void SendToPrivate(Message msg) {
        //Private Connect: my name,my color,useless,my ID,friendname,friendID,friendID(ARR),room_name
        //Private Message: path,data,myname,showID,friendname,showID,ids
        //App BroadCast: name,color,text,emoji,onlineClientsNames(ARR),showID,onlineClientsID(ARR),String&

        ArrayList receive = (ArrayList) msg.content;
        ArrayList ids = (ArrayList) receive.get(6);

        for (SClient client : Clients) {
            for (Object j : ids) {
                if (client.Id_SClient == (int) j) {
                    try {
                        client.sc_Output.writeObject(msg);
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                }
            }
        }
    }

    public static void SendBroadCastList(Message msg) {
        ArrayList icerik = (ArrayList) msg.content;
        String name = (String) icerik.get(0);
        int id = (int) icerik.get(3); // 0=connect,1=disconnect

        String[] names = new String[Clients.size()];
        Integer[] ids = new Integer[Clients.size()];

        int i = 0;
        if (id == 1) {  //disconnect te, dahil etmiyoruz.
            for (SClient cl : Clients) {
                if (!name.equals(cl.name)) {
                    names[i] = cl.name;
                    ids[i] = cl.Id_SClient;
                    i++;
                }

            }
        } else {
            for (SClient cl : Clients) {
                names[i] = cl.name;
                ids[i] = cl.Id_SClient;
                i++;
            }
        }
//        names=new String[i+1];
//        ids = new Integer[i+1];
        ArrayList List = new ArrayList<>();
        List.add(names);
        List.add(ids);
        msg.content = List;
        for (SClient client : Clients) {
            try {
                client.sc_Output.writeObject(msg);
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public static void Bagli_Clientler() {
        System.out.print("Bagli Clientlar : ");
        for (SClient client : Clients) {
            System.out.print(client.name);
            System.out.print(", ");
        }
        System.out.println("");

    }

//    public static void main(String[] args) {
//        Start(2000);         //Starting Server on 2000. port
//    }

}

class ServerThread extends Thread {         // Listening for Clients

    @Override
    public void run() {
        while (Server.is_Open) {            // While Server is Open, wait for Client
            try {
                System.out.println("Client is Waiting...");
                Socket client_Socket = Server.server_Socket.accept();               // Accepting Clients in Thread
                System.out.println("Client Entered");

                SClient new_Client = new SClient(client_Socket, Server.Id_Client);  // Create SClient with entry Client
                Server.Id_Client++;
                Server.Clients.add(new_Client);                                     // Add SClient to ArrayList
                new_Client.sc_Listen_Threadd.start();                               // Starting SClient's listen Thread

                Thread.sleep(1000);

            } catch (IOException ex) {
                Server.is_Open = false;                                             // Server Closed
                System.out.println("Server Closed");
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
