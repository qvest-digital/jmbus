/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openmuc.jmbus;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

    public Object testINT32Data() {
        Object[] p1 = { HexUtils.hexToBytes("0403e4050000"), 1508L };
        Object[] p2 = { HexUtils.hexToBytes("0403ffffffff"), -1L };
        return new Object[] { p1, p2 };
    }

    @Test
    @Parameters(method = "testINT32Data")
    public void testINT32(byte[] bytes, long expectedVal) throws Exception {

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

        // /* e111_1101 Remaining battery life time in months */
        Object[] p17 = { "02fdfd02b400", Description.REMAINING_BATTERY_LIFE_TIME, DlmsUnit.MONTH, 0, 180L };
        return new Object[] { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17 };
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

    /*
    Data from OMS Spec Annex B (https://oms-group.org/fileadmin/files/download4all/specification/Vol2/4.4.2/OMS-Spec_Vol2_AnnexB_E442.pdf)
     */
    public Object testDescriptionExtensionValues() throws DecodingException {
        Object[] ca01 = { "0cfdd0fc0101000000", Description.CURRENT, Arrays.asList(DataRecord.DescriptionExtension.PHASE_L1), DlmsUnit.AMPERE, -12, 1L };
        Object[] ca02 = { "0cfdd0fc0201000000", Description.CURRENT, Arrays.asList(DataRecord.DescriptionExtension.PHASE_L2), DlmsUnit.AMPERE, -12, 1L };
        Object[] ca03 = { "0cfdd0fc0301000000", Description.CURRENT, Arrays.asList(DataRecord.DescriptionExtension.PHASE_L3), DlmsUnit.AMPERE, -12, 1L };
        Object[] ca04 = { "0cfdd0fc0401000000", Description.CURRENT, Arrays.asList(DataRecord.DescriptionExtension.NEUTRAL_CONDUCTOR), DlmsUnit.AMPERE, -12, 1L };

        Object[] ej01 = { "0c0801000000", Description.ENERGY, Arrays.asList(), DlmsUnit.JOULE, 0, 1L };
        Object[] ej02 = { "0cfb0801000000", Description.ENERGY, Arrays.asList(), DlmsUnit.JOULE, 8, 1L };
        Object[] ej03 = { "0cfb887d01000000", Description.ENERGY, Arrays.asList(), DlmsUnit.JOULE, 11, 1L };
        Object[] ej04 = { "0c883c01000000", Description.ENERGY, Arrays.asList(DataRecord.DescriptionExtension.NEGATIVE_ACCUMULATION), DlmsUnit.JOULE, 0, 1L };
        Object[] ej05 = { "0cfb883c01000000", Description.ENERGY, Arrays.asList(DataRecord.DescriptionExtension.NEGATIVE_ACCUMULATION), DlmsUnit.JOULE, 8, 1L };
        Object[] ej06 = { "0cfb88fd3c01000000", Description.ENERGY, Arrays.asList(DataRecord.DescriptionExtension.NEGATIVE_ACCUMULATION), DlmsUnit.JOULE, 11, 1L };

        Object[] ew01 = { "0c0001000000", Description.ENERGY, Arrays.asList(), DlmsUnit.WATT_HOUR, -3, 1L };
        Object[] ew02 = { "0cfb0001000000", Description.ENERGY, Arrays.asList(), DlmsUnit.WATT_HOUR, 5, 1L };
        Object[] ew03 = { "0cfb807d01000000", Description.ENERGY, Arrays.asList(), DlmsUnit.WATT_HOUR, 8, 1L };
        Object[] ew04 = { "0c803c01000000", Description.ENERGY, Arrays.asList(DataRecord.DescriptionExtension.NEGATIVE_ACCUMULATION), DlmsUnit.WATT_HOUR, -3, 1L };
        Object[] ew05 = { "0cfb803c01000000", Description.ENERGY, Arrays.asList(DataRecord.DescriptionExtension.NEGATIVE_ACCUMULATION), DlmsUnit.WATT_HOUR, 5, 1L };
        Object[] ew06 = { "0cfb80fd3c01000000", Description.ENERGY, Arrays.asList(DataRecord.DescriptionExtension.NEGATIVE_ACCUMULATION), DlmsUnit.WATT_HOUR, 8, 1L };
        Object[] ew07 = { "0c80fc1001000000", Description.ENERGY, Arrays.asList(DataRecord.DescriptionExtension.ABSOLUTE), DlmsUnit.WATT_HOUR, -3, 1L };
        Object[] ew08 = { "0cfb80fc1001000000", Description.ENERGY, Arrays.asList(DataRecord.DescriptionExtension.ABSOLUTE), DlmsUnit.WATT_HOUR, 5, 1L };
        Object[] ew09 = { "0cfb80fdfc1001000000", Description.ENERGY, Arrays.asList(DataRecord.DescriptionExtension.ABSOLUTE), DlmsUnit.WATT_HOUR, 8, 1L };

        Object[] pd01 = { "0cfbaafc0501000000", Description.PHASE, Arrays.asList(DataRecord.DescriptionExtension.PHASE_L1_L2), DlmsUnit.DEGREE, -1, 1L };
        Object[] pd02 = { "0cfbaafc0601000000", Description.PHASE, Arrays.asList(DataRecord.DescriptionExtension.PHASE_L2_L3), DlmsUnit.DEGREE, -1, 1L };
        Object[] pd03 = { "0cfbaafc0701000000", Description.PHASE, Arrays.asList(DataRecord.DescriptionExtension.PHASE_L3_L1), DlmsUnit.DEGREE, -1, 1L };

        Object[] pr01 = { "0ce83e01000000", Description.PRESSURE, Arrays.asList(DataRecord.DescriptionExtension.BASE_CONDITION), DlmsUnit.BAR, -3, 1L };
        Object[] pr02 = { "0ce8f33e01000000", Description.PRESSURE, Arrays.asList(DataRecord.DescriptionExtension.BASE_CONDITION), DlmsUnit.BAR, -6, 1L };
        Object[] pr03 = { "0c6801000000", Description.PRESSURE, Arrays.asList(), DlmsUnit.BAR, -3, 1L };
        Object[] pr04 = { "0ce87301000000", Description.PRESSURE, Arrays.asList(), DlmsUnit.BAR, -6, 1L };
        Object[] pr05 = { "0ce84801000000", Description.PRESSURE, Arrays.asList(DataRecord.DescriptionExtension.UPPER_LIMIT), DlmsUnit.BAR, -3, 1L };
        Object[] pr06 = { "0ce8c87301000000", Description.PRESSURE, Arrays.asList(DataRecord.DescriptionExtension.UPPER_LIMIT), DlmsUnit.BAR, -6, 1L };
        Object[] pr07 = { "0ce84001000000", Description.PRESSURE, Arrays.asList(DataRecord.DescriptionExtension.LOWER_LIMIT), DlmsUnit.BAR, -3, 1L };
        Object[] pr08 = { "0ce8c07301000000", Description.PRESSURE, Arrays.asList(DataRecord.DescriptionExtension.LOWER_LIMIT), DlmsUnit.BAR, -6, 1L };

        Object[] pw01 = { "0c2801000000", Description.POWER, Arrays.asList(), DlmsUnit.WATT, -3, 1L };
        Object[] pw03 = { "0ca83c01000000", Description.POWER, Arrays.asList(DataRecord.DescriptionExtension.NEGATIVE_ACCUMULATION), DlmsUnit.WATT, -3, 1L };
        // does "cum." in documentation mean MAX?
        Object[] pw04 = { "0cfb7801000000", Description.MAX_POWER, Arrays.asList(), DlmsUnit.WATT, -3, 1L };
        // does "cum." in documentation mean MAX?
        Object[] pw06 = { "0cfbf83c01000000", Description.MAX_POWER, Arrays.asList(DataRecord.DescriptionExtension.NEGATIVE_ACCUMULATION), DlmsUnit.WATT, -3, 1L };
        Object[] pw07 = { "0ca8fc1001000000", Description.POWER, Arrays.asList(DataRecord.DescriptionExtension.ABSOLUTE), DlmsUnit.WATT, -3, 1L };
        Object[] pw08 = { "0cfba8fc1001000000", Description.POWER, Arrays.asList(DataRecord.DescriptionExtension.ABSOLUTE), DlmsUnit.WATT, 5, 1L };
        Object[] pw09 = { "0ca8fc0c01000000", Description.POWER, Arrays.asList(DataRecord.DescriptionExtension.DELTA), DlmsUnit.WATT, -3, 1L };
        Object[] pw10 = { "0cfba8fc0c01000000", Description.POWER, Arrays.asList(DataRecord.DescriptionExtension.DELTA), DlmsUnit.WATT, 5, 1L };

        Object[] vf01 = { "0c3801000000", Description.VOLUME_FLOW, Arrays.asList(), DlmsUnit.CUBIC_METRE_PER_HOUR, -6, 1L };
        Object[] vf02 = { "0cb83a01000000", Description.VOLUME_FLOW, Arrays.asList(DataRecord.DescriptionExtension.UNCORRECTED_UNIT), DlmsUnit.CUBIC_METRE_PER_HOUR, -6, 1L };
        Object[] vf03 = { "0cb83e01000000", Description.VOLUME_FLOW, Arrays.asList(DataRecord.DescriptionExtension.BASE_CONDITION), DlmsUnit.CUBIC_METRE_PER_HOUR, -6, 1L };

        Object[] vm01 = { "0c1001000000", Description.VOLUME, Arrays.asList(), DlmsUnit.CUBIC_METRE, -6, 1L };
        Object[] vm02 = { "0c907d01000000", Description.VOLUME, Arrays.asList(), DlmsUnit.CUBIC_METRE, -3, 1L };
        Object[] vm03 = { "0c903a01000000", Description.VOLUME, Arrays.asList(DataRecord.DescriptionExtension.UNCORRECTED_UNIT), DlmsUnit.CUBIC_METRE, -6, 1L };
        Object[] vm04 = { "0c90fd3a01000000", Description.VOLUME, Arrays.asList(DataRecord.DescriptionExtension.UNCORRECTED_UNIT), DlmsUnit.CUBIC_METRE, -3, 1L };
        Object[] vm05 = { "0c903e01000000", Description.VOLUME, Arrays.asList(DataRecord.DescriptionExtension.BASE_CONDITION), DlmsUnit.CUBIC_METRE, -6, 1L };
        Object[] vm06 = { "0c90fd3e01000000", Description.VOLUME, Arrays.asList(DataRecord.DescriptionExtension.BASE_CONDITION), DlmsUnit.CUBIC_METRE, -3, 1L };
        Object[] vm07 = { "0c903b01000000", Description.VOLUME, Arrays.asList(DataRecord.DescriptionExtension.POSITIVE_ACCUMULATION), DlmsUnit.CUBIC_METRE, -6, 1L };
        Object[] vm08 = { "0c90fd3b01000000", Description.VOLUME, Arrays.asList(DataRecord.DescriptionExtension.POSITIVE_ACCUMULATION), DlmsUnit.CUBIC_METRE, -3, 1L };
        Object[] vm09 = { "0c903c01000000", Description.VOLUME, Arrays.asList(DataRecord.DescriptionExtension.NEGATIVE_ACCUMULATION), DlmsUnit.CUBIC_METRE, -6, 1L };
        Object[] vm10 = { "0c90fd3c01000000", Description.VOLUME, Arrays.asList(DataRecord.DescriptionExtension.NEGATIVE_ACCUMULATION), DlmsUnit.CUBIC_METRE, -3, 1L };

        Object[] vv01 = { "0cfdc0fc0101000000", Description.VOLTAGE, Arrays.asList(DataRecord.DescriptionExtension.PHASE_L1), DlmsUnit.VOLT, -9, 1L };
        Object[] vv02 = { "0cfdc0fc0201000000", Description.VOLTAGE, Arrays.asList(DataRecord.DescriptionExtension.PHASE_L2), DlmsUnit.VOLT, -9, 1L };
        Object[] vv03 = { "0cfdc0fc0301000000", Description.VOLTAGE, Arrays.asList(DataRecord.DescriptionExtension.PHASE_L3), DlmsUnit.VOLT, -9, 1L };

        return new Object[] { ca01, ca02, ca03, ca04, ej01, ej02, ej03, ej04, ej05, ej06, ew01, ew02, ew03, ew04, ew05, ew06, ew07, ew08, ew09, pd01, pd02, pd03, pr01, pr02, pr03, pr04, pr05, pr06, pr07, pr08, pw01, pw03, pw04, pw06, pw07, pw08, pw09, pw10, vf01, vf02, vf03, vm01, vm02, vm03, vm04, vm05, vm06, vm07, vm08, vm09, vm10, vv01, vv02, vv03};
    }
    @Test
    @Parameters(method = "testDescriptionExtensionValues")
    public void testDescriptionExtensions(String bytesStr, Description desc, List<DataRecord.DescriptionExtension> extensions, DlmsUnit unit, int scaler, Long data)
            throws DecodingException {
        byte[] bytes = HexUtils.hexToBytes(bytesStr);

        DataRecord dataRecord = new DataRecord();
        dataRecord.decode(bytes, 0);

        assertEquals(desc, dataRecord.getDescription());
        assertEquals(extensions, dataRecord.getDescriptionExtensions());
        assertEquals(unit, dataRecord.getUnit());
        assertEquals(scaler, dataRecord.getMultiplierExponent());
        Object dataValue = ((Number)dataRecord.getDataValue()).longValue();
        assertEquals(data, dataValue);
    }
}
