package client;

import app.App;
import static app.App.ThisApp;
import app.Private;
import app.Message;
import app.Room;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author Yunus Emre Atik (yunusemre.atik@stu.fsm.edu.tr)
 */
public class Client {

    public static Socket socket;
    public static ObjectInputStream sInput;
    public static ObjectOutputStream sOutput;

    public static boolean isConnected = false;
    public static Listen listenMe;

    Private priv;

    ArrayList online_Clientss;

    public static void Start(String ip, int port) {
        try {
            System.out.println("Connecting to Server");
            Client.socket = new Socket(ip, port);
            System.out.println("Connected");

            App.ThisApp.isConnected = true;
            Client.isConnected = true;

            Client.sInput = new ObjectInputStream(Client.socket.getInputStream());
            Client.sOutput = new ObjectOutputStream(Client.socket.getOutputStream());

            Client.listenMe = new Listen();
            Client.listenMe.start();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(App.ThisApp, "Failed to Connect");
            App.ThisApp.txt_Name_Girdi.requestFocusInWindow();

            App.ThisApp.btn_Connect.setEnabled(true);
            App.ThisApp.txt_Name_Girdi.setEnabled(true);
            App.ThisApp.txt_Ip_Girdi.setEnabled(true);
            App.ThisApp.txt_Port_Girdi.setEnabled(true);

            App.ThisApp.isConnected = false;
            System.out.println("Failed to Connect");
        }
    }

