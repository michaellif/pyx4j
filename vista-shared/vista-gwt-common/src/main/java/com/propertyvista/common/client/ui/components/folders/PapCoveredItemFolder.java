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

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.PapBillableItemLabel;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.AutopayAgreement.PreauthorizedPaymentCoveredItem;

public class PapCoveredItemFolder extends VistaTableFolder<AutopayAgreement.PreauthorizedPaymentCoveredItem> {

    private static final I18n i18n = I18n.get(PapCoveredItemFolder.class);

    public PapCoveredItemFolder() {
        this(false);
    }

    public PapCoveredItemFolder(boolean editable) {
        super(AutopayAgreement.PreauthorizedPaymentCoveredItem.class, editable);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return Arrays.asList(//@formatter:off
                new EntityFolderColumnDescriptor(proto().billableItem(),"31em", i18n.tr("Lease Charge")),
                new EntityFolderColumnDescriptor(proto().billableItem().agreedPrice(),"8em", i18n.tr("Price"), true),
                new EntityFolderColumnDescriptor(proto().amount(), "8em", i18n.tr("Payment")));
          //@formatter:on                
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof PreauthorizedPaymentCoveredItem) {
            return new CoveredItemViewer();
        }
        return super.create(member);
    }

    class CoveredItemViewer extends CEntityFolderRowEditor<PreauthorizedPaymentCoveredItem> {

        public CoveredItemViewer() {
            super(PreauthorizedPaymentCoveredItem.class, columns());

            setViewable(true);
            inheritViewable(false);
        }

        @Override
        protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
            CComponent<?> comp;

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