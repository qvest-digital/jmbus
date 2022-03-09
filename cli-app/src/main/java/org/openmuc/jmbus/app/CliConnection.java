/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openmuc.jmbus.app;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import org.openmuc.jmbus.DataRecord;
import org.openmuc.jmbus.MBusConnection;
import org.openmuc.jmbus.SecondaryAddress;
import org.openmuc.jmbus.SecondaryAddressListener;
import org.openmuc.jmbus.VariableDataStructure;
import org.openmuc.jrxtx.SerialPortTimeoutException;

class CliConnection {

    private static void printWriteExample(CliPrinter cliPrinter) {
        cliPrinter.printlnInfo("Example for writing to a meter: \n"
                + "\tChange primary address: ./jmbus-app.sh write -cp /dev/ttyUSB0 -a <old_primary_address> -dif 01 -vif 7a -data <data_new_primary_address>\n"
                + "\tExample:\n"
                + "\t\tChange primary address from 20 to 26: ./jmbus-app.sh write -cp /dev/ttyUSB0 -a 20 -dif 01 -vif 7a -data 1a\n\n"
                + "\tSet primary address with secondary address: ./jmbus-app.sh write -cp /dev/ttyUSB0 -a <secondary_address> -dif 01  -vif 7a -data <data_primary_address>\n"
                + "\tExample:\n"
                + "\t\tSet primary address to 47 (0x2f) with secondary address 3a453b4f4f343423: ./jmbus-app.sh write -cp /dev/ttyUSB0 -a 3a453b4f4f343423 -dif 01 -vif 7a -data 2f\n\n");
    }

    public static void write(ConsoleLineParser cliParser, MBusConnection mBusConnection, CliPrinter cliPrinter) {
        int primaryAddress = cliParser.getPrimaryAddress();
        long waitTime = cliParser.getWaitTime();
        SecondaryAddress secondaryAddress = cliParser.getSecondaryAddress();

        byte[] data = cliParser.getData();
        byte[] dif = cliParser.getDif();
        byte[] vif = cliParser.getVif();

        if (dif.length == 0 || vif.length == 0) {
            printWriteExample(cliPrinter);
            cliPrinter.printError("No dif or vif set.", true);
        }

        VerboseMessageListenerImpl messageListener = new VerboseMessageListenerImpl(cliPrinter);
        mBusConnection.setVerboseMessageListener(messageListener);

        if (secondaryAddress != null) {
            try {
                mBusConnection.selectComponent(secondaryAddress);
                sleep(waitTime, cliPrinter);
            } catch (SerialPortTimeoutException e) {
                mBusConnection.close();
                cliPrinter.printError("Selecting secondary address attempt timed out.");
            } catch (IOException e) {
                mBusConnection.close();
                cliPrinter.printError("Error selecting secondary address: " + e.getMessage());
            }
            primaryAddress = 0xfd;
        }

        int length = dif.length + vif.length + data.length;
        byte[] dataRecord = ByteBuffer.allocate(length).put(dif).put(vif).put(data).array();
        try {
            mBusConnection.write(primaryAddress, dataRecord);
            sleep(waitTime, cliPrinter);
            cliPrinter.printInfo("Data was sent.");
        } catch (SerialPortTimeoutException e) {
            mBusConnection.close();
            cliPrinter.printError("Write attempt timed out.");
        } catch (IOException e) {
            mBusConnection.close();
            cliPrinter.printError("Error writing meter: " + e.getMessage());
        }

        cliPrinter.printlnInfo("");

        mBusConnection.close();
    }

