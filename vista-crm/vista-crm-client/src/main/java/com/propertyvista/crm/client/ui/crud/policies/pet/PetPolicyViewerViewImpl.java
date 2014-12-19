/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 9, 2012
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.crud.policies.pet;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.domain.policy.dto.PetPolicyDTO;

public class PetPolicyViewerViewImpl extends CrmViewerViewImplBase<PetPolicyDTO> implements PetPolicyViewerView {

    public PetPolicyViewerViewImpl() {
        setForm(new PetPolicyForm(this));
    }
}
