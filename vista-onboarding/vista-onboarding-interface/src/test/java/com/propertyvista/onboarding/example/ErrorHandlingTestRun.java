/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-03-06
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.onboarding.example;

import java.util.Date;

import junit.framework.Assert;

import com.propertyvista.onboarding.example.model.CreateOnboardingUserRequest;
import com.propertyvista.onboarding.example.model.RequestMessage;
import com.propertyvista.onboarding.example.model.ReserveDnsNameRequest;
import com.propertyvista.onboarding.example.model.ResponseMessage;
import com.propertyvista.onboarding.example.utils.ExampleClient;

public class ErrorHandlingTestRun {

    public static void main(String[] args) throws Exception {

        CreateOnboardingUserRequest user = new CreateOnboardingUserRequest();
        // Missing filed
        //user.email = String.valueOf(System.currentTimeMillis()) + "@test.com";
        user.password = "pwd~" + String.valueOf(System.currentTimeMillis());
        user.firstName = "Bob " + new Date().toString();
        user.lastName = "McAdams";
        user.onboardingAccountId = "acc" + System.nanoTime();
        user.requestId = "CreateOnboardingUserRequest";
        user.requestRemoteAddr = "1.1.1.1";

        {
            RequestMessage rm = new RequestMessage();
            rm.interfaceEntity = ExampleClient.interfaceEntity;
            rm.interfaceEntityPassword = ExampleClient.interfaceEntityPassword;
            rm.addRequest(user);

            ResponseMessage response = ExampleClient.execute(rm);

            System.out.println("response Status   : " + response.status);
            System.out.println("response Message  : " + response.errorMessage);

            Assert.assertEquals("status", ResponseMessage.StatusCode.MessageFormatError, response.status);
            Assert.assertNull("responses", response.responses);
        }

        ReserveDnsNameRequest resDnsNamereq = new ReserveDnsNameRequest();
        resDnsNamereq.requestId = "TestCreatePMCRun";
        resDnsNamereq.dnsName = "welcome";
        resDnsNamereq.onboardingAccountId = user.onboardingAccountId;
        resDnsNamereq.requestRemoteAddr = "1.1.1.1";

        {
            RequestMessage rm = new RequestMessage();
            rm.interfaceEntity = ExampleClient.interfaceEntity;
            rm.interfaceEntityPassword = ExampleClient.interfaceEntityPassword;

            rm.addRequest(resDnsNamereq);

            ResponseMessage response = ExampleClient.execute(rm);

            System.out.println("response Status   : " + response.status);
            System.out.println("response Message  : " + response.errorMessage);

            Assert.assertEquals("status", ResponseMessage.StatusCode.OK, response.status);
            Assert.assertEquals("responses", 1, response.responses.size());

            System.out.println("echo requestId    : " + response.responses.get(0).requestId);
            System.out.println("response Code     : " + response.responses.get(0).success);
            System.out.println("response Message  : " + response.responses.get(0).errorMessage);

            Assert.assertEquals("response.success", false, response.responses.get(0).success);
        }

    }
}
