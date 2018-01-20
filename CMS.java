import com.leapmotion.leap.*;
import java.io.*;

public class CMS{

  public static void main(String[] args){
    Controller controller = new Controller();
    SampleListener listener = new SampleListener();
    FrameListener frame = new FrameListener();

    controller.addListener(listener);
    controller.addListener(frame);
    while(!controller.isConnected()){
      Frame frame2 = controller.frame();
    }
    System.out.println("Press Enter to quit...");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Remove the sample listener when done
        controller.removeListener(listener);
        controller.removeListener(frame);
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
