package app;

import client.Client;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author Yunus Emre Atik (yunusemre.atik@stu.fsm.edu.tr)
 */
public class App extends javax.swing.JFrame {

    public static App ThisApp;                              // Create ThisApp
    public Color App_Color;                                 // App's Color

    public int App_ID;                                      // App's ID
    public int show_ID = 0;                                 // Show Message ID (App,Private,Room)
    public int room_ID = 0;
    public int check_Room_ID;

    public DefaultListModel listClientsNamesModel;          // Clients Names ListModel
    public DefaultListModel listClientsIdModel;             // Clients Ids   ListModel
    public DefaultListModel listRoomsNamesModel;            // Rooms   Names ListModel
    public DefaultListModel listRoomsIdModel;               // Rooms   Ids   ListModel
    public DefaultListModel listRoomsOwnerIdModel;          // Rooms Owner Ids   ListModel

    public boolean room_Entry_Control = false;              // Clear room txtfield on key touch
    public boolean name_Entry_Control = false;              // Clear name txtfield on key touch
    public boolean can_Send_File = true;                    // Don't send file yourself Control
    public boolean isConnected = false;                     // isConnected Control
    public boolean text_Entry_Control = true;               // Don't catch ENTER on every screen Control

    public boolean same_Person_Control = true;              // Don't message to same person Control
    public boolean same_Room_Control = true;                // Don't join to same person Room

    public static ArrayList<Integer> emoji = new ArrayList<>();                 // Emoji 
    public static ArrayList<String> online_Clients_Names = new ArrayList<>();   // Clients Names
    public static ArrayList online_Cients_ID = new ArrayList<>();               // Clients ID

    public ArrayList<String> public_Rooms_Names = new ArrayList<>();     // Clients Names
    public ArrayList public_Rooms_Id = new ArrayList<>();                // Clients ID

    public ArrayList<Room> public_Rooms = new ArrayList<>();                    // Public Rooms
    public ArrayList<Private> private_Rooms = new ArrayList<>();         // Private Rooms

    public static Color[] colors = {Color.BLUE, Color.red, Color.cyan, Color.green, Color.pink, Color.yellow, Color.orange};

    public App() {
        initComponents();
        ThisApp = this;

        Color frameColor = new Color(255, 204, 204);
        ThisApp.getContentPane().setBackground(frameColor);             // Change Backgorund Color

        listClientsNamesModel = new DefaultListModel();
        listClientsIdModel = new DefaultListModel();
        listRoomsIdModel = new DefaultListModel();
        listRoomsNamesModel = new DefaultListModel();
        listRoomsOwnerIdModel = new DefaultListModel();

        lst_Clients_Names.setModel(listClientsNamesModel);              // Set Clients Names ListModel
        lst_Clients_Id.setModel(listClientsIdModel);                    // Set Clients  ID   ListModel
        lst_Rooms_Names.setModel(listRoomsNamesModel);                  // Set Rooms   Names ListModel
        lst_Rooms_Id.setModel(listRoomsIdModel);                        // Set Rooms    ID   ListModel
        lst_Room_Owner_Id.setModel(listRoomsOwnerIdModel);

        txt_Name_Girdi.requestFocus();                                  // Focus Name Girdi
        txt_Room_Name_Girdi.setEnabled(false);
        txtPane_Girdi.setEnabled(false);

        btn_Disconnect.setEnabled(false);
        btn_Send_Private_Message.setEnabled(false);
        btn_Join_Room.setEnabled(false);
        btn_Create_Room.setEnabled(false);

        lst_Clients_Names.setEnabled(false);
        lst_Clients_Id.setEnabled(false);
        lst_Rooms_Names.setEnabled(false);
        lst_Rooms_Id.setEnabled(false);

        lbl_Connected.setVisible(false);
        lbl_Disconnected.setVisible(false);

        Random rand = new Random();                 // Create a Random Number
        int a = rand.nextInt(7);
        App_Color = colors[a];                      // Assign a Random Color
        
        DefaultCaret caret = (DefaultCaret) txtPanel_Ekran.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        ThisApp.addWindowListener(exitListener);
    }
    WindowListener exitListener = new WindowAdapter() {

        @Override
        public void windowClosing(WindowEvent e) {
            for (int i = 0; i < private_Rooms.size(); i++) {
                private_Rooms.get(i).ThisPrivate.btn_Disconnect.doClick();
            }
            for (int i = 0; i < public_Rooms.size(); i++) {
                public_Rooms.get(i).ThisRoom.btn_Disconnect.doClick();
            }
            private_Rooms.clear();
            public_Rooms.clear();
            System.exit(0);
            ThisApp.setVisible(false);
        }
    };

    public static void display_ArrayList(ArrayList arr) {

        System.out.print("Display ArrayList: ");
        for (int i = 0; i < arr.size(); i++) {
            System.out.print((String) arr.get(i) + ",");
        }
        System.out.println("");
    }

    public static void display_Integer(int a) {
        System.out.println("Display Integer: " + a);
    }

