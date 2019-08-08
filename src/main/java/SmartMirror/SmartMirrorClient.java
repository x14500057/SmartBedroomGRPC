/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SmartMirror;

import SmartMirror.MirrorGUI;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import SmartWardrobe.ServiceDescription;
import SmartWardrobe.ServiceObserver;
import SmartWardrobe.jmDNSServiceTracker;




/**
 *
 * @author Paul
 */
public class SmartMirrorClient implements ServiceObserver{
    
    protected MirrorGUI ui;
    private static final Logger logger = Logger.getLogger(SmartMirrorClient.class.getName());

    private ManagedChannel channel;
    private SmartMirrorGrpc.SmartMirrorBlockingStub blockingStub;
    private final String interestedService;
    private final String serviceType;
    private final String name;
    
    /**
     * Construct client connecting to HelloWorld server at {@code host:port}.
     */
    public SmartMirrorClient() {
        interestedService = "_smartmirror._udp.local.";
        serviceType = "_smartmirror._udp.local.";
        name = "Pauls";

        jmDNSServiceTracker clientManager = jmDNSServiceTracker.getInstance();
        clientManager.register(this);
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ui = new MirrorGUI(SmartMirrorClient.this);
                ui.setVisible(true);
            }
        });
    }
    
    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
    
    public void switchOnOff() {
        try {
            Empty request = Empty.newBuilder().build();
            System.out.println("I am about to try to call the get switch the power on smart mirror . . .");
            PowerStatus status = blockingStub.switchOnOff(request);
            ui.changePwrColor(status.getStatus());
            System.out.println("Power Status: " +status.getStatus());
            
        
            
        } catch (RuntimeException e) {
            logger.log(Level.WARNING, "RPC failed", e);
            return;
        }
    }
    
    public void getAppointments() {
        
            
            Day request = Day.newBuilder().setDay("28-04-19").build();
            Iterator<Appointment> appointments;
            
            try {
                appointments = blockingStub.getAppointments(request);
            for (int i = 1; appointments.hasNext(); i++) {
                Appointment appointment = appointments.next();
                
                System.out.println("Result #" + i + ": " + (appointment).toString());
                ui.append("Title: "+appointment.getTitle()+"\nNote: "+appointment.getNote());
                
            }
        } catch (RuntimeException e) {
            logger.log(Level.WARNING, "RPC failed", e);
            return;
        }
    }
    
    public void getNewsUpdates() {
        
        Topic request = Topic.newBuilder().setTopic("tech").build();
        Iterator<NewsReport> newsReports;
        
        try {
            ui.clear();
            newsReports = blockingStub.getNewsUpdates(request);
            for (int i = 1; newsReports.hasNext(); i++) {
                NewsReport newsR = newsReports.next();
                
                System.out.println(i +": "+newsR);
                
                ui.append(i +": "+newsR );
            }
        } catch(RuntimeException e) {
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
        return list;
    }
    
    public void serviceAdded(ServiceDescription service) {

        channel = ManagedChannelBuilder.forAddress(service.getAddress(), service.getPort())
                .usePlaintext(true)
                .build();
        blockingStub = SmartMirrorGrpc.newBlockingStub(channel);
        System.out.println("I got the information about the service, now i can call the service");
        switchOnOff();
//        getAppointments();
    }
    
    public String getName() {
        return "Movie Client";
    }

    public void switchService(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static void main(String[] args) throws Exception {
        SmartMirrorClient client = new SmartMirrorClient();
        
//        try {
//            client.getRecommendations();
//            client.getAllMovies();
//        } finally {
//            client.shutdown();
//        }
    }

    public void serviceAdded1(ServiceDescription service) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
