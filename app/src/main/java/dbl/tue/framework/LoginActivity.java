package dbl.tue.framework;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBSettings;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.location.model.QBEnvironment;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBDevice;
import com.quickblox.messages.model.QBNotificationChannel;
import com.quickblox.messages.model.QBSubscription;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.io.IOException;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    static final String APP_ID = "37437";
    static final String AUTH_KEY = "5Ozb4CuDbuYWvfL";
    static final String AUTH_SECRET = "KkkSK8UF7OVUb8a";
    static final String ACCOUNT_KEY = "JZ7TyYHuzycJGrHH72iu";
    private PlayServiceHelper helper;

    private static LoginActivity instance;

    public static LoginActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText username = (EditText) findViewById(R.id.username);
        final EditText password = (EditText) findViewById(R.id.password);
        final EditText email = (EditText) findViewById(R.id.Email);
        username.setVisibility(View.INVISIBLE);
        final Button button = (Button) findViewById(R.id.login);
        instance = this;
        helper = new PlayServiceHelper(this);
        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
        QBAuth.createSession(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle params) {
                // success
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final QBUser user = new QBUser();
                        user.setPassword(password.getText().toString());

                        user.setEmail(email.getText().toString());

                        QBAuth.createSession(user, new QBEntityCallback<QBSession>() {
                            @Override
                            public void onSuccess(QBSession session, Bundle params) {
                                // success
                                QBUsers.getUserByEmail(user.getEmail(), new QBEntityCallback<QBUser>() {
                                    @Override
                                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                                        gotoMain(qbUser);
                                    }

                                    @Override
                                    public void onError(QBResponseException e) {

                                    }
                                });

                            }

                            @Override
                            public void onError(QBResponseException error) {
                                // errors
                                Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                });


                Button sign_up = (Button) findViewById(R.id.signup);
                sign_up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (username.getVisibility() == View.INVISIBLE) {
                            username.setVisibility(View.VISIBLE);
                        } else {
                            final QBUser user = new QBUser();
                            user.setEmail(email.getText().toString());
                            user.setFullName(username.getText().toString());
                            user.setPassword(password.getText().toString());


                            QBUsers.signUp(user, new QBEntityCallback<QBUser>() {
                                @Override
                                public void onSuccess(QBUser user, Bundle args) {
                                    Globals.getInstance().setCurrentuser(user);
                                    user.setPassword(password.getText().toString());
                                    Log.v("TEST", user.getPassword());
                                    //notification subscription
                                    QBAuth.createSession(user, new QBEntityCallback<QBSession>() {
                                        @Override
                                        public void onSuccess(QBSession qbSession, Bundle bundle) {
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
                                            QBUser user2 = Globals.getInstance().getCurrentuser();

                                            subscription.setId(user2.getId());

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
                                        }

                                        @Override
                                        public void onError(QBResponseException e) {

                                        }
                                    });
                                    QBUsers.getUserByEmail(user.getEmail(), new QBEntityCallback<QBUser>() {
                                        @Override
                                        public void onSuccess(QBUser qbUser, Bundle bundle) {
                                            gotoMain(qbUser);
                                        }

                                        @Override
                                        public void onError(QBResponseException e) {

                                        }
                                    });

                                }

                                @Override
                                public void onError(QBResponseException errors) {
                                    Toast.makeText(LoginActivity.this, errors.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });


            }

            @Override
            public void onError(QBResponseException error) {
                // errors
            }
        });
    }

    public void gotoMain(QBUser user) {
        Globals.getInstance().setCurrentuser(user);
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        finish();
        startActivity(intent);
    }

    // Subscribe to Push Notifications
    public void subscribeToPushNotifications(final String registrationID) {
        final LoginActivity app = this;


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
}
