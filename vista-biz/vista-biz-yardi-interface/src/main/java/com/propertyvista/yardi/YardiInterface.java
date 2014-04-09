/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 8, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi;

import java.rmi.RemoteException;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.stubs.ExternalInterfaceLoggingStub;

public interface YardiInterface extends ExternalInterfaceLoggingStub {

    String ping(PmcYardiCredential yc) throws RemoteException;

    void validate(PmcYardiCredential yc) throws RemoteException, YardiServiceException;

    String getPluginVersion(PmcYardiCredential yc) throws RemoteException;

}
