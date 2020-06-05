package org.openmuc.jmbus.sample;

import java.io.IOException;

import org.openmuc.jmbus.MBusConnection;
import org.openmuc.jmbus.MBusConnection.MBusTcpBuilder;

public class WiredTcpMBusConnection {

    public static void newConnection() throws IOException {
        // tag::todoc[]
        MBusTcpBuilder builder = MBusConnection.newTcpBuilder("192.168.2.15", 1234);
        try (MBusConnection mBusConnection = builder.build()) {
            // read/write
        }
        // end::todoc[]
    }

    public static void read() throws IOException {
        MBusTcpBuilder builder = MBusConnection.newTcpBuilder("192.168.2.15", 1234);
        try (MBusConnection mBusConnection = builder.build()) {
            // tag::readtodoc[]
            int primaryAddress = 1;
            mBusConnection.read(primaryAddress);
            // end::readtodoc[]
        }
    }

    public static void write() throws IOException {
        MBusTcpBuilder builder = MBusConnection.newTcpBuilder("192.168.2.15", 1234);
        try (MBusConnection mBusConnection = builder.build()) {
            // tag::writetodoc[]
            int primaryAddress = 5;
            byte[] data = { 0x01, 0x7a, 0x09 };
            mBusConnection.write(primaryAddress, data);
            // end::writetodoc[]
        }
    }
}
