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
package com.propertyvista.crm.client.ui.crud.settings.financial.arcode;

import com.pyx4j.site.client.ui.prime.lister.ILister;
import com.pyx4j.site.client.ui.prime.lister.ListerInternalViewImplBase;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.property.asset.Utility;

public class ARCodeListerViewImpl extends CrmViewerViewImplBase<ARCode> implements ARCodeListerView {

    private final ILister<ARCode> productCodeLister;

    private final ILister<Utility> utilityLister;

    public ARCodeListerViewImpl() {
        super(true);
        setNotesVisible(false); // disable notes for this view

        productCodeLister = new ListerInternalViewImplBase<ARCode>(new ARCodeLister());
        utilityLister = new ListerInternalViewImplBase<Utility>(new UtilityLister());

        // set main form here: 
        setForm(new ProductDictionaryViewForm(this));
    }

    @Override
    public ILister<ARCode> getProductCodeListerView() {
        return productCodeLister;
    }

    @Override
    public ILister<Utility> getUtilityListerView() {
        return utilityLister;
    }
}