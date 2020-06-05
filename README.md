# jMBus

jMBus is an implementation of the M-Bus wired and wireless protocols. You can use it to program individual M-Bus wired masters or wireless M-Bus receivers that read meters such as gas, water, heat, or electricity meters.

jMBus is licensed under the MPL v2.0 (Mozilla Public License).

For M-Bus wired communication the library communicates over a serial port, USB port or TCP with an M-Bus master/level-converter device. We have successfully tested the library with level-converters from Relay and Techbase.

For wireless M-Bus communication the library requires a transceiver usually connected via USB. Currently jMBus support transceivers from Amber, Radiocrafts and IMST. The library was tested with the AMB8465-M from Amber, the RC1180-MBUS from Radiocrafts and the iM871A-USB from IMST. The jMBus library only supports passive listening for messages in modes S, T and C at the moment.

Our library is used by OpenEMS and OGEMA, among others.

Authors: Dirk Zimmermann

Alumni: Stefan Feuerhahn, Michael Zillgith

Original website: https://www.openmuc.org/m-bus/

## [tarent](http://tarent.de) fork

We implemented Encryption Mode 7, along with additional necessary header parsing.

