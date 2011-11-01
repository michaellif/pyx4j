/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.folders;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.editors.CTenantGuarantorEditor;
import com.propertyvista.domain.tenant.income.TenantGuarantor;

public class TenantGuarantorFolder extends VistaBoxFolder<TenantGuarantor> {

    public TenantGuarantorFolder(boolean modifyable) {
        super(TenantGuarantor.class, modifyable);
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member instanceof TenantGuarantor) {
            return new CTenantGuarantorEditor();
        }
        return super.create(member);
    }
}