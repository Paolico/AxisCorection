package model;


/**
 * ============================================================================
 * Funktionen zur Verbindung und Kommunikation über LSV2-Protokoll mit der
 * Steuerung
 * ============================================================================
 **/

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;

public class Client {

    public static final String PW_FILEPLC = "FILEPLC";    // Dateisystem mit Zugriff auf PLC:-Drive
    public static final String PW_FILESYS = "FILESYS";    // Dateisystem mit Zugriff auf PLC:-Drive


    public static final byte OK = 0;
    public static final byte TIMEOUT = 1;
    public static final byte ERROR = 2;


    private static final String CHAR0 = Character.toString((char) 0);


    public static boolean is_timedout = false;
    public static String Steuerung = "";
    public static String NcVersion = "";
    public static String PlcVersion = "";
    public static String Zusatzoptionen = "";

    public static String LSV_info = "";
    public static String LSV_data = "";
    public static int BufferLength = 256 - 10;

    private static Socket client;
    private static SimpleDateFormat now = new SimpleDateFormat("HH:mm:ss");
    private static String newPasswort = null;
    private static InputStream inFromServer = null;
    private static OutputStreamWriter outToServer = null;


    //=========================================================================
    public static boolean Connect(String serverName, int port, int timeout) {
        is_timedout = false;
        try {

            client = new Socket(serverName, port);
            if (client.isConnected()) {
                System.out.println("Připojeno: " + serverName + " port: " + port);
            }

            client.setSoTimeout(timeout);
            client.setTcpNoDelay(false);
            client.setSendBufferSize(65535);
            //Connect with timeout (ms)

            OutputStream outTo = client.getOutputStream();
            inFromServer = client.getInputStream();
            outToServer = new OutputStreamWriter(outTo, "ISO-8859-1");
            System.out.println("{Pripojen");
        } catch (IOException e) {
            System.out.println("Nepripojen");
            // SyncWithUi.ConsolePrint( now.format(new Date()).concat(" - "), Console.BLACK, 0);
            // SyncWithUi.ConsolePrintln("ERROR: ".concat(e.toString()),Console.RED);
            // ToolbarSelectionListener.requestRefresh();
            return false;
        }
        //  ToolbarSelectionListener.requestRefresh();
        return true;
    }


    //=========================================================================
    public static void Disconnect() {
        try {
            if (client != null) {
                outToServer.close();
                inFromServer.close();
                client.close();
                Steuerung = "";
                NcVersion = "";
                PlcVersion = "";
                Zusatzoptionen = "";
            }
        } catch (IOException e) {
            //  SyncWithUi.ConsolePrint( now.format(new Date()).concat(" - "), Console.BLACK, 0);
            //  SyncWithUi.ConsolePrintln("ERROR: ".concat(e.getLocalizedMessage()),Console.RED);
        }
    }


    // String senden / empfangen
    //=========================================================================
    private static boolean Send(String data) {
        int length = 0;
        String LSV_length = "";
        byte[] buffer = new byte[4];

        //alte Daten löschen
        LSV_data = "";

        try {
            outToServer.write(data); //.writeBytes(data);
            outToServer.flush();

            inFromServer.read(buffer);
            length = (buffer[2] << 8) | buffer[3];
            LSV_length = new String(buffer, "UTF-8");

            //Info
            inFromServer.read(buffer);
            LSV_info = new String(buffer, "UTF-8");
            if (length > 0) {
                //Daten
                buffer = new byte[length];
                inFromServer.read(buffer);
                LSV_data = new String(buffer, "UTF-8");
                //syncWithUiConsolePrintln("LSV_data " + LSV_data);
            }
        } catch (IOException e) {
            //   SyncWithUi.ConsolePrint( now.format(new Date()).concat(" - "), Console.BLACK, 0 );
            //    SyncWithUi.ConsolePrintln("ERROR: ".concat(e.toString()),Console.RED);
            is_timedout = true;
            return false;
        }
        // if ( Global.prefTelegram ) {
        //   if ( Global.prefTelegramHex ) {
        //     SyncWithUi.ConsolePrintln("Senden :\n".concat(hexString(data)).concat("\n"), Console.BLACK);
        //    SyncWithUi.ConsolePrintln("Empfangen :\n".concat(hexString(LSV_length.concat(LSV_info).concat(LSV_data))).concat("\n"), 0);
        // } else {
        //    SyncWithUi.ConsolePrintln("Senden   : ".concat(data),0);
        //    SyncWithUi.ConsolePrintln("Empfangen: ".concat(LSV_length).concat(LSV_info).concat(LSV_data),Console.BLACK);
        // }
        return true;
    }
    //buffer = null;



