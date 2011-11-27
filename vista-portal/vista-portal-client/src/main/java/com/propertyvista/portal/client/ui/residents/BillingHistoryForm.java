/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 11, 2011
 * @author dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.CEntityFolder;
import com.pyx4j.entity.client.ui.folder.IFolderDecorator;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.portal.domain.dto.BillDTO;
import com.propertyvista.portal.domain.dto.BillListDTO;

public class BillingHistoryForm extends CEntityEditor<BillListDTO> implements BillingHistoryView {

    protected static I18n i18n = I18n.get(BillingHistoryForm.class);

    private BillingHistoryView.Presenter presenter;

    public BillingHistoryForm() {
        super(BillListDTO.class, new VistaViewersComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        FlowPanel container = new FlowPanel();
        container.add(inject(proto().bills(), createBillingHistoryViewer()));
        return container;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

    private CEntityFolder<BillDTO> createBillingHistoryViewer() {
        return new VistaTableFolder<BillDTO>(BillDTO.class, false) {
            @Override
            public List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "10em"));
                columns.add(new EntityFolderColumnDescriptor(proto().paidOn(), "7em"));
                columns.add(new EntityFolderColumnDescriptor(proto().total(), "10em"));
                columns.add(new EntityFolderColumnDescriptor(proto().transactionID(), "10em"));
                return columns;
            }

            @Override
            protected IFolderDecorator<BillDTO> createDecorator() {
                return new VistaTableFolderDecorator<BillDTO>(this);
            }
        };
    }

}
