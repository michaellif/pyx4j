/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 21, 2012
 * @author igor
 * @version $Id$
 */
package com.propertyvista.yardi.jaxws;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

@WebService(endpointInterface = "com.propertyvista.yardi.jaxws.Greeting")
public class GreetingImpl implements Greeting {

    @Resource
    WebServiceContext wsContext;

    @Override
    public String say(String string) {
        MessageContext messageContext = wsContext.getMessageContext();
        //get detail from request headers
        Map httpHeaders = (Map) messageContext.get(MessageContext.HTTP_REQUEST_HEADERS);
        List userList = (List) httpHeaders.get("Username");
        List passList = (List) httpHeaders.get("Password");

        String username = "";
        String password = "";

        if (userList != null) {
            username = userList.get(0).toString();
        }

        if (passList != null) {
            password = passList.get(0).toString();
        }

        if (username.equals("user") && password.equals("password")) {
            return "User says: " + string;
        } else {
            return "Unknown User!";
        }

    }

}