package org.openmuc.jmbus.app;

import org.openmuc.jmbus.HexUtils;
import org.openmuc.jmbus.internal.cli.FlagCliParameter;

public class CliPrinter {

    private final String usage;
    private final FlagCliParameter printVerboseMsg;

    public CliPrinter(String usage, FlagCliParameter verbose) {
        this.usage = usage;
        this.printVerboseMsg = verbose;
    }

    /**
     * Print error message without usage printout
     * 
     * @param errMsg
     *            error message to print
     */
    public void printError(String errMsg) {
        printError(errMsg, false);
    }

    /**
     * Print error message with optional usage printout
     * 
     * @param errMsg
     *            error message to print
     * @param printUsage
     *            prints usage if true
     */
    public void printError(String errMsg, boolean printUsage) {
        System.err.println("Error: " + errMsg + '\n');
        if (printUsage) {
            System.err.flush();

            System.out.println(usage);
        }
        System.exit(1);
    }

    public void printlnDebug(Object... msg) {
        if (!printVerboseMsg.isSelected()) {
            return;
        }
        println(msg);
    }

    public void printInfo(Object... msg) {
        print(msg);
    }

    public void printlnInfo(Object... msg) {
        println(msg);
    }

    private String msgToString(Object[] msg) {
        StringBuilder sb = new StringBuilder();
        for (Object message : msg) {
            if (message instanceof byte[]) {
                sb.append(HexUtils.bytesToHex((byte[]) message));
            }
            else {
                sb.append(message);
            }
        }
        return sb.toString();
    }

    private void println(Object[] msg) {
        System.out.println(msgToString(msg));
    }

    private void print(Object[] msg) {
        System.out.print(msgToString(msg));
    }

}
