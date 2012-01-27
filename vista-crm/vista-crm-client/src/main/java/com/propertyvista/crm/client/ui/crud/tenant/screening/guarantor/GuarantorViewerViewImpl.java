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
package com.propertyvista.crm.client.ui.crud.tenant.screening.guarantor;

import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.crud.tenant.screening.PersonScreeningLister;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.dto.GuarantorDTO;

public class GuarantorViewerViewImpl extends CrmViewerViewImplBase<GuarantorDTO> implements GuarantorViewerView {

    private final IListerView<PersonScreening> screeningLister;

    public GuarantorViewerViewImpl() {
        super(CrmSiteMap.Tenants.Guarantor.class);

        screeningLister = new ListerInternalViewImplBase<PersonScreening>(new PersonScreeningLister());

        //set main form here: 
        setForm(new GuarantorEditorForm(true));
    }

    @Override
    public IListerView<PersonScreening> getScreeningListerView() {
        return screeningLister;
    }
}