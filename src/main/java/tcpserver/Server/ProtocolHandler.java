package tcpserver.Server;

import java.io.IOException;

public class ProtocolHandler {
    ClientHandler client;

    private int byteSize = 2;

    public ProtocolHandler(ClientHandler client) {
        this.client = client;
    }

    public void handleProtocol(String protocol, String dataString) {
        /*
         * The different kinds of packets are indicated by two hexadecimals
         */
        switch (protocol) {
            case "01":
                System.out.println("Login Message");
                System.out.println();
                handleLogin(dataString, client.isn);
                break;
            case "22":
                System.out.println("Location Message");
                System.out.println();
                handleLocation(dataString);
                break;
            case "12":
                System.out.println("Location Message");
                System.out.println();
                handleLocation(dataString);
                break;
            case "13":
                System.out.println("Status Message");
                System.out.println();
                handleStatus(dataString, client.isn);
                break;
            case "15":
                System.out.println("Command Response");
                System.out.println();
                handleCommandResponse(dataString);
                break;
            case "26":
                System.out.println("Alarm Message");
                System.out.println();
                handleAlarm(dataString, client.isn);
                break;
            case "16":
                System.out.println("Alarm Message");
                System.out.println();
                handleAlarm(dataString, client.isn);
                break;
            case "80":
                break;
            case "f3":
                break;
            case "f1":
                break;
            case "f2":
                break;
            case "8a":
                break;
            case "2c":
                System.out.println("Location Message");
                System.out.println();
                handleLocation(dataString);
                break;
            default:
                System.out.println("No such command");
                break;
        }
    }

    /*
     * The different kinds of packets have set bytes for each field
     */

    private void handleLogin(String d, String isn) {
        String imei = d.substring(4*byteSize, 12*byteSize);
        imei = client.removeProZeros(imei);
        client.setName(imei);
        System.out.println("IMEI number: " + imei);
        System.out.println();

        String typeID = d.substring(12*byteSize, 14*byteSize);
        System.out.println("Type ID: " + typeID);
        checkType(typeID);

        String timeZone = d.substring(14*byteSize, 16*byteSize);
        System.out.println("Time zone:");
        checkTimeZone(timeZone);

        client.checkDups();
        try {
            client.respondToLogin(isn);
        } catch(IOException e) {
            System.out.println("Fail to send Login");
        }
    }

    private void handleLocation(String d) {
        String dateAndTime = d.substring(4*byteSize, 10*byteSize);
        System.out.println("Date and Time:");
        checkDate(dateAndTime);

        String gpsQual = d.substring(10*byteSize, 11*byteSize);
        System.out.println("Quality of GPS:");
        checkGPS(gpsQual);

        String latitude = d.substring(11*byteSize, 15*byteSize);
        System.out.println("Latitude:");
        float latVal = checkLat(latitude);

        String longitude = d.substring(15*byteSize, 19*byteSize);
        System.out.println("Longitude:");
        float lonVal = checkLong(longitude);

        String speed = d.substring(19*byteSize, 20*byteSize);
        System.out.println("Speed:");
        checkSpeed(speed);

        String course = d.substring(20*byteSize, 22*byteSize);
        System.out.println("Course and Status:");
        checkCourse(course);

        String mcc = d.substring(22*byteSize, 24*byteSize);
        System.out.println("Mobile Country Code:");
        checkMcc(mcc);

        String mnc = d.substring(24*byteSize, 25*byteSize);
        System.out.println("Mobile Network Code:");
        checkMnc(mnc);

        String lac = d.substring(25*byteSize, 27*byteSize);
        System.out.println("Location Area Code:");
        checkLac(lac);

        String cellID = d.substring(27*byteSize, 30*byteSize);
        System.out.println("Cell ID:");
        checkCell(cellID);

        String acc = d.substring(30*byteSize, 31*byteSize);
        System.out.println("ACC:");
        checkAcc(acc);

        String escalation = d.substring(31*byteSize, 32*byteSize);
        System.out.println("Data Escalation Mode:");
        checkEsc(escalation);

        String realTime = d.substring(32*byteSize, 33*byteSize);
        System.out.println("GPS Real-time Retransmission:");
        checkReal(realTime);

        // client.publish(latVal, lonVal);
    }

    private void handleStatus(String d, String isn) {
        String tic = d.substring(4*byteSize, 5*byteSize);
        System.out.println("Terminal Information Content:");
        checkTic(tic);

        String volLevel = d.substring(5*byteSize, 6*byteSize);
        System.out.println("Voltage Level:");
        checkVol(volLevel);

        String sigStrength = d.substring(6*byteSize, 7*byteSize);
        System.out.println("Signal Strength:");
        checkSig(sigStrength);

        String alarm = d.substring(7*byteSize, 8*byteSize);
        System.out.println("Alarm:");
        checkAlarm(alarm);

        String language = d.substring(8*byteSize, 9*byteSize);
        System.out.println("Language:");
        checkLanguage(language);
        
        try {
            client.respondToStatus(isn);
        } catch(IOException e) {
            System.out.println("Fail to send Status");
        }
    }

