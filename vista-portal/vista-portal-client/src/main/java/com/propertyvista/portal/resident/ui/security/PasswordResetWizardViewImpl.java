/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 24, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.security;

import com.pyx4j.security.rpc.PasswordChangeRequest;

import com.propertyvista.portal.resident.ui.AbstractWizardView;

public class PasswordResetWizardViewImpl extends AbstractWizardView<PasswordChangeRequest> implements PasswordResetWizardView {

    public PasswordResetWizardViewImpl() {
        setWizard(new PasswordResetWizard(this));
    }

}
