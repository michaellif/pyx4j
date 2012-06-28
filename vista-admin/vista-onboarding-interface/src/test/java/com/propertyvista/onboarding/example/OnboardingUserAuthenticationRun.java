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

import java.util.Date;

import com.propertyvista.onboarding.example.model.CreateOnboardingUserRequest;
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
        CreateOnboardingUserRequest createRequest = new CreateOnboardingUserRequest();
        createRequest.requestId = "OnboardingUserAuthenticationRun";
        createRequest.firstName = "Bob ";
        createRequest.lastName = new Date().toString();
        createRequest.email = String.valueOf(System.currentTimeMillis()) + "@rossul.com";
        createRequest.password = "pwd~" + String.valueOf(System.currentTimeMillis());
        createRequest.onboardingAccountId = "acc" + System.nanoTime();

        createRequest.requestRemoteAddr = "1.1.1.1";

        {
            RequestMessage rm = new RequestMessage();
            rm.interfaceEntity = ExampleClient.interfaceEntity;
            rm.interfaceEntityPassword = ExampleClient.interfaceEntityPassword;
            rm.addRequest(createRequest);

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

        /**
         * Authenticate using this user
         */
        OnboardingUserAuthenticationRequest authRequest = new OnboardingUserAuthenticationRequest();
        {
            authRequest.email = createRequest.email;
            authRequest.password = createRequest.password;
            authRequest.requestRemoteAddr = "1.1.1.1";
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
                    if (!authResponse.success) {
                        throw new AssertionError("Not Authenticated");
                    }
                    if (authResponse.status != OnboardingUserAuthenticationResponse.AuthenticationStatusCode.OK) {
                        throw new AssertionError(authResponse.status);
                    }
                }
            } else {
                throw new AssertionError(response.status);
            }
        }
    }
}
