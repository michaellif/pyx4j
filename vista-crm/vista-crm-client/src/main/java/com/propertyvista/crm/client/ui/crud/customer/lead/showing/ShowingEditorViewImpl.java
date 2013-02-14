/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.customer.lead.showing;

import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.crm.rpc.dto.tenant.ShowingDTO;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class ShowingEditorViewImpl extends CrmEditorViewImplBase<ShowingDTO> implements ShowingEditorView {

    public ShowingEditorViewImpl() {
        setForm(new ShowingForm(this));
    }

    @Override
    public void setUnitData(AptUnit selected) {
        ShowingForm form = (ShowingForm) getForm();
        form.get(form.proto().unit()).setValue(selected);
        form.get(form.proto().unit().building()).setValue(selected.building());
        form.get(form.proto().unit().floorplan()).setValue(selected.floorplan());
    }
}
