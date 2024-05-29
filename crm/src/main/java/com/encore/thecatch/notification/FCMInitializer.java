//package com.encore.thecatch.notification;
//
//import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//import java.io.IOException;
//
//@Component
//@Slf4j
//public class FCMInitializer {
//    private static final String FIREBASE_CONFIG_PAHT = "catch-push-firebase-adminsdk-x6w7k-bded31fa6b.json";
//
//    @PostConstruct
//    public void initialize(){
//        ClassPathResource resource = new ClassPathResource(FIREBASE_CONFIG_PAHT);
//        try{
//            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new ClassPathResource(FIREBASE_CONFIG_PAHT).getInputStream());
//            FirebaseOptions options = new FirebaseOptions.Builder()
//                    .setCredentials(googleCredentials)
//                    .build();
//            FirebaseApp.initializeApp(options);
//            System.out.println("성공");
//        }catch (IOException e){
//            log.info(">>>>>>>>FCM error");
//            log.error(">>>>>>FCM error message : " + e.getMessage());
//        }
//    }
//}
