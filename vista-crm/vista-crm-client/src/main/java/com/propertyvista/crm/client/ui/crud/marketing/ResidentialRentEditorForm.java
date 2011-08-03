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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.marketing;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.site.client.ui.crud.IFormView;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.domain.financial.offering.ResidentialRent;
import com.propertyvista.domain.financial.offeringnew.Feature;

public class ResidentialRentEditorForm extends FeatureEditorForm<ResidentialRent> {

    public ResidentialRentEditorForm(IFormView<Feature> parentView) {
        this(new CrmEditorsComponentFactory(), parentView);
    }

    public ResidentialRentEditorForm(IEditableComponentFactory factory, IFormView<Feature> parentView) {
        super(ResidentialRent.class, factory);
        setParentView(parentView);
    }

    @Override
    protected void addMoreTabs(VistaTabLayoutPanel tabPanel) {
    }
}