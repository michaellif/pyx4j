/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 15, 2012
 * @author igor
 * @version $Id$
 */
package com.propertyvista.onboarding.example;

import com.propertyvista.onboarding.example.model.ApproveBankAccountInfoRequest;
import com.propertyvista.onboarding.example.model.BankAccountInfoApproval;
import com.propertyvista.onboarding.example.model.RequestMessage;
import com.propertyvista.onboarding.example.model.ResponseMessage;
import com.propertyvista.onboarding.example.utils.ExampleClient;

public class UpdateBankAccountInfoRun {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        int cnt = 2;
        int num = 0;

//        UpdateBankAccountInfoRequest request = new UpdateBankAccountInfoRequest();
//        request.requestRemoteAddr = "1.1.1.1";
//        request.onboardingAccountId = "2";
//
//        {
//            BankAccountInfo bai = new BankAccountInfo();
//            bai.onboardingBankAccountId = "onb_003";
//            bai.bankId = "8";
//            bai.branchTransitNumber = "98765";
//            bai.accountNumber = "258963";
//            bai.chargeDescription = "Pay for PMC 1-5";
//            request.accounts.add(bai);
//        }
//
//        {
//            BankAccountInfo bai = new BankAccountInfo();
//            bai.onboardingBankAccountId = "onb_004";
//            bai.bankId = "9";
//            bai.branchTransitNumber = "54874";
//            bai.accountNumber = "123456987123";
//            bai.chargeDescription = "Pay for PMC 1-5";
//
//            request.accounts.add(bai);
//        }
//
//        {
//            RequestMessage rm = new RequestMessage();
//            rm.interfaceEntity = ExampleClient.interfaceEntity;
//            rm.interfaceEntityPassword = ExampleClient.interfaceEntityPassword;
//
//            rm.addRequest(request);
//
//            ResponseMessage response = ExampleClient.execute(rm);
//
//            System.out.println("response Status   : " + response.status);
//            System.out.println("response Message  : " + response.errorMessage);
//
//            if (response.status == ResponseMessage.StatusCode.OK) {
//                System.out.println("echo requestId    : " + response.responses.get(0).requestId);
//                System.out.println("response Code     : " + response.responses.get(0).success);
//                System.out.println("response Message  : " + response.responses.get(0).errorMessage);
//            }
//        }

        ApproveBankAccountInfoRequest requestA = new ApproveBankAccountInfoRequest();
        requestA.requestRemoteAddr = "1.1.1.1";
        requestA.onboardingAccountId = "2";

        {
            BankAccountInfoApproval bai = new BankAccountInfoApproval();
            bai.onboardingBankAccountId = "onb_003";
            bai.terminalId = "AAAAAA";
            bai.bankId = "8";
            bai.branchTransitNumber = "98765";
            bai.accountNumber = "258963";
            bai.chargeDescription = "Pay for PMC 1-5";
            requestA.accounts.add(bai);
        }

        {
            BankAccountInfoApproval bai = new BankAccountInfoApproval();
            bai.onboardingBankAccountId = "onb_004";
            bai.terminalId = "BBBBBB";
            bai.bankId = "9";
            bai.branchTransitNumber = "54874";
            bai.accountNumber = "123456987123";
            bai.chargeDescription = "Pay for PMC 1-5";

            requestA.accounts.add(bai);
        }

        {
            RequestMessage rm = new RequestMessage();
            rm.interfaceEntity = ExampleClient.interfaceEntity;
            rm.interfaceEntityPassword = ExampleClient.interfaceEntityPassword;

            rm.addRequest(requestA);

            ResponseMessage response = ExampleClient.execute(rm);

            System.out.println("response Status   : " + response.status);
            System.out.println("response Message  : " + response.errorMessage);

            if (response.status == ResponseMessage.StatusCode.OK) {
                System.out.println("echo requestId    : " + response.responses.get(0).requestId);
                System.out.println("response Code     : " + response.responses.get(0).success);
                System.out.println("response Message  : " + response.responses.get(0).errorMessage);
            }
        }

    }

}
