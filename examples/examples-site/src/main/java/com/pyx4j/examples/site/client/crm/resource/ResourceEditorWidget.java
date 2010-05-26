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

import java.util.EnumSet;

import com.pyx4j.entity.client.ui.CEntityForm;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.essentials.client.crud.EntityEditorPanel;
import com.pyx4j.essentials.client.crud.EntityEditorWidget;
import com.pyx4j.examples.domain.crm.Province;
import com.pyx4j.examples.domain.crm.Resource;
import com.pyx4j.examples.domain.crm.Resource.RepStatus;
import com.pyx4j.examples.site.client.ExamplesSiteMap;
import com.pyx4j.forms.client.ui.CComboBox;

public class ResourceEditorWidget extends EntityEditorWidget<Resource> {

    public ResourceEditorWidget() {
        super(Resource.class, ExamplesSiteMap.Crm.Resource.Edit.class, new EntityEditorPanel<Resource>(Resource.class) {

            @Override
            protected IObject<?>[][] getFormMembers() {
                return new IObject[][] {

                { meta().name(), meta().status() },

                { meta().phone(), null },

                { meta().address().street(), meta().address().city() },

                { meta().address().province(), meta().address().zip() },

                };
            }

            @Override
            protected void enhanceComponents(CEntityForm<Resource> form) {
                ((CComboBox<RepStatus>) get(meta().status())).setOptions(EnumSet.allOf(RepStatus.class));
                ((CComboBox<Province>) get(meta().address().province())).setOptions(EnumSet.allOf(Province.class));
            }

        });

    }

    @Override
    protected Resource createNewEntity() {
        Resource rep = EntityFactory.create(Resource.class);
        rep.status().setValue(RepStatus.ACTIVE);
        return rep;
    }

}
