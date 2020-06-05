package org.openmuc.jmbus;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class SecondaryAddressTest {

    @Test
    public void testFromLongHeade() {
        byte[] buffer1 = new byte[] { (byte) 0xee, 0x4d, 0x49, 0x53, 0x53, 0x21, 0x16, 0x06 };
        byte[] buffer2 = new byte[] { (byte) 0xee, 0x4d, 0x48, 0x72, 0x53, 0x21, 0x16, 0x06 };

        SecondaryAddress sa1 = SecondaryAddress.newFromLongHeader(buffer1, 0);
        SecondaryAddress sa2 = SecondaryAddress.newFromLongHeader(buffer2, 0);
        SecondaryAddress sa3 = SecondaryAddress.newFromLongHeader(buffer2, 0);

        Map<SecondaryAddress, Integer> testMap = new HashMap<>();

        testMap.put(sa1, 1);
        testMap.put(sa2, 2);
        assertNotEquals(testMap.get(sa1), testMap.get(sa2));
        testMap.put(sa3, 3);
        assertEquals(testMap.get(sa2), testMap.get(sa3));
        assertNotEquals(testMap.get(sa1), testMap.get(sa2));
        assertNotEquals(testMap.get(sa1), testMap.get(sa3));
    }

    @Test
    public void testFromManufactureId() {
        // (ID = 01020304 (BCD), Man = 4024h (PAD), Ver = 1, Dev. Type = 4 (heat)
        // 04 03 02 01 24 40 01 04
        byte[] idNumber = new byte[] { (byte) 0x04, 0x03, 0x02, 0x01 };
        String manufacturerID = "PAD";
        byte version = (byte) 1;
        byte medium = (byte) 4;
        boolean longHeader = true;

        SecondaryAddress address = SecondaryAddress.newFromManufactureId(idNumber, manufacturerID, version, medium,
                longHeader);
        byte[] expectes = new byte[] { 0x04, 0x03, 0x02, 0x01, 0x24, 0x40, 0x01, 0x04 };
        byte[] actuals = address.asByteArray();

        assertArrayEquals(expectes, actuals);
    }

}
