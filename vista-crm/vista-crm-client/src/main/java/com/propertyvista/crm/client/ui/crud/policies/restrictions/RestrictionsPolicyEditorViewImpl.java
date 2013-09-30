/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 13, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.restrictions;

import com.propertyvista.crm.client.ui.crud.policies.common.PolicyEditorViewImplBase;
import com.propertyvista.domain.policy.dto.RestrictionsPolicyDTO;

public class RestrictionsPolicyEditorViewImpl extends PolicyEditorViewImplBase<RestrictionsPolicyDTO> implements RestrictionsPolicyEditorView {

    public RestrictionsPolicyEditorViewImpl() {
        setForm(new RestrictionsPolicyForm(this));
    }

}
