/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.admin.client.app.user;

import java.util.ArrayList;
import java.util.List;

import com.propertyvista.common.domain.User;
import com.propertyvista.portal.admin.client.app.VistaAdminAppSiteMap;
import com.propertyvista.portal.admin.rpc.VistaAdminServices;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.essentials.client.crud.EntityListPanel;
import com.pyx4j.essentials.client.crud.EntityListWithCriteriaWidget;
import com.pyx4j.essentials.client.crud.EntitySearchCriteriaPanel;
import com.pyx4j.forms.client.ui.CComponent;

public class UserListWidget extends EntityListWithCriteriaWidget<User> {

    public UserListWidget() {
        super(User.class, VistaAdminAppSiteMap.App.Users.class, VistaAdminAppSiteMap.App.Users.Edit.class, new EntitySearchCriteriaPanel<User>(User.class) {

            @Override
            protected CComponent<?>[][] getComponents() {
                CComponent<?>[][] components = new CComponent[][] {

                { form.create(form.proto().name()) },

                { form.create(form.proto().email()) },

                };

                return components;
            }
        }, new EntityListPanel<User>(User.class) {

            @Override
            public List<ColumnDescriptor<User>> getColumnDescriptors() {
                List<ColumnDescriptor<User>> columnDescriptors = new ArrayList<ColumnDescriptor<User>>();
                columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().name(), "200px"));
                columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().email(), "300px"));
                return columnDescriptors;
            }
        });

    }

    @Override
    protected Class<? extends EntityServices.Search> getSearchService() {
        return VistaAdminServices.Search.class;
    }

}
