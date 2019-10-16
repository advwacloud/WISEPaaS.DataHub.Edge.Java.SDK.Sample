package wisepaas.scada.scadasdksample;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

import wisepaas.scada.java.sdk.EdgeAgent;
import wisepaas.scada.java.sdk.common.Const;
import wisepaas.scada.java.sdk.model.edge.EdgeConfig;
import wisepaas.scada.java.sdk.model.edge.EdgeDeviceStatus;

import static wisepaas.scada.scadasdksample.EdgeHelpers.dataLoopTask;
import static wisepaas.scada.scadasdksample.EdgeHelpers.dataLoopTimer;
import static wisepaas.scada.scadasdksample.EdgeHelpers.sendLoopInterval;


class EdgeActions {

    static void doConnect(EdgeAgent agent) {
        try {
            agent.Connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void doUploadCfg(EdgeAgent agent) {
        try {
            EdgeConfig config = new EdgeConfig();
            int deviceCount = 2;
            int ATagCount = 5;
            int DTagCount = 5;
            int TTagCount = 5;

            config.Scada = new EdgeConfig.ScadaConfig();
            {
                config.Scada.Name = "TEST_SCADA";
                config.Scada.Description = "JAVA SDK TEST";
            }
            config.Scada.DeviceList = new ArrayList<>();

            for (int i = 1; i <= deviceCount; i++) {
                EdgeConfig.DeviceConfig device = new EdgeConfig.DeviceConfig();
                {
                    device.Id = "Device" + i;
                    device.Name = "Device " + i;
                    device.Type = "Smart Device";
                    device.Description = "Device " + i;
                }

                device.AnalogTagList = new ArrayList<>();
                device.DiscreteTagList = new ArrayList<>();
                device.TextTagList = new ArrayList<>();

                for (int j = 1; j <= ATagCount; j++) {
                    EdgeConfig.AnalogTagConfig analogTag = new EdgeConfig.AnalogTagConfig();
                    {
                        analogTag.Name = "ATag" + j;
                        analogTag.Description = "ATag " + j;
                        analogTag.ReadOnly = false;
                        analogTag.ArraySize = 0;
                        analogTag.SpanHigh = 1000.0;
                        analogTag.SpanLow = 0.0;
                        analogTag.EngineerUnit = "";
                        analogTag.IntegerDisplayFormat = 4;
                        analogTag.FractionDisplayFormat = 2;
                        analogTag.HHPriority = 0;
                        analogTag.HHAlarmLimit = 0.0;
                        analogTag.HighPriority = 0;
                        analogTag.HighAlarmLimit = 0.0;
                        analogTag.LowPriority = 0;
                        analogTag.LowAlarmLimit = 0.0;
                        analogTag.LLPriority = 0;
                        analogTag.LLAlarmLimit = 0.0;
                    }
                    device.AnalogTagList.add(analogTag);
                }

                for (int j = 1; j <= DTagCount; j++) {
                    EdgeConfig.DiscreteTagConfig discreteTag = new EdgeConfig.DiscreteTagConfig();
                    {
                        discreteTag.Name = "DTag" + j;
                        discreteTag.Description = "DTag " + j;
                        discreteTag.ReadOnly = false;
                        discreteTag.ArraySize = 0;
                        discreteTag.State0 = "0";
                        discreteTag.State1 = "1";
                        discreteTag.State2 = "";
                        discreteTag.State3 = "";
                        discreteTag.State4 = "";
                        discreteTag.State5 = "";
                        discreteTag.State6 = "";
                        discreteTag.State7 = "";
                        discreteTag.State0AlarmPriority = 0;
                        discreteTag.State1AlarmPriority = 0;
                        discreteTag.State2AlarmPriority = 0;
                        discreteTag.State3AlarmPriority = 0;
                        discreteTag.State4AlarmPriority = 0;
                        discreteTag.State5AlarmPriority = 0;
                        discreteTag.State6AlarmPriority = 0;
                        discreteTag.State7AlarmPriority = 0;
                    }
                    device.DiscreteTagList.add(discreteTag);

                }

                for (int j = 1; j <= TTagCount; j++) {
                    EdgeConfig.TextTagConfig textTag = new EdgeConfig.TextTagConfig();
                    {
                        textTag.Name = "TTag" + j;
                        textTag.Description = "TTag " + j;
                        textTag.ReadOnly = false;
                        textTag.ArraySize = 0;
                        textTag.AlarmStatus = false;
                    }
                    device.TextTagList.add(textTag);
                }

                config.Scada.DeviceList.add(device);
            }

            agent.UploadConfig(Const.ActionType.Create, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void doDisconnect(EdgeAgent agent) {
        try {
            if (dataLoopTimer != null) {
                dataLoopTimer.cancel();
                dataLoopTimer = null;
            }
            agent.Disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void doSendDataLoop(EdgeAgent agent) {
        try {
            if (dataLoopTimer != null) {
                return;
            }
            dataLoopTimer = new Timer();
            dataLoopTask = new EdgeHelpers.DataLoopTask(agent);
            dataLoopTimer.schedule(dataLoopTask, 0, sendLoopInterval);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void doDeleteAllCfg(EdgeAgent agent) {
        try {
            if (agent == null) {
                return;
            }

            EdgeConfig config = new EdgeConfig();
            config.Scada = new EdgeConfig.ScadaConfig();

            agent.UploadConfig(Const.ActionType.Delete, config);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void doDeleteDevices(EdgeAgent agent) {
        try {
            int deviceCount = 2;
            if (agent == null) {
                return;
            }

            EdgeConfig config = new EdgeConfig();
            config.Scada = new EdgeConfig.ScadaConfig();

            config.Scada.DeviceList = new ArrayList<>();

            for (int i = 1; i <= deviceCount; i++) {
                EdgeConfig.DeviceConfig device = new EdgeConfig.DeviceConfig();
                device.Id = "Device" + i;


                config.Scada.DeviceList.add(device);
            }
            agent.UploadConfig(Const.ActionType.Delete, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void doDeleteTags(EdgeAgent agent) {
        try {
            int deviceCount = 2;

            int ATagCount = 5;
            int DTagCount = 5;
            int TTagCount = 5;
            if (agent == null) {
                return;
            }

            EdgeConfig config = new EdgeConfig();
            config.Scada = new EdgeConfig.ScadaConfig();

            config.Scada.DeviceList = new ArrayList<>();

            for (int i = 1; i <= deviceCount; i++) {
                EdgeConfig.DeviceConfig device = new EdgeConfig.DeviceConfig();
                device.Id = "Device" + i;

                device.AnalogTagList = new ArrayList<>();
                device.DiscreteTagList = new ArrayList<>();
                device.TextTagList = new ArrayList<>();
                for (int j = 1; j <= ATagCount; j++) {
                    EdgeConfig.AnalogTagConfig analogTag = new EdgeConfig.AnalogTagConfig();
                    analogTag.Name = "ATag" + j;

                    device.AnalogTagList.add(analogTag);
                }
                for (int j = 1; j <= DTagCount; j++) {
                    EdgeConfig.DiscreteTagConfig discreteTag = new EdgeConfig.DiscreteTagConfig();
                    discreteTag.Name = "DTag" + j;

                    device.DiscreteTagList.add(discreteTag);
                }
                for (int j = 1; j <= TTagCount; j++) {
                    EdgeConfig.TextTagConfig textTag = new EdgeConfig.TextTagConfig();
                    textTag.Name = "TTag" + j;

                    device.TextTagList.add(textTag);
                }

                config.Scada.DeviceList.add(device);
            }

            agent.UploadConfig(Const.ActionType.Delete, config);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void doUpdateDeviceStatus(EdgeAgent agent) {
        try {
            int deviceCount = 2;

            if (agent == null) {
                return;
            }


            EdgeDeviceStatus deviceStatus = new EdgeDeviceStatus();
            for (int i = 1; i <= deviceCount; i++) {
                EdgeDeviceStatus.Device device = new EdgeDeviceStatus.Device();
                device.Id = "Device" + i;
                device.Status = Const.Status.Online;

                deviceStatus.DeviceList.add(device);
            }
            deviceStatus.Timestamp = new Date();

            agent.SendDeviceStatus(deviceStatus);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void doUpdateCfg(EdgeAgent agent) {
        try {
            int deviceCount = 2;

            int ATagCount = 5;
            int DTagCount = 5;
            int TTagCount = 5;
            if (agent == null) {
                return;
            }


            EdgeConfig config = new EdgeConfig();
            config.Scada = new EdgeConfig.ScadaConfig();

            config.Scada.Name = "TEST_SCADA";
            config.Scada.Description = "For Test";


            config.Scada.DeviceList = new ArrayList<>();
            for (int i = 1; i <= deviceCount; i++) {
                EdgeConfig.DeviceConfig device = new EdgeConfig.DeviceConfig();

                device.Id = "Device" + i;
                device.Description = "Device " + i;


                device.AnalogTagList = new ArrayList<>();
                device.DiscreteTagList = new ArrayList<>();
                device.TextTagList = new ArrayList<>();

                for (int j = 1; j <= ATagCount; j++) {
                    EdgeConfig.AnalogTagConfig analogTag = new EdgeConfig.AnalogTagConfig();

                    analogTag.Name = "ATag" + j;
                    analogTag.Description = "Updatetest - ATag " + j;
//                        analogTag.ReadOnly = false;
//                        analogTag.ArraySize = 0;
//                        analogTag.AlarmStatus = false;
//                        analogTag.SpanHigh = 1000.0;
//                        analogTag.SpanLow = 0.0;
//                        analogTag.EngineerUnit = "";
//                        analogTag.IntegerDisplayFormat = 4;
//                        analogTag.FractionDisplayFormat = 2;
//                        analogTag.HHPriority = 0;
//                        analogTag.HHAlarmLimit = 0.0;
//                        analogTag.HighPriority = 0;
//                        analogTag.HighAlarmLimit = 0.0;
//                        analogTag.LowPriority = 0;
//                        analogTag.LowAlarmLimit = 0.0;
//                        analogTag.LLPriority = 0;
//                        analogTag.LLAlarmLimit = 0.0;

                    device.AnalogTagList.add(analogTag);
                }
                for (int j = 1; j <= DTagCount; j++) {
                    EdgeConfig.DiscreteTagConfig discreteTag = new EdgeConfig.DiscreteTagConfig();

                    discreteTag.Name = "DTag" + j;
                    discreteTag.Description = "Updatetest - DTag " + j;
//                    discreteTag.ReadOnly = false;
//                    discreteTag.ArraySize = 0;
//                    discreteTag.AlarmStatus = true;
//                    discreteTag.State0 = "0";
//                    discreteTag.State1 = "1";
//                    discreteTag.State2 = "";
//                    discreteTag.State3 = "";
//                    discreteTag.State4 = "";
//                    discreteTag.State5 = "";
//                    discreteTag.State6 = "";
//                    discreteTag.State7 = "";
//                    discreteTag.State0AlarmPriority = 0;
//                    discreteTag.State1AlarmPriority = 0;
//                    discreteTag.State2AlarmPriority = 0;
//                    discreteTag.State3AlarmPriority = 0;
//                    discreteTag.State4AlarmPriority = 0;
//                    discreteTag.State5AlarmPriority = 0;
//                    discreteTag.State6AlarmPriority = 0;
//                    discreteTag.State7AlarmPriority = 0;

                    device.DiscreteTagList.add(discreteTag);
                }
                for (int j = 1; j <= TTagCount; j++) {
                    EdgeConfig.TextTagConfig textTag = new EdgeConfig.TextTagConfig();
                    textTag.Name = "TTag" + j;
                    textTag.Description = "Updatetest - TTag " + j;
//                    textTag.ReadOnly = false;
//                    textTag.ArraySize = 0;
//                    textTag.AlarmStatus = false;

                    device.TextTagList.add(textTag);
                }

                config.Scada.DeviceList.add(device);
            }

            agent.UploadConfig(Const.ActionType.Update, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
