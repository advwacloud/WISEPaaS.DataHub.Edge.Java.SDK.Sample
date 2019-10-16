package wisepaas.scada.scadasdksample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import java.time.Instant;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import wisepaas.scada.java.sdk.EdgeAgent;
import wisepaas.scada.java.sdk.model.edge.EdgeData;

 class EdgeHelpers {
    static  DataLoopTask dataLoopTask;
    static  Timer dataLoopTimer;
    static  int sendLoopInterval = 1000;

     static class DataLoopTask extends TimerTask {
        EdgeAgent agent;

         DataLoopTask(EdgeAgent agent) {
            this.agent = agent;
        }

        public void run() {
            try {
                sendDataOnce(agent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static EdgeData prepareData() {
        Random random = new Random();
        EdgeData data = new EdgeData();
        for (int i = 1; i <= 2; i++) {
            for (int j = 1; j <= 5; j++) {
                EdgeData.Tag aTag = new EdgeData.Tag();
                {
                    aTag.DeviceId = "Device" + i;
                    aTag.TagName = "ATag" + j;
                    aTag.Value = random.nextInt(100);
                }
                EdgeData.Tag dTag = new EdgeData.Tag();
                {
                    dTag.DeviceId = "Device" + i;
                    dTag.TagName = "DTag" + j;
                    dTag.Value = j % 2;
                }

                EdgeData.Tag tTag = new EdgeData.Tag();
                {
                    tTag.DeviceId = "Device" + i;
                    tTag.TagName = "TTag" + j;
                    tTag.Value = "TEST " + j;
                }

                data.TagList.add(aTag);
                data.TagList.add(dTag);
                data.TagList.add(tTag);
            }
        }
        data.Timestamp = new Date();
        return data;
    }

    private static void sendDataOnce(EdgeAgent agent) {
        try {
            EdgeData data = prepareData();
            agent.SendData(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected static class showConfigResultTask extends AsyncTask<Void, Void, Void> {
        String result;
        Context ctx;

         showConfigResultTask(String result, Context ctx) {
            this.result = result;
            this.ctx = ctx;
        }

        protected Void doInBackground(Void... param) {
            return null;
        }

        protected void onPostExecute(Void param) {
            showNormalDialog(result, ctx);
        }
    }

    private static void showNormalDialog(String result, Context ctx) {
        final Context _ctx = ctx;
        final String resultMsg = "Upload Config Result: " + result;
        AlertDialog.Builder normalDialog = new AlertDialog.Builder(_ctx);
        normalDialog.setTitle("Result");
        normalDialog.setIcon(R.mipmap.ic_launcher_round);
        normalDialog.setMessage(resultMsg);
        normalDialog.setPositiveButton("OK"
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(_ctx, resultMsg
                                , Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
        normalDialog.create().show();
    }
}
