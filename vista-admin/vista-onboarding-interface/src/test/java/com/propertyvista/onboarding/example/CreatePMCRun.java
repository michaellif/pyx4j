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

import com.propertyvista.onboarding.example.model.AccountInfoResponse;
import com.propertyvista.onboarding.example.model.CreateOnboardingUserRequest;
import com.propertyvista.onboarding.example.model.CreatePMCRequest;
import com.propertyvista.onboarding.example.model.RequestMessage;
import com.propertyvista.onboarding.example.model.ResponseMessage;
import com.propertyvista.onboarding.example.utils.ExampleClient;

public class CreatePMCRun {

    public static void main(String[] args) throws Exception {

        CreateOnboardingUserRequest user = new CreateOnboardingUserRequest();
        user.email = String.valueOf(System.currentTimeMillis()) + "@test.com";
        user.password = "pwd~" + String.valueOf(System.currentTimeMillis());
        user.name = "Bob " + new Date().toString();
        user.onboardingAccountId = "acc" + System.nanoTime();
        user.requestId = "CreateOnboardingUserRequest";

        {
            RequestMessage rm = new RequestMessage();
            rm.interfaceEntity = "rossul";
            rm.interfaceEntityPassword = "secret";
            rm.addRequest(user);

            ResponseMessage response = ExampleClient.execute(rm);

            System.out.println("response Status   : " + response.status);
            System.out.println("response Message  : " + response.errorMessage);

            if (response.status == ResponseMessage.StatusCode.OK) {
                System.out.println("echo requestId    : " + response.responses.get(0).requestId);
                System.out.println("response Code     : " + response.responses.get(0).success);
                System.out.println("response Message  : " + response.responses.get(0).errorMessage);
            }
        }

        CreatePMCRequest r = new CreatePMCRequest();
        r.requestId = "TestCreatePMCRun";
        r.dnsName = "testcomp" + String.valueOf(System.currentTimeMillis());
        //r.dnsNameAliases = Arrays.asList(new String[] { "www.rossul.com", "www.rossul.ca" });
        r.name = "Apartments & Co" + String.valueOf(System.currentTimeMillis());
        r.onboardingAccountId = user.onboardingAccountId;
        r.requestId = "CreatePMCRequest";

        {
            RequestMessage rm = new RequestMessage();
            rm.interfaceEntity = "rossul";
            rm.interfaceEntityPassword = "secret";
            rm.addRequest(r);

            ResponseMessage response = ExampleClient.execute(rm);

            System.out.println("response Status   : " + response.status);
            System.out.println("response Message  : " + response.errorMessage);

            if (response.status == ResponseMessage.StatusCode.OK) {
                System.out.println("echo requestId    : " + response.responses.get(0).requestId);
                System.out.println("response Code     : " + response.responses.get(0).success);
                System.out.println("response Message  : " + response.responses.get(0).errorMessage);

                if (response.responses.get(0) instanceof AccountInfoResponse) {
                    AccountInfoResponse resp = (AccountInfoResponse) response.responses.get(0);
                    System.out.println("vistaCrmUrl : " + resp.vistaCrmUrl);
                    System.out.println("residentPortalUrl : " + resp.residentPortalUrl);
                    System.out.println("prospectPortalUrl : " + resp.prospectPortalUrl);
                }
            }
        }
    }
}
