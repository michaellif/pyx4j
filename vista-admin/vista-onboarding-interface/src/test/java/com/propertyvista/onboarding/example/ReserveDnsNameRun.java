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

import com.propertyvista.onboarding.example.model.RequestMessage;
import com.propertyvista.onboarding.example.model.ReserveDnsNameRequest;
import com.propertyvista.onboarding.example.model.ResponseMessage;
import com.propertyvista.onboarding.example.utils.ExampleClient;

public class ReserveDnsNameRun {

    public static void main(String[] args) throws Exception {
        ReserveDnsNameRequest r = new ReserveDnsNameRequest();
        r.requestId = "TestCreatePMCRun";
        r.dnsName = "test" + String.valueOf(System.currentTimeMillis());
        r.onboardingAccountId = "TestAccId" + String.valueOf(System.currentTimeMillis());
        r.requestRemoteAddr = "1.1.1.1";

        {
            RequestMessage rm = new RequestMessage();
            rm.interfaceEntity = ExampleClient.interfaceEntity;
            rm.interfaceEntityPassword = ExampleClient.interfaceEntityPassword;
            rm.addRequest(r);

            ResponseMessage response = ExampleClient.execute(rm);

            System.out.println("response Status   : " + response.status);
            System.out.println("response Message  : " + response.errorMessage);

            if (response.status == ResponseMessage.StatusCode.OK) {
                System.out.println("echo requestId    : " + response.responses.get(0).requestId);
                System.out.println("response Code     : " + response.responses.get(0).success);
                System.out.println("response Message  : " + response.responses.get(0).errorMessage);
            } else {
                throw new AssertionError(response.status);
            }
        }
    }
}
