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
package com.propertyvista.portal.web.client.ui.security;

import com.pyx4j.security.rpc.PasswordChangeRequest;

import com.propertyvista.portal.web.client.ui.AbstractWizardView;

public class PasswordChangeWizardViewImpl extends AbstractWizardView<PasswordChangeRequest> implements PasswordChangeWizardView {

    public PasswordChangeWizardViewImpl() {
        setWizard(new PasswordChangeWizard(this));
    }

//    @Override
//    public void initialize(Key userPk) {
//        PasswordChangeRequest newRequest = EntityFactory.create(PasswordChangeRequest.class);
//        newRequest.userPk().setValue(userPk);
//        form.populate(newRequest);
//
//    }

}
