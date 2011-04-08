/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Feb 16, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.examples.site.client.crm.user;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.essentials.client.crud.EntityListPanel;
import com.pyx4j.essentials.client.crud.EntityListWithCriteriaWidget;
import com.pyx4j.essentials.client.crud.EntitySearchCriteriaPanel;
import com.pyx4j.examples.domain.User;
import com.pyx4j.examples.site.client.ExamplesSiteMap;
import com.pyx4j.forms.client.ui.CComponent;

public class UserListWidget extends EntityListWithCriteriaWidget<User> {

    public UserListWidget() {
        super(User.class, ExamplesSiteMap.Crm.Users.class, ExamplesSiteMap.Crm.Users.Edit.class, new EntitySearchCriteriaPanel<User>(User.class) {

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

}