    private void handleAlarm(String d, String isn) {
        String date = d.substring(4*byteSize, 10*byteSize);
        System.out.println("Date and Time:");
        checkDate(date);

        String gpsQual = d.substring(10*byteSize, 11*byteSize);
        System.out.println("Quality of GPS:");
        checkGPS(gpsQual);

        String latitude = d.substring(11*byteSize, 15*byteSize);
        System.out.println("Latitude:");
        checkLat(latitude);

        String longitude = d.substring(15*byteSize, 19*byteSize);
        System.out.println("Longitude:");
        checkLong(longitude);

        String speed = d.substring(19*byteSize, 20*byteSize);
        System.out.println("Speed:");
        checkSpeed(speed);

        String course = d.substring(20*byteSize, 22*byteSize);
        System.out.println("Course and Status:");
        checkCourse(course);

        String lbsLen = d.substring(22*byteSize, 23*byteSize);
        System.out.println("LBS Length: " + lbsLen);

        String mcc = d.substring(23*byteSize, 25*byteSize);
        System.out.println("Mobile Country Code:");
        checkMcc(mcc);

        String mnc = d.substring(25*byteSize, 26*byteSize);
        System.out.println("Mobile Network Code:");
        checkMnc(mnc);

        String lac = d.substring(26*byteSize, 28*byteSize);
        System.out.println("Location Area Code:");
        checkLac(lac);

        String cellID = d.substring(28*byteSize, 31*byteSize);
        System.out.println("Cell ID:");
        checkCell(cellID);

        String tic = d.substring(31*byteSize, 32*byteSize);
        System.out.println("Terminal Information Content:");
        checkTic(tic);

        String volLevel = d.substring(32*byteSize, 33*byteSize);
        System.out.println("Voltage Level:");
        checkVol(volLevel);

        String sigStrength = d.substring(33*byteSize, 34*byteSize);
        System.out.println("Signal Strength:");
        checkSig(sigStrength);

        String alarm = d.substring(34*byteSize, 35*byteSize);
        System.out.println("Alarm:");
        checkAlarm(alarm);

        String language = d.substring(35*byteSize, 36*byteSize);
        System.out.println("Language:");
        checkLanguage(language);

        try {
            client.respondToAlarm(isn);
        } catch(IOException e) {
            System.out.println("Fail to send Status");
        }
    }

    private void handleCommandResponse(String d) {
        String comLen = d.substring(4*byteSize, 5*byteSize);
        int comLenInt = Integer.parseInt(comLen, 16);
        System.out.println("Length of Command: " + comLenInt);
        System.out.println();

        String serverFlag = d.substring(5*byteSize, 9*byteSize);
        System.out.println("Server Flag: " + serverFlag);
        System.out.println();

        String comContent = d.substring(9*byteSize, (5+comLenInt)*byteSize);
        System.out.println("Command Response:");
        System.out.println();
        checkCommandResponse(comContent);

        String language = d.substring((5+comLenInt)*byteSize, (7+comLenInt)*byteSize);
        System.out.println("Language:");
        System.out.println();
        checkLanguage(language);
    }

    private void checkCommandResponse(String response) {
        String txt = "";
        for (int i = 0; i < response.length(); i += 2) {
            String str = response.substring(i, i + 2);
            txt = txt + (char) Integer.parseInt(str, 16);
        }

        System.out.println("    " + txt);
        System.out.println();
    }

    private void checkCourse(String course) {
        int courseInt = Integer.parseInt(course, 16);
        String c = String.format("%16s", Integer.toBinaryString(courseInt)).replace(" ", "0");

        if (c.substring(0, 1).equals("1")) {
            System.out.println("    ACC: On");
            System.out.println();
        }
        else {
            System.out.println("    ACC: Off");
            System.out.println();
        }

        if (c.substring(1, 2).equals("1")) {
            System.out.println("    Input2: On");
            System.out.println();
        }
        else {
            System.out.println("    Input2: Off");
            System.out.println();
        }

        if (c.substring(2, 3).equals("1")) {
            System.out.println("    GPS: Real-time");
            System.out.println();
        }
        else {
            System.out.println("    GPS: Differential Positioning");
            System.out.println();
        }

        if (c.substring(3, 4).equals("1")) {
            System.out.println("    GPS: Positioned");
            System.out.println();
        }
        else {
            System.out.println("    GPS: Not Positioned");
            System.out.println();
        }

        if (c.substring(4, 5).equals("1")) {
            System.out.println("    Longitude: West");
            System.out.println();
        }
        else {
            System.out.println("    Longitude: East");
            System.out.println();
        }

        if (c.substring(5, 6).equals("1")) {
            System.out.println("    Latitude: North");
            System.out.println();
        }
        else {
            System.out.println("    Latitude: South");
            System.out.println();
        }

        int courseDegrees = Integer.parseInt(c.substring(6), 2);
        System.out.println("    Course In Degrees: " + courseDegrees);
        System.out.println();
    }

