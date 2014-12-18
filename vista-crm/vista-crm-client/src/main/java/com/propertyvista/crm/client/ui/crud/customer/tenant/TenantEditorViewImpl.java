/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 */
package com.propertyvista.crm.client.ui.crud.customer.tenant;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.dto.TenantDTO;

public class TenantEditorViewImpl extends CrmEditorViewImplBase<TenantDTO> implements TenantEditorView {

    private final static I18n i18n = I18n.get(TenantEditorViewImpl.class);

    public TenantEditorViewImpl() {
        setForm(new TenantForm(this));
    }

    @Override
    public void populate(TenantDTO value) {
        // tweak legal naming: 
        if (value.lease().status().getValue().isDraft()) {
            setCaptionBase(i18n.tr("Applicant"));
        } else {
            setCaptionBase(i18n.tr("Tenant"));
        }

        super.populate(value);
    }
}
