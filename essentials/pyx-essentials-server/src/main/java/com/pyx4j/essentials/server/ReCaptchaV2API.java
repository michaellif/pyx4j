/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Feb 3, 2016
 * @author vlads
 */
package com.pyx4j.essentials.server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public interface ReCaptchaV2API {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReCaptchaVerificationResponse {

        public boolean success;

        @JsonProperty("error-codes")
        public String[] errorCodes;

    }

    @GET
    @Path("siteverify")
    public ReCaptchaVerificationResponse verify(//
            @QueryParam("secret") String privateKey, //
            @QueryParam("response") String userResponseToken, //
            @QueryParam("remoteip") String remoteAddr);

}
