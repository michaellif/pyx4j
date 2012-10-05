/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-16
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building.catalog;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;

import com.propertyvista.common.client.ui.components.versioning.VersionedLister;
import com.propertyvista.domain.financial.offering.Service;

public class ServiceLister extends VersionedLister<Service> {

    private final static I18n i18n = I18n.get(ServiceLister.class);

    public ServiceLister() {
        super(Service.class, true);
        getDataTablePanel().setFilteringEnabled(false);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().version().versionNumber()).build(),
            new MemberColumnDescriptor.Builder(proto().version().serviceType()).build(),
            new MemberColumnDescriptor.Builder(proto().version().name()).build(),
            new MemberColumnDescriptor.Builder(proto().version().visibility(), true).build()
        );//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().version().serviceType().getPath().toString(), false));
    }

    @Override
    protected void onItemNew() {
        new SelectEnumDialog<Service.ServiceType>(i18n.tr("Select Service Type"), EnumSet.allOf(Service.ServiceType.class)) {
            @Override
            public boolean onClickOk() {
                Service newService = EntityFactory.create(Service.class);
                newService.version().serviceType().setValue(getSelectedType());
                newService.catalog().setPrimaryKey(getPresenter().getParent());
                getPresenter().editNew(getItemOpenPlaceClass(), newService);
                return true;
            }
        }.show();
    }
}
