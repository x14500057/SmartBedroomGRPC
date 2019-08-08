/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SmartMirror;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.util.logging.Logger;
import SmartWardrobe.JmDNSRegistrationHelper;

import SmartMirror.SmartMirrorGrpc;
import SmartMirror.PowerStatus;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Paul
 */
public class SmartMirrorServer {
    
    private static final Logger logger = Logger.getLogger(SmartMirrorServer.class.getName());
    private int port = 40001;
    private Server server;
    
    private void start() throws Exception {
        server = ServerBuilder.forPort(port)
                .addService(new SmartMirrorImpl())
                .build()
                .start();
        JmDNSRegistrationHelper helper = new JmDNSRegistrationHelper("Paul's", "_smartmirror._udp.local.", "", port);
        logger.info("Server started on port: " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
        
            @Override
            public void run() {
                System.err.println(" *** shutting down gRPC server since JVM is shutting down ***");
                SmartMirrorServer.this.stop();
                System.err.println("*** server shut down ***");
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
        final SmartMirrorServer server = new SmartMirrorServer();
        server.start();
        server.blockUntilShutdown();
    }
    
    private class SmartMirrorImpl extends SmartMirrorGrpc.SmartMirrorImplBase {
        
        private boolean power = false;
        
        
        //constructors for getAppointments
        private List<Appointment> appointments;
        private List<NewsReport> newsReports;
        private String title;
        private String note;
        private String date;
        private String time;
        
        //constructors for getNewsUpdates
        private String headline;
        private String topic;
       
        
        public SmartMirrorImpl() {
            String name = "Paul";
            String serviceType = "_smartmirror._udp.local.";
            
            appointments = new ArrayList<Appointment>();
            Appointment googleM = Appointment.newBuilder().setTitle("Google CEO").setNote("Meeting about SEO Implementation Enhancements").setDate("28-04-19").setTime("10:50").build();
            Appointment amazonM = Appointment.newBuilder().setTitle("Jeff B").setNote("Meeting about multi-mill data centre usagee deal").setDate("28-04-19").setTime("12:30").build();
            Appointment appleM = Appointment.newBuilder().setTitle("Tim C").setNote("Meeting about new quadCore processing algorithmic enhancements").setDate("29-04-19").setTime("10:00").build();
            appointments.add(googleM);
            appointments.add(amazonM);
            appointments.add(appleM);
            
            
            newsReports = new ArrayList<NewsReport>();
            
            NewsReport newsReport1 = NewsReport.newBuilder().setHeadline("Facebook given Ted talk challenge").setTopic("tech").build();
            NewsReport newsReport2 = NewsReport.newBuilder().setHeadline("Tech Tent - Sri Lankas social media ban").setTopic("tech").build();
            NewsReport newsReport3 = NewsReport.newBuilder().setHeadline("Twitter boss under spotlight at TED").setTopic("tech").build();
            NewsReport newsReport4 = NewsReport.newBuilder().setHeadline("Microsoft hits $1 trillion market valuation").setTopic("tech").build();
            NewsReport newsReport5 = NewsReport.newBuilder().setHeadline("Apple recalls UK plugs over safety fears").setTopic("tech").build();
            NewsReport newsReport6 = NewsReport.newBuilder().setHeadline("US Uber drivers plan shutdown over pay").setTopic("tech").build();
            NewsReport newsReport7 = NewsReport.newBuilder().setHeadline("Criminal probe possible over Huawei leak").setTopic("tech").build();
            NewsReport newsReport8 = NewsReport.newBuilder().setHeadline("Debenhams survival plan puts about 1,200 jobs at risk").setTopic("stocks").build();
            NewsReport newsReport9 = NewsReport.newBuilder().setHeadline("Google doc listings 13,000 employees salaries goes viral").setTopic("stocks").build();
            NewsReport newsReport10 = NewsReport.newBuilder().setHeadline("Why you should consider putting some money in CD's").setTopic("stocks").build();
            NewsReport newsReport11 = NewsReport.newBuilder().setHeadline("China trade deal could spark a big rally").setTopic("stocks").build();
            
           newsReports.add(newsReport1);
           newsReports.add(newsReport2);
           newsReports.add(newsReport3);
           newsReports.add(newsReport4);
           newsReports.add(newsReport5);
           newsReports.add(newsReport6);
           newsReports.add(newsReport7);
        }
        
        @Override
        public void switchOnOff (com.google.protobuf.Empty request,
                io.grpc.stub.StreamObserver<SmartMirror.PowerStatus> responseObserver) {
            
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
            responseObserver.onCompleted();
            
        }
        
        @Override
        public void getAppointments (Day request, 
                io.grpc.stub.StreamObserver<SmartMirror.Appointment> responseObserver) {
                System.out.println(request.getDay());
                
                for (Appointment appoint : appointments) {
                    if (!appoint.getDate().equals(request.getDay())) {
                        System.out.println("Wrong Day");
                        continue;
                    }
                    title = appoint.getTitle();
                    note = appoint.getNote();
                    time = appoint.getTime();
                    date = appoint.getDate();
                    responseObserver.onNext(appoint);
                    System.out.println(appoint);
                }
               responseObserver.onCompleted();
                
                
        }
        
        @Override
        public void getNewsUpdates (Topic request, 
                io.grpc.stub.StreamObserver<SmartMirror.NewsReport> responseObserver) {
            for (NewsReport newsR : newsReports) {
                if(newsR.getTopic().equals(request.getTopic())) {
                    System.out.println("No News Available !");
                    continue;
                }
                headline = newsR.getHeadline();
//                topic = newsR.getTopic();
                responseObserver.onNext(newsR);
                System.out.println(newsR);
            }
            responseObserver.onCompleted();
        }
    }
}


//for (Appointment appointment : appointments) {
//                    if (appointment.getDate() == "28-04-19") {
//                        responseObserver.onNext(Appointment.newBuilder()
//                                .setTitle(appointment.getTitle())
//                                .setNote(appointment.getNote())
//                                .setTime(appointment.getTime())
//                                .setDate(appointment.getDate())
//                                .build());
//                        System.out.println(appointment.getNote());
//                    }
//                                                       
//                }