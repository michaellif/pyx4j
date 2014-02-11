/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 11, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.operations.server.services.dev;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.operations.rpc.services.dev.PmcYardiCredentialService;
import com.propertyvista.server.config.DevYardiCredentials;

public class PmcYardiCredentialServiceImpl implements PmcYardiCredentialService {

    @Override
    public void getYardiCredentials(AsyncCallback<Vector<PmcYardiCredential>> callback) {
        callback.onSuccess(new Vector<PmcYardiCredential>(DevYardiCredentials.getTestPmcYardiCredentialList()));
    }

}
