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
package com.propertyvista.portal.web.client.ui.residents.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.portal.domain.dto.BillDataDTO;
import com.propertyvista.portal.domain.dto.BillListDTO;

public class BillingHistoryForm extends CEntityForm<BillListDTO> {

    private BillingHistoryView.Presenter presenter;

    public BillingHistoryForm() {
        super(BillListDTO.class, new VistaViewersComponentFactory());
    }

    public void setPresenter(BillingHistoryView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public IsWidget createContent() {
        FlowPanel container = new FlowPanel();
        container.add(inject(proto().bills(), new BillingHistoryFolder()));
        return container;
    }

    private class BillingHistoryFolder extends VistaTableFolder<BillDataDTO> {

        public BillingHistoryFolder() {
            super(BillDataDTO.class, false);
            setOrderable(false);
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
            columns.add(new EntityFolderColumnDescriptor(proto().referenceNo(), "5em"));
            columns.add(new EntityFolderColumnDescriptor(proto().amount(), "10em"));
            columns.add(new EntityFolderColumnDescriptor(proto().dueDate(), "10em"));
            columns.add(new EntityFolderColumnDescriptor(proto().fromDate(), "10em"));
            return columns;
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof BillDataDTO) {
                return new BillEditor();
            }
            return super.create(member);
        }

        private class BillEditor extends CEntityFolderRowEditor<BillDataDTO> {

            public BillEditor() {
                super(BillDataDTO.class, columns());
            }

            @Override
            public CComponent<?, ?> create(IObject<?> member) {
                CComponent<?, ?> comp = null;
                if (member.equals(proto().amount())) {
                    comp = new CLabel<BigDecimal>();
                    comp.setViewable(true);
                    comp.setNavigationCommand(new Command() {
                        @Override
                        public void execute() {
                            presenter.view(getValue());
                        }
                    });

                } else {
                    comp = super.create(member);
                }
                return comp;
            }
        }
    }
}
