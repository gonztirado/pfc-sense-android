package com.celulabs.pfcsense.ble.common;

import java.util.HashMap;
import java.util.Map;

public class HCIDefines {
    public static final Map<Integer,String> hciErrorCodeStrings;
    static
    {
        hciErrorCodeStrings = new HashMap<Integer,String>();
        hciErrorCodeStrings.put(0x01,"Unknown HCI Command");
        hciErrorCodeStrings.put(0x02,"Unknown Connection Identifier");
        hciErrorCodeStrings.put(0x03,"Hardware Failure");
        hciErrorCodeStrings.put(0x04,"Page Timeout");
        hciErrorCodeStrings.put(0x05,"Authentication Failure");
        hciErrorCodeStrings.put(0x06,"Pin or Key Missing");
        hciErrorCodeStrings.put(0x07,"Memory Capacity Exceeded");
        hciErrorCodeStrings.put(0x08,"Connnection Timeout");
        hciErrorCodeStrings.put(0x09,"Connection Limit Exceeded");
        hciErrorCodeStrings.put(0x0A,"Synchronous Connection Limit To A Device Exceeded");
        hciErrorCodeStrings.put(0x0B,"ACL Connection Already Exists");
        hciErrorCodeStrings.put(0x0C,"Command Disallowed");
        hciErrorCodeStrings.put(0x0D,"Connected Rejected Due To Limited Resources");
        hciErrorCodeStrings.put(0x0E,"Connection Rejected Due To Security Reasons");
        hciErrorCodeStrings.put(0x0F,"Connection Rejected Due To Unacceptable BD_ADDR");
        hciErrorCodeStrings.put(0x10,"Connection Accept Timeout Exceeded");
        hciErrorCodeStrings.put(0x11,"Unsupported Feature Or Parameter Value");
        hciErrorCodeStrings.put(0x12,"Invalid HCI Command Parameters");
        hciErrorCodeStrings.put(0x13,"Remote User Terminated Connection");
        hciErrorCodeStrings.put(0x14,"Remote Device Terminated Connection Due To Low Resources");
        hciErrorCodeStrings.put(0x15,"Remote Device Terminated Connection Due To Power Off");
        hciErrorCodeStrings.put(0x16,"Connection Terminated By Local Host");
        hciErrorCodeStrings.put(0x17,"Repeated Attempts");
        hciErrorCodeStrings.put(0x18,"Pairing Not Allowed");
        hciErrorCodeStrings.put(0x19,"Unknown LMP PDU");
        hciErrorCodeStrings.put(0x1A,"Unsupported Remote Feature / Unsupported LMP Feature");
        hciErrorCodeStrings.put(0x1B,"SCO Offset Rejected");
        hciErrorCodeStrings.put(0x1C,"SCO Interval Rejected");
        hciErrorCodeStrings.put(0x1D,"SCO Air Mode Rejected");
        hciErrorCodeStrings.put(0x1E,"Invalid LMP Parameters / Invalid LL Parameters");
        hciErrorCodeStrings.put(0x1F,"Unspecified Error");
        hciErrorCodeStrings.put(0x20,"Unsupported LMP Parameter Value / Unsupported LL Parameter Value");
        hciErrorCodeStrings.put(0x21,"Role Change Not Allowed");
        hciErrorCodeStrings.put(0x22,"LMP Response Timeout / LL Response Timeout");
        hciErrorCodeStrings.put(0x23,"LMP Error Transaction Collision");
        hciErrorCodeStrings.put(0x24,"LMP PDU Not Allowed");
        hciErrorCodeStrings.put(0x25,"Encryption Mode Not Acceptable");
        hciErrorCodeStrings.put(0x26,"Link Key Cannot Be Changed");
        hciErrorCodeStrings.put(0x27,"Requested QoS Not Supported");
        hciErrorCodeStrings.put(0x28,"Instant Passed");
        hciErrorCodeStrings.put(0x29,"Pairing With Unit Key Not Supported");
        hciErrorCodeStrings.put(0x2A,"Differemt Tramsaction Collision");
        hciErrorCodeStrings.put(0x2C,"QoS Unacceptable Parameter");
        hciErrorCodeStrings.put(0x2D,"QoS Rejected");
        hciErrorCodeStrings.put(0x2E,"Channel Assessment Not Supported");
        hciErrorCodeStrings.put(0x2F,"Insufficient Security");
        hciErrorCodeStrings.put(0x30,"Parameter Out of Mandatory Range");
        hciErrorCodeStrings.put(0x32,"Role Switch Pending");
        hciErrorCodeStrings.put(0x34,"Reserved Slot Violation");
        hciErrorCodeStrings.put(0x35,"Role Switch Failed");
        hciErrorCodeStrings.put(0x36,"Extended Inquiry Response Too Large");
        hciErrorCodeStrings.put(0x37,"Simple Pairing Not Supported By Host");
        hciErrorCodeStrings.put(0x38,"Host Busy-Pairing");
        hciErrorCodeStrings.put(0x39,"Connection Rejected Due To No Suitable Channel Found");
        hciErrorCodeStrings.put(0x3A,"Controller Busy");
        hciErrorCodeStrings.put(0x3B,"Unacceptable Connection Parameters");
        hciErrorCodeStrings.put(0x3C,"Directed Advertising Timeout");
        hciErrorCodeStrings.put(0x3D,"Connection Terminated Due To MIC Failure");
        hciErrorCodeStrings.put(0x3E,"Connection Failed To Be Established");
        hciErrorCodeStrings.put(0x3F,"MAC Connection Failed");
        hciErrorCodeStrings.put(0x40,"Coarse Clock Adjustment Rejected But Will Try to Adjust Using Clock Dragging");


    }
}