    public static void read(ConsoleLineParser cliParser, MBusConnection mBusConnection, CliPrinter cliPrinter) {
        int primaryAddress = cliParser.getPrimaryAddress();
        long waitTime = cliParser.getWaitTime();
        SecondaryAddress secondaryAddress = cliParser.getSecondaryAddress();

        List<DataRecord> dataRecordsToSelectForReadout = new LinkedList<>();

        VerboseMessageListenerImpl messageListener = new VerboseMessageListenerImpl(cliPrinter);
        mBusConnection.setVerboseMessageListener(messageListener);

        VariableDataStructure variableDataStructure = null;
        if (secondaryAddress != null) {
            try {
                mBusConnection.selectComponent(secondaryAddress);
                sleep(waitTime, cliPrinter);
            } catch (SerialPortTimeoutException e) {
                mBusConnection.close();
                cliPrinter.printError("Selecting secondary address attempt timed out.");
            } catch (IOException e) {
                mBusConnection.close();
                cliPrinter.printError("Error selecting secondary address: " + e.getMessage());
            }
            primaryAddress = 0xfd;
        }
        else {
            if (!cliParser.isLinkResetDisabled()) {
                try {
                    mBusConnection.linkReset(primaryAddress);
                    sleep(waitTime, cliPrinter);
                } catch (InterruptedIOException e) {
                    mBusConnection.close();
                    cliPrinter.printError("Resetting link (SND_NKE) attempt timed out.");
                } catch (IOException e) {
                    mBusConnection.close();
                    cliPrinter.printError("Error resetting link (SND_NKE): " + e.getMessage());
                }
                sleep(100 + waitTime, cliPrinter); // for slow slaves
            }
        }
        if (!dataRecordsToSelectForReadout.isEmpty()) {
            try {
                mBusConnection.selectForReadout(primaryAddress, dataRecordsToSelectForReadout);
                sleep(waitTime, cliPrinter);
            } catch (InterruptedIOException e) {
                mBusConnection.close();
                cliPrinter.printError("Selecting data record for readout timed out.");
            } catch (IOException e) {
                mBusConnection.close();
                cliPrinter.printError("Error selecting data record for readout: " + e.getMessage());
            }
        }

        do {
            try {
                variableDataStructure = mBusConnection.read(primaryAddress);
                sleep(waitTime, cliPrinter);
            } catch (InterruptedIOException e) {
                mBusConnection.close();
                cliPrinter.printError("Read attempt timed out.");
            } catch (IOException e) {
                mBusConnection.close();
                cliPrinter.printError(e.getMessage());
            }

            if (!dataRecordsToSelectForReadout.isEmpty()) {
                try {
                    mBusConnection.resetReadout(primaryAddress);
                    sleep(waitTime, cliPrinter);
                } catch (InterruptedIOException e) {
                    cliPrinter.printError("Resetting meter for standard readout timed out.");
                } catch (IOException e) {
                    cliPrinter.printError("Error resetting meter for standard readout: " + e.getMessage());
                }
            }

            cliPrinter.printlnInfo(variableDataStructure.toString());

        } while (variableDataStructure.moreRecordsFollow());

        mBusConnection.close();
    }

    public static void scan(ConsoleLineParser cliParser, MBusConnection mBusConnection, CliPrinter cliPrinter)
            throws IOException {
        long waitTime = cliParser.getWaitTime();
        boolean scanSecondaryAddress = cliParser.isSecondaryScan();
        String wildcardMask = cliParser.getWildcard();

        try {
            mBusConnection.setVerboseMessageListener(new VerboseMessageListenerImpl(cliPrinter));

            cliPrinter.printInfo("Scanning address: \n");

            if (scanSecondaryAddress) {
                mBusConnection.scan(wildcardMask, new SecondaryAddressListenerImpl(cliPrinter), waitTime);
            }
            else {
                scanPrimaryAddresses(mBusConnection, waitTime, cliPrinter);
            }

        } finally {
            mBusConnection.close();
        }
        cliPrinter.printInfo("\nScan finished.");
    }

    private static void scanPrimaryAddresses(MBusConnection mBusConnection, long waitTime, CliPrinter cliPrinter) {
        for (int i = 0; i <= 250; i++) {
            if (i % 10 == 0 && i != 0) {
                cliPrinter.printlnInfo();
            }
            cliPrinter.printInfo(String.format("%3d%c", i, ','));
            try {
                mBusConnection.linkReset(i);
                sleep(50 + waitTime, cliPrinter);

                VariableDataStructure vdr = mBusConnection.read(i);
                cliPrinter.printInfo("\nFound device at primary address :" + i + ", ");
                cliPrinter.printlnInfo(vdr.getSecondaryAddress());

            } catch (InterruptedIOException e) {
                // do nothing
            } catch (IOException e) {
                cliPrinter.printlnInfo("\nError reading meter at primary address " + i + ": " + e.getMessage());
            }
        }
    }

    static class SecondaryAddressListenerImpl implements SecondaryAddressListener {

        private final CliPrinter cliPrinter;

        public SecondaryAddressListenerImpl(CliPrinter cliPrinter) {
            this.cliPrinter = cliPrinter;
        }

        @Override
        public void newDeviceFound(SecondaryAddress secondaryAddress) {
            // do nothing, this application uses the return value of ScanSecondaryAddress.scan
        }

        @Override
        public void newScanMessage(String message) {
            cliPrinter.printlnInfo(message);
        }
    }

    private static void sleep(long time, CliPrinter cliPrinter) {
        if (time != 0) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                cliPrinter.printError("Thread sleep fails.\n" + e.getMessage());
            }
        }
    }

    private CliConnection() {
        // hide the constructor
    }
}