    // Debug Ausgabe als HEX-Darstellung erstellen
    //=========================================================================
    private static String hexString(String data){
        char sendbyte[];
        sendbyte = data.toCharArray();// .getBytes("UTF-8");
        String regex = "[" + (char) 0 + "\\a\\t\\n\\v\\f\\r]";
        String hex = "0000   ";
        String ret = "";
        String ascii = "";
        int row = 0;
        int i = 0;
        for ( char chr : sendbyte){
            hex = hex.concat(" ").concat(String.format("%02x", chr  & 0xff ));
            ascii += chr;
            i++;
            if ( i % 16 == 0 ){
                ret = ret.concat(hex).concat("    ").concat(ascii.replaceAll(regex, ".")).concat("\n");
                ascii="";
                hex = String.format("%04x   ", row += 16 );
            }
        }
        if ( hex.length() > 8){
            while (hex.length() < 55){
                hex += " ";
            }
            ret = ret.concat(hex).concat("    ").concat(ascii.replaceAll(regex, "."));
        } else if ( ret.endsWith("\n") ) {
            return ret.substring(0, ret.length() -1);
        }
        return ret;
    }


    //=========================================================================
    public static byte LoginDrive(String drive) {
        byte loggedin = ERROR;
        newPasswort = "807668";
        while (loggedin == ERROR) {
            // Login PLC or SYS

            /**
             * Leeres Passwort ausfiltern!
             * Vermutlich Bug der iTNC530 das mit leerem Passwort auf PLC:\
             * eingeloggt werden kann
             **/
            if ( newPasswort.isEmpty() == false) {
                if (drive.equalsIgnoreCase("PLC:")) {
                    loggedin = Login(PW_FILEPLC, newPasswort);
                } else if (drive.equalsIgnoreCase("SYS:")) {
                    loggedin = Login(PW_FILESYS, newPasswort);
                } else {
                    loggedin = OK;
                }
            }
            if ( loggedin == TIMEOUT ) {
                return TIMEOUT;
            } else if (loggedin == ERROR) {
                newPasswort = "807668";
                if (newPasswort == null) {
                    return ERROR;
                }
            }
        }
        return OK;
    }


    //=========================================================================
    public static byte Login(String logon,String Password) {
        String s = "A_LG".concat(logon).concat(CHAR0);
        if ( Password.isEmpty() == false ) {
            s = s.concat(Password).concat(CHAR0);
        }
        int length = s.length() -4;
        String loByte = Character.toString (((char) (length & 0xFF)));
        String hiByte = Character.toString (((char) ((length >> 8) & 0xFF)));
        String data = CHAR0.concat(CHAR0).concat(hiByte).concat(loByte).concat(s);
        if ( Send(data) == false ) { return TIMEOUT; }
        if ( LSV_info.equals("T_OK") ) {
            return OK;
        }
        showLSVError();
        return ERROR;
    }


    //=========================================================================
    public static byte Logout(String logon) {
        String s = "A_LO";
        if ( logon.length() > 0 ) {
            s = s.concat(logon).concat(CHAR0);
        }
        int length = s.length() -4;
        String loByte = Character.toString (((char) (length & 0xFF)));
        String hiByte = Character.toString (((char) ((length >> 8) & 0xFF)));
        String data = CHAR0.concat(CHAR0).concat(hiByte).concat(loByte).concat(s);
        if ( Send(data) == false ) { return TIMEOUT; }
        if ( LSV_info.equals("T_OK") ) {
            return OK;
        } else {
            return ERROR;
        }
    }


