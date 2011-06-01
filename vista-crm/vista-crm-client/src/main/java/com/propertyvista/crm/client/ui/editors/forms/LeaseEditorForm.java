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
package com.propertyvista.crm.client.ui.editors.forms;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmEntityFolder;
import com.propertyvista.crm.client.ui.components.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.portal.domain.ptapp.ChargeLine;

public class LeaseEditorForm extends CrmEntityForm<LeaseDTO> {

    public LeaseEditorForm() {
        super(LeaseDTO.class, new CrmEditorsComponentFactory());
    }

    public LeaseEditorForm(IEditableComponentFactory factory) {
        super(LeaseDTO.class, factory);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();

        main.add(inject(proto().leaseID()), 15);
        main.add(inject(proto().unit()), 15);
        main.add(inject(proto().application()), 7);
        main.add(inject(proto().leaseFrom()), 10);
        main.add(inject(proto().leaseTo()), 10);
        main.add(inject(proto().expectedMoveIn()), 10);
        main.add(inject(proto().expectedMoveOut()), 10);
        main.add(inject(proto().actualMoveIn()), 10);
        main.add(inject(proto().actualMoveOut()), 10);
        main.add(inject(proto().signDate()), 10);

        main.add(new CrmHeaderDecorator(i18n.tr("Financials")));

        main.add(inject(proto().accountNumber()), 15);
        main.add(inject(proto().currentRent()), 7);
        main.add(inject(proto().paymentAccepted()), 15);
        main.add(inject(proto().charges(), createChargesListEditor()));
        //TODO Leon
        //Not sure how to reuse code yet
        //main.add(inject(proto().concessions()), 7);
        main.add(inject(proto().specialStatus()), 15);
        //TODO Leon
        //Not sure what to do with the rest of the domain

        main.setWidth("100%");
        return main;
    }

    private CEntityFolder<ChargeLine> createChargesListEditor() {
        return new CrmEntityFolder<ChargeLine>(ChargeLine.class, i18n.tr("Charge Line"), isEditable()) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().label(), "10em"));
                return columns;
            }
        };
    }
}