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

import com.propertyvista.onboarding.UsageType;
import com.propertyvista.onboarding.example.model.GetUsageRequest;
import com.propertyvista.onboarding.example.model.RequestMessage;
import com.propertyvista.onboarding.example.model.ResponseMessage;
import com.propertyvista.onboarding.example.model.UsageRecord;
import com.propertyvista.onboarding.example.model.UsageReportResponse;
import com.propertyvista.onboarding.example.utils.ExampleClient;

public class GetUsageRun {

    public static void main(String[] args) throws Exception {

        GetUsageRequest request = new GetUsageRequest();
        request.usageType = UsageType.BuildingUnitCount;
        request.onboardingAccountId = "acc1338253812822304000";
        request.requestId = "GetUsageRequest";

        {
            RequestMessage rm = new RequestMessage();
            rm.interfaceEntity = ExampleClient.interfaceEntity;
            rm.interfaceEntityPassword = ExampleClient.interfaceEntityPassword;
            rm.addRequest(request);

            ResponseMessage response = ExampleClient.execute(rm);

            System.out.println("response Status   : " + response.status);
            System.out.println("response Message  : " + response.errorMessage);

            if (response.status == ResponseMessage.StatusCode.OK) {
                System.out.println("echo requestId    : " + response.responses.get(0).requestId);
                System.out.println("response Code     : " + response.responses.get(0).success);
                System.out.println("response Message  : " + response.responses.get(0).errorMessage);

                if (response.responses.get(0) instanceof UsageReportResponse) {
                    UsageReportResponse ur = (UsageReportResponse) response.responses.get(0);

                    for (UsageRecord rec : ur.records) {
                        System.out.println("Building : " + rec.text);
                        System.out.println("Units # : " + rec.value);
                    }
                }
            }
        }

        request.usageType = UsageType.Equifax;
        request.onboardingAccountId = "acc1338253812822304000";
        request.requestId = "GetUsageRequest";

        {
            RequestMessage rm = new RequestMessage();
            rm.interfaceEntity = ExampleClient.interfaceEntity;
            rm.interfaceEntityPassword = ExampleClient.interfaceEntityPassword;
            rm.addRequest(request);

            ResponseMessage response = ExampleClient.execute(rm);

            System.out.println("response Status   : " + response.status);
            System.out.println("response Message  : " + response.errorMessage);

            if (response.status == ResponseMessage.StatusCode.OK) {
                System.out.println("echo requestId    : " + response.responses.get(0).requestId);
                System.out.println("response Code     : " + response.responses.get(0).success);
                System.out.println("response Message  : " + response.responses.get(0).errorMessage);

                if (response.responses.get(0) instanceof UsageReportResponse) {
                    UsageReportResponse ur = (UsageReportResponse) response.responses.get(0);

                    for (UsageRecord rec : ur.records) {
                        System.out.println("Equifax  usage : " + rec.value);
                    }
                }
            }
        }

    }
}
