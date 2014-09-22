/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 22, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.resident.services.movein;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.IService;

import com.propertyvista.portal.rpc.portal.resident.dto.movein.MoveInWizardStep;
import com.propertyvista.portal.rpc.portal.resident.dto.movein.MoveInWizardStepTO;

/**
 *
 * This service is available when PortalResidentBehavior.MoveInWizardCompletionRequired is set.
 *
 */
public interface MoveInWizardService extends IService {

    public void obtainIncompleteSteps(AsyncCallback<Vector<MoveInWizardStepTO>> callback);

    public void skipSteps(AsyncCallback<Void> callback, MoveInWizardStep step);

}
