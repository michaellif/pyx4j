/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-14
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.getsatisfaction;

import java.io.IOException;
import java.net.URISyntaxException;

import net.oauth.OAuthException;

public class GetSatisfactionUrl {

    private static String vistaGSFNkey = "e5i5h5pun8lw";

    private static String vistaGSFNsecret = "qm2r8h01tylp6jax88lgzjielct0mabq";

    public static String url(String email, String name, String uid, boolean isSecure) throws OAuthException, IOException, URISyntaxException {
        return FastPass.url(vistaGSFNkey, vistaGSFNsecret, email, name, uid, isSecure);
    }
}
