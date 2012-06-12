/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 22, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.onboarding.example;

import com.propertyvista.onboarding.example.model.OnboardingUserAuthenticationRequest;
import com.propertyvista.onboarding.example.model.OnboardingUserAuthenticationResponse;
import com.propertyvista.onboarding.example.model.RequestMessage;
import com.propertyvista.onboarding.example.model.ResponseMessage;
import com.propertyvista.onboarding.example.utils.ExampleClient;

public class OnboardingUserAuthenticationRun {

    public static void main(String[] args) throws Exception {
        /**
         * First create user
         */
//        CreateOnboardingUserRequest createRequest = new CreateOnboardingUserRequest();
//        createRequest.requestId = "OnboardingUserAuthenticationRun";
//        createRequest.name = "Bob " + new Date().toString();
//        createRequest.email = String.valueOf(System.currentTimeMillis()) + "@rossul.com";
//        createRequest.password = "pwd~" + String.valueOf(System.currentTimeMillis());
//        createRequest.onboardingAccountId = "acc" + System.nanoTime();
//
//        {
//            RequestMessage rm = new RequestMessage();
//            rm.interfaceEntity = "rossul";
//            rm.interfaceEntityPassword = "secret";
//            rm.addRequest(createRequest);
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

        /**
         * Authenticate using this user
         */
        OnboardingUserAuthenticationRequest authRequest = new OnboardingUserAuthenticationRequest();
        {
            authRequest.email = "vista.equifax@rossul.com";//createRequest.email;
            authRequest.password = "vista.equifax@rossul.com";//createRequest.password;
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
            }
        }
    }
}
