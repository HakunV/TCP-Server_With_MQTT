package tcpserver.Server;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.Border;

public class Window extends JFrame implements ActionListener, Runnable {
    private ClientWriter cw;

    private JPanel panelTxt;
    private JPanel panelIn;
    private JPanel buttonPan;
    private JTextArea input, txt;
    private JScrollPane scr;
    private JButton enter, setApn, timer, workMode, reboot, num, alarmNum, cancelNum, setTimezone, setGeo, cancelGeo, checkParam, checkVer;
    private JLabel buttonsLbl;

    private Border border = BorderFactory.createLineBorder(Color.black);

    private boolean running = true;
    private boolean success = true;
    private boolean close = false;

    public int FRAME_WIDTH = 800;
    public int FRAME_HEIGHT = 700;

    public Window(ClientWriter cw) {
        this.cw = cw;

        getContentPane().setLayout(new BorderLayout());

        panelTxt = new JPanel();
        panelTxt.setLayout(new BoxLayout(panelTxt, BoxLayout.LINE_AXIS));

        panelIn = new JPanel();
        panelIn.setLayout(new BoxLayout(panelIn, BoxLayout.LINE_AXIS));

        buttonPan = new JPanel();
        buttonPan.setLayout(new BoxLayout(buttonPan, BoxLayout.PAGE_AXIS));

        txt = new JTextArea();
        txt.setEditable(false);
        txt.setLineWrap(true);
        txt.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        input = new JTextArea();
        input.setEditable(true);
        input.setBorder(border);
        input.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        scr = new JScrollPane(txt);
        scr.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scr.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        enter = new JButton("Enter");
		enter.addActionListener(this);

        buttonsLbl = new JLabel("Commands:");
        buttonsLbl.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        buttonPan.add(buttonsLbl);

        setButtons();

        panelTxt.add(txt);
        panelTxt.add(scr);
        panelTxt.add(buttonPan);

        panelIn.add(input);
        panelIn.add(enter);

        getContentPane().add(panelTxt, BorderLayout.CENTER);
        getContentPane().add(panelIn, BorderLayout.SOUTH);

        this.setTitle("Terminal");
		this.setSize(this.FRAME_WIDTH, this.FRAME_HEIGHT);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);

