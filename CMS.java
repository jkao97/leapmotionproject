import com.leapmotion.leap.*;
import java.io.*;
import java.util.*;

public class CMS{
  static long lastFrameID = 0;
  static ArrayList<Process> processes = new ArrayList<Process>();
  //Hardcode executables; implement prompt user
  static ProcessBuilder notepad = new ProcessBuilder("C:\\Windows\\notepad.exe");
  static ProcessBuilder music = new ProcessBuilder("C:\\Program Files (x86)\\Windows Media Player\\wmplayer.exe","C:\\Users\\James\\Music\\04.AmazingGrace.mp3");
  static ProcessBuilder paint = new ProcessBuilder("C:\\Windows\\System32\\mspaint.exe");
  static ProcessBuilder chrome = new ProcessBuilder("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe");
  static int opennedprograms = 0;


  public static void main(String[] args){
    Controller controller = new Controller();
    SampleListener listener = new SampleListener();
    FrameListener frame = new FrameListener();

    while(!controller.isConnected()){
      //do nothing; wait till connected
    }
    Frame frame2 = controller.frame();
    while(controller.isConnected()){
      try{
        Thread.sleep(1750);
      } catch (InterruptedException e){
        e.printStackTrace();
      }
      frame2 = controller.frame();
      processFrame(frame2);
    }

    System.out.println("Press Enter to quit...");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
  }

  static void processFrame(Frame frame ){
    if( frame.id() == lastFrameID ) return;
    checkFrame(frame);
    lastFrameID = frame.id();
  }

  static void checkFrame(Frame frame){
    HandList list = frame.hands();
    Hand hand = list.get(0);
    FingerList list2 = frame.fingers();
    Pointable finger = list2.leftmost();
    com.leapmotion.leap.Vector direction = finger.direction();
    double threshold = 0.80;

    //Determine what to execute
    if (list.count() == 1 && opennedprograms < 5){
      if(direction.getX() < ( -1 * threshold)){
        startProcess(notepad);
      } else if (direction.getY() < (-1 * threshold)){
        startProcess(music);
      } else if (direction.getZ() < (-1 * threshold)){
        startProcess(paint);
      } else if ( direction.getX() > ( threshold)){
        startProcess(chrome);
      }
    } else if (list.count() > 1 && processes.size() > 0) {
      processes.get(0).destroy();
      processes.remove(0);
    }

    //Print out log info
    System.out.println("X: " + direction.getX()
                        + ", Y: " + direction.getY()
                        + ", Z: " + direction.getZ());
  }

//Start child process
  public static void startProcess(ProcessBuilder build){
    try{
      processes.add(build.start());
    } catch (IOException e){
      e.printStackTrace();
    }
  }
}

/************Listeners (not used) **************/
class SampleListener extends Listener{
  public void onConnect(Controller controller) {
      System.out.println("Connected");
  }

  public void onFrame(Controller controller) {
      System.out.println("Frame available");
  }
}

class FrameListener extends Listener{
  int chromeopened = 0;
  Process chrome;

  public void onFrame(Controller controller){
    Frame frame = controller.frame();
    HandList list = frame.hands();

    if(list.count() == 2 && chromeopened == 0){
      try{
        chrome = Runtime.getRuntime().exec("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe", null, new File("C:\\Program Files (x86)\\Google\\Chrome\\Application"));
      } catch (IOException e){
        e.printStackTrace();
      }
      chromeopened = 1;
    } else if (list.count() == 1 && chromeopened == 1){
        chrome.destroy();
        chromeopened = 0;
    }
    System.out.println("Frame id: " + frame.id()
               + ", timestamp: " + frame.timestamp()
               + ", hands: " + frame.hands().count()
               + ", fingers: " + frame.fingers().count()
               + "chromeopened: " + chromeopened);
  }
/**************************************************/
}
