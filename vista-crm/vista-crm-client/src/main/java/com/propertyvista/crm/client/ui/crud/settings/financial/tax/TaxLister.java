/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.financial.tax;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.ConfirmDecline;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.domain.financial.tax.Tax;

public class TaxLister extends ListerBase<Tax> {

    private static final I18n i18n = I18n.get(TaxLister.class);

    public TaxLister() {
        super(Tax.class, true);

        getDataTablePanel().getDataTable().setHasCheckboxColumn(true);

        addActionItem(new Button(i18n.tr("Delete Checked"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                MessageDialog.confirm(i18n.tr("Confirm"), i18n.tr("Do you really want to delete checked items?"), new ConfirmDecline() {
                    @Override
                    public void onConfirmed() {
                        for (Tax item : getDataTablePanel().getDataTable().getCheckedItems()) {
                            getPresenter().delete(item.getPrimaryKey());
                        }
                    }

                    @Override
                    public void onDeclined() {
                    }
                });
            }
        }));

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().name()).build(),
            new MemberColumnDescriptor.Builder(proto().authority()).build(),
            new MemberColumnDescriptor.Builder(proto().rate()).build(),
            new MemberColumnDescriptor.Builder(proto().compound()).build()
        );//@formatter:on
    }
}
