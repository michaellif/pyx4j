/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 1, 2011
 * @author Mark Levitin
 * @version $Id$
 */
package com.propertyvista.callfire;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class CallFireTest {

    public void testNotificationCampaign() {
        String message = "Hello ${b} ${c}. How are you today";
        String caller = "14167275409";
        String campaignid = CallFire.createNotificationCampaign(message, caller);
        assertNotNull(campaignid);
        List<String> numbers = new ArrayList<String>();
        //numbers.add("14167275409,Mark,Levitin");
        assertTrue(CallFire.sendCalls(campaignid, numbers));
    }

}
