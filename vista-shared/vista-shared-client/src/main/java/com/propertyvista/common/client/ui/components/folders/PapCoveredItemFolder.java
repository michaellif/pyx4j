/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-16
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.folders;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.dom.client.Style.FontWeight;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.folder.CFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.PapBillableItemLabel;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.AutopayAgreement.AutopayAgreementCoveredItem;

public class PapCoveredItemFolder extends VistaTableFolder<AutopayAgreement.AutopayAgreementCoveredItem> {

    private static final I18n i18n = I18n.get(PapCoveredItemFolder.class);

    public PapCoveredItemFolder() {
        this(false);
    }

    public PapCoveredItemFolder(boolean editable) {
        super(AutopayAgreement.AutopayAgreementCoveredItem.class, editable);
    }

    @Override
    public List<FolderColumnDescriptor> columns() {
        return Arrays.asList(//@formatter:off
                new FolderColumnDescriptor(proto().billableItem(),"31em", i18n.tr("Lease Charge")),
                new FolderColumnDescriptor(proto().billableItem().agreedPrice(),"8em", i18n.tr("Price"), true),
                new FolderColumnDescriptor(proto().amount(), "8em", i18n.tr("Payment")));
          //@formatter:on                
    }

    @Override
    protected CForm<AutopayAgreementCoveredItem> createItemForm(IObject<?> member) {
        return new CoveredItemViewer();
    }

    class CoveredItemViewer extends CFolderRowEditor<AutopayAgreementCoveredItem> {

        public CoveredItemViewer() {
            super(AutopayAgreementCoveredItem.class, columns());

            setViewable(true);
            inheritViewable(false);
        }

        @Override
        protected CField<?, ?> createCell(FolderColumnDescriptor column) {
            CField<?, ?> comp;

            if (column.getObject() == proto().billableItem()) {
                comp = inject(column.getObject(), new PapBillableItemLabel());
            } else {
                comp = super.createCell(column);
            }

            if (column.getObject() == proto().amount()) {
                comp.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLD);
            }

            return comp;
        }
    }

}