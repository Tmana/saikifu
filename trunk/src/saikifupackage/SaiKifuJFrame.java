/*
 * SaiKifuJFrame.java
 *
 * Created on den 11 december 2005, 01:16
 * Copyright (c) Lars Englund
 */

package saikifupackage;

import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;
import javax.media.control.*;
import java.util.*;
import java.awt.*;
import java.io.*;


public class SaiKifuJFrame extends javax.swing.JFrame implements ControllerListener {
    
    static final String gobanIntToAlpha = "abcdefghijklmnopqrs";
    
    Object waitSync = new Object();
    static Processor p;
    boolean stateTransitionOK = true;
    static StoneDetectionCodec mde = new StoneDetectionCodec();
    static GobanJPanel gp = new GobanJPanel();
    
    static int BLACK = 1;
    static int WHITE = 2;
    int blackSeconds, whiteSeconds;
    int turn = BLACK;
    Timer timer = new Timer();
    boolean timerRunning = false;
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    boolean gameRunning = false;
    PrintWriter sgfPw;
    
    
    /**
     * Creates new form SaiKifuJFrame
     */
    public SaiKifuJFrame() {
        DataSource ds = null;
        p = camera();
        initComponents();
        p.addControllerListener(this);
        p.configure();
        if (!waitForState(p.Configured)) {
            System.err.println("Failed to configure the processor.");
            System.exit(0);
        }
        
        // So I can use it as a player.
        p.setContentDescriptor(null);
        // Obtain the track controls.
        TrackControl tc[] = p.getTrackControls();
        if (tc == null) {
            System.err.println("Failed to obtain track controls from the processor.");
            System.exit(0);
        }
        
        // Search for the track control for the video track.
        TrackControl videoTrack = null;
        for (int i = 0; i < tc.length; i++) {
            if (tc[i].getFormat() instanceof VideoFormat) {
                videoTrack = tc[i];
                break;
            }
        }
        if (videoTrack == null) {
            System.err.println("The input media does not contain a video track.");
            System.exit(0);
        }
        
        
        // Instantiate and set the frame access codec to the data flow path.
        try {
            Codec codec[] = { mde };
            videoTrack.setCodecChain(codec);
        } catch (UnsupportedPlugInException e) {
            System.err.println("The processor does not support effects.");
        }
        
        p.realize();
        if (!waitForState(p.Realized)) {
            System.err.println("Failed to configure the player.");
            System.exit(0);
        }
        
        p.prefetch();
        if(!waitForState(p.Prefetched)){
            System.err.println("Failed to prefetch the player");
        }
        
    }
    
