/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-29
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.common;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.dto.LeaseDTO2;

public class LeaseEditorViewImplBase2<DTO extends LeaseDTO2> extends CrmEditorViewImplBase<DTO> implements LeaseEditorViewBase2<DTO> {

    public LeaseEditorViewImplBase2(Class<? extends CrudAppPlace> placeClass) {
        super(placeClass);
    }

    @Override
    public void updateUnitValue(DTO value) {
        LeaseEditorFormBase2<DTO> form = (LeaseEditorFormBase2<DTO>) getForm();

        form.get(form.proto().unit()).setValue(value.unit());
        form.get(form.proto().unit().building()).setValue(value.unit().building());
    }
}