    private void checkTimeZone(String tz) {
        int tzInt = Integer.parseInt(tz, 16);
        String t = String.format("%16s", Integer.toBinaryString(tzInt)).replace(" ", "0");

        int timezone = Integer.parseInt(t.substring(0, 12), 2);

        int districtInt = timezone/100;

        String district = t.substring(12, 13).equals("0") ? "East" : "West";

        if (district.equals("East")) {
            System.out.println("    " + district + " District " + districtInt + ", GMT+" + districtInt + ":00");
        }
        else {
            System.out.println("    " + district + " District " + districtInt + ", GMT-" + districtInt + ":00");
        }
        System.out.println();
    }

    private void checkType(String id) {
        String oil = id.substring(2);

        if (oil.equals("00")) {
            System.out.println("    With Oil-Cut Function");
        }
        else {
            System.out.println("    Without Oil-Cut Function");
        }
        System.out.println();
    }

    private void checkReal(String realTime) {
        if (realTime.equals("01")) {
            System.out.println("    Differential Positioning Upload");
            System.out.println();
        }
        else {
            System.out.println("    Not Known");
            System.out.println();
        }
    }

    private void checkEsc(String escalation) {
        if (escalation.equals("00")) {
            System.out.println("    Timed Upload");
            System.out.println();
        }
        else {
            System.out.println("    Not Known");
            System.out.println();
        }
    }

    private void checkAcc(String acc) {
        if (acc.equals("00")) {
            System.out.println("    ACC is Off");
            System.out.println();
        }
        else {
            System.out.println("    Not Known");
            System.out.println();
        }
    }

    private void checkCell(String cellID) {
        int c = Integer.parseInt(cellID, 16);

        System.out.println("    " + c);
        System.out.println();
    }

    private void checkLac(String lac) {
        int l = Integer.parseInt(lac, 16);

        System.out.println("    " + l);
        System.out.println();
    }

    private void checkMnc(String mnc) {
        int m = Integer.parseInt(mnc, 16);

        System.out.println("    " + m);
        System.out.println();
    }

    private void checkMcc(String mcc) {
        int m = Integer.parseInt(mcc, 16);

        System.out.println("    " + m);
        System.out.println();
    }

    private void checkSpeed(String speed) {
        int s = Integer.parseInt(speed, 16);

        System.out.println("    " + s + " km/h");
        System.out.println();
    }

    private float checkLong(String longitude) {
        int longInt = Integer.parseInt(longitude, 16);

        float longFloat = ((float) 180 / (float) 324000000)*longInt;

        System.out.println("    " + longFloat);
        System.out.println();

        return longFloat;
    }

    private float checkLat(String latitude) {
        int latInt = Integer.parseInt(latitude, 16);

        float latFloat = ((float) 90 / (float) 162000000)*latInt;

        System.out.println("    " + latFloat);
        System.out.println();

        return latFloat;
    }

    private void checkGPS(String str) {
        int gpsBits = Integer.parseInt(str.substring(0, 1), 16);
        System.out.println("    Bits of GPS Info: " + gpsBits);
        System.out.println();

        int sat = Integer.parseInt(str.substring(1), 16);
        System.out.println("    Satelittes connected: " + sat);
        System.out.println();
    }

    private void checkDate(String dateAndTime) {
        int year = Integer.parseInt(dateAndTime.substring(0, 1*byteSize), 16);
        System.out.print("    20" + year);

        int month = Integer.parseInt(dateAndTime.substring(1*byteSize, 2*byteSize), 16);
        System.out.print("." + month);

        int day = Integer.parseInt(dateAndTime.substring(2*byteSize, 3*byteSize), 16);
        System.out.print("." + day);

        int hour = Integer.parseInt(dateAndTime.substring(3*byteSize, 4*byteSize), 16);
        System.out.print("   " + hour);

        int minute = Integer.parseInt(dateAndTime.substring(4*byteSize, 5*byteSize), 16);
        System.out.print("." + minute);

        int second = Integer.parseInt(dateAndTime.substring(5*byteSize), 16);
        System.out.print("." + second);
        System.out.println();
        System.out.println();
    }

