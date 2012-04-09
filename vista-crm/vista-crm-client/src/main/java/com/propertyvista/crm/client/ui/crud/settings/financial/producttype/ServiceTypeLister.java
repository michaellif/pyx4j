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
package com.propertyvista.crm.client.ui.crud.settings.financial.producttype;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.financial.offering.ServiceItemType;

public class ServiceTypeLister extends ListerBase<ServiceItemType> {

    private static final I18n i18n = I18n.get(ServiceTypeLister.class);

    public ServiceTypeLister() {
        super(ServiceItemType.class, false, true);

        getDataTablePanel().getDataTable().setHasCheckboxColumn(true);

        addActionItem(new Button(i18n.tr("Delete Checked"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                for (ProductItemType item : getDataTablePanel().getDataTable().getCheckedItems()) {
                    getPresenter().delete(item.getPrimaryKey());
                }
            }
        }));

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().name()).build(),
            new MemberColumnDescriptor.Builder(proto().serviceType()).build(),
            new MemberColumnDescriptor.Builder(proto().glCode()).build()

        );//@formatter:on
    }

}
