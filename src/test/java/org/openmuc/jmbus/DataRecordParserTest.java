/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openmuc.jmbus;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmuc.jmbus.DataRecord.DataValueType;
import org.openmuc.jmbus.DataRecord.Description;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class DataRecordParserTest {

    @Test
    @Parameters({ "0704FFFFFFFFFFFFFFFF, -1", "07041223344556677812, 1330927310113874706" })
    public void decode1(String bytesStr, long expected) throws DecodingException {
        byte[] bytes = HexUtils.hexToBytes(bytesStr);

        long val = decodeAndGetVal(bytes);

        assertEquals(expected, val);
    }

    private static Long decodeAndGetVal(byte[] bytes) throws DecodingException {
        DataRecord dataRecord = new DataRecord();
        dataRecord.decode(bytes, 0);

        Object obj = dataRecord.getDataValue();

        assertThat(obj, instanceOf(Long.class));

        return (Long) obj;
    }

    public Object testINT16Data() {
        Object[] p1 = { HexUtils.hexToBytes("0203ff7f"), Short.MAX_VALUE };
        Object[] p2 = { HexUtils.hexToBytes("0203ffff"), -1L };
        return new Object[] { p1, p2 };
    }

    public Object testINT24Data() {
        Object[] p1 = { HexUtils.hexToBytes("0303ffff7f"), 8_388_607L };
        Object[] p2 = { HexUtils.hexToBytes("0303ffffff"), -1L };
        return new Object[] { p1, p2 };
    }

    public Object testINT32Data() {
        Object[] p1 = { HexUtils.hexToBytes("0403ffffff7f"), Integer.MAX_VALUE };
        Object[] p2 = { HexUtils.hexToBytes("0403ffffffff"), -1L };
        return new Object[] { p1, p2 };
    }

    public Object testINT48Data() {
        Object[] p1 = { HexUtils.hexToBytes("0603ffffffffff7f"), 140737488355327L };
        Object[] p2 = { HexUtils.hexToBytes("0603ffffffffffff"), -1L };
        return new Object[] { p1, p2 };
    }

    public Object testINT64Data() {
        Object[] p1 = { HexUtils.hexToBytes("0703ffffffffffffff7f"), Long.MAX_VALUE };
        Object[] p2 = { HexUtils.hexToBytes("0703ffffffffffffffff"), -1L };
        return new Object[] { p1, p2 };
    }

    @Test
    @Parameters(method = "testINT16Data")
    public void testINT16(byte[] bytes, long expectedVal) throws Exception {
        testLong(bytes, expectedVal);
    }

    @Test
    @Parameters(method = "testINT24Data")
    public void testINT24(byte[] bytes, long expectedVal) throws Exception {
        testLong(bytes, expectedVal);
    }

    @Test
    @Parameters(method = "testINT32Data")
    public void testINT32(byte[] bytes, long expectedVal) throws Exception {
        testLong(bytes, expectedVal);
    }

    @Test
    @Parameters(method = "testINT48Data")
    public void testINT48(byte[] bytes, long expectedVal) throws Exception {
        testLong(bytes, expectedVal);
    }

    @Test
    @Parameters(method = "testINT64Data")
    public void testINT64(byte[] bytes, long expectedVal) throws Exception {
        testLong(bytes, expectedVal);
    }

    private void testLong(byte[] bytes, long expectedVal) throws DecodingException {
        DataRecord dataRecord = new DataRecord();

        dataRecord.decode(bytes, 0);

        Object obj = dataRecord.getDataValue();

        assertThat(obj, instanceOf(Long.class));

        assertEquals(DataValueType.LONG, dataRecord.getDataValueType());

        Long integer = (Long) obj;

        assertEquals(expectedVal, integer.longValue());
    }

    public Object testDataRecordsValues() throws DecodingException {
        /* e0000nnn Energy Wh */
        Object[] p1 = { "0407c81e0000", Description.ENERGY, DlmsUnit.WATT_HOUR, 4, 7880L };
        /* extended Energy Wh */
        Object[] p13 = { "04fb01c81e0000", Description.ENERGY, DlmsUnit.WATT_HOUR, 6, 7880L };

        /* e0001nnn Energy J */
        Object[] p11 = { "040fc81e0000", Description.ENERGY, DlmsUnit.JOULE, 7, 7880L };
        /* extended Energy J*/
        Object[] p12 = { "04fb09c81e0000", Description.ENERGY, DlmsUnit.JOULE, 9, 7880L };

        /* Energy BTU (TODO needs to be implemented) */
        //        Object[] pxx = { "04863dc81e0000", Description.ENERGY, DlmsUnit.BTU, 3, 7880L };
        /* Energy cal */
        Object[] p14 = { "04fb0dc81e0000", Description.ENERGY, DlmsUnit.CALORIFIC_VALUE, 6, 7880L };

        /* e0010nnn Volume m^3 */
        Object[] p2 = { "0415febf0000", Description.VOLUME, DlmsUnit.CUBIC_METRE, -1, 49150L };
        Object[] p3 = { "844015f8bf0000", Description.VOLUME, DlmsUnit.CUBIC_METRE, -1, 49144L };
        Object[] p15 = { "04fb21c81e0000", Description.VOLUME, DlmsUnit.CUBIC_FEET, -1, 7880L };
        Object[] p16 = { "04fb22c81e0000", Description.VOLUME, DlmsUnit.US_GALLON, -1, 7880L };

        /* e0011nnn Mass kg */

        /* e01000nn On Time seconds/minutes/hours/days */

        Object[] p4 = { "042238090000", Description.ON_TIME, DlmsUnit.HOUR, 0, 2360L };

        /* e01001nn Operating Time seconds/minutes/hours/days */

        Object[] p5 = { "04263d070000", Description.OPERATING_TIME, DlmsUnit.HOUR, 0, 1853L };

        /* e10110nn Flow Temperature °C */
        Object[] p6 = { "025a7902", Description.FLOW_TEMPERATURE, DlmsUnit.DEGREE_CELSIUS, -1, 633L };

        /* e10111nn Return Temperature °C */
        Object[] p7 = { "025ea601", Description.RETURN_TEMPERATURE, DlmsUnit.DEGREE_CELSIUS, -1, 422L };

        /* e11000nn Temperature Difference K */

        Object[] p8 = { "0262d300", Description.TEMPERATURE_DIFFERENCE, DlmsUnit.KELVIN, -1, 211L };

        // /* e1101101 Date and time - type F */
        Object[] p9 = { "046d2b117811", Description.DATE_TIME, null, 0, 1295887380000L };

        // /* e1101101 Date and time - type I */
        Object[] p10 = { "066d221ae0100100", Description.DATE_TIME, null, 0, 979601194000L };
        return new Object[] { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16 };
    }

    @Test
    @Parameters(method = "testDataRecordsValues")
    public void testDataRecords(String bytesStr, Description desc, DlmsUnit unit, int scaler, Long data)
            throws DecodingException {
        byte[] bytes = HexUtils.hexToBytes(bytesStr);

        DataRecord dataRecord = new DataRecord();
        dataRecord.decode(bytes, 0);

        assertEquals(desc, dataRecord.getDescription());
        assertEquals(unit, dataRecord.getUnit());
        assertEquals(scaler, dataRecord.getMultiplierExponent());
        Object dataValue = dataRecord.getDataValue();

        if (dataValue instanceof Date) {
            assertEquals((long) data, ((Date) dataValue).getTime());
        }
        else {
            assertEquals(data, dataValue);
        }

    }

}
