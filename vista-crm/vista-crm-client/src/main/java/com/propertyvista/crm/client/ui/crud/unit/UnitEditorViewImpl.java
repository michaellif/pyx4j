/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.unit;

import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.AptUnitDTO;

public class UnitEditorViewImpl extends CrmEditorViewImplBase<AptUnitDTO> implements UnitEditorView {

    public UnitEditorViewImpl() {
        super(CrmSiteMap.Properties.Unit.class);

        // create/init/set main form here: 
        CrmEntityForm<AptUnitDTO> form = new UnitEditorForm(this);
        form.initialize();
        setForm(form);
    }
}
