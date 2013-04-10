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
package com.propertyvista.crm.client.ui.crud.lease.financial;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Command;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.rpc.dto.lease.financial.DebitLinkDTO;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;

public class DebitCreditLinkFolder extends VistaTableFolder<DebitLinkDTO> {

    private static final I18n i18n = I18n.get(DebitCreditLinkFolder.class);

    private static final List<EntityFolderColumnDescriptor> COLUMNS;
    static {
        DebitLinkDTO proto = EntityFactory.getEntityPrototype(DebitLinkDTO.class);
        COLUMNS = Arrays.asList(//@formatter:off
                new EntityFolderColumnDescriptor(proto.date(), "10em"),
                new EntityFolderColumnDescriptor(proto.arCodeType(), "10em"),
                new EntityFolderColumnDescriptor(proto.arCode(), "10em"),                
                new EntityFolderColumnDescriptor(proto.description(), "20em"),
                new EntityFolderColumnDescriptor(proto.debitAmount(), "10em"),
                new EntityFolderColumnDescriptor(proto.paidAmount(), "10em"),
                new EntityFolderColumnDescriptor(proto.outstandingAmount(), "10em")                
        );//@formatter:on
    }

    public DebitCreditLinkFolder() {
        super(DebitLinkDTO.class);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return COLUMNS;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (DebitLinkDTO.class.equals(member.getObjectClass())) {
            return new CEntityFolderRowEditor<DebitLinkDTO>(DebitLinkDTO.class, COLUMNS) {
                @Override
                protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
                    if (column.getObject() == proto().debitAmount()) {
                        return inject(column.getObject(), makeAmountHyperlink(new Command() {
                            @Override
                            public void execute() {
                                AppSite.getPlaceController().goTo(
                                        AppPlaceEntityMapper.resolvePlace(InvoiceDebit.class, getValue().debitItemStub().getPrimaryKey()));
                            }
                        }));
                    } else if (column.getObject() == proto().paidAmount()) {
                        return inject(column.getObject(), makeAmountHyperlink(new Command() {
                            @Override
                            public void execute() {
                                AppSite.getPlaceController().goTo(
                                        AppPlaceEntityMapper.resolvePlace(InvoiceCredit.class, getValue().creditItemStub().getPrimaryKey()));
                            }
                        }));
                    } else {
                        return super.createCell(column);
                    }
                }
            };
        } else {
            return super.create(member);
        }
    }

    private CHyperlink<BigDecimal> makeAmountHyperlink(Command command) {
        CHyperlink<BigDecimal> hyperLink = new CHyperlink<BigDecimal>(command);
        hyperLink.setFormat(new IFormat<BigDecimal>() {
            NumberFormat moneyFormat = NumberFormat.getFormat(i18n.tr("#,##0.00"));

            @Override
            public BigDecimal parse(String string) throws ParseException {
                throw new Error("Operation Not Supported"); // the folder is read only                
            }

            @Override
            public String format(BigDecimal value) {
                return value == null ? "" : moneyFormat.format(value);
            }
        });
        return hyperLink;
    }

}
