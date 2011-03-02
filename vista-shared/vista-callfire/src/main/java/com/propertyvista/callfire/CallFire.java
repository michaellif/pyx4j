/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 28, 2011
 * @author Mark Levitin
 * @version $Id$
 */
package com.propertyvista.callfire;

import java.util.List;

import com.propertyvista.callfire.impl.RequestExecutor;

public class CallFire {

    private static final String NOTIFICATION_CAMPAIGN_TEMPLATE = "<dialplan name=\"Root\"><play name=\"play\" type=\"tts\"  voice=\"male1\">%s</play></dialplan>";

    /**
     * Creates simple notification campaign.
     * 
     * @param message
     *            campaign message. May include call parameters in a form ${X} where ${b}
     *            stays for 1st parameter, ${c} for 2nd, ${d} for 3rd and so on.
     *            For example: Hello. Your first name is ${b} and your second name is ${c}
     * @param caller
     *            caller number. Is used for defining caller id during calls.
     * 
     * @return campaign id if successful or null otherwise.
     */
    public static String createNotificationCampaign(String message, String caller) {
        String message_with_parameters = message.replaceAll("\\$\\{(.)\\}", "\\$\\{call.field.$1\\}");
        String definition = String.format(NOTIFICATION_CAMPAIGN_TEMPLATE, message_with_parameters);
        try {
            return RequestExecutor.createCampaign(definition, caller);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Initiates calls for given campaign.
     * 
     * @param campaignid
     *            campaign id as returned by create campaign method.
     * @param numbers
     *            list of numbers and parameters for the campaign message in a form:
     *            number,param_b,param_c,param_d,...
     *            For example: if campaign message contains first and second names as
     *            parameters, the list should look like that:
     *            1111111111,John,Smith
     *            2222222222,James,Brown
     *            ...
     * 
     * @return campaign id if successful or null otherwise.
     */
    public static boolean sendCalls(String campaignid, List<String> numbers) {
        String numbers_as_text = "";
        for (String number : numbers) {
            numbers_as_text += number + "\n";
        }
        try {
            return RequestExecutor.sendCalls(campaignid, numbers_as_text);
        } catch (Exception e) {
            return false;
        }
    }
}
