/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package ly.pp.justpiano3.midi;

import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiInputPort;
import android.media.midi.MidiManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class AppMidiManager {

    private final MidiManager mMidiManager;

    // Selected Device(s)
    private MidiDevice mReceiveDevice; // an "Output" device is one we will RECEIVE data FROM

    public AppMidiManager(MidiManager midiManager) {
        mMidiManager = midiManager;
    }

    /**
     * Scan attached Midi devices forcefully from scratch
     *
     * @param sendDevices,    container for send devices
     * @param receiveDevices, container for receive devices
     */
    public void scanMidiDevices(ArrayList<MidiDeviceInfo> sendDevices,
                                ArrayList<MidiDeviceInfo> receiveDevices) {
        sendDevices.clear();
        receiveDevices.clear();
        MidiDeviceInfo[] devInfos = mMidiManager.getDevices();
        for (MidiDeviceInfo devInfo : devInfos) {
            int numInPorts = devInfo.getInputPortCount();
            String deviceName = devInfo.getProperties().getString(MidiDeviceInfo.PROPERTY_NAME);
            if (deviceName == null) {
                continue;
            }
            if (numInPorts > 0) {
                sendDevices.add(devInfo);
            }

            int numOutPorts = devInfo.getOutputPortCount();
            if (numOutPorts > 0) {
                receiveDevices.add(devInfo);
            }
        }
    }

    //
    // Receive Device
    //
    public class OpenMidiReceiveDeviceListener implements MidiManager.OnDeviceOpenedListener {
        @Override
        public void onDeviceOpened(MidiDevice device) {
            mReceiveDevice = device;
            startReadingMidi(mReceiveDevice, 0/*mPortNumber*/);
        }
    }

    public void openReceiveDevice(MidiDeviceInfo devInfo) {
        mMidiManager.openDevice(devInfo, new OpenMidiReceiveDeviceListener(), null);
    }

    public void closeReceiveDevice() {
        if (mReceiveDevice != null) {
            // Native API
            mReceiveDevice = null;
        }
    }

    //
    // Native API stuff
    //
    public static void loadNativeAPI() {
        System.loadLibrary("native_midi");
    }

    public native void startReadingMidi(MidiDevice receiveDevice, int portNumber);

    public native void stopReadingMidi();
}
