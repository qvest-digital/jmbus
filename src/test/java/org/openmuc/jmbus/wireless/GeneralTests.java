/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openmuc.jmbus.wireless;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmuc.jmbus.DataRecord;
import org.openmuc.jmbus.DataRecord.Description;
import org.openmuc.jmbus.HexUtils;
import org.openmuc.jmbus.SecondaryAddress;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class GeneralTests {
    public Object testData() {
        Object[] p1 = { "2644333003000000011B72030000003330011B542000002F2F02FD1701002F2F2F2F2F2F2F2F2F80", "LAS", 1.0,
                DataRecord.Description.ERROR_FLAGS, 0, DataRecord.FunctionField.INST_VAL, "" };

        Object[] p2 = { "2C44A7320613996707047A2A1000202F2F0C06000000000C14000000000C22381701000B5A1702000B5E1702006E",
                "LUG", 0.0, DataRecord.Description.ENERGY, 0, DataRecord.FunctionField.INST_VAL, "" };

        Object[] p3 = { "6044961599030023021B7A2B0000202F2F0265A5FA426566FA8201"
                + "655FFA22650DFA1265C9FA6265FBF95265F2FA02FB1ACC0242FB1A0B038201FB1A040322FB1A850212FB1A270362FB1A810252FB"
                + "1A380302FD1B30830DFD0F05312E302E310F", "ELV", -13.71, DataRecord.Description.EXTERNAL_TEMPERATURE, 0,
                DataRecord.FunctionField.INST_VAL, "" };

        Object[] p4 = { "B244C5149706009503077239099923C514000765009025BAEDCE696A8CB53A2190E402C4AFEBD8865EB6EE"
                + "B651C9A7F6E912378F2554B16E6C6BAD76F16AD3CE859C1DB33FC34365723CDCEC9E3B6975BAD2B32E7E5D0B897AE945580CEBBE"
                + "E64CCAF68A176D59FC40CC023F46360131ED3BF135A78E2EE7FA86B512767F47E9FB2C603045A4D34AA6320BD8D30C7DDBDADD85"
                + "FFFDCDC30CACF855D48BB99873F192FCD097A2AB03FD0C02032102FD0B111112", "EFE", 0.,
                DataRecord.Description.VOLUME, 1, DataRecord.FunctionField.INST_VAL,
                "51728910E66D83F851728910E66D83F8" };

        Object[] p5 = {
                "47447916618151614037724901276179161102b200300595f1a8f30fb5e27866ab03df28"
                        + "3487a4f4de7217ee3abfb11191c258cb15513ab523860a510dedf5475b4fecda5917a212",
                "ESY", 13557.93, DataRecord.Description.POWER, 1, DataRecord.FunctionField.INST_VAL,
                "7840A86FF6C79266DE07A879C4373BB2" };

        return new Object[] { p1, p2, p3, p4, p5 };
    }

    @Test
    @Parameters(method = "testData")
    public void test(String lexicalXSDHexBinary, String expectedManId, double expectedDataValue,
            Description expectedDesc, int dataRecordsIndex, DataRecord.FunctionField expectedFunctionField, String key)
            throws Exception {
        byte[] buffer = HexUtils.hexToBytes(lexicalXSDHexBinary);
        byte[] securityKey = HexUtils.hexToBytes(key);

        SecondaryAddress secondaryAddress = SecondaryAddress.newFromLongHeader(buffer, 2);
        HashMap<SecondaryAddress, byte[]> keyMap = new HashMap<>();
        keyMap.put(secondaryAddress, securityKey);

        WMBusMessage wmBusDataMessage = WMBusMessage.decode(buffer, 0, keyMap);
        wmBusDataMessage.getVariableDataResponse().decode();
        System.out.println("##################");
        System.out.println(wmBusDataMessage);
        // System.out.println(HexUtils.bytesToHex(wmBusDataMessage.asBlob()));

        DataRecord dataRecord = wmBusDataMessage.getVariableDataResponse().getDataRecords().get(dataRecordsIndex);

        assertEquals(expectedManId, wmBusDataMessage.getSecondaryAddress().getManufacturerId());
        assertEquals(expectedFunctionField, dataRecord.getFunctionField());
        assertEquals(expectedDataValue, dataRecord.getScaledDataValue(), 0.001);
        assertEquals(expectedDesc, dataRecord.getDescription());

    }
}
