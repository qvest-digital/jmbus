package org.openmuc.jmbus;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.openmuc.jmbus.DataRecord.DataValueType;

public class VariableDataStructureTest {

    @Test
    public void test1() throws Exception {
        SecondaryAddress linkLayerSecondaryAddress = SecondaryAddress
                .newFromWMBusHeader(HexUtils.hexToBytes("2423759468372507"), 0);

        final byte[] key = "HalloWorldTestPW".getBytes();
        Map<SecondaryAddress, byte[]> keyMap = new HashMap<>();
        keyMap.put(linkLayerSecondaryAddress, key);

        byte[] encrypt = HexUtils.hexToBytes("7ACB5030055E861434F34A14AE2B9973AEE9811E32578336455E9AC7E7EF"
                + "960B2253CA7F2BB6632C35E3DD95D66FE96C699A298A53");

        VariableDataStructure vds = new VariableDataStructure(encrypt, 0, encrypt.length, linkLayerSecondaryAddress,
                keyMap);
        vds.decode();

        System.out.println(vds);

        assertEquals(203, vds.getAccessNumber());
        assertEquals(80, vds.getStatus());
        assertEquals(EncryptionMode.AES_CBC_IV, vds.getEncryptionMode());
        assertEquals(3, vds.getNumberOfEncryptedBlocks());
        List<DataRecord> dataRecords = vds.getDataRecords();

        DataRecord dr = dataRecords.get(0);
        assertEquals(12, dr.getDataLength());
        assertEquals(DataValueType.BCD, dr.getDataValueType());
        assertArrayEquals(new byte[] { 12 }, dr.getDib());

    }

    @Test
    public void testExample() throws Exception {
        String wmbus = "434493157856341233038C2075900F002C25B30A000021924D4F2FB66E017A75002007109058475F4BC91DF878B80A1B0F98B629024AAC727942BFC549233C0140829B93";

        SecondaryAddress linkLayerSecondaryAddress = SecondaryAddress.newFromWMBusHeader(HexUtils.hexToBytes(wmbus), 2);

        String keystring = "00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F".replaceAll(" ", "");
        final byte[] key = HexUtils.hexToBytes(keystring);

        Map<SecondaryAddress, byte[]> keyMap = new HashMap<>();
        keyMap.put(linkLayerSecondaryAddress, key);

        byte[] crypted = HexUtils.hexToBytes(wmbus);
        int offset = 10;
        VariableDataStructure vds = new VariableDataStructure(crypted, offset, crypted.length-offset, linkLayerSecondaryAddress, keyMap);
        try {
            vds.decode();
        } catch (DecodingException de) {
            fail("should not throw DecodingException: " + de);
        }

        System.out.println(vds);
    }

}