    private Processor camera() {
        Processor process;
        //Vector v = CaptureDeviceManager.getDeviceList(new VideoFormat(VideoFormat.RGB, new Dimension(640, 480), 640*480*3, VideoFormat.byteArray, (float)15.0));
        Vector v = CaptureDeviceManager.getDeviceList(new VideoFormat(VideoFormat.RGB));
        System.out.println("Found " + v.size() + " capture devices.");
        System.out.println(((CaptureDeviceInfo) v.get(0)).getName());
        try {
            MediaLocator ml = ((CaptureDeviceInfo) v.get(0)).getLocator();
            System.out.println(ml.toString());
            process = Manager.createProcessor(ml);
            return process;
        } catch(Exception e) {
            System.err.println("Exception occured\n");
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }
    
    
    boolean waitForState(int state) {
        synchronized (waitSync) {
            try {
                while (p.getState() != state && stateTransitionOK)
                    waitSync.wait();
            } catch (Exception e) {}
        }
        return stateTransitionOK;
    }
    
    
    /**
     * Controller Listener.
     */
    public void controllerUpdate(ControllerEvent evt) {
        
        System.out.println(this.getClass().getName()+evt);
        if (evt instanceof ConfigureCompleteEvent ||
                evt instanceof RealizeCompleteEvent ||
                evt instanceof PrefetchCompleteEvent) {
            synchronized (waitSync) {
                stateTransitionOK = true;
                waitSync.notifyAll();
            }
        } else if (evt instanceof ResourceUnavailableEvent) {
            synchronized (waitSync) {
                System.out.println("ResourceUnavailable");
                stateTransitionOK = false;
                waitSync.notifyAll();
            }
        } else if (evt instanceof EndOfMediaEvent) {
            p.close();
            System.exit(0);
        }
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        videoPanel = new javax.swing.JPanel();
        gobanPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        startButton = new javax.swing.JButton();
        pauseButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        saveReferenceButton = new javax.swing.JButton();
        resetGobanButton = new javax.swing.JButton();
        thresholdSpinner = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        resetButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        blackTimeTextField = new javax.swing.JTextField();
        whiteTimeTextField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        debugTextArea = new javax.swing.JTextArea();
        saiPanel = new javax.swing.JPanel();

        getContentPane().setLayout(null);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SaiKifu");
        setName("Kifu");
        videoPanel.setLayout(new java.awt.BorderLayout());

        videoPanel.setBorder(new javax.swing.border.EtchedBorder());
        getContentPane().add(videoPanel);
        videoPanel.setBounds(2, 2, 640, 480);

        gobanPanel.setLayout(null);

        gobanPanel.setBackground(new java.awt.Color(255, 204, 51));
        gobanPanel.setBorder(new javax.swing.border.EtchedBorder());
        getContentPane().add(gobanPanel);
        gobanPanel.setBounds(400, 490, 240, 240);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Black");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(160, 550, 100, 20);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("White");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(290, 550, 100, 20);

        startButton.setText("Start");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        getContentPane().add(startButton);
        startButton.setBounds(160, 490, 110, 23);

        pauseButton.setText("Pause");
        pauseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseButtonActionPerformed(evt);
            }
        });

        getContentPane().add(pauseButton);
        pauseButton.setBounds(160, 520, 110, 23);

        jPanel1.setLayout(null);

        jPanel1.setBorder(new javax.swing.border.TitledBorder("Detection"));
        saveReferenceButton.setText("Save reference");
        saveReferenceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveReferenceButtonActionPerformed(evt);
            }
        });

        jPanel1.add(saveReferenceButton);
        saveReferenceButton.setBounds(10, 20, 130, 23);

        resetGobanButton.setText("Reset clicks");
        resetGobanButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetGobanButtonActionPerformed(evt);
            }
        });

        jPanel1.add(resetGobanButton);
        resetGobanButton.setBounds(10, 50, 130, 23);

        thresholdSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                thresholdSpinnerStateChanged(evt);
            }
        });

        jPanel1.add(thresholdSpinner);
        thresholdSpinner.setBounds(90, 80, 50, 18);

        jLabel1.setText("Threshold");
        jPanel1.add(jLabel1);
        jLabel1.setBounds(10, 80, 80, 20);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 490, 150, 110);

        resetButton.setText("Reset");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        getContentPane().add(resetButton);
        resetButton.setBounds(280, 520, 110, 23);

        stopButton.setText("Stop");
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });

        getContentPane().add(stopButton);
        stopButton.setBounds(280, 490, 110, 23);

        blackTimeTextField.setFont(new java.awt.Font("Tahoma", 0, 24));
        blackTimeTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        blackTimeTextField.setText("1800");
        getContentPane().add(blackTimeTextField);
        blackTimeTextField.setBounds(160, 570, 100, 30);

        whiteTimeTextField.setFont(new java.awt.Font("Tahoma", 0, 24));
        whiteTimeTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        whiteTimeTextField.setText("1800");
        getContentPane().add(whiteTimeTextField);
        whiteTimeTextField.setBounds(290, 570, 100, 30);

        jScrollPane1.setBorder(new javax.swing.border.EtchedBorder());
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setAutoscrolls(true);
        debugTextArea.setBorder(null);
        jScrollPane1.setViewportView(debugTextArea);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(160, 610, 230, 120);

        saiPanel.setLayout(new java.awt.BorderLayout());

        saiPanel.setBorder(new javax.swing.border.EtchedBorder());
        getContentPane().add(saiPanel);
        saiPanel.setBounds(10, 610, 130, 120);

        pack();
    }
    // </editor-fold>//GEN-END:initComponents

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
// TODO add your handling code here:
        timer.cancel();
        timerRunning = false;
        gameRunning = false;
        try {
            sgfPw.println(")");
            sgfPw.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            sgfPw.close();
        }
        debugTextArea.append("Game stopped\n");
    }//GEN-LAST:event_stopButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
// TODO add your handling code here:
        timer.cancel();
        timerRunning = false;
        gameRunning = false;
        mde.clearStones();
        gp.clearStones();
        gp.repaint();
        blackTimeTextField.setText("1800");
        whiteTimeTextField.setText("1800");
    }//GEN-LAST:event_resetButtonActionPerformed
    
    private void resetGobanButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetGobanButtonActionPerformed
// TODO add your handling code here:
        mde.resetGoban();
    }//GEN-LAST:event_resetGobanButtonActionPerformed
    
    private void thresholdSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_thresholdSpinnerStateChanged
// TODO add your handling code here:
        //System.out.println( "Value: " + ((javax.swing.JSpinner)evt.getSource()).getValue() );
        mde.setThreshold((Integer)((javax.swing.JSpinner)evt.getSource()).getValue());
    }//GEN-LAST:event_thresholdSpinnerStateChanged
    
    private void pauseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseButtonActionPerformed
