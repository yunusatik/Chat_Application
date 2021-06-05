/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import app.Message;
import java.awt.Color;
import java.net.Socket;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 *
 * @author Yunus Emre Atik (yunusemre.atik@stu.fsm.edu.tr)
 */
public class SClient {

    public int Id_SClient;
    public String name;
    public Color color;

    Socket socket;
    ObjectOutputStream sc_Output;
    ObjectInputStream sc_Input;

    Listen sc_Listen_Threadd;
    PairingThread sc_Pair_Thread;
    SClient friend;

    public boolean paired = false;
    public boolean is_Connected = false;
    public boolean temp_Rival = true;

    SClient(Socket clientSocket, int IdClient) {
        this.socket = clientSocket;
        this.Id_SClient = IdClient;
        try {
            this.sc_Output = new ObjectOutputStream(this.socket.getOutputStream());
            this.sc_Input = new ObjectInputStream(this.socket.getInputStream());

        } catch (IOException ex) {
            Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.sc_Listen_Threadd = new Listen(this);
        this.sc_Pair_Thread = new PairingThread(this);
        this.is_Connected = true;

    }

}

class Listen extends Thread {

    SClient sClient;

    Listen(SClient sClient) {
        this.sClient = sClient;
    }

    @Override
    public void run() {
        while (sClient.is_Connected) {
            try {
                Message received = (Message) (sClient.sc_Input.readObject());

                switch (received.type) {
                    case NameCheck: //name,color
                        ArrayList nameInfo = (ArrayList) received.content;
                        sClient.name = (String) nameInfo.get(0);
                        sClient.color = (Color) nameInfo.get(1);
                        Server.Bagli_Clientler();

                        Message msg = new Message(Message.Message_Type.Name);
                        msg.content = received.content;
                        Server.SendBroadCastMessage(msg);

                        break;
                    case IdCheck:   //showID (APP=0,PRIVATE=1)
                        ArrayList IdInfo = (ArrayList) received.content;
                        IdInfo.add(sClient.Id_SClient);
                        IdInfo.add(sClient.color);
                        Message msg0 = new Message(Message.Message_Type.Id);
                        msg0.content = IdInfo;
                        Server.Send(sClient, msg0); //showID,ID
                        break;
                    case SendClientsListCheck:
                        Message msgDeneme = new Message(Message.Message_Type.SendClientsList);
                        Server.SendClientList(msgDeneme);
                        break;
                    case SendBroadCastMessageCheck:
                        // name,color,text,emoji,onlineClientsNames(ARR),showID,onlineClientsID(ARR),String
                        Message msg2 = new Message(Message.Message_Type.SendBroadCastMessage);
                        msg2.content = received.content;
                        Server.SendBroadCastMessage(msg2);
                        break;
                    case SendFileCheck: //path,data,myname,showID,friendnames
                        Message msg3 = new Message(Message.Message_Type.SendFile);
                        msg3.content = received.content;
                        Server.SendBroadCastMessage(msg3);
                        break;
                    case DisconnectCheck:   //name,color    
                        Server.Clients.remove(sClient);
                        Server.Bagli_Clientler();
                        Message msg5 = new Message(Message.Message_Type.SendClientsList);
                        Server.SendClientList(msg5);
                        Message msg4 = new Message(Message.Message_Type.Disconnect);
                        msg4.content = received.content;
                        Server.SendBroadCastMessage(msg4);
                        break;
                    case PrivateConnectCheck:
                        Message msg6 = new Message(Message.Message_Type.PrivateConnect);
                        msg6.content = received.content;
                        Server.SendToPrivate(msg6);
                        break;
                    case PrivateCloseCheck:
                        Message msg7 = new Message(Message.Message_Type.PrivateClose);
                        msg7.content = received.content;
                        Server.SendToPrivate(msg7);
                        break;
                    case SendPrivateMessageCheck: // isim,color,girdi,emoji,friend(string ARR),showID,friendID
                        Message msg11 = new Message(Message.Message_Type.SendBroadCastMessage);
                        msg11.content = received.content;
                        Server.SendToPrivate(msg11);
                        break;
                    case SendFilePrivateCheck: //path,data,myname,showID,friendname,ID,ID
                        Message msg8 = new Message(Message.Message_Type.SendFile);
                        msg8.content = received.content;
                        Server.SendToPrivate(msg8);
                        break;
                    case CreateRoomCheck:   //ARR(roomname,odasahibiID)
                        ArrayList createRoomInfo = (ArrayList) received.content;

                        Message msg12 = new Message(Message.Message_Type.CreateRoom);
                        msg12.content = Server.Id_Room;
                        Server.Send(sClient, msg12);

                        Server.Rooms_Names.add(createRoomInfo.get(0).toString());
                        Server.Rooms_IDs.add(Server.Id_Room);
                        Server.Rooms_Sahip_Ids.add((int) createRoomInfo.get(1));

                        Message msg13 = new Message(Message.Message_Type.SendRoomsList);
                        Server.SendRoomList(msg13);
                        Server.Id_Room++;
                        break;
                    case SendRoomsListCheck:
                        Message msg1 = new Message(Message.Message_Type.SendRoomsList);
                        Server.SendRoomList(msg1);
                        break;
                    case RoomCloseCheck:    //Arr(roomname,roomID,odasahibiID,odasahibi or kişi) 
                        ArrayList roomInfo = (ArrayList) received.content;
                        if ((int) roomInfo.get(3) == 0) {    //oda sahibi kapatıyor
                            Server.Rooms_Names.remove(roomInfo.get(0).toString());
                            Server.Rooms_IDs.remove(roomInfo.get(1));
                            Server.Rooms_Sahip_Ids.remove(roomInfo.get(2));

                            ArrayList closeInfo = new ArrayList<>();
                            Message msg17 = new Message(Message.Message_Type.RoomClose);
                            closeInfo.add(roomInfo.get(3));        //odasahibi mi?
                            closeInfo.add((int) roomInfo.get(1));   //roomID
                            msg17.content = closeInfo;
                            Server.SendBroadCastMessage(msg17);

                            Message msg14 = new Message(Message.Message_Type.SendRoomsList);
                            Server.SendRoomList(msg14);
                        } else {   // odadaki birisi kapatıyor
                            ArrayList closeInfo =  new ArrayList<>();
                            Message msg17 = new Message(Message.Message_Type.RoomClose);
                            closeInfo.add(roomInfo.get(3));            //odasahibi mi?
                            closeInfo.add((int) roomInfo.get(1));      //roomID
                            closeInfo.add(roomInfo.get(4).toString()); //kapanankişi isim
                            closeInfo.add((int)roomInfo.get(5));       //kapanankişi id
                            msg17.content = closeInfo;
                            Server.SendBroadCastMessage(msg17);
                            
                        }

                        break;
                    case JoinRoomCheck:     //ARR(name,nameid,id,ownerId,roomID)
                        Message msg16 = new Message(Message.Message_Type.JoinRoom);
                        msg16.content = received.content;
                        Server.SendToPrivate(msg16);

                        break;
                    case SahipSendListCheck:
                        Message msg18 = new Message(Message.Message_Type.SahipSendList);
                        msg18.content = received.content;
                        Server.Deneme(msg18);
                        break;
                    case SendRoomCheck:
                        Message msg19 = new Message(Message.Message_Type.SendRoom);
                        msg19.content = received.content;
                        Server.SendBroadCastMessage(msg19);
                        break;
                }

            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("'" + sClient.name + "'" + " Client'i Kapandi");
                this.sClient.is_Connected = false;
                Server.Clients.remove(sClient);
                Server.Bagli_Clientler();

                Message msg1 = new Message(Message.Message_Type.SendClientsList);
                Server.SendClientList(msg1);

                ArrayList disconnect = new ArrayList<>();
                disconnect.add(sClient.name);
                disconnect.add(sClient.color);
                Message msg2 = new Message(Message.Message_Type.Disconnect);
                msg2.content = disconnect;
                Server.SendBroadCastMessage(msg2);
                break;

            }

        }

    }
}

class PairingThread extends Thread {

    SClient sClient;

    PairingThread(SClient sClient) {
        this.sClient = sClient;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(PairingThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (sClient.is_Connected && sClient.paired == false) {
            try {
                Server.lock.acquire(1);
                while (sClient.temp_Rival) {
                    for (SClient clnt : Server.Clients) {
                        if (this.sClient != clnt && clnt.friend == null) {
                            clnt.paired = true;
                            clnt.friend = sClient;

                            sClient.paired = true;
                            sClient.friend = clnt;
                            sClient.temp_Rival = false;

                            break;
                        }
                    }
                    sleep(1000);
                }

//                Message msg1 = new Message(Message.Message_Type.RivalConnected);
//                msg1.content = sClient.name;
//                Server.Send(sClient.friend, msg1);   //Rival'e kendi ismimi gonderiyorum
//
//                Message msg2 = new Message(Message.Message_Type.RivalConnected);
//                msg2.content = sClient.friend.name;
//                Server.Send(sClient, msg2);         //Kendime Rival ismini gonderiyorum
                Server.lock.release(1);
            } catch (InterruptedException ex) {
                System.out.println("Error in Pairing");
                Logger.getLogger(PairingThread.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
        }
    }
}