    private void checkTic(String tic) {
        int ticInt = Integer.parseInt(tic, 16);
        String t = String.format("%8s", ticInt).replace(" ", "0");

        if (t.substring(0, 1).equals("1")) {
            System.out.println("    Oil and Electricity: Disconnected");
            System.out.println();
        }
        else {
            System.out.println("    Oil and Electricity: Connected");
            System.out.println();
        }

        if (t.substring(1, 2).equals("1")) {
            System.out.println("    GPS Tracking: On");
            System.out.println();
        }
        else {
            System.out.println("    GPS Tracking: Off");
            System.out.println();
        }

        System.out.println("    Alarm Status");
        switch (t.substring(4, 5) + t.substring(3, 4) + t.substring(2, 3)) {
            case "000":
                System.out.print(" Normal");
                System.out.println();
                break;
            case "001":
                System.out.print(" Shock Alarm");
                System.out.println();
                break;
            case "010":
                System.out.print(" Power Cut Alarm");
                System.out.println();
                break;
            case "011":
                System.out.print(" Low Battery Alarm");
                System.out.println();
                break;
            case "100":
                System.out.print(" SOS");
                System.out.println();
                break;
            default:
                System.out.print(" Not known");
                System.out.println();
                break;
        }

        if (t.substring(5, 6).equals("1")) {
            System.out.println("    Charge: On");
            System.out.println();
        }
        else {
            System.out.println("    Charge: Off");
            System.out.println();
        }

        if (t.substring(6, 7).equals("1")) {
            System.out.println("    ACC: High");
            System.out.println();
        }
        else {
            System.out.println("    ACC: Low");
            System.out.println();
        }

        if (t.substring(7).equals("1")) {
            System.out.println("    Air Condition: On");
            System.out.println();
        }
        else {
            System.out.println("Air Condition: Off");
            System.out.println();
        }
    }

    private void checkLanguage(String language) {
        if (language.equals("01") || language.equals("0001")) {
            System.out.println("    Language is Chinese");
            System.out.println();
        }
        else if (language.equals("02") || language.equals("0002")) {
            System.out.println("    Language is English");
            System.out.println();
        }
        else {
            System.out.println("    Not Known");
            System.out.println();
        }
    }

    private void checkAlarm(String alarm) {
        switch (alarm) {
            case "00":
                System.out.println("    Normal");
                System.out.println();
                break;
            case "01":
                System.out.println("    SOS");
                System.out.println();
                break;
            case "02":
                System.out.println("    Power Cut Alarm");
                System.out.println();
                break;
            case "03":
                System.out.println("    Shock Alarm");
                System.out.println();
                break;
            case "04":
                System.out.println("    Fence In Alarm");
                System.out.println();
                break;
            case "05":
                System.out.println("    Fence Out Alarm");
                System.out.println();
                break;
            case "09":
                System.out.println("    Move Alarm");
                System.out.println();
                break;
            case "10":
                System.out.println("    Low Battery Alarm");
                System.out.println();
                break;
            case "12":
                System.out.println("    Over Speed Alarm");
                System.out.println();
                break;
            case "20":
                System.out.println("    Light Alarm");
                System.out.println();
                break;
            case "21":
                System.out.println("    Off Line Alarm");
                System.out.println();
                break;
            case "0C":
                System.out.println("    Removal Alarm");
                System.out.println();
                break;
            default:
                System.out.println("    Not known");
                break;
        }
    }

    private void checkVol(String volLevel) {
        int vol = Integer.parseInt(volLevel, 16);

        switch (vol) {
            case 0:
                System.out.println("    No power");
                System.out.println();
                break;
            case 1:
                System.out.println("    Extremely low power");
                System.out.println();
                break;
            case 2:
                System.out.println("    Very low battery");
                System.out.println();
                break;
            case 3:
                System.out.println("    Low battery");
                System.out.println();
                break;
            case 4:
                System.out.println("    Medium");
                System.out.println();
                break;
            case 5:
                System.out.println("    High");
                System.out.println();
                break;
            case 6:
                System.out.println("    Very high");
                System.out.println();
                break;
            default:
                System.out.println("    Not known");
                break;
        }
    }

    private void checkSig(String sig) {
        int s = Integer.parseInt(sig, 16);

        switch (s) {
            case 0:
                System.out.println("    No signal");
                System.out.println();
                break;
            case 1:
                System.out.println("    Extremely weak signal");
                System.out.println();
                break;
            case 2:
                System.out.println("    Very weak signal");
                System.out.println();
                break;
            case 3:
                System.out.println("    Good signal");
                System.out.println();
                break;
            case 4:
                System.out.println("    Strong signal");
                System.out.println();
                break;
            default:
                System.out.println("    Not known");
                break;
        }
    }
}
