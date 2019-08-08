/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SmartWardrobe;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import SmartWardrobe.Outfit;
import SmartWardrobe.WardrobeAssistantGrpc;
import SmartWardrobe.WardrobeAssistantGrpc;
import SmartWardrobe.weather;
import java.util.Collection;
import SmartWardrobe.Hanger;
import io.grpc.stub.StreamObserver;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Paul
 */
public class wardrobeServer {
    
    private static final Logger logger = Logger.getLogger(wardrobeServer.class.getName());
    
    private int port = 50051;
    private Server server;
    private  Hanger[] hanger;
    
    private void start() throws Exception {
        server = ServerBuilder.forPort(port)
                .addService(new wardrobeImpl())
                .build()
                .start();
        JmDNSRegistrationHelper helper = new JmDNSRegistrationHelper("Pauls", "_wardrobeA._udp.local.", "", port);
        logger.info("Server started, listening on: "+port);
        Runtime.getRuntime().addShutdownHook(new Thread(){
            
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                wardrobeServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }
    
    private void stop() {
        if(server != null) {
            server.shutdown();
        }
    }
    
    /**
     * Await termination on the main thread since the grpc library uses daemon
     * threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
    
    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws Exception {
        final wardrobeServer server = new wardrobeServer();
        
        server.start();
        server.blockUntilShutdown();
    }
    
    
    private class wardrobeImpl extends WardrobeAssistantGrpc.WardrobeAssistantImplBase {
        
        private boolean power = false;
        private List<CItem> clothes;         
        private List<Outfit> outfits;
        private int xDestinationCoor;
        private int yDestinationCoor;
        private int xCurrCoor;
        private int yCurrCoor;
        private boolean itemGrabbed = false;
        
        
        public wardrobeImpl() {
            
            clothes = new ArrayList<CItem>();
            CItem item1 = CItem.newBuilder().setType("Jacket").setBrand("Nike").setColor("Black").setSize("M").build();
            CItem item2 = CItem.newBuilder().setType("Jacket").setBrand("Puma").setColor("Black").setSize("M").build();
            CItem item3 = CItem.newBuilder().setType("Jacket").setBrand("Adidas").setColor("Blue").setSize("S").build();
            CItem item4 = CItem.newBuilder().setType("Jumper").setBrand("JacknJones").setColor("Blue").setSize("L").build();
            CItem item5 = CItem.newBuilder().setType("Jumper").setBrand("SuperDry").setColor("White").setSize("M").build();
            
            CItem item6 = CItem.newBuilder().setType("T-Shirt").setBrand("Nike").setColor("Blue").setSize("M").build();
            CItem item7 = CItem.newBuilder().setType("T-Shirt").setBrand("Couture").setColor("Green").setSize("M").build();
            CItem item8 = CItem.newBuilder().setType("Shirt").setBrand("Abercrombe").setColor("Green").setSize("M").build();
            CItem item9 = CItem.newBuilder().setType("Shirt").setBrand("TommyHilfiger").setColor("").setSize("L").build();
            
            CItem item10 = CItem.newBuilder().setType("TrackBottoms").setBrand("Nike").setColor("Navy").setSize("S").build();
            CItem item11 = CItem.newBuilder().setType("TrackBottoms").setBrand("Adidas").setColor("Grey").setSize("L").build();
            CItem item12 = CItem.newBuilder().setType("Jeans").setBrand("Abercrombe").setColor("Navy").setSize("M").build();
            CItem item13 = CItem.newBuilder().setType("Shoes").setBrand("Nike").setColor("Navy").setSize("10").build();
            
            CItem item14 = CItem.newBuilder().setType("Shoes").setBrand("Nike").setColor("White").setSize("9").build();
            CItem item15 = CItem.newBuilder().setType("Shoes").setBrand("Adidas").setColor("White").setSize("10").build();
            CItem item16 = CItem.newBuilder().setType("Shoes").setBrand("Timberland").setColor("Brown").setSize("11").build();
            
            clothes.add(item1);            
            clothes.add(item2);            
            clothes.add(item3);            
            clothes.add(item4);            
            clothes.add(item5);            
            clothes.add(item6);            
            clothes.add(item7);            
            clothes.add(item8);            
            clothes.add(item9);            
            clothes.add(item10);            
            clothes.add(item11);            
            clothes.add(item12);            
            clothes.add(item13);            
            clothes.add(item14);            
            clothes.add(item15);            
            clothes.add(item16);     
            
//                    Wardrobe Coordinates
//            -------------------------------------
//            |0,0|1,0|2,0|3,0|4,0|5,0|6,0|7,0|8,0|    Jackets/Jumpers
//            -------------------------------------
//            |0,1|1,1|2,1|3,1|4,1|5,1|6,1|7,1|8,1|    Shirts/T-shirts
//            -------------------------------------
//            |0,2|1,2|2,2|3,2|4,2|5,2|6,2|7,2|8,2|    Shoes
//            -------------------------------------
            
            
            
            hanger = new Hanger[24];
            
            //////////////////////////////////////////
            //        Jackets || Jumpers            //
            //////////////////////////////////////////
            hanger[0] = new Hanger(0,0,1,null);
            hanger[1] = new Hanger(1,0,1,null);
            hanger[2] = new Hanger(2,0,1,null);
            hanger[3] = new Hanger(3,0,1,null);
            hanger[4] = new Hanger(4,0,1,null);
            hanger[5] = new Hanger(5,0,1,null);
            hanger[6] = new Hanger(6,0,1,null);
            hanger[7] = new Hanger(7,0,1,null);
            
            //////////////////////////////////////////
            //        Shirts || T-Shirts            //
            //////////////////////////////////////////
            hanger[8] = new Hanger(0,1,1,null);
            hanger[9] = new Hanger(1,1,1,null);
            hanger[10] = new Hanger(2,1,1,null);
            hanger[11] = new Hanger(3,1,1,null);
            hanger[12] = new Hanger(4,1,1,null);
            hanger[13] = new Hanger(5,1,1,null);
            hanger[14] = new Hanger(6,1,1,null);
            hanger[15] = new Hanger(7,1,1,null);
            
            //////////////////////////////////////////
            //               Shoes                  //
            //////////////////////////////////////////
            hanger[16] = new Hanger(0,2,1,null);
            hanger[17] = new Hanger(1,2,1,null);
            hanger[18] = new Hanger(2,2,1,null);
            hanger[19] = new Hanger(3,2,1,null);
            hanger[20] = new Hanger(4,2,1,null);
            hanger[21] = new Hanger(5,2,1,null);
            hanger[22] = new Hanger(6,2,1,null);
            hanger[23] = new Hanger(7,2,1,null);
            
            System.out.println("Initializing Wardrobe Hanger Coorindinates . . . . .");
            for (int i=0; i < hanger.length; i++){
                if(hanger[i].yCoor == 0) {
                    System.out.println(i+"  "+hanger[i].xCoor + "||"+hanger[i].yCoor +"||" + hanger[i].zCoor + "    Jackets or Jumpers compartment");
                }
                else if (hanger[i].yCoor == 1){
                    System.out.println(i+"  "+hanger[i].xCoor + "||"+hanger[i].yCoor +"||" + hanger[i].zCoor + "    Shirts or T-Shirts compartment");
                }
                else if (hanger[i].yCoor == 2){
                    System.out.println(i+"  "+hanger[i].xCoor + "||"+hanger[i].yCoor +"||" + hanger[i].zCoor + "     Shoes compartment");
                }
            }
            
            System.out.println("-------------------------------------------------\n Now Allocating Clothing Items to free catagorical Hangers\n-------------------------------------------------");
            
            for (CItem cItem : clothes) {
                addCItem (cItem.getType(), cItem.getBrand(), cItem.getColor(), cItem.getSize());
            }

       
    
            System.out.println("-------------------------------------------------\n Now Displaying All Stored Items . . .\n-------------------------------------------------");
              
            for (int i=0; i< hanger.length; i++){
                if(hanger[i].cItem != null) {
                    System.out.println("Hanger No: "+i+" "+hanger[i].cItem);
                }
                else {
                    System.out.println("** Hanger No: "+i+ "is Free **");
                }
            }
            
            System.out.println("-------------------------------------------------\n Adding Extra Items via addCItem() method . . .\n-------------------------------------------------");

            System.out.println(findItem (1,1));
            System.out.println("\n-----------------------\n\n");
            
            CItem cI = findItem (1,5);
            if (cI == null) {
                System.out.println("empty");
            }
            else {
                System.out.println(cI);
            }
//            addCItem("jacket","penny's","white","m");
//            addCItem("jacket","penny's","white","m");
//            addCItem("jacket","penny's","white","m");
//            addCItem("jacket","penny's","white","m");
//              
//            addCItem("shirt","nike","white","m");
//            addCItem("shirt","nike","white","m");
//            addCItem("shirt","nike","white","m");
//            addCItem("shirt","nike","white","m");
//            addCItem("shirt","nike","white","m");
//              
//            addCItem("shoes","adidas","blue","10");
//            addCItem("shoes","adidas","blue","10");
//            addCItem("shoes","adidas","blue","10");
//            addCItem("shoes","adidas","blue","10");
//            addCItem("shoes","adidas","blue","10");
//            addCItem("shoes","adidas","blue","10");
              
        }
        
        
        @Override
        public void powerOnOff (com.google.protobuf.Empty request,
                io.grpc.stub.StreamObserver<SmartWardrobe.PowerStatus> responseObserver) {
            System.out.println("wokring");
            if(power != true){
                power = true;
                responseObserver.onNext(PowerStatus.newBuilder().setStatus(power).build());
            }
            else if(power = true) {
                    power = false;
                    responseObserver.onNext(PowerStatus.newBuilder().setStatus(power).build());
                }
            else {
                responseObserver.onNext(PowerStatus.newBuilder().setStatus(power).build());
            }
            System.out.println(power);
            responseObserver.onCompleted();
            
        }
        
        @Override
        public void getItemName (ItemCoor request, io.grpc.stub.StreamObserver<SmartWardrobe.Item> responseObserver ) {
            int itemX = request.getItemCoorX();
            int itemY = request.getItemCoorY();
            
            System.out.println(itemX);
            System.out.println(itemY);
            
            CItem cI = findItem (itemX,itemY);
            if (cI == null ) {
                System.out.println("empty");
                responseObserver.onNext(SmartWardrobe.Item.newBuilder()
                        .setType("f").setBrand("f").setColor("f").setSize("f").build());
                responseObserver.onCompleted();
            }
            else {
                System.out.println(cI);
                responseObserver.onNext(SmartWardrobe.Item.newBuilder()
                        .setType(cI.getType()).setBrand(cI.getBrand()).setColor(cI.getColor()).setSize(cI.getSize()).build());
                responseObserver.onCompleted();
            }
        }
        
        public CItem findItem (int x , int y) {
            for (Hanger hanger : hanger) {
                if (hanger.xCoor == x && hanger.yCoor == y && hanger.cItem != null) {
                    return hanger.getcItem();
                }
                
            }
            return null;
        }
                    
        
        
        @Override
        public void getItem (ItemCoor request, io.grpc.stub.StreamObserver<SmartWardrobe.RobotMovemts> responseObserver ) {
            xDestinationCoor = request.getItemCoorX();
            yDestinationCoor = request.getItemCoorY();
            Timer t = new Timer();
            t.schedule(new RemindTask(responseObserver), 0, 700);
            

        }
        @Override
        public void recommendOutfit (weather request, io.grpc.stub.StreamObserver<SmartWardrobe.Outfit> responseObserver) {
            int temp = request.getTemp();
            
            if(temp > 15){
               for (Outfit outfit : outfits){
                   if(outfit.getUpperBody().equals("none")){
                   responseObserver.onNext(SmartWardrobe.Outfit.newBuilder()
                           .setUpperBody(outfit.getUpperBody())
                           .setLowerBody(outfit.getLowerBody())
                           .setFootwear(outfit.getFootwear()).build());
                    }
                }
            }
            else{
                for (Outfit outfit : outfits){
                   if(outfit.getUpperBody().equals("Jacket")){
                   responseObserver.onNext(SmartWardrobe.Outfit.newBuilder()
                           .setUpperBody(outfit.getUpperBody())
                           .setLowerBody(outfit.getLowerBody())
                           .setFootwear(outfit.getFootwear()).build());
                    
                    }
                }
            }
            
            responseObserver.onCompleted();
        }
    
    
    public void addCItem (String type, String brand, String color, String size) {
        CItem item = CItem.newBuilder().setType(type).setBrand(brand).setColor(color).setSize(size).build();
        if (item.getType().equalsIgnoreCase("jacket") || item.getType().equalsIgnoreCase("jumper")){
            for (int i=0; i<hanger.length; i++){
                if (hanger[i].yCoor == 0 && hanger[i].cItem == null){
                    hanger[i].cItem = item;
                    System.out.println(item +"Added to Hanger @ (" +hanger[i].xCoor +", "+hanger[i].yCoor +")\n");
                    break;
                }
                else {
                    if(i == hanger.length- 17  && hanger[i].cItem != null) {
                        System.out.println("Sorry, No more room for your " +item.getBrand() +": "+ item.getType());
                    }                            
                    else {
                        continue;
                    }
                }
            }
        }
        else if (item.getType().equalsIgnoreCase("shirt") || item.getType().equalsIgnoreCase("t-shirt")){
            for (int i=0; i<hanger.length; i++){
                if (hanger[i].yCoor == 1 && hanger[i].cItem == null){
                    hanger[i].cItem = item;
                    System.out.println(item +"Added to Hanger @ (" +hanger[i].xCoor +", "+hanger[i].yCoor +")\n");
                    break;
                }
                else {
                    if(i == hanger.length- 9  && hanger[i].cItem != null) {
                        System.out.println("Sorry, No more room for your " +item.getBrand() +": "+ item.getType());
                    }                            
                    else {
                        continue;
                    }
                }
            }
        }
        else if (item.getType().equalsIgnoreCase("shoes")){
            for (int i=0; i<hanger.length; i++){
                if (hanger[i].yCoor == 2 && hanger[i].cItem == null){
                    hanger[i].cItem = item;
                    System.out.println(item +"Added to Hanger @ (" +hanger[i].xCoor +", "+hanger[i].yCoor +")\n");
                    break;
                }
                else {
                    if(i == hanger.length-1  && hanger[i].cItem != null) {
                        System.out.println("Sorry, No more room for your " +item.getBrand() +": "+ item.getType());
                    }                            
                    else {
                        continue;
                    }
                }
            }
        }
    } 
    
   
    class RemindTask extends TimerTask {

            StreamObserver<RobotMovemts> o;

            public RemindTask(StreamObserver<RobotMovemts> j) {
                o = j;
            }

            @Override
            public void run() {
                
                boolean complete = false;
                if(itemGrabbed == false && complete !=true) {
                    if (xCurrCoor < xDestinationCoor) {
                        xCurrCoor += 1;
                        RobotMovemts robotMove = RobotMovemts.newBuilder().setCurCoorX(xCurrCoor).setCurCoorY(0).setComplete(false).build();
                        o.onNext(robotMove);
                    } 
                    else if ( xCurrCoor == xDestinationCoor && yCurrCoor < yDestinationCoor && itemGrabbed == false) {

                        yCurrCoor += 1;
                        RobotMovemts robotMove = RobotMovemts.newBuilder().setCurCoorX(xCurrCoor).setCurCoorY(yCurrCoor).setComplete(false).build();
                        o.onNext(robotMove);

                        if (xCurrCoor == xDestinationCoor && yCurrCoor == yDestinationCoor && itemGrabbed == false) {
                            itemGrabbed = true;
                            System.out.println("Item Grabbed");
                            
                        }
                    }
                    
                }
                else if (itemGrabbed == true && complete != true) {
                    if (yCurrCoor >= 1) {
                        yCurrCoor -= 1;
                        System.out.println("robotArm @: (" + xCurrCoor + "," + yCurrCoor +")");   
                        RobotMovemts robotMove = RobotMovemts.newBuilder().setCurCoorX(xCurrCoor).setCurCoorY(yCurrCoor).setComplete(false).build();
                        o.onNext(robotMove);
                    }
                    else if (yCurrCoor == 0 && xCurrCoor >= 1) {
                        xCurrCoor -= 1;
                        System.out.println("robotArm @: (" + xCurrCoor + "," + yCurrCoor +")");
                        if (xCurrCoor == 0) {
                            RobotMovemts robotMove = RobotMovemts.newBuilder().setCurCoorX(xCurrCoor).setCurCoorY(yCurrCoor).setComplete(true).build();
                            o.onNext(robotMove);
                            complete = true;
                        }
                        else {
                            RobotMovemts robotMove = RobotMovemts.newBuilder().setCurCoorX(xCurrCoor).setCurCoorY(yCurrCoor).setComplete(false).build();
                            o.onNext(robotMove);
                        }
                        System.out.println("loading...");
                    }
                
                }                
                o.onCompleted();
                System.out.println("x: " + xCurrCoor +"\ny: " + yCurrCoor);
                System.out.println("completed 2");
                this.cancel();
            }
            
        }
    }
}

    
    
    
    

