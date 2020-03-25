package wisepaas.datahub.edgesdksample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.graphics.Color;

import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import wisepaas.datahub.java.sdk.EdgeAgent;
import wisepaas.datahub.java.sdk.common.Const;
import wisepaas.datahub.java.sdk.common.EdgeAgentListener;
import wisepaas.datahub.java.sdk.common.Enum.ConnectType;

import wisepaas.datahub.java.sdk.model.edge.ConfigAck;
import wisepaas.datahub.java.sdk.model.edge.EdgeAgentOptions;
import wisepaas.datahub.java.sdk.model.edge.TimeSyncCommand;
import wisepaas.datahub.java.sdk.model.edge.WriteValueCommand;
import wisepaas.datahub.java.sdk.model.event.DisconnectedEventArgs;
import wisepaas.datahub.java.sdk.model.event.EdgeAgentConnectedEventArgs;
import wisepaas.datahub.java.sdk.model.event.MessageReceivedEventArgs;

import static wisepaas.datahub.edgesdksample.EdgeActions.doConnect;
import static wisepaas.datahub.edgesdksample.EdgeActions.doDeleteAllCfg;
import static wisepaas.datahub.edgesdksample.EdgeActions.doDeleteDevices;
import static wisepaas.datahub.edgesdksample.EdgeActions.doDeleteTags;
import static wisepaas.datahub.edgesdksample.EdgeActions.doDisconnect;
import static wisepaas.datahub.edgesdksample.EdgeActions.doSendDataLoop;
import static wisepaas.datahub.edgesdksample.EdgeActions.doUpdateCfg;
import static wisepaas.datahub.edgesdksample.EdgeActions.doUpdateDeviceStatus;
import static wisepaas.datahub.edgesdksample.EdgeActions.doUploadCfg;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // workaround way, because it is only a demo app
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final Button connected = findViewById(R.id.connected);
        final Button connectBtn = findViewById(R.id.connectBtn);
        final Button disconnectBtn = findViewById(R.id.disconnectBtn);
        final Button uploadCfgBtn = findViewById(R.id.uploadCfgBtn);
        final Button updateCfgBtn = findViewById(R.id.updateCfgBtn);
        final Button sendDataBtn = findViewById(R.id.sendDataBtn);
        final Button deleteAllCfgBtn = findViewById(R.id.deleteAllcfgBtn);
        final Button deleteDevicesBtn = findViewById(R.id.deleteDeviceBtn);
        final Button deleteTagsBtn = findViewById(R.id.deleteTagsBtn);
        final Button updateDeviceStatusBtn = findViewById(R.id.updateDeviceStatusBtn);

        final CheckBox useSecureBox = findViewById(R.id.useSecure);

        final EditText nodeIdInput = findViewById(R.id.nodeIdInput);
        final EditText dccsKeyInput = findViewById(R.id.dccsKeyInput);
        final EditText dccsUrlInput = findViewById(R.id.dccsUrlInput);


        EdgeAgentListener agentListener = new EdgeAgentListener() {
            @Override
            public void Connected(EdgeAgent agent, EdgeAgentConnectedEventArgs args) {
                System.out.println("Connected");
                connected.setBackgroundColor(Color.GREEN);
                connected.setText(R.string.connected_status);
            }

            @Override
            public void Disconnected(EdgeAgent agent, DisconnectedEventArgs args) {
                System.out.println("Disconnected");
                connected.setBackgroundColor(Color.parseColor("#373C3F41"));
                connected.setText(R.string.disconnected_status);
            }

            @Override
            public void MessageReceived(EdgeAgent agent, MessageReceivedEventArgs e) {
                System.out.println("MessageReceived");
                switch (e.Type) {
                    case Const.MessageType.WriteValue:
                        WriteValueCommand wvcMsg = (WriteValueCommand) e.Message;
                        for (WriteValueCommand.Device device : wvcMsg.DeviceList) {
                            System.out.println("DeviceId: " + device.Id);
                            for (WriteValueCommand.Tag tag : device.TagList) {
                                System.out.printf("TagName: %s, Value: %s\n", tag.Name, tag.Value.toString());
                            }
                        }
                        break;
                    case Const.MessageType.TimeSync:
                        TimeSyncCommand tscMsg = (TimeSyncCommand) e.Message;
                        System.out.println("UTC Time: " + tscMsg.UTCTime.toString());
                        break;
                    case Const.MessageType.ConfigAck:
                        ConfigAck cfgAckMsg = (ConfigAck) e.Message;
                        String result = cfgAckMsg.Result.toString();
                        new EdgeHelpers.showConfigResultTask(result, MainActivity.this).execute((Void[]) null);
                        break;
                }
            }
        };

        EdgeAgentOptions options = new EdgeAgentOptions();
        options.AndroidPackageName = getPackageName();
        final EdgeAgent _edgeAgent = new EdgeAgent(options, agentListener);

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EdgeAgentOptions options = new EdgeAgentOptions();
                    options.NodeId = nodeIdInput.getText().toString();
                    options.UseSecure = useSecureBox.isChecked();
                    options.ConnectType = ConnectType.DCCS;
                    options.DCCS.CredentialKey = dccsKeyInput.getText().toString();
                    options.DCCS.APIUrl = dccsUrlInput.getText().toString();
                    options.AndroidPackageName = getPackageName();

                    _edgeAgent.Options = options;
                    doConnect(_edgeAgent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        disconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDisconnect(_edgeAgent);
            }
        });

        uploadCfgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doUploadCfg(_edgeAgent);
            }
        });

        sendDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSendDataLoop(_edgeAgent);
            }
        });

        deleteAllCfgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDeleteAllCfg(_edgeAgent);
            }
        });

        deleteDevicesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDeleteDevices(_edgeAgent);
            }
        });

        deleteTagsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDeleteTags(_edgeAgent);
            }
        });

        updateDeviceStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doUpdateDeviceStatus(_edgeAgent);
            }
        });

        updateCfgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doUpdateCfg(_edgeAgent);
            }
        });
    }

}
