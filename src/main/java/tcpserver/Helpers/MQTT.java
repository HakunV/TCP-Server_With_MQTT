package tcpserver.Helpers;

public class MQTT {
    
    /*
     * Converts the remaining length from integer to hex string
     */
    public static String calcRemLen(int x) {
        int xn = x;
        String res = "";

        while (xn > 0) {
            int digit = xn % 128;
            xn = xn / 128;
            if (xn > 0) {
                digit = digit | 128;
            }
            res += String.format("%02X", digit);
        }
        return res;
    }

    /*
     * Converts the remaining length from hex to int, returns an integer array of length 2
     * [0] value, the remaining length of the packet
     * [1] amount of bytes the remaining length field occupied
     */
    public static int[] recRemLen(String inLen) {
        int amountBytes = 0;

        int start = 0;
        int end = 2;

        int multiplier = 1;
        int value = 0;

        while (true) {
            int digit = Integer.parseInt(inLen.substring(start, end), 16);

            value += (digit & 127) * multiplier;
            multiplier *= 128;

            amountBytes++;

            if ((digit & 128) == 0) {
                break;
            }

            start += 2;
            end += 2;
        }

        return new int[] {value, amountBytes};
    }

    /*
     * Generates random 5 digit packet ID
     */
    public static String generatePacketID() {
        int min = 10000;
        int max = 60000;

        int id = (int) (Math.random()*(max-min+1)+min);
        return Integer.toString(id);
    }
}
