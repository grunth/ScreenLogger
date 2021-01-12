package threads;

import services.files.Dirs;
import services.log.Errors;
import services.screen.Shoot;

import java.io.File;

/**
 * Создание потока для скриншотера
 */

public class ShootingThread implements Runnable {

    public boolean suspendFlag = false;
    public int i = 1;
    public int time = 1;
    public String name;
    public File rootDir;
    public File currentDir;
    private Dirs dirs = new Dirs();
    private Shoot goShot = new Shoot();

    public ShootingThread(String threadname, File rDir) {
        rootDir = rDir;
        name = threadname;
        Thread t = new Thread(this, name);
        t.start();
    }

    @Override
    public void run() {
        try {
            Boolean b = true;
            for (;;) {

                synchronized (this) {
                    while (suspendFlag) {
                        wait();
                    }
                }
                //выполнить один раз и больше не выполнять
                if (b) currentDir = dirs.createNewDir(rootDir);
                b = false;

                goShot.Shut(currentDir);
                Thread.sleep(time * 1000);
                i++;
                //Сохранять только 1000 фоток на папку
                if (i > 1000) {
                    i = 1;
                    currentDir = dirs.createNewDir(rootDir);
                }
            }
        } catch (InterruptedException e) {
            Errors.createErrorFile(e);
        }
    }

    public synchronized void msuspend() {
        suspendFlag = true;
    }

    public synchronized void mresume() {
        suspendFlag = false;
        notify();
    }
}
