/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-21
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.pad;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.domain.policy.dto.PADPolicyDTO;

public class PADPolicyViewerViewImpl extends CrmViewerViewImplBase<PADPolicyDTO> implements PADPolicyViewerView {

    public PADPolicyViewerViewImpl() {
        setForm(new PADPolicyForm(this));
    }
}
