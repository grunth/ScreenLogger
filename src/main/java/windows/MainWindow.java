package windows;

import services.files.Dirs;
import services.log.Errors;
import threads.ShootingThread;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class MainWindow extends JFrame {
    private JFileChooser fileChooser = null;
    private static MainWindow app;
    private TrayIcon iconTr;
    private SystemTray sT = SystemTray.getSystemTray();
    private boolean chetTray = false; // переменная, чтобы был вывод сообщения в трее только при первом сворачивании
    private JPanel contentPane;
    private JTextField tf_Interval;
    private JTextField tf_Path;
    private Integer period = 1;
    private static final String ICON_ON_URL = "play-icon.png";
    private static final String ICON_OFF_URL = "stop-icon.png";
    private static final String LOGO_URL = "logo.png";
    private static Image Icon_ON = null;
    private static Image Icon_OFF = null;
    private static Image Logo = null;

    private Dirs dirs = new Dirs();
    private File rootPath = dirs.getHomeDir();

    ShootingThread newThread = new ShootingThread("newThreat", rootPath);

    public static void main(String[] args) {

        createTrayIcons();

        EventQueue.invokeLater(() -> {
            try {
                createApp();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(new MainWindow()
                        , e.getLocalizedMessage()
                        , "Ошибка!"
                        , JOptionPane.PLAIN_MESSAGE);
                System.exit(0);
            }
        });

    }

    private static void createTrayIcons() {
        try {
            Icon_ON = ImageIO.read(MainWindow.class.getClassLoader().getResource(ICON_ON_URL));
            Icon_OFF = ImageIO.read(MainWindow.class.getClassLoader().getResource(ICON_OFF_URL));
            Logo = ImageIO.read(MainWindow.class.getClassLoader().getResource(LOGO_URL));
        } catch (IOException e) {
            Errors.createErrorFile(e);
        }
    }

    private static void createApp() {
        app = new MainWindow();
        app.setVisible(true);
        app.setDefaultLookAndFeelDecorated(true);
        app.setResizable(false);

        //Обработчик основного окна со всеми методами
        app.addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                Object[] options = {"Да", "Нет!"};
                int n = JOptionPane.showOptionDialog(e.getWindow(), "Выйти из программы?",
                        "Подтверждение", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                        options, options[0]);
                if (n == 0) {
                    e.getWindow().setVisible(false);
                    System.exit(0);
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });

    }

    /**
     * Create the frame.
     */

    public MainWindow() {

        newThread.msuspend();

        setTitle("Screen logger");
        setIconImage(Logo);

        if (SystemTray.isSupported()) {
            createTrayIcon();
        }

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setBounds(100, 100, 400, 150);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // Блок "Путь к файлам"
        JPanel panel_path = new JPanel();
        panel_path.setBounds(5, 5, 390, 32);
        contentPane.add(panel_path);

        JLabel label_path = new JLabel("Путь к файлам");
        panel_path.add(label_path);

        tf_Path = new JTextField();
        tf_Path.setPreferredSize(new Dimension(100, 27));
        tf_Path.setColumns(18);
        tf_Path.setEditable(false);
        tf_Path.setText(rootPath.getPath());
        panel_path.add(tf_Path);

        JButton b_Path = new JButton("...");
        panel_path.add(b_Path);

        // Блок "интервал"
        JPanel panel_2 = new JPanel();
        panel_2.setBounds(5, 40, 390, 32);
        contentPane.add(panel_2);

        JLabel label_2 = new JLabel("Интервал:");
        panel_2.add(label_2);

        tf_Interval = new JTextField();
        tf_Interval.setToolTipText("Интервал через который будем делать фотки");
        tf_Interval.setEditable(false);
        tf_Interval.setColumns(5);
        tf_Interval.setText(String.valueOf(period) + " сек.");
        panel_2.add(tf_Interval);

        JSlider slider = new JSlider(1, 3600, 1);
        panel_2.add(slider);

        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                period = ((JSlider) e.getSource()).getValue();
                if (period > 60) {
                    tf_Interval.setText(String.valueOf(period / 60) + " мин.");
                } else tf_Interval.setText(String.valueOf(period) + " сек.");
            }
        });

        // Блок "Включить!"
        JPanel panel_3 = new JPanel();
        panel_3.setBounds(5, 70, 390, 32);
        contentPane.add(panel_3);

        JCheckBox cb_Start = new JCheckBox("Включить!");
        panel_3.add(cb_Start);

        cb_Start.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (cb_Start.isSelected()) {
                    iconTr.setImage(Icon_ON);
                    newThread.rootDir = rootPath;
                    newThread.time = period;
                    newThread.mresume();
                } else {
                    iconTr.setImage(Icon_OFF);
                    newThread.msuspend();
                }
            }
        });

        // Создание экземпляра JFileChooser
        fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Выбор директории");
        // Определение режима - только каталог
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        b_Path.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                fileChooser.showOpenDialog(app);
                try {
                    tf_Path.setText(fileChooser.getSelectedFile().toString());
                    rootPath = fileChooser.getSelectedFile();
                } catch (Exception e) {
                    //Тут ничего не надо делать
                }

            }
        });
    }

    //Создание иконки в трее
    private void createTrayIcon() {
        iconTr = new TrayIcon(Icon_OFF, "ScreenLogger v.1.1.0");
        iconTr.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                setVisible(true);
                setState(JFrame.NORMAL);
                removeTr();
            }
        });

        // обработчик мыши
        MouseListener mouS = new MouseListener() {
            public void mouseClicked(MouseEvent ev) {
            }

            public void mouseEntered(MouseEvent ev) {
            }

            public void mouseExited(MouseEvent ev) {
            }

            public void mousePressed(MouseEvent ev) {
            }

            public void mouseReleased(MouseEvent ev) {
            }
        };
//
        iconTr.addMouseListener(mouS);
        MouseMotionListener mouM = new MouseMotionListener() {
            public void mouseDragged(MouseEvent ev) {
            }

            // при наведении
            public void mouseMoved(MouseEvent ev) {
                iconTr.setToolTip("Двойной щелчок - развернуть");
            }
        };

        iconTr.addMouseMotionListener(mouM);
        addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent ev) {
                if (ev.getNewState() == JFrame.ICONIFIED) {
                    setVisible(false);
                    addT();
                }
            }
        });
    }

    // метод удаления из трея
    private void removeTr() {
        sT.remove(iconTr);
    }

    // метод добавления в трей
    private void addT() {
        try {
            sT.add(iconTr);
            if (chetTray == false) {
                iconTr.displayMessage("Screen logger", "Программа все еще запущена!", TrayIcon.MessageType.INFO);
            }
            chetTray = true;
        } catch (AWTException ex) {
            JOptionPane.showMessageDialog(new MainWindow()
                    , ex.getLocalizedMessage()
                    , "Ошибка!"
                    , JOptionPane.PLAIN_MESSAGE);
            System.exit(0);
        }
    }

}
