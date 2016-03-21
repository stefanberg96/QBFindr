package dbl.tue.framework;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBProvider;
import com.quickblox.auth.model.QBSession;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.location.model.QBEnvironment;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBDevice;
import com.quickblox.messages.model.QBNotificationChannel;
import com.quickblox.messages.model.QBSubscription;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.facebook.FacebookSdk;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    static final String APP_ID = "37437";
    static final String AUTH_KEY = "5Ozb4CuDbuYWvfL";
    static final String AUTH_SECRET = "KkkSK8UF7OVUb8a";
    static final String ACCOUNT_KEY = "JZ7TyYHuzycJGrHH72iu";
    private PlayServiceHelper helper;
    CallbackManager callbackManager;
    LoginResult loginResult;
    private static LoginActivity instance;

    public static LoginActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        instance = this;
        helper = new PlayServiceHelper(this);
        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);


        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");

        // Other app specific specialization

        // Callback registration
        //has been logged in
        if (AccessToken.getCurrentAccessToken() != null) {
            loginqb(AccessToken.getCurrentAccessToken());
        } else {
            //has never logged in


            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d("FaceBook:", "succes");
                    signup();
                    // signUpUser(Profile.getCurrentProfile());
                    loginqb(AccessToken.getCurrentAccessToken());
                }


                @Override
                public void onCancel() {
                    // App code
                    Log.d("FaceBook:", "cancelled");
                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                    Log.d("FaceBook:", exception.toString());
                }
            });
        }
    }


    public void loginqb(final AccessToken loginResult) {
        Toast.makeText(getApplicationContext(), "Please wait, Logging in", Toast.LENGTH_LONG).show();
        QBAuth.createSession(new QBEntityCallback<QBSession>() {

            @Override
            public void onSuccess(QBSession session, Bundle params) {
                // You have successfully created the session
                //
                // Now you can use QuickBlox API!
                Log.v("QB:", "Sessionstarted");
                String accesstoken = loginResult.getToken();
                QBUsers.signInUsingSocialProvider(QBProvider.FACEBOOK, accesstoken, null, new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser user, Bundle args) {
                        gotoMain(user);
                    }

                    @Override
                    public void onError(QBResponseException errors) {

                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    public void signup() {
        QBAuth.createSession(new QBEntityCallback<QBSession>() {

            @Override
            public void onSuccess(QBSession session, Bundle bundle) {
                final QBUser user = new QBUser();
                Profile profile = Profile.getCurrentProfile();
                user.setFacebookId(profile.getId());
                user.setLogin(profile.getId());
                user.setFullName(profile.getName());
                user.setPassword(profile.getId());
                QBUsers.signUp(user, new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        loginqb(AccessToken.getCurrentAccessToken());
                        Log.v("QB:", "Succesfull login qb");
                        QBSubscription subscription = new QBSubscription(QBNotificationChannel.GCM);
                        subscription.setEnvironment(com.quickblox.messages.model.QBEnvironment.PRODUCTION);
                        String id = helper.getId();
                        //
                        String deviceId;
                        final TelephonyManager mTelephony = (TelephonyManager) instance.getSystemService(
                                Context.TELEPHONY_SERVICE);
                        if (mTelephony.getDeviceId() != null) {
                            deviceId = mTelephony.getDeviceId(); //*** use for mobiles
                        } else {
                            deviceId = Settings.Secure.getString(instance.getContentResolver(),
                                    Settings.Secure.ANDROID_ID); //*** use for tablets
                        }
                        subscription.setDeviceUdid(deviceId);
                        //
                        subscription.setRegistrationID(id);
                        subscription.setDevice(new QBDevice(getApplicationContext()));
                        subscription.setId(user.getId());

                        Log.v("Sub", subscription.toString());
                        //

                        QBPushNotifications.createSubscription(subscription, new QBEntityCallback<ArrayList<QBSubscription>>() {

                            @Override
                            public void onSuccess(ArrayList<QBSubscription> subscriptions, Bundle args) {
                                Log.v("Sub", "Woho");
                                System.out.println("YEEEY");
                            }

                            @Override
                            public void onError(QBResponseException error) {
                                Log.v("Sub", ":(");
                                Log.v("Error", error.toString() + "hoi");
                            }
                        });
                        Globals.getInstance().setCurrentuser(user);
                        gotoMain(user);
                    }

                    @Override
                    public void onError(QBResponseException errors) {

                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

    }


    public void gotoMain(QBUser user) {
        Globals.getInstance().setCurrentuser(user);
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        finish();
        startActivity(intent);
    }


    public int getAppVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


//    public void signUpUser(Profile newProfile) {
//
//        QBUser qbUser = new QBUser();
//        qbUser.setFullName(newProfile.getName());
//        qbUser.setLogin(newProfile.getId());
//        qbUser.setPassword(newProfile.getId());
//        qbUser.setFacebookId(newProfile.getId());
//
//
//        QBUsers.signUp(qbUser, new QBEntityCallback<QBUser>() {
//            @Override
//            public void onSuccess(QBUser qbUser, Bundle bundle) {
//                loginqb(AccessToken.getCurrentAccessToken());
//            }
//
//            @Override
//            public void onError(QBResponseException e) {
//
//            }
//        });
//    }
}
