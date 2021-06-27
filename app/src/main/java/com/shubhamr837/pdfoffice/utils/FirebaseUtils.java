//package com.shubhamr837.pdfoffice.utils;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.StringRes;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.auth.GetTokenResult;
//
//public class FirebaseUtils {
//    private static FirebaseUser user;
//    private static String idToken;
//    private static boolean result;
//
//
//    public static String getToken()
//    {
//
//        user = FirebaseAuth.getInstance().getCurrentUser();
//
//        user.getIdToken(true)
//                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
//                    public void onComplete(@NonNull Task<GetTokenResult> task) {
//                        if (task.isSuccessful()) {
//                            idToken = task.getResult().getToken();
//                            result = true;// Send token to your backend via HTTPS
//                            // ...
//                        } else {
//                            result = false;
//                            // Handle error -> task.getException();
//                        }
//                    }
//                });
//
//        if(result==true){
//            return idToken;
//        }
//
//        return null;
//    }
//
//}
