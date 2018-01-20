import com.leapmotion.leap.*;
import java.io.*;
import java.util.*;

public class CMS{
  static long lastFrameID = 0;
  static ArrayList<Process> processes = new ArrayList<Process>();

  public static void main(String[] args){
    Controller controller = new Controller();
    SampleListener listener = new SampleListener();
    FrameListener frame = new FrameListener();

    //controller.addListener(listener);
    //controller.addListener(frame);
    while(!controller.isConnected()){
      //do nothing
    }
    Frame frame2 = controller.frame();
    while(controller.isConnected()){
      try{
        Thread.sleep(500);
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
        // Remove the sample listener when done
        //controller.removeListener(listener);
        //controller.removeListener(frame);
  }

  static void processFrame(Frame frame ){
    if( frame.id() == lastFrameID ) return;
    checkFrame(frame);
    lastFrameID = frame.id();
  }

  static void checkFrame(Frame frame){
    HandList list = frame.hands();

    if(list.count() == 2 && processes.size() < 5){
      try{
        processes.add(new ProcessBuilder("C:\\Windows\\notepad.exe").start());
        //Runtime.getRuntime().exec("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe", null, new File("C:\\Program Files (x86)\\Google\\Chrome\\Application"));
      } catch (IOException e){
        e.printStackTrace();
      }
    } else if (list.count() == 1 && processes.size() > 0){
        System.out.println("Number of processes: " + processes.size());
        processes.get(0).destroy();
        processes.remove(0);
    }
    System.out.println("Frame id: " + frame.id()
               + ", timestamp: " + frame.timestamp()
               + ", hands: " + frame.hands().count()
               + ", fingers: " + frame.fingers().count());
  }
}


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
}
