/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 15, 2014
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.customer.common.components;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.domain.tenant.CustomerScreeningLegalQuestion;
import com.propertyvista.misc.VistaTODO;

public class LegalQuestionFolder extends VistaBoxFolder<CustomerScreeningLegalQuestion> {

    public LegalQuestionFolder() {
        super(CustomerScreeningLegalQuestion.class, false);
    }

    @Override
    public VistaBoxFolderItemDecorator<CustomerScreeningLegalQuestion> createItemDecorator() {
        VistaBoxFolderItemDecorator<CustomerScreeningLegalQuestion> decor = super.createItemDecorator();
//        decor.setExpended(isEditable());
        return decor;
    }

    @Override
    protected CForm<CustomerScreeningLegalQuestion> createItemForm(IObject<?> member) {
        return new CForm<CustomerScreeningLegalQuestion>(CustomerScreeningLegalQuestion.class) {
            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Dual, proto().question()).decorate();

                formPanel.append(Location.Left, proto().answer()).decorate();
                formPanel.append(Location.Right, proto().notes()).decorate().labelWidth(60);

                // waiting for 'soft mode' validation!
                if (VistaTODO.VISTA_4498_Remove_Unnecessary_Validation_Screening_CRM) {
                    get(proto().answer()).setMandatory(false);
                }

                return formPanel;
            }
        };
    }
}