        txt.setText(stockText());
    }

    public void run() {
        while (running) {
            try {
                synchronized(this) {
                    wait();
                }

                if (close) {
                    break;
                }

                if (success) {
                    txt.setText("Command sent successfully");
                }
                else {
                    txt.setText("Command is not valid");
                    success = true;
                }
                Thread.sleep(3000);

                txt.setText(stockText());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        this.dispose();
    }

    public void closeWindow() {
        close = true;
        synchronized (this) {
            notify();
        }
    }

    private void setButtons() {
        setApn = new JButton("Set APN");
        setApn.addActionListener(this);
        setApn.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        buttonPan.add(setApn);

        timer = new JButton("Set Timer");
        timer.addActionListener(this);
        timer.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        buttonPan.add(timer);

        workMode = new JButton("Set Work Mode");
        workMode.addActionListener(this);
        workMode.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        buttonPan.add(workMode);

        reboot = new JButton("Reboot");
        reboot.addActionListener(this);
        reboot.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        buttonPan.add(reboot);

        num = new JButton("Set Phone Number");
        num.addActionListener(this);
        num.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        buttonPan.add(num);

        alarmNum = new JButton("Set Alarm Number");
        alarmNum.addActionListener(this);
        alarmNum.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        buttonPan.add(alarmNum);

        cancelNum = new JButton("Cancel Alarm Number");
        cancelNum.addActionListener(this);
        cancelNum.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        buttonPan.add(cancelNum);

        setTimezone = new JButton("Set Time Zone");
        setTimezone.addActionListener(this);
        setTimezone.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        buttonPan.add(setTimezone);

        setGeo = new JButton("Set GEO Fence");
        setGeo.addActionListener(this);
        setGeo.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        buttonPan.add(setGeo);

        cancelGeo = new JButton("Cancel GEO Fence");
        cancelGeo.addActionListener(this);
        cancelGeo.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        buttonPan.add(cancelGeo);

        checkParam = new JButton("Check Parameters");
        checkParam.addActionListener(this);
        checkParam.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        buttonPan.add(checkParam);

        checkVer = new JButton("Check Version");
        checkVer.addActionListener(this);
        checkVer.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        buttonPan.add(checkVer);
    }

    public void write(String message) {
        input.setText(message);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == enter) {
            String str = input.getText();

            input.setText("");

            if (str.contains("APN")) {
                String exCheck = str.substring(0, 4) + str.substring(str.length()-1);
                if (exCheck.equals("APN,#")) {

                }
                else {
                    success = false;
                }
            }
            else if (str.contains("TIMER")) {
                String exCheck = str.substring(0, 6) + str.substring(str.length()-1);
                if (exCheck.equals("TIMER,#")) {

                }
                else {
                    success = false;
                }
            }
            else if (str.contains("3P")) {
                String exCheck = str.substring(0, 19) + str.substring(str.length()-1);
                if (exCheck.equals("<SPBSJ*P:BSJGPS*3P:>")) {

                }
                else {
                    success = false;
                }
            }
            else if (str.equals("RESET#")) {

            }
            else if (str.contains("6F")) {
                String exCheck = str.substring(0, 19) + str.substring(str.length()-1);
                if (exCheck.equals("<SPBSJ*P:BSJGPS*6F:>")) {

                }
                else {
                    success = false;
                }
            }
            else if (str.contains("3C")) {
                String exCheck = str.substring(0, 19) + str.substring(str.length()-1);
                if (exCheck.equals("<SPBSJ*P:BSJGPS*3C:>")) {

                }
                else {
                    success = false;
                }
            }
            else if (str.contains("GMT")) {
                String exCheck = str.substring(0, 4) + str.substring(str.length()-1);
                if (exCheck.equals("GMT,#")) {

                }
                else {
                    success = false;
                }
            }
            else if (str.contains("3D")) {
                String exCheck = str.substring(0, 19) + str.substring(str.length()-1);
                if (exCheck.equals("<SPBSJ*P:BSJGPS*3D:>")) {

                }
                else {
                    success = false;
                }
            }
            else if (str.equals("<CKBSJ>")) {
                
            }
            else if (str.equals("<CKVER>")) {
                
            }
            else {
                success = false;
            }

            if (success) {
                
                try {
                    cw.sendCommand(str);
                } catch (IOException e1) {
                    System.out.println("Failed to send command");
                    e1.printStackTrace();
                }
            }

            synchronized (this) {
                notify();
            }
        }
        else if (e.getSource() == setApn) {
            input.setText("APN,#");
            input.setCaretPosition(4);
            input.requestFocus();
        }
        else if (e.getSource() == timer) {
            input.setText("TIMER,#");
            input.setCaretPosition(6);
            input.requestFocus();
        }
        else if (e.getSource() == workMode) {
            input.setText("<SPBSJ*P:BSJGPS*3P:>");
            input.setCaretPosition(19);
            input.requestFocus();
        }
        else if (e.getSource() == reboot) {
            input.setText("RESET#");
            input.requestFocus();
        }
        else if (e.getSource() == num) {
            input.setText("<SPBSJ*P:BSJGPS*6F:>");
            input.setCaretPosition(19);
            input.requestFocus();
        }
        else if (e.getSource() == alarmNum) {
            input.setText("<SPBSJ*P:BSJGPS*3C:>");
            input.setCaretPosition(19);
            input.requestFocus();
        }
        else if (e.getSource() == cancelNum) {
            input.setText("<SPBSJ*P:BSJGPS*3C:00000000000>");
            input.requestFocus();
        }
        else if (e.getSource() == setTimezone) {
            input.setText("GMT,,,#");
            input.setCaretPosition(4);
            input.requestFocus();
        }
        else if (e.getSource() == setGeo) {
            input.setText("<SPBSJ*P:BSJGPS*3D:,,>");
            input.setCaretPosition(19);
            input.requestFocus();
        }
        else if (e.getSource() == cancelGeo) {
            input.setText("<SPBSJ*P:BSJGPS*3D:0>");
            input.setCaretPosition(19);
            input.requestFocus();
        }
        else if (e.getSource() == checkParam) {
            input.setText("<CKBSJ>");
            input.requestFocus();
        }
        else if (e.getSource() == checkVer) {
            input.setText("<CKVER>");
            input.requestFocus();
        }
    }

    private static String stockText() {
        String newline = "\n";

        String text = "Available Commands:\n";

        String apnCom = "    -  APN,apn#    // replace apn\n";
        String timerCom = "    -  TIMER,T#    // T is in seconds\n";
        String workCom = "    -  <SPBSJ*P:BSJGPS*3P:x>    // replace x\n";
        String rebootCom = "    -  RESET#\n";
        String numCom = "    -  <SPBSJ*P:BSJGPS*6F:car owner number>    // replace car owner number, to clear put \",,,,\"\n";
        String alarmNumCom = "    -  <SPBSJ*P:BSJGPS*3C:number>    // replace number\n";
        String cancelAlarmNumCom = "    -  <SPBSJ*P:BSJGPS*3C:00000000000>\n";
        String timeZoneCom = "    -  GMT,[A],[B],[C]#    // replace [A], [B] and [C]\n";
        String geoFenceCom = "    -  <SPBSJ*P:BSJGPS*3D:x,llll,m>    // replace x, 1111 and m\n";
        String cancelGeoFenceCom = "    -  <SPBSJ*P:BSJGPS*3D:x,llll,m>\n";
        String checkParamCom = "    -  <CKBSJ>\n";
        String checkVerCom = "    -  <CKVER>\n";

        return text + newline + apnCom + newline + timerCom + newline + workCom + newline + rebootCom + newline + numCom + newline + alarmNumCom
        + newline + cancelAlarmNumCom + newline + timeZoneCom + newline + geoFenceCom + newline + cancelGeoFenceCom + newline + checkParamCom
        + newline + checkVerCom;
    }
}
