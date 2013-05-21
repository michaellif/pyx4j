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
package com.propertyvista.portal.client.ui.residents.payment.autopay;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.payment.PreauthorizedPayment.CoveredItem;
import com.propertyvista.domain.tenant.lease.BillableItem;

class CoveredItemFolder extends VistaTableFolder<PreauthorizedPayment.CoveredItem> {

    public CoveredItemFolder() {
        super(PreauthorizedPayment.CoveredItem.class, false);
        setViewable(true);
        inheritViewable(false);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return Arrays.asList(//@formatter:off
                new EntityFolderColumnDescriptor(proto().billableItem(),"40em"),
                new EntityFolderColumnDescriptor(proto().amount(), "5em"));
          //@formatter:on                
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof CoveredItem) {
            return new CoveredItemViewer();
        }
        return super.create(member);
    }

    class CoveredItemViewer extends CEntityFolderRowEditor<CoveredItem> {

        public CoveredItemViewer() {
            super(CoveredItem.class, columns());
        }

        @SuppressWarnings("unchecked")
        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            CComponent<?, ?> comp;

            if (column.getObject() == proto().billableItem()) {
                comp = inject(column.getObject(), new CEntityLabel<BillableItem>());
                ((CLabel<BillableItem>) comp).setFormat(new IFormat<BillableItem>() {
                    @Override
                    public String format(BillableItem value) {
                        if (value != null) {
                            if (!value.description().isNull()) {
                                return value.description().getValue();
                            } else if (!value.item().isNull() && !value.item().description().isNull()) {
                                return value.item().description().getValue();
                            }
                            return "";
                        } else {
                            return null;
                        }
                    }

                    @Override
                    public BillableItem parse(String string) {
                        return null;
                    }
                });
            } else {
                comp = super.createCell(column);
            }

            return comp;
        }
    }

}