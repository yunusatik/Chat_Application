package app;

/**
 *
 * @author Yunus Emre Atik (yunusemre.atik@stu.fsm.edu.tr)
 */
public class Message implements java.io.Serializable {

    public static enum Message_Type {
        Name, NameCheck, Disconnect, DisconnectCheck, SendFile, SendFileCheck, ListRefresh,
        ListRefreshCheck, SendBroadCastMessageCheck, SendBroadCastMessage, PrivateConnect,
        PrivateConnectCheck, Id, IdCheck, PrivateClose, PrivateCloseCheck, SendClientsList, SendClientsListCheck,
        SendFilePrivate, SendFilePrivateCheck, SendPrivateMessage, SendPrivateMessageCheck,
        CreateRoom, CreateRoomCheck, SendRoomsList, SendRoomsListCheck, RoomClose, RoomCloseCheck,
        JoinRoom, JoinRoomCheck, CreateId, CreateIdCheck, SahipSendList, SahipSendListCheck, SendRoom, SendRoomCheck
    }

    public Message_Type type;
    public Object content;

    public Message(Message_Type t) {
        this.type = t;
    }

}
