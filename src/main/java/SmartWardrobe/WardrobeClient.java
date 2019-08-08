/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SmartWardrobe;

import SmartWardrobe.WardrobeGUI;
import SmartWardrobe.ServiceDescription;
import SmartWardrobe.ServiceObserver;
import SmartWardrobe.jmDNSServiceTracker;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import SmartWardrobe.Outfit;
import SmartWardrobe.WardrobeAssistantGrpc;
import SmartWardrobe.weather;
import java.util.Iterator;
import javax.swing.JOptionPane;


/**
 *
 * @author Paul
 */
public class WardrobeClient implements ServiceObserver {
    
    protected WardrobeGUI ui;
    private int destX;
    private int destY;
    private static final Logger logger = Logger.getLogger(WardrobeClient.class.getName());

    private ManagedChannel channel;
    private WardrobeAssistantGrpc.WardrobeAssistantBlockingStub blockingStub;
    private final String interestedService;
    private final String interestedService2;

    
    /**
     * Construct client connecting to HelloWorld server at {@code host:port}.
     */
    public WardrobeClient() {
        interestedService = "_wardrobeA._udp.local.";
        interestedService2 = "_wardrobeB._udp.local.";

        jmDNSServiceTracker clientManager = jmDNSServiceTracker.getInstance();
        clientManager.register(this);
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ui = new WardrobeGUI(WardrobeClient.this);
                ui.setVisible(true);
            }
        });
        
        
    }
    
    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
    
    public void powerOnOff() {
        try {
            Empty request = Empty.newBuilder().build();
            System.out.println("I am about to try to call the get switch the power on smart mirror . . .");
            PowerStatus status = blockingStub.powerOnOff(request);
            ui.changePwrColor(status.getStatus());
            if (status.getStatus() == false) {
                ui.changeStatusMsg("Unavailable");
                
            }
            System.out.println("Power Status: " +status.getStatus());
            
        
            
        } catch (RuntimeException e) {
            logger.log(Level.WARNING, "RPC failed", e);
            return;
        }
    }
    
    public void getItem( int x, int y) {
        
            final int destX = x;
            final int destY = y;
            
            System.out.println("I Am about To Call getItem method");
            
            try {
                
               ItemCoor itemCoords = ItemCoor.newBuilder().setItemCoorX(destX).setItemCoorY(destY).build();
            final Item item = blockingStub.getItemName(itemCoords);
                        if (item.getType() == "f") {
                            System.out.println("Hanger Empty");
                        }
                        else {
                        
                        
                new Thread() {
                    public void run() {
              
                        
                        ItemCoor itemCoors = ItemCoor.newBuilder().setItemCoorX(destX).setItemCoorY(destY).build();
                        Iterator<RobotMovemts> response = blockingStub.getItem(itemCoors);
                        ui.changeStatusMsg("In Use . . .");
                       while (response.hasNext()) {
                            RobotMovemts currMove = response.next();
                            int xCoor = currMove.getCurCoorX(); //0,1,2,3,4  ,3,2,1,0
                            int yCoor = currMove.getCurCoorY(); //0,1,2   ,1,0
                            
                            System.out.println("Robot @:" + xCoor +"," + yCoor);
                            ui.moveArmX(xCoor);
                            ui.moveArmY(yCoor);
                            System.out.println(currMove.getComplete());
                            if (xCoor == destX && yCoor == destY) {
                                ui.grabItem();
                                
                            }
                            
                            if (currMove.getComplete() == true) {
                                JOptionPane.showMessageDialog(null,item.toString() +"Your Item Is ready for you !");
                            }
                            
                       }
                       
                }
                }.start();
            } } catch (RuntimeException e) {
            logger.log(Level.WARNING, "RPC failed", e);
            return;
        }
    }
    

    public void recommendOutfit() {
        
            try {
                weather weathr = weather.newBuilder().setTemp(15).setRain(1).build();
                Outfit outfit = blockingStub.recommendOutfit(weathr);
                

                System.out.println("Recommended Outfit: " + outfit);
            } catch (RuntimeException e ){
                logger.log(Level.WARNING, "RPC failed", e);
                return;
            }
        
    }
    
    public boolean interested(String type) {
        return interestedService.equals(type);
    }
    
    public List<String> serviceInterests() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(interestedService);
        list.add(interestedService2);
        return list;
    }
    
    public void serviceAdded(ServiceDescription service) {
        
        
        channel = ManagedChannelBuilder.forAddress(service.getAddress(), service.getPort())
                .usePlaintext(true)
                .build();
        blockingStub = WardrobeAssistantGrpc.newBlockingStub(channel);
        
        System.out.println("I got the information about the service, now i can call the service");
       
           
//        powerOnOff();
//        recommendOutfit();
        
    }
//    public void serviceAdded1(ServiceDescription service) {
//        
//        
//        channel = ManagedChannelBuilder.forAddress(service.getAddress(), service.getPort())
//                .usePlaintext(true)
//                .build();
//        blockingStub = WardrobeAssistantGrpc.newBlockingStub(channel);
//        
//        System.out.println("I got the information about the service, now i can call the service");
////        recommendOutfit();
//        powerOnOff();
//    }
    
    public void switchService(String name) {
//        channel = ManagedChannelBuilder.forAddress(address, port)
//                .usePlaintext(true)
//                .build();
//    blockingStub = WardrobeAssistantGrpc.newBlockingStub(channel);
//    System.out.println("2222 ---- I got the information about the service, now i can call the service");
//        recommendOutfit();
    }
    
    public String getName() {
        return "Wardrobe Client";
    }
    
    public static void main(String[] args) throws Exception {
        WardrobeClient client = new WardrobeClient();
        
//        try {
//            client.recommendOutfit();
//        } finally {
//            client.shutdown();
//        }
    }

    
}
    
    
      

