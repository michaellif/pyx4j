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
package com.propertyvista.crm.client.ui.crud.settings.dictionary;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.offering.ServiceItemType;

public class FeatureTypeLister extends ListerBase<ServiceItemType> {

    public FeatureTypeLister() {
        super(ServiceItemType.class, CrmSiteMap.Settings.FeatureItemType.class);

        getListPanel().getDataTable().setHasCheckboxColumn(true);

        Button btnDelete = new Button(i18n.tr("Delete&nbspChecked"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                for (ServiceItemType item : getListPanel().getDataTable().getCheckedItems()) {
                    getPresenter().delete(item.getPrimaryKey());
                }
            }
        });
        btnDelete.addStyleName(btnDelete.getStylePrimaryName() + VistaCrmTheme.StyleSuffixEx.ActionButton);
        addActionButton(btnDelete);
    }

    @Override
    protected void fillDefaultColumnDescriptors(List<ColumnDescriptor<ServiceItemType>> columnDescriptors, ServiceItemType proto) {
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.name()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.featureType()));
    }
}