// TODO add your handling code here:
        toggleTimer();
    }//GEN-LAST:event_pauseButtonActionPerformed
    
    private void toggleTimer() {
        if (timerRunning) {
            timer.cancel();
            timerRunning = false;
            gameRunning = false;
            debugTextArea.append("Game paused\n");
        }
        else {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    if (turn == BLACK) {
                        blackSeconds --;
                        if (blackSeconds == 0) {
                            debugTextArea.append("Black run out of time!\n");
                            timer.cancel(); //Terminate the timer thread
                        }
                        blackTimeTextField.setText("" + blackSeconds);
                    } else {
                        whiteSeconds --;
                        if (whiteSeconds == 0) {
                            debugTextArea.append("White run out of time!\n");
                            timer.cancel(); //Terminate the timer thread
                        }
                        whiteTimeTextField.setText("" + whiteSeconds);
                    }
                }
            }, 1000, 1000);
            timerRunning = true;
            gameRunning = true;
            debugTextArea.append("Game resumed\n");
        }
    }
    
    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
// TODO add your handling code here:
        
        int n = 1;
        File sgf_file = new File("saikifu_" + n + ".sgf");
        while (sgf_file.exists()) {
            n++;
            sgf_file = new File("saikifu_" + n + ".sgf");
        }
        debugTextArea.append("Created " + sgf_file.getName() + "\n");
        try {
            sgfPw = new PrintWriter(new FileOutputStream(sgf_file), true);
            sgfPw.println("(;GM[1]FF[4]AP[glGo:1.3.1]ST[1]");
            sgfPw.println("");
            sgfPw.println("SZ[19]KM[6.5]");
            sgfPw.println("PW[White]PB[Black]DT[2005-12-12]");
            sgfPw.println("");
        }
        catch (Exception e) {
            e.printStackTrace();
            sgfPw.close();
        }
        
        blackSeconds = Integer.parseInt(blackTimeTextField.getText());
        whiteSeconds = Integer.parseInt(whiteTimeTextField.getText());
        turn = BLACK;
        toolkit.beep();
        gameRunning = true;
        debugTextArea.append("Game started\n");
        toggleTimer();
    }//GEN-LAST:event_startButtonActionPerformed
    
    private void saveReferenceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveReferenceButtonActionPerformed
// TODO add your handling code here:
        mde.saveReference();
    }//GEN-LAST:event_saveReferenceButtonActionPerformed
    
    private static void videoClicked(java.awt.event.MouseEvent evt) {
        mde.videoClicked(evt);
    }
    
    public void newStonePlaced(int x, int y) {
        if (gameRunning) {
            gp.addStone(new Point(x, y));
            gp.repaint();
            sgfPw.println("");
            if (turn == BLACK) {
                sgfPw.print(";B");
                debugTextArea.append("Black");
                turn = WHITE;
            }
            else {
                sgfPw.print(";W");
                debugTextArea.append("White");
                turn = BLACK;
            }
            sgfPw.print("[" + gobanIntToAlpha.charAt(x-1) + gobanIntToAlpha.charAt(y-1) + "]");
            debugTextArea.append(" placed a stone at " + x + ", " + y + " [" + gobanIntToAlpha.charAt(x-1) + gobanIntToAlpha.charAt(y-1) + "]\n");
        }
        else {
            debugTextArea.append("Game not running, new stone ignored\n");
        }
    }
    
   
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        SaiKifuJFrame m;
        m = new SaiKifuJFrame();
        
        Component video_component = p.getVisualComponent();
        video_component.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                System.out.println("clicked at " + evt.getX() + ", " + evt.getY());
                videoClicked(evt);
            }
        }); 
        videoPanel.add("Center", video_component);

        gp.setSize(gobanPanel.getWidth(), gobanPanel.getHeight());
        gobanPanel.add(gp);
        
        ClassLoader cldr = m.getClass().getClassLoader();
        java.net.URL imageURL   = cldr.getResource("saikifupackage/images/sai.jpg");
        javax.swing.ImageIcon sai_icon = new javax.swing.ImageIcon(imageURL);
        saiPanel.add("Center", new javax.swing.JLabel(sai_icon));
        
        thresholdSpinner.setValue(new Integer(50));
        mde.setThreshold(50);
        
        mde.setMainptr(m);
                
        //m.pack();
        m.setSize(654, 770);
        m.setVisible(true);
        m.show();
        System.out.println("Starting player");
        p.start();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField blackTimeTextField;
    private javax.swing.JTextArea debugTextArea;
    private static javax.swing.JPanel gobanPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton pauseButton;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton resetGobanButton;
    private static javax.swing.JPanel saiPanel;
    private javax.swing.JButton saveReferenceButton;
    private javax.swing.JButton startButton;
    private javax.swing.JButton stopButton;
    private static javax.swing.JSpinner thresholdSpinner;
    private static javax.swing.JPanel videoPanel;
    private javax.swing.JTextField whiteTimeTextField;
    // End of variables declaration//GEN-END:variables
    
}