    public static void Stop() {
        try {
            if (Client.socket != null) {
                Client.isConnected = false;
                Client.listenMe.stop();
                Client.socket.close();
                Client.sOutput.flush();
                Client.sOutput.close();

                Client.sInput.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void Send(Message msg) {

        try {
            Client.sOutput.writeObject(msg);
        } catch (IOException ex) {
            System.out.println("Client->Send'te Hata!");
        }

    }
}

class Listen extends Thread {

    String[] clients;
    Private priv;

    @Override
    public void run() {
        while (Client.isConnected) {
            try {
                Message received = (Message) (Client.sInput.readObject());

                switch (received.type) {
                    case Name:  //name,color
                        ArrayList nameInfo = (ArrayList) received.content;
                        String name = (String) nameInfo.get(0);
                        Color color = (Color) nameInfo.get(1);

                        StyledDocument doc = App.ThisApp.txtPanel_Ekran.getStyledDocument();
                        Style style_Join = App.ThisApp.txtPanel_Ekran.addStyle(null, null);
                        StyleConstants.setForeground(style_Join, color);

                        doc.insertString(doc.getLength(), name, style_Join);
                        StyleConstants.setForeground(style_Join, Color.black);
                        doc.insertString(doc.getLength(), " has joined the chat.", style_Join);

                        App.ThisApp.txtPanel_Ekran.setCaretPosition(App.ThisApp.txtPanel_Ekran.getDocument().getLength());
                        java.net.URL image = getClass().getResource("/app/images/Checking_Mark_Mini.png");
                        App.ThisApp.txtPanel_Ekran.insertIcon(new ImageIcon(image));
                        doc.insertString(doc.getLength(), "\n", style_Join);
                        break;
                    case Id:    //showID,Id,color
                        ArrayList IdInfo = (ArrayList) received.content;
                        if ((Integer) IdInfo.get(0) == 0) {     // App
                            App.ThisApp.App_ID = (int) IdInfo.get(1);
                        }
                        break;
                    case SendClientsList:    //names[String], ids[Integer]
                        ArrayList List_Client = (ArrayList) received.content;
                        String[] names_Client = (String[]) List_Client.get(0);        //a,b
                        Integer[] ids_Client = (Integer[]) List_Client.get(1);       //1,2

                        App.ThisApp.listClientsNamesModel.removeAllElements();
                        App.ThisApp.listClientsIdModel.removeAllElements();

                        App.online_Clients_Names.clear();
                        App.online_Cients_ID.clear();

                        for (int a = 0; a < names_Client.length; a++) {
                            App.ThisApp.listClientsNamesModel.addElement(names_Client[a]);   // Add names
                            App.ThisApp.listClientsIdModel.addElement(ids_Client[a]);       // Add ids

                            App.online_Clients_Names.add(names_Client[a]);
                            App.online_Cients_ID.add(ids_Client[a]);
                        }

                        App.ThisApp.lst_Clients_Names.setModel(App.ThisApp.listClientsNamesModel);
                        App.ThisApp.lst_Clients_Id.setModel(App.ThisApp.listClientsIdModel);

                        break;

                    case Disconnect: // name,App_Color,App_ID
                        ArrayList disconnectInfo = (ArrayList) received.content;
                        String nameDC = (String) disconnectInfo.get(0);
                        Color colorDC = (Color) disconnectInfo.get(1);

                        StyledDocument docDC = App.ThisApp.txtPanel_Ekran.getStyledDocument();
                        Style style_DC = App.ThisApp.txtPanel_Ekran.addStyle(null, null);
                        StyleConstants.setForeground(style_DC, colorDC);

                        docDC.insertString(docDC.getLength(), nameDC, style_DC);
                        StyleConstants.setForeground(style_DC, Color.black);
                        docDC.insertString(docDC.getLength(), " has disconnected.", style_DC);

                        App.ThisApp.txtPanel_Ekran.setCaretPosition(App.ThisApp.txtPanel_Ekran.getDocument().getLength());
                        java.net.URL imageDC = getClass().getResource("/app/images/Close_Mark_Mini.png");
                        App.ThisApp.txtPanel_Ekran.insertIcon(new ImageIcon(imageDC));
                        docDC.insertString(docDC.getLength(), "\n", style_DC);
                        for (int i = 0; i < App.online_Clients_Names.size(); i++) {
                            App.online_Clients_Names.remove(i);
                        }

                        break;

                    case SendFile:  //path,data,myname,showID,friendname
                        ArrayList Infos = (ArrayList) received.content;
                        switch ((Integer) Infos.get(3)) {
                            case 0:     //AppID
                                if (App.ThisApp.can_Send_File == true) {

                                    int cevap = JOptionPane.showConfirmDialog(App.ThisApp, (String) Infos.get(2) + " herkese " + (String) Infos.get(0)
                                            + " adlı dosayı gönderemek istiyor?\n", "Dosya Aktarimi", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                                    if (cevap == JOptionPane.YES_OPTION) {
                                        byte[] content = (byte[]) Infos.get(1);

                                        File receiveFile = new File((String) Infos.get(0));
                                        Files.write(receiveFile.toPath(), content);
                                    }
                                } else {
                                    App.ThisApp.can_Send_File = true;
                                }
                                break;
                            case 1:     //PrivateID
                                for (Private privR : App.ThisApp.private_Rooms) {
                                    if (privR.lbl_Room_Name.getText().equals((String) Infos.get(7))) {
                                        int cevap = JOptionPane.showConfirmDialog(privR, (String) Infos.get(2) + " sana " + (String) Infos.get(0)
                                                + " adlı dosayı gönderemek istiyor?\n", "Dosya Aktarimi", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                                        if (cevap == JOptionPane.YES_OPTION) {
                                            byte[] content = (byte[]) Infos.get(1);

                                            File receiveFile = new File((String) Infos.get(0));
                                            Files.write(receiveFile.toPath(), content);
                                        }
                                    }
                                }
                                break;
                            case 2:     //RoomID
                                for (Room public_Room : App.ThisApp.public_Rooms) {
                                    if (public_Room.ThisRoom.room_ID == (int) Infos.get(5)) {   // 
                                        if (public_Room.ThisRoom.send_File_Control == true) {
                                            int cevap = JOptionPane.showConfirmDialog(public_Room, (String) Infos.get(2) + " odadaki herkese " + (String) Infos.get(0)
                                                    + " adlı dosayı gönderemek istiyor?\n", "Dosya Aktarimi", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                                            if (cevap == JOptionPane.YES_OPTION) {
                                                byte[] content = (byte[]) Infos.get(1);

                                                File receiveFile = new File((String) Infos.get(0));
                                                Files.write(receiveFile.toPath(), content);
                                            }
                                        } else {
                                            public_Room.ThisRoom.send_File_Control = true;
                                        }
                                    }
                                }
                                break;
                        }
                        break;
                    case PrivateClose:
                        ArrayList privateCloseInfo = (ArrayList) received.content;
                        int t = 0;
                        for (Private privR : App.ThisApp.private_Rooms) {
                            if (privR.lbl_Room_Name.getText().equals((String) privateCloseInfo.get(7))) {
                                JOptionPane.showMessageDialog(privR, privateCloseInfo.get(3) + " Özel Chat'ten Ayrıldı.");
                                privR.setVisible(false);
                                break;
                            }
                            t++;
                        }
                        App.ThisApp.private_Rooms.remove(t);
                        //App.ThisApp.same_Person_Control = 1453;

                        break;
                    case PrivateConnect:  //myname,mycolor,useless,my App_ID,friendname(ARR),friendID,friendID(ARR),room_name
                        ArrayList privateInfo = (ArrayList) received.content;
                        int[] c = new int[1];
                        c[0] = (int) privateInfo.get(5);
                        Private pr = new Private(App.ThisApp.txt_Name_Girdi.getText(), c[0], (String) privateInfo.get(0), (int) privateInfo.get(3), App.ThisApp.App_Color, (String) privateInfo.get(7));
                        pr.setLocationRelativeTo(App.ThisApp);
                        App.ThisApp.private_Rooms.add(pr);
                        //App.ThisApp.same_Person_Control = (int) privateInfo.get(3);
                        pr.setVisible(true);

                        break;
                    case SendRoomsList: //names[String], ids[Integer]
                        ArrayList List_Room = (ArrayList) received.content;
                        String[] rooms_Names = (String[]) List_Room.get(0);
                        Integer[] rooms_IDs = (Integer[]) List_Room.get(1);
                        Integer[] rooms_Owner_IDs = (Integer[]) List_Room.get(2);

                        App.ThisApp.listRoomsNamesModel.removeAllElements();
                        App.ThisApp.listRoomsIdModel.removeAllElements();
                        App.ThisApp.listRoomsOwnerIdModel.removeAllElements();

                        for (int a = 0; a < rooms_Names.length; a++) {
                            App.ThisApp.listRoomsNamesModel.addElement(rooms_Names[a]);
                            App.ThisApp.listRoomsIdModel.addElement(rooms_IDs[a]);
                            App.ThisApp.listRoomsOwnerIdModel.addElement(rooms_Owner_IDs[a]);

                        }
                        App.ThisApp.lst_Rooms_Names.setModel(App.ThisApp.listRoomsNamesModel);
                        App.ThisApp.lst_Rooms_Id.setModel(App.ThisApp.listRoomsIdModel);
                        App.ThisApp.lst_Room_Owner_Id.setModel(App.ThisApp.listRoomsOwnerIdModel);
                        break;
                    case CreateRoom:    //RoomID
                        Room room = new Room(App.ThisApp.txt_Room_Name_Girdi.getText(), (int) received.content, App.ThisApp.App_ID,
                                App.ThisApp.App_ID, App.ThisApp.txt_Name_Girdi.getText(), App.ThisApp.App_Color);
                        room.setLocationRelativeTo(ThisApp);
                        room.ThisRoom.online_clients_names.add(App.ThisApp.txt_Name_Girdi.getText());
                        room.ThisRoom.online_clients_ids.add(App.ThisApp.App_ID);

                        room.ThisRoom.listClientsNamesModel.addElement((String) App.ThisApp.txt_Name_Girdi.getText());
                        room.ThisRoom.listClientsIdModel.addElement(App.ThisApp.App_ID);

                        room.ThisRoom.lst_Clients_Names.setModel(room.ThisRoom.listClientsNamesModel);
                        room.ThisRoom.lst_Clients_Id.setModel(room.ThisRoom.listClientsIdModel);
                        App.ThisApp.public_Rooms.add(room);
                        room.setVisible(true);

                        App.ThisApp.txt_Room_Name_Girdi.setText("");
                        break;
                    case SendRoom:
                        App.ThisApp.public_Rooms.add((Room) received.content);
                        break;
                    case RoomClose: //odasahibi mi?, roomID, kapapnkişi isim,kapanankişi id
                        ArrayList Room_Close = (ArrayList) received.content;
                        if ((int) Room_Close.get(0) == 0) { //oda sahibi
                            for (int i = 0; i < App.ThisApp.public_Rooms.size(); i++) {
                                if (App.ThisApp.public_Rooms.get(i).ThisRoom.room_ID == (int) Room_Close.get(1)) {
                                    App.ThisApp.public_Rooms.get(i).ThisRoom.setVisible(false);
                                    App.ThisApp.public_Rooms.remove(App.ThisApp.public_Rooms.get(i));
                                    break;
                                }
                            }
                        } else {   //oda sahibi değil 
                            for (Room public_Room : App.ThisApp.public_Rooms) {
                                if (public_Room.ThisRoom.room_ID == (int) Room_Close.get(1)) {
                                    public_Room.ThisRoom.online_clients_names.remove(Room_Close.get(2).toString());
                                    public_Room.ThisRoom.online_clients_ids.remove(Room_Close.get(3));

                                    public_Room.ThisRoom.listClientsNamesModel.removeElement(Room_Close.get(2).toString());
                                    public_Room.ThisRoom.listClientsIdModel.removeElement(Room_Close.get(3));

                                    public_Room.ThisRoom.lst_Clients_Names.setModel(public_Room.ThisRoom.listClientsNamesModel);
                                    public_Room.ThisRoom.lst_Clients_Id.setModel(public_Room.ThisRoom.listClientsIdModel);
                                }
                            }
                        }

                        break;
                    case JoinRoom:  //ARR(name,nameid,ownerid,roomID)
                        System.out.println("geldi");
                        ArrayList Join_Info = (ArrayList) received.content;

                        for (Room public_Room : App.ThisApp.public_Rooms) {
                            if (public_Room.ThisRoom.room_ID == (int) Join_Info.get(3)) {   //Gelen kisini katıldığı odadyı buluyor

                                public_Room.ThisRoom.online_clients_names.add(Join_Info.get(0).toString());
                                public_Room.ThisRoom.online_clients_ids.add((int) Join_Info.get(1));

                                App.cikti(public_Room.ThisRoom.online_clients_names);

                                ArrayList dene = new ArrayList();
                                ArrayList dene1 = new ArrayList();
                                for (int i = 0; i < public_Room.ThisRoom.online_clients_names.size(); i++) {
                                    dene.add(public_Room.ThisRoom.online_clients_names.get(i));
                                }
                                for (int i = 0; i < public_Room.ThisRoom.online_clients_ids.size(); i++) {
                                    dene1.add(public_Room.ThisRoom.online_clients_ids.get(i));
                                }

                                ArrayList JInfo = new ArrayList<>();
                                Message msg = new Message(Message.Message_Type.SahipSendListCheck);
                                JInfo.add(dene);
                                JInfo.add(dene1);
                                JInfo.add(public_Room.ThisRoom.room_ID);
                                msg.content = JInfo;
                                Client.Send(msg);
                                break;
                            }

                        }
                        break;
                    case SahipSendList: // Arr(Arr(names),Arr(ids),roomID)
                        ArrayList Clients_Info = (ArrayList) received.content;
                        ArrayList NAMES = (ArrayList) Clients_Info.get(0);
                        ArrayList IDS = (ArrayList) Clients_Info.get(1);
                        int ROOM_ID = (int) Clients_Info.get(2);
                        for (Room public_Room : App.ThisApp.public_Rooms) {
                            if (public_Room.ThisRoom.room_ID == ROOM_ID) {
                                public_Room.ThisRoom.listClientsNamesModel.removeAllElements();
                                public_Room.ThisRoom.listClientsIdModel.removeAllElements();

                                for (int i = 0; i < NAMES.size(); i++) {
                                    public_Room.ThisRoom.listClientsNamesModel.addElement(NAMES.get(i).toString());
                                    public_Room.ThisRoom.listClientsIdModel.addElement((int) IDS.get(i));

                                }
                                public_Room.ThisRoom.lst_Clients_Names.setModel(public_Room.ThisRoom.listClientsNamesModel);
                                public_Room.ThisRoom.lst_Clients_Id.setModel(public_Room.ThisRoom.listClientsIdModel);
                            }
                        }
                        break;
//                    case CreateId:
//                        int roomId=(int)received.content;
//                        App.ThisApp.check_Room_ID=roomId;
//                        break;
                    case SendBroadCastMessage:  // name,App_Color,girdi,emoji,friend,showID
                        ArrayList messageInfo = (ArrayList) received.content;
                        String name1 = (String) messageInfo.get(0);         //name
                        Color color1 = (Color) messageInfo.get(1);          //color
                        String girdi1 = (String) messageInfo.get(2);        //girdi
                        ArrayList emoji = (ArrayList) messageInfo.get(3);   //emojiler
                        int ID = (Integer) messageInfo.get(5);              //showID
                        String gelen_Room_Name = (String) messageInfo.get(7);   //roomname
                        int roomId = (Integer) messageInfo.get(8);              //roomId

                        switch (ID) {
                            case 0:     //AppID
                                App.ThisApp.txtPane_Girdi.setText("");
                                StyledDocument doc1 = App.ThisApp.txtPanel_Ekran.getStyledDocument();
                                Style style_Message = App.ThisApp.txtPanel_Ekran.addStyle(null, null);
                                StyleConstants.setForeground(style_Message, color1);        //rengi isim rengi yaptik                                
                                doc1.insertString(doc1.getLength(), name1, style_Message);  //ismi yazdik

                                StyleConstants.setForeground(style_Message, Color.black);   //rengi yazi rengi yaptik
                                int a = App.ThisApp.txtPanel_Ekran.getDocument().getLength() + 4;
                                doc1.insertString(doc1.getLength(), " : " + girdi1, style_Message);  //girdi'yi yazdik

                                for (int i = 0; i < emoji.size(); i++) {
                                    switch ((int) emoji.get(i)) {//gozkirpma,cry,teeth,tedirgin,dead,mouthlesss,saskin
                                        case 0:     //Smile Emoji
                                            i++;
                                            App.ThisApp.txtPanel_Ekran.setCaretPosition((a + (int) emoji.get(i)));                  //location 
                                            java.net.URL image1 = getClass().getResource("/app/images/Emoji_Smile_Mini.png");       //path
                                            App.ThisApp.txtPanel_Ekran.insertIcon(new ImageIcon(image1));                           //insert
                                            break;
                                        case 1:     //Smile Teeth Emoji
                                            i++;
                                            App.ThisApp.txtPanel_Ekran.setCaretPosition((a + (int) emoji.get(i)));                  //location 
                                            java.net.URL image2 = getClass().getResource("/app/images/Emoji_Smile_Teeth_Mini.png"); //path
                                            App.ThisApp.txtPanel_Ekran.insertIcon(new ImageIcon(image2));                           //insert
                                            break;
                                        case 2:     //Goz Kirpma Emoji
                                            i++;
                                            App.ThisApp.txtPanel_Ekran.setCaretPosition((a + (int) emoji.get(i)));                  //location 
                                            java.net.URL image3 = getClass().getResource("/app/images/Emoji_Goz_Kirpma_Mini.png");  //path
                                            App.ThisApp.txtPanel_Ekran.insertIcon(new ImageIcon(image3));                           //insert
                                            break;
                                        case 3:     //Cry Emoji
                                            i++;
                                            App.ThisApp.txtPanel_Ekran.setCaretPosition((a + (int) emoji.get(i)));                  //location 
                                            java.net.URL image4 = getClass().getResource("/app/images/Emoji_Cry_Mini.png");         //path
                                            App.ThisApp.txtPanel_Ekran.insertIcon(new ImageIcon(image4));                           //insert
                                            break;
                                        case 4:     //Teeth Emoji
                                            i++;
                                            App.ThisApp.txtPanel_Ekran.setCaretPosition((a + (int) emoji.get(i)));                  //location 
                                            java.net.URL image5 = getClass().getResource("/app/images/Emoji_Teeth_Mini.png");       //path
                                            App.ThisApp.txtPanel_Ekran.insertIcon(new ImageIcon(image5));                           //insert
                                            break;
                                        case 5:     //Tedirgin Emoji
                                            i++;
                                            App.ThisApp.txtPanel_Ekran.setCaretPosition((a + (int) emoji.get(i)));                  //location 
                                            java.net.URL image6 = getClass().getResource("/app/images/Emoji_Tedirgin_Mini.png");    //path
                                            App.ThisApp.txtPanel_Ekran.insertIcon(new ImageIcon(image6));                           //insert
                                            break;
                                        case 6:     //Dead Emoji
                                            i++;
                                            App.ThisApp.txtPanel_Ekran.setCaretPosition((a + (int) emoji.get(i)));                  //location 
                                            java.net.URL image7 = getClass().getResource("/app/images/Emoji_Dead_Mini.png");        //path
                                            App.ThisApp.txtPanel_Ekran.insertIcon(new ImageIcon(image7));                           //insert
                                            break;
                                        case 7:     //Mouthless Emoji
                                            i++;
                                            App.ThisApp.txtPanel_Ekran.setCaretPosition((a + (int) emoji.get(i)));                  //location 
                                            java.net.URL image8 = getClass().getResource("/app/images/Emoji_Mouthless_Mini.png");   //path
                                            App.ThisApp.txtPanel_Ekran.insertIcon(new ImageIcon(image8));                           //insert
                                            break;
                                        case 8:     //Saskin Emoji
                                            i++;
                                            App.ThisApp.txtPanel_Ekran.setCaretPosition((a + (int) emoji.get(i)));                  //location 
                                            java.net.URL image9 = getClass().getResource("/app/images/Emoji_Saskin_Mini.png");      //path
                                            App.ThisApp.txtPanel_Ekran.insertIcon(new ImageIcon(image9));                           //insert
                                            break;

                                    }
                                }
                                doc1.insertString(doc1.getLength(), "\n", style_Message);
                                App.emoji.clear();
                                break;
                            case 1:     //PrivateID
                                for (Private priv : App.ThisApp.private_Rooms) {
                                    System.out.println(priv.lbl_Room_Name.getText());
                                    System.out.println(priv.friendID + "," + priv.ID);
                                    if (priv.lbl_Room_Name.getText().equals(gelen_Room_Name)) {
                                        priv.txtPanel_Girdi.setText("");
                                        StyledDocument doc2 = priv.txtPanel_Ekran.getStyledDocument();
                                        Style style_Private = priv.txtPanel_Ekran.addStyle(null, null);
                                        StyleConstants.setForeground(style_Private, color1);        //rengi isim rengi yaptik                                
                                        doc2.insertString(doc2.getLength(), name1, style_Private);  //ismi yazdik

                                        StyleConstants.setForeground(style_Private, Color.black);   //rengi yazi rengi yaptik
                                        int b = priv.txtPanel_Ekran.getDocument().getLength() + 4;
                                        doc2.insertString(doc2.getLength(), " : " + girdi1, style_Private);  //girdi'yi yazdik

                                        for (int i = 0; i < emoji.size(); i++) {
                                            switch ((int) emoji.get(i)) {//gozkirpma,cry,teeth,tedirgin,dead,mouthlesss,saskin
                                                case 0:     //Smile Emoji
                                                    i++;
                                                    priv.txtPanel_Ekran.setCaretPosition((b + (int) emoji.get(i)));                  //location 
                                                    java.net.URL image1 = getClass().getResource("/app/images/Emoji_Smile_Mini.png");       //path
                                                    priv.txtPanel_Ekran.insertIcon(new ImageIcon(image1));                           //insert
                                                    break;
                                                case 1:     //Smile Teeth Emoji
                                                    i++;
                                                    priv.txtPanel_Ekran.setCaretPosition((b + (int) emoji.get(i)));                  //location 
                                                    java.net.URL image2 = getClass().getResource("/app/images/Emoji_Smile_Teeth_Mini.png"); //path
                                                    priv.txtPanel_Ekran.insertIcon(new ImageIcon(image2));                           //insert
                                                    break;
                                                case 2:     //Goz Kirpma Emoji
                                                    i++;
                                                    priv.txtPanel_Ekran.setCaretPosition((b + (int) emoji.get(i)));                  //location 
                                                    java.net.URL image3 = getClass().getResource("/app/images/Emoji_Goz_Kirpma_Mini.png");  //path
                                                    priv.txtPanel_Ekran.insertIcon(new ImageIcon(image3));                           //insert
                                                    break;
                                                case 3:     //Cry Emoji
                                                    i++;
                                                    priv.txtPanel_Ekran.setCaretPosition((b + (int) emoji.get(i)));                  //location 
                                                    java.net.URL image4 = getClass().getResource("/app/images/Emoji_Cry_Mini.png");         //path
                                                    priv.txtPanel_Ekran.insertIcon(new ImageIcon(image4));                           //insert
                                                    break;
                                                case 4:     //Teeth Emoji
                                                    i++;
                                                    priv.txtPanel_Ekran.setCaretPosition((b + (int) emoji.get(i)));                  //location 
                                                    java.net.URL image5 = getClass().getResource("/app/images/Emoji_Teeth_Mini.png");       //path
                                                    priv.txtPanel_Ekran.insertIcon(new ImageIcon(image5));                           //insert
                                                    break;
                                                case 5:     //Tedirgin Emoji
                                                    i++;
                                                    priv.txtPanel_Ekran.setCaretPosition((b + (int) emoji.get(i)));                  //location 
                                                    java.net.URL image6 = getClass().getResource("/app/images/Emoji_Tedirgin_Mini.png");    //path
                                                    priv.txtPanel_Ekran.insertIcon(new ImageIcon(image6));                           //insert
                                                    break;
                                                case 6:     //Dead Emoji
                                                    i++;
                                                    priv.txtPanel_Ekran.setCaretPosition((b + (int) emoji.get(i)));                  //location 
                                                    java.net.URL image7 = getClass().getResource("/app/images/Emoji_Dead_Mini.png");        //path
                                                    priv.txtPanel_Ekran.insertIcon(new ImageIcon(image7));                           //insert
                                                    break;
                                                case 7:     //Mouthless Emoji
                                                    i++;
                                                    priv.txtPanel_Ekran.setCaretPosition((b + (int) emoji.get(i)));                  //location 
                                                    java.net.URL image8 = getClass().getResource("/app/images/Emoji_Mouthless_Mini.png");   //path
                                                    priv.txtPanel_Ekran.insertIcon(new ImageIcon(image8));                           //insert
                                                    break;
                                                case 8:     //Saskin Emoji
                                                    i++;
                                                    priv.txtPanel_Ekran.setCaretPosition((b + (int) emoji.get(i)));                  //location 
                                                    java.net.URL image9 = getClass().getResource("/app/images/Emoji_Saskin_Mini.png");      //path
                                                    priv.txtPanel_Ekran.insertIcon(new ImageIcon(image9));                           //insert
                                                    break;

                                            }
                                        }
                                        doc2.insertString(doc2.getLength(), "\n", style_Private);
                                        priv.txtPanel_Girdi.setText("");
                                        App.emoji.clear();
                                        priv.txtPanel_Girdi.setText("");
                                    }
                                }

                                break;
                            case 2:     //RoomID
                                for (Room public_Room : App.ThisApp.public_Rooms) {
                                    if (public_Room.ThisRoom.room_ID == roomId) {
                                        public_Room.txtPanel_Girdi.setText("");
                                        StyledDocument doc2 = public_Room.txtPanel_Ekran.getStyledDocument();
                                        Style style_Private = public_Room.txtPanel_Ekran.addStyle(null, null);
                                        StyleConstants.setForeground(style_Private, color1);        //rengi isim rengi yaptik                                
                                        doc2.insertString(doc2.getLength(), name1, style_Private);  //ismi yazdik

                                        StyleConstants.setForeground(style_Private, Color.black);   //rengi yazi rengi yaptik
                                        int b = public_Room.txtPanel_Ekran.getDocument().getLength() + 4;
                                        doc2.insertString(doc2.getLength(), " : " + girdi1, style_Private);  //girdi'yi yazdik

                                        for (int i = 0; i < emoji.size(); i++) {
                                            switch ((int) emoji.get(i)) {//gozkirpma,cry,teeth,tedirgin,dead,mouthlesss,saskin
                                                case 0:     //Smile Emoji
                                                    i++;
                                                    public_Room.txtPanel_Ekran.setCaretPosition((b + (int) emoji.get(i)));                  //location 
                                                    java.net.URL image1 = getClass().getResource("/app/images/Emoji_Smile_Mini.png");       //path
                                                    public_Room.txtPanel_Ekran.insertIcon(new ImageIcon(image1));                           //insert
                                                    break;
                                                case 1:     //Smile Teeth Emoji
                                                    i++;
                                                    public_Room.txtPanel_Ekran.setCaretPosition((b + (int) emoji.get(i)));                  //location 
                                                    java.net.URL image2 = getClass().getResource("/app/images/Emoji_Smile_Teeth_Mini.png"); //path
                                                    public_Room.txtPanel_Ekran.insertIcon(new ImageIcon(image2));                           //insert
                                                    break;
                                                case 2:     //Goz Kirpma Emoji
                                                    i++;
                                                    public_Room.txtPanel_Ekran.setCaretPosition((b + (int) emoji.get(i)));                  //location 
                                                    java.net.URL image3 = getClass().getResource("/app/images/Emoji_Goz_Kirpma_Mini.png");  //path
                                                    public_Room.txtPanel_Ekran.insertIcon(new ImageIcon(image3));                           //insert
                                                    break;
                                                case 3:     //Cry Emoji
                                                    i++;
                                                    public_Room.txtPanel_Ekran.setCaretPosition((b + (int) emoji.get(i)));                  //location 
                                                    java.net.URL image4 = getClass().getResource("/app/images/Emoji_Cry_Mini.png");         //path
                                                    public_Room.txtPanel_Ekran.insertIcon(new ImageIcon(image4));                           //insert
                                                    break;
                                                case 4:     //Teeth Emoji
                                                    i++;
                                                    public_Room.txtPanel_Ekran.setCaretPosition((b + (int) emoji.get(i)));                  //location 
                                                    java.net.URL image5 = getClass().getResource("/app/images/Emoji_Teeth_Mini.png");       //path
                                                    public_Room.txtPanel_Ekran.insertIcon(new ImageIcon(image5));                           //insert
                                                    break;
                                                case 5:     //Tedirgin Emoji
                                                    i++;
                                                    public_Room.txtPanel_Ekran.setCaretPosition((b + (int) emoji.get(i)));                  //location 
                                                    java.net.URL image6 = getClass().getResource("/app/images/Emoji_Tedirgin_Mini.png");    //path
                                                    public_Room.txtPanel_Ekran.insertIcon(new ImageIcon(image6));                           //insert
                                                    break;
                                                case 6:     //Dead Emoji
                                                    i++;
                                                    public_Room.txtPanel_Ekran.setCaretPosition((b + (int) emoji.get(i)));                  //location 
                                                    java.net.URL image7 = getClass().getResource("/app/images/Emoji_Dead_Mini.png");        //path
                                                    public_Room.txtPanel_Ekran.insertIcon(new ImageIcon(image7));                           //insert
                                                    break;
                                                case 7:     //Mouthless Emoji
                                                    i++;
                                                    public_Room.txtPanel_Ekran.setCaretPosition((b + (int) emoji.get(i)));                  //location 
                                                    java.net.URL image8 = getClass().getResource("/app/images/Emoji_Mouthless_Mini.png");   //path
                                                    public_Room.txtPanel_Ekran.insertIcon(new ImageIcon(image8));                           //insert
                                                    break;
                                                case 8:     //Saskin Emoji
                                                    i++;
                                                    public_Room.txtPanel_Ekran.setCaretPosition((b + (int) emoji.get(i)));                  //location 
                                                    java.net.URL image9 = getClass().getResource("/app/images/Emoji_Saskin_Mini.png");      //path
                                                    public_Room.txtPanel_Ekran.insertIcon(new ImageIcon(image9));                           //insert
                                                    break;

                                            }
                                        }
                                        doc2.insertString(doc2.getLength(), "\n", style_Private);
                                        public_Room.txtPanel_Girdi.setText("");
                                        App.emoji.clear();
                                        public_Room.txtPanel_Girdi.setText("");

                                    }
                                }

                                break;
                        }
                        break;
                }

            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("Kapandım");
                Client.Stop();
                break;
            } catch (BadLocationException ex) {
                System.out.println("Client Bad LocATİON 470");
            }
            //Client.Stop();

        }

    }
}
