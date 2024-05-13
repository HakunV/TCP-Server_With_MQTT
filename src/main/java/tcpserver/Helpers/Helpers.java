package tcpserver.Helpers;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.sql.Timestamp;

public class Helpers {
    private static int byteSize = 2;

    public static int getByteSize() {
        return byteSize;
    }

    /*
     * Returns a new array with only the length of the bytes read, and with the same elements
     */
    public static byte[] byteCutoff(byte[] dataT, int nRead) {
        byte[] d = new byte[nRead];

        for (int i = 0; i < nRead; i++) {
            d[i] = dataT[i];
        }
        return d;
    }

    /*
     * Converts from string to hex string
     */
    public static String textToHex(String s) {
        char[] userArr = s.toCharArray();
        String userHex = "";
        for (int i = 0; i < userArr.length; i++) {
            userHex += String.format("%02X", (int) userArr[i]);
        }
        return userHex;
    }

    public static String hexToString(String str) {
        String txt = "";
        for (int i = 0; i <= str.length()-2; i += 2) {
            String s = str.substring(i, i + 2);
            txt = txt + (char) Integer.parseInt(s, 16);
        }

        return txt;
    }

    /*
     * Converts byte array to hex string
     */
    public static String byteToHex(byte[] byteArray) {
        StringBuilder sb = new StringBuilder();
        for (byte b : byteArray) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

    /*
     * Converts hex string to byte array
     */
    public static byte[] hexStrToByteArr(String data) {
        int len = data.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(data.charAt(i), 16) << 4)
                                + Character.digit(data.charAt(i+1), 16));
        }
        return bytes;
    }

    /*
     * Removes proceeding zeros in a String
     */
    public static String removeProZeros(String imei) {
        int i = 0;
        while(imei.charAt(i) == '0') {
            i += 1;
        }

        return imei.substring(i);
    }

    /*
     * Removes white space
     */
    public static String removeWhiteSpace(String in) {
        String out = "";
 
        for (int i = 0; i < in.length(); i++) {
            char ch = in.charAt(i);
 
            // Checking whether is white space or not
            if (!Character.isWhitespace(ch)) {
                out += ch;
            }
        }
        return out;
    }

    /*
     * Makes characters lowercase
     */
    public static String toLowerCase(String str) {
        String res = "";

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            if (!Character.isDigit(c)) {
                char cLower = c;
                if (Character.isUpperCase(c)) {
                    cLower = (char) (c ^ 0x20);
                }
                res += cLower;
            }
            else {
                res += c;
            } 
        }
        return res;
    }

    /*
     * Gets timestamp
     */
    public static String ts() {
        return "Timestamp: " + new Timestamp(new java.util.Date().getTime());
    }

    /*
     * Takes a string and sends to the provided outputstream
     */
    public static void sendMessage(String mes, BufferedOutputStream bos) throws IOException {
        System.out.println("Message: " + mes);
        System.out.println();

        byte[] mesBytes = hexStrToByteArr(mes);
        // byte[] mesBytes = mes.getBytes();

        bos.write(mesBytes);
        bos.flush();

        System.out.println("Sent packet");
        System.out.println();
    }
}
