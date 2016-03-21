package dbl.tue.framework;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBProvider;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBPrivateChat;
import com.quickblox.chat.QBPrivateChatManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListener;
import com.quickblox.chat.listeners.QBPrivateChatManagerListener;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.server.BaseService;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = (EditText) findViewById(R.id.editText);
                String message = editText.getText().toString();
                sendMessage(message);
                editText.setText("");
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void sendMessage(final String message) {
        QBUser currentuser = Globals.getInstance().getCurrentuser();
        try {
            currentuser.setPassword(BaseService.getBaseService().getToken());
        } catch (BaseServiceException e) {
            e.printStackTrace();
        }
        QBUser user = Globals.getInstance().getCurrentuser();
        user.setPassword(Profile.getCurrentProfile().getId());
        QBAuth.createSession(user, new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle params) {
                Log.v("LOGGEDIN", session.toString());

                QBUsers.signInUsingSocialProvider(QBProvider.FACEBOOK, AccessToken.getCurrentAccessToken().getToken(), null, new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser user, Bundle bundle) {
                        // success
                        user.setPassword(Profile.getCurrentProfile().getId());
                        QBChatService.getInstance().login(user, new QBEntityCallback<Void>() {
                            @Override
                            public void onSuccess(Void result, Bundle bundle) {
                                final QBMessageListener<QBPrivateChat> privateChatMessageListener = new QBMessageListener<QBPrivateChat>() {
                                    @Override
                                    public void processMessage(QBPrivateChat privateChat, final QBChatMessage chatMessage) {

                                    }

                                    @Override
                                    public void processError(QBPrivateChat privateChat, QBChatException error, QBChatMessage originMessage) {

                                    }
                                };

                                QBPrivateChatManagerListener privateChatManagerListener = new QBPrivateChatManagerListener() {
                                    @Override
                                    public void chatCreated(final QBPrivateChat privateChat, final boolean createdLocally) {
                                        if (!createdLocally) {
                                            privateChat.addMessageListener(privateChatMessageListener);
                                        }
                                    }
                                };
                                QBChatService.getInstance().getPrivateChatManager().addPrivateChatManagerListener(privateChatManagerListener);

                                Integer opponentId = 45;
                                QBPrivateChatManager privateChatManager = QBChatService.getInstance().getPrivateChatManager();
                                try {
                                    QBChatMessage chatMessage = new QBChatMessage();
                                    chatMessage.setBody(message);
                                    chatMessage.setProperty("save_to_history", "1"); // Save a message to history

                                    QBPrivateChat privateChat = privateChatManager.getChat(opponentId);
                                    if (privateChat == null) {
                                        privateChat = privateChatManager.createChat(opponentId, privateChatMessageListener);
                                    }
                                    privateChat.sendMessage(chatMessage);
                                } catch (SmackException.NotConnectedException e) {

                                }
                            }

                            @Override
                            public void onError(QBResponseException errors) {
                                //chatservicelogin

                            }
                        });

                    }

                    @Override
                    public void onError(QBResponseException e) {
                        //loginwithfacebook
                    }
                });

            }

            @Override
            public void onError(QBResponseException error) {
                // errors authenticate
            }
        });
    }

}