    public static void cikti(ArrayList arr) {
        System.out.print("Cikti: ");
        for (int i = 0; i < arr.size(); i++) {
            System.out.print(arr.get(i) + ",");
        }
        System.out.println("");
    }

    public static void ciktii() {
        for (Private private_Room : ThisApp.private_Rooms) {
            System.out.println(private_Room.lbl_Room_Name.getText());
        }
    }

    public static void denek(int id) {
        ArrayList JInfo = new ArrayList<>();
        Message msg = new Message(Message.Message_Type.SahipSendListCheck);
        for (Room r : App.ThisApp.public_Rooms) {
            if (r.ThisRoom.room_ID == id) {
                JInfo.add(r.ThisRoom.online_clients_names);
                JInfo.add(r.ThisRoom.online_clients_ids);
                JInfo.add(r.ThisRoom.room_ID);
                break;
            }
        }

        msg.content = JInfo;
        Client.Send(msg);
    }

    public static void myList() {
        System.out.print("myList: ");
        for (int i = 0; i < online_Clients_Names.size(); i++) {
            System.out.print(online_Clients_Names.get(i) + ",");
        }
        System.out.println("");
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel7 = new javax.swing.JLabel();
        txt_Room_Name_Girdi = new javax.swing.JTextField();
        txt_Ip_Girdi = new javax.swing.JTextField();
        txt_Port_Girdi = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btn_Disconnect = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        lst_Clients_Names = new javax.swing.JList<>();
        btn_Send_Private_Message = new javax.swing.JButton();
        txt_Selected_Room = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        lst_Rooms_Names = new javax.swing.JList<>();
        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txt_Selected_User = new javax.swing.JTextField();
        btn_Join_Room = new javax.swing.JButton();
        txt_Name_Girdi = new javax.swing.JTextField();
        btn_Create_Room = new javax.swing.JButton();
        btn_Connect = new javax.swing.JButton();
        lbl_Connected = new javax.swing.JLabel();
        lbl_Disconnected = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtPanel_Ekran = new javax.swing.JTextPane();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtPane_Girdi = new javax.swing.JTextPane();
        lbl_File_Send = new javax.swing.JLabel();
        emoji_Smile = new javax.swing.JLabel();
        emoji_Smile_Teeth = new javax.swing.JLabel();
        emoji_Goz_Kirpma = new javax.swing.JLabel();
        emoji_Cry = new javax.swing.JLabel();
        emoji_Teeth = new javax.swing.JLabel();
        emoji_Tedirgin = new javax.swing.JLabel();
        emoji_Dead = new javax.swing.JLabel();
        emoji_Mouthless = new javax.swing.JLabel();
        emoji_Saskin = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lst_Clients_Id = new javax.swing.JList<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        lst_Rooms_Id = new javax.swing.JList<>();
        jScrollPane7 = new javax.swing.JScrollPane();
        lst_Room_Owner_Id = new javax.swing.JList<>();

        jLabel7.setText("jLabel7");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ChatApp");
        setBackground(new java.awt.Color(255, 204, 204));

        txt_Room_Name_Girdi.setForeground(new java.awt.Color(196, 196, 200));
        txt_Room_Name_Girdi.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_Room_Name_Girdi.setText(" Type ...");
        txt_Room_Name_Girdi.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txt_Room_Name_Girdi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_Room_Name_GirdiKeyPressed(evt);
            }
        });

        txt_Ip_Girdi.setForeground(new java.awt.Color(153, 153, 153));
        txt_Ip_Girdi.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_Ip_Girdi.setText("18.117.221.118");
        txt_Ip_Girdi.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txt_Ip_Girdi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_Ip_GirdiActionPerformed(evt);
            }
        });

        txt_Port_Girdi.setForeground(new java.awt.Color(153, 153, 153));
        txt_Port_Girdi.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_Port_Girdi.setText("2000");
        txt_Port_Girdi.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("IP");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("PORT");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("NAME");

        btn_Disconnect.setBackground(new java.awt.Color(255, 51, 51));
        btn_Disconnect.setText("Disconnect");
        btn_Disconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_DisconnectActionPerformed(evt);
            }
        });

        lst_Clients_Names.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lst_Clients_Names.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lst_Clients_NamesMousePressed(evt);
            }
        });
        jScrollPane2.setViewportView(lst_Clients_Names);

        btn_Send_Private_Message.setBackground(new java.awt.Color(255, 204, 0));
        btn_Send_Private_Message.setText("Send Private Message");
        btn_Send_Private_Message.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_Send_Private_Message.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Send_Private_MessageActionPerformed(evt);
            }
        });

        txt_Selected_Room.setEditable(false);
        txt_Selected_Room.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_Selected_Room.setEnabled(false);

        jPanel1.setBackground(new java.awt.Color(204, 204, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel5.setBackground(new java.awt.Color(255, 255, 102));
        jLabel5.setFont(new java.awt.Font("Berlin Sans FB", 0, 14)); // NOI18N
        jLabel5.setText("Online  Users");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addGap(0, 2, Short.MAX_VALUE))
        );

        lst_Rooms_Names.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lst_Rooms_Names.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lst_Rooms_NamesMousePressed(evt);
            }
        });
        jScrollPane3.setViewportView(lst_Rooms_Names);

        jPanel2.setBackground(new java.awt.Color(204, 204, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel8.setBackground(new java.awt.Color(255, 255, 102));
        jLabel8.setFont(new java.awt.Font("Berlin Sans FB", 0, 14)); // NOI18N
        jLabel8.setText("Rooms");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(26, Short.MAX_VALUE)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addGap(0, 2, Short.MAX_VALUE))
        );

        txt_Selected_User.setEditable(false);
        txt_Selected_User.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_Selected_User.setEnabled(false);

        btn_Join_Room.setBackground(new java.awt.Color(255, 204, 0));
        btn_Join_Room.setText("Join Room");
        btn_Join_Room.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Join_RoomActionPerformed(evt);
            }
        });

        txt_Name_Girdi.setForeground(new java.awt.Color(196, 196, 200));
        txt_Name_Girdi.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_Name_Girdi.setText(" Type ...");
        txt_Name_Girdi.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txt_Name_Girdi.setFocusCycleRoot(true);
        txt_Name_Girdi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_Name_GirdiKeyPressed(evt);
            }
        });

        btn_Create_Room.setBackground(new java.awt.Color(102, 255, 0));
        btn_Create_Room.setText("Create Room");
        btn_Create_Room.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Create_RoomActionPerformed(evt);
            }
        });

        btn_Connect.setBackground(new java.awt.Color(102, 255, 0));
        btn_Connect.setText("Connect");
        btn_Connect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ConnectActionPerformed(evt);
            }
        });

        lbl_Connected.setBackground(new java.awt.Color(0, 255, 51));
        lbl_Connected.setFont(new java.awt.Font("Berlin Sans FB", 0, 14)); // NOI18N
        lbl_Connected.setForeground(new java.awt.Color(102, 153, 0));
        lbl_Connected.setIcon(new javax.swing.ImageIcon(getClass().getResource("/app/images/Checking_Mark.png"))); // NOI18N
        lbl_Connected.setText("CONNECTED");

        lbl_Disconnected.setBackground(new java.awt.Color(0, 255, 51));
        lbl_Disconnected.setFont(new java.awt.Font("Berlin Sans FB", 0, 14)); // NOI18N
        lbl_Disconnected.setForeground(new java.awt.Color(102, 153, 0));
        lbl_Disconnected.setIcon(new javax.swing.ImageIcon(getClass().getResource("/app/images/Close_Mark.png"))); // NOI18N
        lbl_Disconnected.setText("DISCONNECTED");

        txtPanel_Ekran.setEditable(false);
        jScrollPane4.setViewportView(txtPanel_Ekran);

        txtPane_Girdi.setAutoscrolls(false);
        txtPane_Girdi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPane_GirdiKeyPressed(evt);
            }
        });
        jScrollPane5.setViewportView(txtPane_Girdi);

        lbl_File_Send.setIcon(new javax.swing.ImageIcon(getClass().getResource("/app/images/icon_File_Attach.png"))); // NOI18N
        lbl_File_Send.setFocusable(false);
        lbl_File_Send.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl_File_SendMousePressed(evt);
            }
        });

        emoji_Smile.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        emoji_Smile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/app/images/Emoji_Smile.png"))); // NOI18N
        emoji_Smile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                emoji_SmileMousePressed(evt);
            }
        });

        emoji_Smile_Teeth.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        emoji_Smile_Teeth.setIcon(new javax.swing.ImageIcon(getClass().getResource("/app/images/Emoji_Smile_Teeth.png"))); // NOI18N
        emoji_Smile_Teeth.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                emoji_Smile_TeethMousePressed(evt);
            }
        });

        emoji_Goz_Kirpma.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        emoji_Goz_Kirpma.setIcon(new javax.swing.ImageIcon(getClass().getResource("/app/images/Emoji_Goz_Kirpma.png"))); // NOI18N
        emoji_Goz_Kirpma.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                emoji_Goz_KirpmaMousePressed(evt);
            }
        });

        emoji_Cry.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        emoji_Cry.setIcon(new javax.swing.ImageIcon(getClass().getResource("/app/images/Emoji_Cry.png"))); // NOI18N
        emoji_Cry.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                emoji_CryMousePressed(evt);
            }
        });

        emoji_Teeth.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        emoji_Teeth.setIcon(new javax.swing.ImageIcon(getClass().getResource("/app/images/Emoji_Teeth.png"))); // NOI18N
        emoji_Teeth.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                emoji_TeethMousePressed(evt);
            }
        });

        emoji_Tedirgin.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        emoji_Tedirgin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/app/images/Emoji_Tedirgin.png"))); // NOI18N
        emoji_Tedirgin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                emoji_TedirginMousePressed(evt);
            }
        });

        emoji_Dead.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        emoji_Dead.setIcon(new javax.swing.ImageIcon(getClass().getResource("/app/images/Emoji_Dead.png"))); // NOI18N
        emoji_Dead.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                emoji_DeadMousePressed(evt);
            }
        });

        emoji_Mouthless.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        emoji_Mouthless.setIcon(new javax.swing.ImageIcon(getClass().getResource("/app/images/Emoji_Mouthless.png"))); // NOI18N
        emoji_Mouthless.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                emoji_MouthlessMousePressed(evt);
            }
        });

        emoji_Saskin.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        emoji_Saskin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/app/images/Emoji_Saskin.png"))); // NOI18N
        emoji_Saskin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                emoji_SaskinMousePressed(evt);
            }
        });

        jScrollPane1.setViewportView(lst_Clients_Id);

        jScrollPane6.setViewportView(lst_Rooms_Id);

        jScrollPane7.setViewportView(lst_Room_Owner_Id);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel3)
                .addGap(89, 89, 89)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(69, 69, 69)
                .addComponent(lbl_Connected)
                .addGap(70, 70, 70)
                .addComponent(lbl_Disconnected, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txt_Name_Girdi, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(txt_Ip_Girdi, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(23, 23, 23)
                        .addComponent(txt_Port_Girdi, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbl_File_Send)
                        .addGap(6, 6, 6)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(emoji_Smile)
                        .addGap(6, 6, 6)
                        .addComponent(emoji_Smile_Teeth)
                        .addGap(6, 6, 6)
                        .addComponent(emoji_Goz_Kirpma)
                        .addGap(6, 6, 6)
                        .addComponent(emoji_Cry)
                        .addGap(6, 6, 6)
                        .addComponent(emoji_Teeth)
                        .addGap(6, 6, 6)
                        .addComponent(emoji_Tedirgin)
                        .addGap(6, 6, 6)
                        .addComponent(emoji_Dead)
                        .addGap(6, 6, 6)
                        .addComponent(emoji_Mouthless)
                        .addGap(6, 6, 6)
                        .addComponent(emoji_Saskin)))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_Connect, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(btn_Disconnect, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txt_Selected_User, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_Send_Private_Message, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txt_Selected_Room, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(40, 40, 40)
                                    .addComponent(btn_Join_Room, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txt_Room_Name_Girdi, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(40, 40, 40)
                                    .addComponent(btn_Create_Room, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 680, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel3))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel2))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(lbl_Connected))
                    .addComponent(lbl_Disconnected))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_Disconnect, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(70, 70, 70)
                        .addComponent(txt_Selected_User, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addComponent(btn_Send_Private_Message)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txt_Selected_Room, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addComponent(btn_Join_Room)
                        .addGap(26, 26, 26)
                        .addComponent(txt_Room_Name_Girdi, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(btn_Create_Room)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txt_Name_Girdi, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_Ip_Girdi, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_Port_Girdi, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(10, 10, 10)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lbl_File_Send)
                                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(emoji_Smile)
                                    .addComponent(emoji_Smile_Teeth)
                                    .addComponent(emoji_Goz_Kirpma)
                                    .addComponent(emoji_Cry)
                                    .addComponent(emoji_Teeth)
                                    .addComponent(emoji_Tedirgin)
                                    .addComponent(emoji_Dead)
                                    .addComponent(emoji_Mouthless)
                                    .addComponent(emoji_Saskin)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btn_Connect, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(10, 10, 10)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                                    .addComponent(jScrollPane6)
                                    .addComponent(jScrollPane7)))
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)))
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txt_Room_Name_GirdiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_Room_Name_GirdiKeyPressed

        if (room_Entry_Control == false) {
            txt_Room_Name_Girdi.setText("");
            txt_Room_Name_Girdi.setForeground(Color.BLACK);
        }
        room_Entry_Control = true;
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            ActionEvent evt1 = null;
            btn_Create_RoomActionPerformed(evt1);
        }

    }//GEN-LAST:event_txt_Room_Name_GirdiKeyPressed

    private void lbl_File_SendMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_File_SendMousePressed
        if (isConnected == true) {

            can_Send_File = false;

            ArrayList fileInfo = new ArrayList<>();

            File[] fileToSend = new File[1];
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setDialogTitle("Choose a file to send.");

            if (jFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {

                    fileToSend[0] = jFileChooser.getSelectedFile();
                    fileInfo.add(fileToSend[0].getName());          //0 path

                    byte[] content = Files.readAllBytes(fileToSend[0].toPath());
                    fileInfo.add(content);                          //1 data

                    fileInfo.add(txt_Name_Girdi.getText());         //2 myname
                    fileInfo.add(show_ID);                           //3 showID
                    fileInfo.add(online_Clients_Names);               //4 friendname

                    Message msg3 = new Message(Message.Message_Type.SendFileCheck);
                    msg3.content = fileInfo;    //path,data,myname,showID,friendname
                    Client.Send(msg3);

                } catch (IOException ex) {
                    System.out.println("Dosya Göndermede Hata! (App.Java)");
                }
            }
        }
    }//GEN-LAST:event_lbl_File_SendMousePressed

    private void txt_Name_GirdiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_Name_GirdiKeyPressed
        if (name_Entry_Control == false) {
            txt_Name_Girdi.setText("");
            txt_Name_Girdi.setForeground(Color.BLACK);
        }
        name_Entry_Control = true;
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            ActionEvent evt1 = null;
            btn_ConnectActionPerformed(evt1);
        }
    }//GEN-LAST:event_txt_Name_GirdiKeyPressed

    private void btn_ConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ConnectActionPerformed
        if (txt_Name_Girdi.getText() != "") {
            btn_Connect.setEnabled(false);
            txt_Name_Girdi.setEnabled(false);
            txt_Ip_Girdi.setEnabled(false);
            txt_Port_Girdi.setEnabled(false);
            int port = Integer.parseInt(txt_Port_Girdi.getText());
            Client.Start(txt_Ip_Girdi.getText(), port);                        // Try to Connect to Server

            if (isConnected == true) {                              // If Connected success
                lbl_Connected.setVisible(true);
                lbl_Connected.setEnabled(true);                     // Connected label Enabled
                lbl_Disconnected.setVisible(false);
                lbl_Disconnected.setEnabled(false);                 // Disconnected label Disabled
                lst_Clients_Names.setEnabled(true);
                lst_Rooms_Names.setEnabled(true);

                btn_Disconnect.setEnabled(true);
                btn_Create_Room.setEnabled(true);

                txtPane_Girdi.requestFocus();                       // Focus text girdi
                txtPane_Girdi.setEnabled(true);
                txt_Room_Name_Girdi.setEnabled(true);

                ArrayList nameInfo = new ArrayList<>();
                ArrayList IdInfo = new ArrayList<>();
                try {
                    Message msg = new Message(Message.Message_Type.NameCheck);
                    nameInfo.add(txt_Name_Girdi.getText());         //0 name
                    nameInfo.add(App_Color);                        //1 color
                    msg.content = nameInfo;
                    Client.Send(msg);                               //name,color

                    Message msg1 = new Message(Message.Message_Type.IdCheck);
                    IdInfo.add(show_ID);                            //0 showID
                    msg1.content = IdInfo;
                    Client.Send(msg1);                              //showID

                    Message msg2 = new Message(Message.Message_Type.SendClientsListCheck);
                    Client.Send(msg2);

                    Message msg3 = new Message(Message.Message_Type.SendRoomsListCheck);
                    Client.Send(msg3);
                } catch (Exception e) {
                    System.out.println("App->Connect Butonu Hata!");
                }
            }
        } else {
            JOptionPane.showMessageDialog(ThisApp, "İsim boş girilmez");
        }


    }//GEN-LAST:event_btn_ConnectActionPerformed

    private void btn_Send_Private_MessageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Send_Private_MessageActionPerformed
        int friend_ID = lst_Clients_Id.getSelectedValue();
        String room_n = txt_Name_Girdi.getText() + " - " + txt_Selected_User.getText();

        if (App_ID == friend_ID) {
            JOptionPane.showMessageDialog(ThisApp, "Kendine Mesaj Yollayamazsın");
        } else {
            if (private_Rooms.isEmpty()) {
                same_Person_Control = true;
            } else {
                int a = 0;
                for (Private private_Room : private_Rooms) {
                    String pr_Name = private_Room.ThisPrivate.private_Room_Name;
                    String[] arr = pr_Name.split(" ");
                    if (arr[0].equals(txt_Name_Girdi.getText()) || arr[0].equals(txt_Selected_User.getText())) {
                        a++;
                    }
                    if (arr[2].equals(txt_Name_Girdi.getText()) || arr[2].equals(txt_Selected_User.getText())) {
                        a++;
                    }
                    if (a == 2) {
                        same_Person_Control = false;
                        a = 0;
                        break;
                    }
                    a = 0;
                    same_Person_Control = true;
                }
            }

            if (same_Person_Control) {
                ArrayList id = new ArrayList<>();
                ArrayList<String> private_friend = new ArrayList<>();
                ArrayList private_Info = new ArrayList<>();
                id.add(friend_ID);
                private_friend.add(txt_Selected_User.getText());

                Message msg = new Message(Message.Message_Type.PrivateConnectCheck);
                private_Info.add(txt_Name_Girdi.getText()); //my name
                private_Info.add(App_Color);                    //my App_Color
                private_Info.add("useless");                //useless
                private_Info.add(App_ID);                       //my App_ID
                private_Info.add(private_friend);           //friend name(ARR)
                private_Info.add(friend_ID);                //friend App_ID
                private_Info.add(id);                       //friend App_ID(ARR)
                private_Info.add(room_n);                   //room name
                msg.content = private_Info;
                Client.Send(msg);

                Private pr = new Private(txt_Name_Girdi.getText(), App_ID, txt_Selected_User.getText(), friend_ID, App_Color, room_n);
                pr.setLocationRelativeTo(ThisApp);
                private_Rooms.add(pr);
                pr.setVisible(true);

                lst_Clients_Names.clearSelection();
                txt_Selected_User.setText("");
                btn_Send_Private_Message.setEnabled(false);
            } else if (!same_Person_Control) {
                JOptionPane.showMessageDialog(ThisApp, "Aynı kisiyle birden fazla oda açamazsın.");
            }
        }


    }//GEN-LAST:event_btn_Send_Private_MessageActionPerformed

    private void txtPane_GirdiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPane_GirdiKeyPressed
        text_Entry_Control = false;
        if (text_Entry_Control == false && evt.getKeyCode() == KeyEvent.VK_ENTER) {
            System.out.println("Name Size: " + online_Clients_Names.size() + " | ID Size: " + online_Cients_ID.size());
            System.out.print("Array: ");
            for (String online_Clients_Name : online_Clients_Names) {
                System.out.print(online_Clients_Name + ",");
            }
            System.out.print("| ");
            for (Object online_client : online_Cients_ID) {
                System.out.print((int) online_client + ",");
            }

            System.out.println("");
            ArrayList nameInfo = new ArrayList<>();
            Message msg = new Message(Message.Message_Type.SendBroadCastMessageCheck);
            nameInfo.add(txt_Name_Girdi.getText());         //0 name
            nameInfo.add(App_Color);                        //1 color
            nameInfo.add(txtPane_Girdi.getText());          //2 text
            nameInfo.add(emoji);                            //3 emoji
            nameInfo.add(online_Clients_Names);             //4 online_Clients_Names(ARR)
            nameInfo.add(show_ID);                          //5 ShowID
            nameInfo.add(online_Cients_ID);                 //6 online_Clients_Names(ID)
            nameInfo.add("useless");                        //7 String
            nameInfo.add(1);                                //useless

            msg.content = nameInfo;
            Client.Send(msg);

            emoji = new ArrayList();                        // clear emoji        
            emoji.clear();
            text_Entry_Control = true;
        }

    }//GEN-LAST:event_txtPane_GirdiKeyPressed

    private void emoji_SmileMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji_SmileMousePressed
        if (isConnected) {
            StyledDocument doc = txtPane_Girdi.getStyledDocument();
            int length = doc.getLength();
            emoji.add(0);       //Smile Op Code
            emoji.add(length);
            java.net.URL imgUrl = getClass().getResource("/app/images/Emoji_Smile_Mini.png");
            txtPane_Girdi.insertIcon(new ImageIcon(imgUrl));
        }


    }//GEN-LAST:event_emoji_SmileMousePressed

    private void emoji_Goz_KirpmaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji_Goz_KirpmaMousePressed
        if (isConnected) {
            StyledDocument doc = txtPane_Girdi.getStyledDocument();
            int length = doc.getLength();
            emoji.add(2);   //Smile Teeth Op Code
            emoji.add(length);
            java.net.URL imgUrl = getClass().getResource("/app/images/Emoji_Goz_Kirpma_Mini.png");
            txtPane_Girdi.insertIcon(new ImageIcon(imgUrl));
        }

    }//GEN-LAST:event_emoji_Goz_KirpmaMousePressed

    private void emoji_CryMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji_CryMousePressed
        if (isConnected) {
            StyledDocument doc = txtPane_Girdi.getStyledDocument();
            int length = doc.getLength();
            emoji.add(3);   //Cry Op Code
            emoji.add(length);
            java.net.URL imgUrl = getClass().getResource("/app/images/Emoji_Cry_Mini.png");
            txtPane_Girdi.insertIcon(new ImageIcon(imgUrl));
        }

    }//GEN-LAST:event_emoji_CryMousePressed

    private void emoji_TeethMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji_TeethMousePressed
        if (isConnected) {
            StyledDocument doc = txtPane_Girdi.getStyledDocument();
            int length = doc.getLength();
            emoji.add(4);   //Teeth Op Code
            emoji.add(length);
            java.net.URL imgUrl = getClass().getResource("/app/images/Emoji_Teeth_Mini.png");
            txtPane_Girdi.insertIcon(new ImageIcon(imgUrl));
        }

    }//GEN-LAST:event_emoji_TeethMousePressed

    private void emoji_TedirginMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji_TedirginMousePressed
        if (isConnected) {
            StyledDocument doc = txtPane_Girdi.getStyledDocument();
            int length = doc.getLength();
            emoji.add(5);   //Tedirgin Op Code
            emoji.add(length);
            java.net.URL imgUrl = getClass().getResource("/app/images/Emoji_Tedirgin_Mini.png");
            txtPane_Girdi.insertIcon(new ImageIcon(imgUrl));
        }

    }//GEN-LAST:event_emoji_TedirginMousePressed

    private void emoji_MouthlessMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji_MouthlessMousePressed
        if (isConnected) {
            StyledDocument doc = txtPane_Girdi.getStyledDocument();
            int length = doc.getLength();
            emoji.add(7);   //Mouthless Op Code
            emoji.add(length);
            java.net.URL imgUrl = getClass().getResource("/app/images/Emoji_Mouthless_Mini.png");
            txtPane_Girdi.insertIcon(new ImageIcon(imgUrl));
        }

    }//GEN-LAST:event_emoji_MouthlessMousePressed

    private void emoji_SaskinMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji_SaskinMousePressed
        if (isConnected) {
            StyledDocument doc = txtPane_Girdi.getStyledDocument();
            int length = doc.getLength();
            emoji.add(8);   //Saskin Op Code
            emoji.add(length);
            java.net.URL imgUrl = getClass().getResource("/app/images/Emoji_Saskin_Mini.png");
            txtPane_Girdi.insertIcon(new ImageIcon(imgUrl));
        }

    }//GEN-LAST:event_emoji_SaskinMousePressed

    private void emoji_Smile_TeethMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji_Smile_TeethMousePressed
        if (isConnected) {
            StyledDocument doc = txtPane_Girdi.getStyledDocument();
            int length = doc.getLength();
            emoji.add(1);   //Smile Teeth Op Code
            emoji.add(length);
            java.net.URL imgUrl = getClass().getResource("/app/images/Emoji_Smile_Teeth_Mini.png");
            txtPane_Girdi.insertIcon(new ImageIcon(imgUrl));
        }

    }//GEN-LAST:event_emoji_Smile_TeethMousePressed

    private void emoji_DeadMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji_DeadMousePressed
        if (isConnected) {
            StyledDocument doc = txtPane_Girdi.getStyledDocument();
            int length = doc.getLength();
            emoji.add(6);   //Saskin Op Code
            emoji.add(length);
            java.net.URL imgUrl = getClass().getResource("/app/images/Emoji_Dead_Mini.png");
            txtPane_Girdi.insertIcon(new ImageIcon(imgUrl));
        }

    }//GEN-LAST:event_emoji_DeadMousePressed

    private void btn_DisconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_DisconnectActionPerformed
        try {
            System.out.println("Disconnected");
            ArrayList disconnect = new ArrayList<>();
            disconnect.add(txt_Name_Girdi.getText());   //0 name
            disconnect.add(App_Color);                      //1 App_Color
            online_Clients_Names.remove(txt_Name_Girdi.getText());
            isConnected = false;
            lst_Clients_Names.clearSelection();
            lst_Clients_Id.clearSelection();
            lst_Rooms_Names.clearSelection();
            lst_Rooms_Id.clearSelection();

            lst_Clients_Names.setEnabled(false);
            lst_Rooms_Names.setEnabled(false);
            lbl_Connected.setVisible(false);
            lbl_Connected.setEnabled(false);
            lbl_Disconnected.setVisible(true);
            lbl_Disconnected.setEnabled(true);

            Random rand = new Random();
            int a = rand.nextInt(7);
            App_Color = colors[a];

            btn_Disconnect.setEnabled(false);
            btn_Connect.setEnabled(true);
            btn_Send_Private_Message.setEnabled(false);
            btn_Join_Room.setEnabled(false);
            btn_Create_Room.setEnabled(false);

            txtPane_Girdi.setEnabled(false);
            txt_Name_Girdi.setEnabled(true);
            txt_Room_Name_Girdi.setEnabled(false);
            txt_Ip_Girdi.setEnabled(true);
            txt_Port_Girdi.setEnabled(true);
            txt_Selected_Room.setText("");
            txt_Selected_User.setText("");

            StyledDocument docDC = App.ThisApp.txtPanel_Ekran.getStyledDocument();
            Style style_DC = App.ThisApp.txtPanel_Ekran.addStyle(null, null);
            StyleConstants.setForeground(style_DC, App_Color);
            docDC.insertString(docDC.getLength(), txt_Name_Girdi.getText(), style_DC);

            StyleConstants.setForeground(style_DC, Color.black);
            docDC.insertString(docDC.getLength(), " has disconnected.", style_DC);

            App.ThisApp.txtPanel_Ekran.setCaretPosition(App.ThisApp.txtPanel_Ekran.getDocument().getLength());
            java.net.URL imageDC = getClass().getResource("/app/images/Close_Mark_Mini.png");
            App.ThisApp.txtPanel_Ekran.insertIcon(new ImageIcon(imageDC));
            docDC.insertString(docDC.getLength(), "\n", style_DC);

            Message msg2 = new Message(Message.Message_Type.DisconnectCheck);
            msg2.content = disconnect;  //name,App_Color
            Client.Send(msg2);

            Message msg4 = new Message(Message.Message_Type.SendClientsListCheck);
            Client.Send(msg4);

            for (Private pr : private_Rooms) {
                pr.ThisPrivate.btn_Disconnect.doClick();
            }

            for (Room public_Room : public_Rooms) {
                public_Room.ThisRoom.btn_Disconnect.doClick();
            }
            private_Rooms.clear();
            public_Rooms.clear();

        } catch (BadLocationException ex) {
            System.out.println("App.Disconnect Hata");
        }


    }//GEN-LAST:event_btn_DisconnectActionPerformed

    private void lst_Clients_NamesMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_Clients_NamesMousePressed
        if (isConnected) {
            lst_Clients_Names.setSelectionBackground(Color.red);
            int index = lst_Clients_Names.getSelectedIndex();
            lst_Clients_Id.setSelectedIndex(index);
            txt_Selected_User.setText(lst_Clients_Names.getSelectedValue());
            if (!txt_Selected_User.getText().equals("")) {
                btn_Send_Private_Message.setEnabled(true);
            }
        }
    }//GEN-LAST:event_lst_Clients_NamesMousePressed

    private void btn_Create_RoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Create_RoomActionPerformed
        if (txt_Room_Name_Girdi.getText() != "") {
            ArrayList createInfo = new ArrayList<>();
            Message msg0 = new Message(Message.Message_Type.CreateRoomCheck);
            createInfo.add(txt_Room_Name_Girdi.getText());
            createInfo.add(ThisApp.App_ID);
            msg0.content = createInfo;
            Client.Send(msg0);
        } else {
            JOptionPane.showMessageDialog(ThisApp, "Oda ismi giriniz");
        }


    }//GEN-LAST:event_btn_Create_RoomActionPerformed

    private void lst_Rooms_NamesMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_Rooms_NamesMousePressed
        if (isConnected) {
            lst_Rooms_Names.setSelectionBackground(Color.blue);
            int index = lst_Rooms_Names.getSelectedIndex();
            lst_Rooms_Id.setSelectedIndex(index);
            lst_Room_Owner_Id.setSelectedIndex(index);

            txt_Selected_Room.setText(lst_Rooms_Names.getSelectedValue());
            if (!txt_Selected_Room.getText().equals("")) {
                btn_Join_Room.setEnabled(true);
            }
        }

    }//GEN-LAST:event_lst_Rooms_NamesMousePressed

    private void btn_Join_RoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Join_RoomActionPerformed
        boolean control = true;
        int room_Id = lst_Rooms_Id.getSelectedValue();
        int owner_Id = lst_Room_Owner_Id.getSelectedValue();
        System.out.println("room_ID:" + room_Id + " | owner_ID:" + owner_Id);
        for (Room public_Room : public_Rooms) {
            if (public_Room.ThisRoom.room_ID == room_Id) {
                control = false;
                break;
            }
        }
        if (control == true) {
            Room room = new Room(txt_Selected_Room.getText(), room_Id, owner_Id, App_ID, ThisApp.txt_Name_Girdi.getText(), App_Color);
            room.setLocationRelativeTo(ThisApp);
            public_Rooms.add(room);
            room.setVisible(true);
            txt_Selected_Room.setText("");

            ArrayList joinInfo = new ArrayList<>();
            ArrayList ids = new ArrayList<>();
            ids.add(owner_Id);
            Message msg = new Message(Message.Message_Type.JoinRoomCheck);
            joinInfo.add(txt_Name_Girdi.getText());
            joinInfo.add(ThisApp.App_ID);
            joinInfo.add(owner_Id);
            joinInfo.add(room_Id);
            joinInfo.add(owner_Id);
            joinInfo.add(owner_Id);
            joinInfo.add(ids);
            msg.content = joinInfo;
            Client.Send(msg);
        }
        if (owner_Id == App_ID || !control) {
            JOptionPane.showMessageDialog(ThisApp, "Oda açık.");
        }
        btn_Join_Room.setEnabled(false);


    }//GEN-LAST:event_btn_Join_RoomActionPerformed

    private void txt_Ip_GirdiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_Ip_GirdiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_Ip_GirdiActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new App().setVisible(true);
                //App.ThisApp.tmr_slider.start();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton btn_Connect;
    public javax.swing.JButton btn_Create_Room;
    public javax.swing.JButton btn_Disconnect;
    public javax.swing.JButton btn_Join_Room;
    public javax.swing.JButton btn_Send_Private_Message;
    public javax.swing.JLabel emoji_Cry;
    public javax.swing.JLabel emoji_Dead;
    public javax.swing.JLabel emoji_Goz_Kirpma;
    public javax.swing.JLabel emoji_Mouthless;
    public javax.swing.JLabel emoji_Saskin;
    public javax.swing.JLabel emoji_Smile;
    public javax.swing.JLabel emoji_Smile_Teeth;
    public javax.swing.JLabel emoji_Tedirgin;
    public javax.swing.JLabel emoji_Teeth;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    public javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    public javax.swing.JLabel lbl_Connected;
    public javax.swing.JLabel lbl_Disconnected;
    public javax.swing.JLabel lbl_File_Send;
    public javax.swing.JList<Integer> lst_Clients_Id;
    public javax.swing.JList<String> lst_Clients_Names;
    public javax.swing.JList<Integer> lst_Room_Owner_Id;
    public javax.swing.JList<Integer> lst_Rooms_Id;
    public javax.swing.JList<String> lst_Rooms_Names;
    public javax.swing.JTextPane txtPane_Girdi;
    public javax.swing.JTextPane txtPanel_Ekran;
    public javax.swing.JTextField txt_Ip_Girdi;
    public javax.swing.JTextField txt_Name_Girdi;
    public javax.swing.JTextField txt_Port_Girdi;
    public javax.swing.JTextField txt_Room_Name_Girdi;
    public javax.swing.JTextField txt_Selected_Room;
    public javax.swing.JTextField txt_Selected_User;
    // End of variables declaration//GEN-END:variables
}
