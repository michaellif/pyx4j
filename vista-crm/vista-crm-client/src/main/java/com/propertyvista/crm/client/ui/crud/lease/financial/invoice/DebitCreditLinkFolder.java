/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.financial.invoice;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.folder.CFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.rpc.dto.lease.financial.DebitLinkDTO;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;

public class DebitCreditLinkFolder extends VistaTableFolder<DebitLinkDTO> {

    private static final I18n i18n = I18n.get(DebitCreditLinkFolder.class);

    private static final List<FolderColumnDescriptor> COLUMNS;
    static {
        DebitLinkDTO proto = EntityFactory.getEntityPrototype(DebitLinkDTO.class);
        COLUMNS = Arrays.asList(//@formatter:off
                new FolderColumnDescriptor(proto.date(), "10em"),
                new FolderColumnDescriptor(proto.arCodeType(), "10em"),
                new FolderColumnDescriptor(proto.arCode(), "10em"),                
                new FolderColumnDescriptor(proto.description(), "20em"),
                new FolderColumnDescriptor(proto.debitAmount(), "10em"),
                new FolderColumnDescriptor(proto.paidAmount(), "10em"),
                new FolderColumnDescriptor(proto.outstandingAmount(), "10em")                
        );//@formatter:on
    }

    public DebitCreditLinkFolder() {
        super(DebitLinkDTO.class);
    }

    @Override
    public List<FolderColumnDescriptor> columns() {
        return COLUMNS;
    }

    @Override
    protected CForm<DebitLinkDTO> createItemForm(IObject<?> member) {
        return new CFolderRowEditor<DebitLinkDTO>(DebitLinkDTO.class, COLUMNS) {
            @Override
            protected CField<?, ?> createCell(FolderColumnDescriptor column) {
                if (column.getObject() == proto().debitAmount()) {
                    CField<?, ?> comp = inject(column.getObject());
                    comp.setNavigationCommand(new Command() {
                        @Override
                        public void execute() {
                            AppSite.getPlaceController()
                                    .goTo(AppPlaceEntityMapper.resolvePlace(InvoiceDebit.class, getValue().debitItemStub().getPrimaryKey()));
                        }
                    });
                    return comp;
                } else if (column.getObject() == proto().paidAmount()) {
                    CField<?, ?> comp = inject(column.getObject());
                    comp.setNavigationCommand(new Command() {
                        @Override
                        public void execute() {
                            AppSite.getPlaceController().goTo(
                                    AppPlaceEntityMapper.resolvePlace(InvoiceCredit.class, getValue().creditItemStub().getPrimaryKey()));
                        }
                    });
                    return comp;
                } else {
                    return super.createCell(column);
                }
            }
        };
    }

}