    //=========================================================================
    public static byte getInfo() {
        String data = CHAR0.concat(CHAR0).concat(CHAR0).concat(CHAR0).concat("R_VR");
        if ( Send(data) == false ) { return TIMEOUT; }
        if ( LSV_info.equals("S_VR") ) {
            String split[] = LSV_data.split(CHAR0);
            int l = split.length;
            if ( l >= 0 ) { Steuerung = split[0]; }
            if ( l >= 1 ) { NcVersion = split[1]; }
            if ( l >= 2 ) { PlcVersion = split[2]; }
            if ( l >= 3 ) { Zusatzoptionen = split[3]; }
            return OK;
        } else {
            showLSVError();
            return ERROR;
        }
    }


    //=========================================================================
    public static byte SetSysCmd(int cmd) {
        String data = CHAR0 + CHAR0 + CHAR0 + (char)2 + "C_CC" + CHAR0 + (char)cmd;
        if ( Send(data) == false ) { return TIMEOUT; }
        if ( LSV_info.equals("T_OK") ) {
            return OK;
        } else {
            showLSVError();
            return ERROR;
        }
    }


    //=========================================================================
    public static byte GetSysPar() {
        String data = CHAR0.concat(CHAR0).concat(CHAR0).concat(CHAR0).concat("R_PR");
        if ( Send(data) == false ) { return TIMEOUT; }
        if ( LSV_info.equals("S_PR") ) {
            //LSV_SysParam.makeParam(LSV_data);
            return OK;
        } else {
            showLSVError();
            return ERROR;
        }
    }


    //=========================================================================
    public static byte ChDir(String directory) {
        String s = "C_DC".concat(directory).concat(CHAR0);
        int length = (s.length()-4);
        String loByte = Character.toString (((char) (length & 0xFF)));
        String hiByte = Character.toString (((char) ((length >> 8) & 0xFF)));
        String data = CHAR0.concat(CHAR0).concat(hiByte).concat(loByte).concat(s);
        if ( Send(data) == false ) { return TIMEOUT; }
        if ( LSV_info.equals("T_OK") ) {
            return OK;
        } else {
            showLSVError();
            return ERROR;
        }
    }


    //=========================================================================
    public static byte MkDir(String directory) {
        String s = "C_DM".concat(directory).concat(CHAR0);
        int length = (s.length()-4);
        String loByte = Character.toString (((char) (length & 0xFF)));
        String hiByte = Character.toString (((char) ((length >> 8) & 0xFF)));
        String data = CHAR0.concat(CHAR0).concat(hiByte).concat(loByte).concat(s);
        if ( Send(data) == false ) { return TIMEOUT; }
        if ( LSV_info.equals("T_OK") ) {
            return OK;
        } else {
            showLSVError();
            return ERROR;
        }
    }


    //=========================================================================
    public static byte FileInfo(String filename) {
        String s = "R_FI".concat(filename).concat(CHAR0);
        int length = (s.length()-4);
        String loByte = Character.toString (((char) (length & 0xFF)));
        String hiByte = Character.toString (((char) ((length >> 8) & 0xFF)));
        String data = CHAR0.concat(CHAR0).concat(hiByte).concat(loByte).concat(s);
        if ( Send(data) == false ) { return TIMEOUT; }
        if ( LSV_info.equals("S_FI") ) {
            return OK;
        } else {
            showLSVError();
            return ERROR;
        }
    }


    //=========================================================================
    public static byte DeleteFile(String filename) {
        int length = 0;
        String s = "C_FD".concat(filename).concat(CHAR0);
        length = (s.length()-4);
        String loByte = Character.toString (((char) (length & 0xFF)));
        String hiByte = Character.toString (((char) ((length >> 8) & 0xFF)));
        String data = CHAR0.concat(CHAR0).concat(hiByte).concat(loByte).concat(s);
        if ( Send(data) == false ) { return TIMEOUT; }
        if ( LSV_info.equals("T_OK") ) {
            return OK;
        } else {
            showLSVError();
            return ERROR;
        }
    }

