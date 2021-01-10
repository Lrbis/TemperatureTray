package com.tt;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.sensors.Temperature;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Objects;

public class TrayUI {
    public static void main(String[] args) {
        String[] geoAPIKeyValue = APIUtils.getKeysFromAPIResponse("https://freegeoip.app/json/", "city");
        //System.out.println(APIUtils.getIPAddress());
        //String[] weatherAPIKeyValue = {"35.0", "21.66", "24.77", "20.44", "50d"};//APIUtils.getKeysFromAPIResponse("http://api.openweathermap.org/data/2.5/weather?q="+ geoAPIKeyValue[0] +"&units=metric&appid=", "main.temp", "main.feels_like", "main.temp_max", "main.temp_min", "weather.0.icon");

        EventQueue.invokeLater(() -> {

            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }

            if (!SystemTray.isSupported()) {
                System.out.println("SystemTray is not supported");
                System.exit(0);
            }

            SystemTray tray = SystemTray.getSystemTray();
            Image image = null;
            try {
                image = ImageIO.read(Objects.requireNonNull(TrayUI.class.getClassLoader().getResourceAsStream("resources/tt.png")));
            } catch (IOException e) {
                e.printStackTrace();
            }

            PopupMenu menu = new PopupMenu();

            MenuItem messageItem = new MenuItem("Show Message");
            messageItem.addActionListener(e -> JOptionPane.showMessageDialog(null, "Temperature Tray is in making!"));
            menu.add(messageItem);

            MenuItem closeItem = new MenuItem("Close");
            closeItem.addActionListener(e -> System.exit(0));
            menu.add(closeItem);

            TrayIcon icon = new TrayIcon(Objects.requireNonNull(image), "Temperature Tray", menu);
            JLabel centerLabel = new JLabel();
            JLabel pageStartLabel = new JLabel();
            JLabel pageEndLabel = new JLabel();
            JLabel sysPnlCenterLbl = new JLabel();
            JLabel sysPnlPageStartLbl = new JLabel();

            JPanel mainPanel = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    //Color temperatureColor = getPanelColorAccordingToTemperature(weatherAPIKeyValue[0]);
                    Color temperatureColor = new Color(0.0f, 0.0f, 0.0f, 0.80f);
                    Dimension arcs = new Dimension(15, 15);
                    int width = getWidth();
                    int height = getHeight();
                    Graphics2D graphics = (Graphics2D) g;
                    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    //Draws the rounded opaque panel
                    graphics.setColor(temperatureColor);
                    graphics.fillRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);//paint background
                }
            };


            TTJPanel sysPanel = new TTJPanel(new BorderLayout());

            CardLayout card = new CardLayout();
            final Container[] c = new Container[1];
            //final String[] metaData = {getDeviceTemperature()};


            MouseAdapter mouseAdapter = new MouseAdapter() {
                JFrame jWindow = null;

                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {

                        //{"20.55", "21.66", "24.77", "20.44", "50d"};
                        String[] weatherAPIKeyValue = {"20.55", "21.66", "24.77", "20.44", "50d"};//APIUtils.getKeysFromAPIResponse("http://api.openweathermap.org/data/2.5/weather?q="+ geoAPIKeyValue[0] +"&units=metric&appid=", "main.temp", "main.feels_like", "main.temp_max", "main.temp_min", "weather.0.icon");
                        if (jWindow == null) {
                            jWindow = new JFrame();
                            c[0] = jWindow.getContentPane();//NEW
                            c[0].setLayout(card);//NEW

                            c[0].add("A", mainPanel);
                            c[0].add("B", sysPanel);//NEW
                            mainPanel.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
                            sysPanel.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.0f));
                        }


                        jWindow.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                super.mouseClicked(e);
                                card.next(c[0]);
                            }
                        });


                        URL url;
                        Image imgIco;
                        ImageIcon i;
                        try {
                            url = new URL("http://openweathermap.org/img/wn/" + weatherAPIKeyValue[4] + "@2x.png");
                            imgIco = ImageIO.read(url);
                            i = new ImageIcon(imgIco);

                        } catch (IOException malformedURLException) {
                            i = new ImageIcon("C:/Users/Pankaj/Downloads/10d@2x.png");
                            malformedURLException.printStackTrace();
                        }

                        centerLabel.setIcon(i);
                        centerLabel.setText("<html><center><span style='color:white;font-size:55px;'>" + (int)Math.ceil(Float.parseFloat(weatherAPIKeyValue[0])) + "°C" + "</span></center></html>");
                        mainPanel.add(centerLabel, BorderLayout.CENTER);


                        pageStartLabel.setText("<html><center><span style='color:white;font-size:18px;'><b>" + geoAPIKeyValue[0] + "</b></span><br/><hr/><span style='color:white;font-size:10px;'><b>" + LocalDate.now().getDayOfWeek() + " &nbsp;|&nbsp; WEEK OF THE MONTH: " + Calendar.getInstance().get(Calendar.WEEK_OF_MONTH) + "</b></span></center></html>");
                        pageStartLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        mainPanel.add(pageStartLabel, BorderLayout.PAGE_START);


                        pageEndLabel.setText("<html><center><span style='color:white;font-size:15px;'><i>Feels like " + (int)Math.ceil(Float.parseFloat(weatherAPIKeyValue[1])) + "°C</i></span><br/><span style='color:white;font-size:10px;font-weight:bold;'>HIGH " + (int)Math.ceil(Float.parseFloat(weatherAPIKeyValue[2])) + "°C &nbsp;&nbsp; | &nbsp;&nbsp; LOW " + (int)Math.ceil(Float.parseFloat(weatherAPIKeyValue[3])) + "°C</span></center></html>");
                        pageEndLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        //pageEndLabel.setVerticalTextPosition(JLabel.TOP);
                        mainPanel.add(pageEndLabel, BorderLayout.PAGE_END);

                        sysPnlPageStartLbl.setText("<html><center><span style='color:white;font-size:18px;'><b>System Temperature</b></span><br/><hr/></center></html>");
                        sysPnlPageStartLbl.setHorizontalAlignment(SwingConstants.CENTER);
                        sysPanel.add(sysPnlPageStartLbl, BorderLayout.PAGE_START);

                        new SwingWorker<Void, Void>() {
                            @Override
                            protected Void doInBackground() {
                                while (true) {
                                    String metaData = getDeviceTemperature();
                                    metaData = metaData.replaceAll(",", "<br/>");
                                    sysPnlCenterLbl.setText("<html><span style='color:white;font-size:15px;font-weight:bold;'>" + metaData + "</span></html>");
                                }
                            }
                        }.execute();
                        sysPnlCenterLbl.setHorizontalAlignment(SwingConstants.CENTER);
                        sysPanel.add(sysPnlCenterLbl, BorderLayout.CENTER);

                        jWindow.setSize(300, 200);
                        Point point1 = e.getPoint(); //system tray icon click x,y/co-ords
                        Rectangle windowSize = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                .getMaximumWindowBounds();
                        String os = System.getProperty("os.name");
                        if (os.contains("Windows")) {
                            if (point1.y <= 300) {
                                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                                jWindow.setLocation(point1.x, (screenSize.height - windowSize.height) - 6 /*windowSize.width - 200 + 6, 6*/);
                                //System.out.println(point1.x + " " + (screenSize.height - windowSize.height));
                            } else {
                                jWindow.setLocation(/*windowSize.width - 200 - 6*/point1.x, windowSize.height - 200 - 6);
                                //System.out.println(point1.x + " " + (windowSize.height - 100 - 6));
                            }
                        } else if (os.contains("Mac")) {
                            //this solution works with windows top taskbar
                            //add logic to check taskbar location
                            //test this on Mac
                            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                            jWindow.setLocation(point1.x, screenSize.height - windowSize.height /*windowSize.width - 200 + 6, 6*/);
                            //System.out.println(point1.x + " " + (screenSize.height - windowSize.height));
                        }

                        jWindow.setType(Window.Type.UTILITY);
                        jWindow.setAlwaysOnTop(true);
                        jWindow.setUndecorated(true);
                        jWindow.setOpacity(0.90f);
                        jWindow.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.0f));
                        jWindow.setVisible(true);
                        jWindow.setFocusable(true);
                        jWindow.addWindowFocusListener(new WindowFocusListener() {
                            @Override
                            public void windowGainedFocus(WindowEvent e) {
                            }

                            @Override
                            public void windowLostFocus(WindowEvent e) {
                                mainPanel.revalidate();
                                jWindow.dispose();
                            }
                        });
                    }
                }
            };


            icon.addMouseListener(mouseAdapter);
            icon.setImageAutoSize(true);
            try {
                tray.add(icon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        });

    }

    public static String getDeviceTemperature() {
        Components components = JSensors.get.components();
        java.util.List<Cpu> cpus = components.cpus;
        StringBuilder sysMetaData = new StringBuilder();
        if (cpus != null) {
            int cpuCount = 0;
            for (final Cpu cpu : cpus) {
                cpuCount++;
                sysMetaData.append("CPU: ").append(cpu.name).append(",");
                if (cpu.sensors != null) {
                    //Print temperatures
                    java.util.List<Temperature> temps = cpu.sensors.temperatures;
                    for (final Temperature temp : temps) {
                        sysMetaData.append(temp.name).append(": ").append(temp.value).append("°C,");
                    }
                    sysMetaData.deleteCharAt(sysMetaData.length() - 1);
                    if (cpuCount != cpus.size()) {
                        sysMetaData.append("|");
                    }
                }
            }
        }

        System.out.println(sysMetaData);
        return sysMetaData.toString();
    }

    private static Color getPanelColorAccordingToTemperature(String temp) {
        //use enum instead of new color objects
        float floatTemp = Float.parseFloat(temp.trim());
        if (floatTemp <= 22 && floatTemp >= 10) { //cold
            return new Color(0.0f,0.0f,1.0f,0.8f);
        } else if (floatTemp > 22 && floatTemp <=30) { // slight hot
            return new Color(1.0f,0.6f,0.0f,0.8f);
        } else if (floatTemp >= 40 && floatTemp <= 50) {// more hot
            return new Color(1.0f,0.2f,0.0f,0.8f);
        } else if (floatTemp < 10) { //very cold
            return new Color(0.0f,0.0f,0.8f,0.8f);
        } else { //very hot
            return new Color(1.0f,0.0f,0.0f,0.8f);
        }
    }

}