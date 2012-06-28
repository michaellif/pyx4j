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
import com.propertyvista.onboarding.example.model.GetSatisfactionFastpassUrlRequest;
import com.propertyvista.onboarding.example.model.GetSatisfactionFastpassUrlResponse;
import com.propertyvista.onboarding.example.model.OnboardingUserAuthenticationRequest;
import com.propertyvista.onboarding.example.model.OnboardingUserAuthenticationResponse;
import com.propertyvista.onboarding.example.model.RequestMessage;
import com.propertyvista.onboarding.example.model.ReserveDnsNameRequest;
import com.propertyvista.onboarding.example.model.ResponseMessage;
import com.propertyvista.onboarding.example.utils.ExampleClient;

public class CreatePMCRun {

    public static void main(String[] args) throws Exception {

        CreateOnboardingUserRequest user = new CreateOnboardingUserRequest();
        user.email = String.valueOf(System.currentTimeMillis()) + "@test.com";
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

            if (response.status == ResponseMessage.StatusCode.OK) {
                System.out.println("echo requestId    : " + response.responses.get(0).requestId);
                System.out.println("response Code     : " + response.responses.get(0).success);
                System.out.println("response Message  : " + response.responses.get(0).errorMessage);
            } else {
                throw new AssertionError(response.status);
            }
        }

        ReserveDnsNameRequest resDnsNamereq = new ReserveDnsNameRequest();
        resDnsNamereq.requestId = "TestCreatePMCRun";
        resDnsNamereq.dnsName = "test" + String.valueOf(System.currentTimeMillis());
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

            if (response.status == ResponseMessage.StatusCode.OK) {
                System.out.println("echo requestId    : " + response.responses.get(0).requestId);
                System.out.println("response Code     : " + response.responses.get(0).success);
                System.out.println("response Message  : " + response.responses.get(0).errorMessage);
            } else {
                throw new AssertionError(response.status);
            }
        }

        CreatePMCRequest r = new CreatePMCRequest();
        r.requestId = "TestCreatePMCRun";
        r.dnsName = resDnsNamereq.dnsName;
        //r.dnsNameAliases = Arrays.asList(new String[] { "www.rossul.com", "www.rossul.ca" });
        r.name = "Apartments & Co " + String.valueOf(System.currentTimeMillis());
        r.onboardingAccountId = resDnsNamereq.onboardingAccountId;
//resDnsNamereq.onboardingAccountId;
        r.requestId = "CreatePMCRequest";
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

                if (response.responses.get(0) instanceof AccountInfoResponse) {
                    AccountInfoResponse resp = (AccountInfoResponse) response.responses.get(0);
                    System.out.println("vistaCrmUrl : " + resp.vistaCrmUrl);
                    System.out.println("residentPortalUrl : " + resp.residentPortalUrl);
                    System.out.println("prospectPortalUrl : " + resp.prospectPortalUrl);
                }
            } else {
                throw new AssertionError(response.status);
            }
        }

        /**
         * Authenticate using this user
         */
        OnboardingUserAuthenticationRequest authRequest = new OnboardingUserAuthenticationRequest();
        authRequest.requestRemoteAddr = "1.1.1.1";
        {
            authRequest.email = user.email;
            authRequest.password = user.password;
//            authRequest.captcha = new Captcha();
//            authRequest.captcha.challenge = "123";
//            authRequest.captcha.response = "rr";

            RequestMessage rm = new RequestMessage();
            rm.interfaceEntity = ExampleClient.interfaceEntity;
            rm.interfaceEntityPassword = ExampleClient.interfaceEntityPassword;
            rm.addRequest(authRequest);

            ResponseMessage response = ExampleClient.execute(rm);

            System.out.println("response Status   : " + response.status);
            System.out.println("response Message  : " + response.errorMessage);

            if (response.status == ResponseMessage.StatusCode.OK) {
                System.out.println("echo requestId    : " + response.responses.get(0).requestId);
                System.out.println("response Code     : " + response.responses.get(0).success);
                System.out.println("response Message  : " + response.responses.get(0).errorMessage);
                if (response.responses.get(0) instanceof OnboardingUserAuthenticationResponse) {
                    OnboardingUserAuthenticationResponse authResponse = (OnboardingUserAuthenticationResponse) response.responses.get(0);
                    System.out.println("Authentication status : " + authResponse.status);
                    System.out.println("Onboarding Account Id : " + authResponse.onboardingAccountId);
                }
            } else {
                throw new AssertionError(response.status);
            }
        }

        GetSatisfactionFastpassUrlRequest gsur = new GetSatisfactionFastpassUrlRequest();
        gsur.requestRemoteAddr = "1.1.1.1";
        {
            gsur.email = user.email;
            gsur.onboardingAccountId = user.onboardingAccountId;
            gsur.secureUrl = Boolean.TRUE;
//            authRequest.captcha = new Captcha();
//            authRequest.captcha.challenge = "123";
//            authRequest.captcha.response = "rr";

            RequestMessage rm = new RequestMessage();
            rm.interfaceEntity = ExampleClient.interfaceEntity;
            rm.interfaceEntityPassword = ExampleClient.interfaceEntityPassword;
            rm.addRequest(gsur);

            ResponseMessage response = ExampleClient.execute(rm);

            System.out.println("response Status   : " + response.status);
            System.out.println("response Message  : " + response.errorMessage);

            if (response.status == ResponseMessage.StatusCode.OK) {
                System.out.println("echo requestId    : " + response.responses.get(0).requestId);
                System.out.println("response Code     : " + response.responses.get(0).success);
                System.out.println("response Message  : " + response.responses.get(0).errorMessage);

                if (response.responses.get(0) instanceof GetSatisfactionFastpassUrlResponse) {
                    GetSatisfactionFastpassUrlResponse resp = (GetSatisfactionFastpassUrlResponse) response.responses.get(0);
                    System.out.println("Fastpass Authentication Url : " + resp.fastpassAuthenticationUrl);
                }
            } else {
                throw new AssertionError(response.status);
            }
        }
    }
}