    private static 		InputStreamReader in;

    //=========================================================================
    public static byte SendFile(String filename, String pcFile) {

        String s = filename.concat(CHAR0);
        String[] binFiles = null;


  //      @SuppressWarnings("unused")
        boolean binary = false;
  //      for ( String binFile : binFiles ) {
  //          if ( filename.toLowerCase().endsWith(binFile) ){
  //              s += Character.toString ((char) 1);
  //              binary = true;
   //             break;
   //         }
   //     }

        int length = s.length();
        String loByte = Character.toString (((char) (length & 0xFF)));
        String hiByte = Character.toString (((char) ((length >> 8) & 0xFF)));
        String data = CHAR0.concat(CHAR0).concat(hiByte).concat(loByte).concat("C_FL").concat(s);
        // Daten Senden/Empfangen
        if ( Send(data) == false ) {
            // socket Fehler beim senden
            return TIMEOUT;
        }
        if (LSV_info.equals("T_OK")) {
            try {
                in = new InputStreamReader(new FileInputStream(pcFile), "ISO-8859-1");
            } catch (UnsupportedEncodingException e2) {
                //SyncWithUi.ConsolePrintlnOkError( false, (char)0 );
               // SyncWithUi.ConsolePrintln("Datei-Encoding nicht unterstützt", 0);
                return ERROR;
            } catch (FileNotFoundException e2) {
                //SyncWithUi.ConsolePrintlnOkError( false, (char)0 );
               // SyncWithUi.ConsolePrintln("Datei nicht gefunden", 0);
                return ERROR;
            }
            try{
                char[] buffer = new char[BufferLength];
                while ((length = in.read(buffer)) != -1) {

                    //Buffer in String wandeln
                    s = String.valueOf(buffer,0,length);

                    if ( binary = false ) {
                        //	Zeilenumbrüche entfernen und durch char 0 ersetzen
                        s = s.replaceAll("\r\n", CHAR0);
                        s = s.replaceAll("\n\r", CHAR0);
                        //s = s.replaceAll("[\r|\n]", CHAR0);
                    }

                    // String-Länge in hi & low Byte ausgeben
                    length = s.length();
                    loByte = Character.toString (((char) (length & 0xFF)));
                    hiByte = Character.toString (((char) ((length >> 8) & 0xFF)));

                    // S_FL Kommando erstellen
                    data = CHAR0.concat(CHAR0).concat(hiByte).concat(loByte).concat("S_FL").concat(s);

                    // Daten senden
                    if ( Send(data) == false ) {
                        // socket Fehler beim senden
                        in.close();
                        return ERROR;
                    } else if ( LSV_info.equals("T_OK") == false) {
                        // senden war fehlerhaft
                        showLSVError();
                        in.close();
                        return ERROR;
                    }
                }

                // buffer löschen und Datei schliessen
                buffer = null;
                in.close();

                // File Done
                data = CHAR0.concat(CHAR0).concat(CHAR0).concat(CHAR0).concat("T_FD");
                if ( Send(data) == false ) { return ERROR; }
                if ( LSV_info.equals("T_OK") == false ) {
                    showLSVError();
                    return ERROR;
                }
                return OK;
            } catch ( IOException e ) {
               // SyncWithUi.ConsolePrintln("exception ".concat(e.getMessage()),Console.RED);
                if ( in != null ) {
                    try {
                        in.close();
                    } catch ( IOException e1 ) {

                    }
                    return ERROR;
                }
            }
        }
        showLSVError();
        return ERROR;
    }


    //=========================================================================
    public static void showLSVError() {
        if ( LSV_data.isEmpty() == false ) {
            @SuppressWarnings("unused")
            char zeichen = LSV_data.charAt(LSV_data.length()-1);
            //SyncWithUi.Notification("Übertragungsfehler","Error " +(int)zeichen + " : " + LSV_Error.getError(zeichen),0);
        }
    }

}
