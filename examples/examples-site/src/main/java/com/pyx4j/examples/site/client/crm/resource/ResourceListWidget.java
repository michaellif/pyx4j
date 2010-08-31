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
package com.pyx4j.examples.site.client.crm.resource;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.essentials.client.crud.EntityListPanel;
import com.pyx4j.essentials.client.crud.EntityListWithCriteriaWidget;
import com.pyx4j.essentials.client.crud.EntitySearchCriteriaPanel;
import com.pyx4j.examples.domain.crm.Resource;
import com.pyx4j.examples.domain.crm.Resource.RepStatus;
import com.pyx4j.examples.site.client.ExamplesSiteMap;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;

public class ResourceListWidget extends EntityListWithCriteriaWidget<Resource> {

    public ResourceListWidget() {
        super(Resource.class, ExamplesSiteMap.Crm.Resource.class, ExamplesSiteMap.Crm.Resource.Edit.class, new EntitySearchCriteriaPanel<Resource>(
                Resource.class) {

            @Override
            protected CComponent<?>[][] getComponents() {
                CComponent<?>[][] components = new CComponent[][] {

                { form.create(form.meta().name()) },

                { form.create(form.meta().status()) },

                };

                return components;
            }

            @Override
            protected void enhanceComponents() {
                ((CComboBox<RepStatus>) form.get(form.meta().status())).setOptions(EnumSet.allOf(RepStatus.class));
            }

        }, new EntityListPanel<Resource>(Resource.class) {

            @Override
            public List<ColumnDescriptor<Resource>> getColumnDescriptors() {
                List<ColumnDescriptor<Resource>> columnDescriptors = new ArrayList<ColumnDescriptor<Resource>>();
                ColumnDescriptor<Resource> name = ColumnDescriptorFactory.createColumnDescriptor(getMetaEntity(), getMetaEntity().name());
                name.setWidth("200px");
                name.setWordWrap(false);
                columnDescriptors.add(name);

                columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(getMetaEntity(), getMetaEntity().phone(), "80px"));

                ColumnDescriptor<Resource> status = ColumnDescriptorFactory.createColumnDescriptor(getMetaEntity(), getMetaEntity().status());
                status.setWidth("200px");
                status.setWordWrap(false);
                columnDescriptors.add(status);
                return columnDescriptors;
            }
        });

    }

}
