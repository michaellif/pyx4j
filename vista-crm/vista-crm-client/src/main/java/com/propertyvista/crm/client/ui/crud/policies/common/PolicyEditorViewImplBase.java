/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-09-30
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.common;

import com.pyx4j.commons.UserRuntimeException;

import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.domain.policy.framework.PolicyDTOBase;

public class PolicyEditorViewImplBase<Policy extends PolicyDTOBase> extends CrmEditorViewImplBase<Policy> {

    @Override
    public boolean onSaveFail(Throwable caught) {
        if (caught instanceof UserRuntimeException) {
            showErrorDialog(caught.getMessage());
            return true;
        } else {
            return super.onSaveFail(caught);
        }
    }
}
