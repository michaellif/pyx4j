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

import java.util.EnumSet;

import com.pyx4j.entity.client.ui.CEntityForm;
import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.essentials.client.crud.EntityEditorPanel;
import com.pyx4j.essentials.client.crud.EntityEditorWidget;
import com.pyx4j.examples.domain.ExamplesBehavior;
import com.pyx4j.examples.rpc.EditableUser;
import com.pyx4j.examples.rpc.ExamplesAdminServices;
import com.pyx4j.examples.site.client.ExamplesSiteMap;
import com.pyx4j.forms.client.ui.CComboBox;

public class UserEditorWidget extends EntityEditorWidget<EditableUser> {

    public UserEditorWidget() {
        super(EditableUser.class, ExamplesSiteMap.Crm.Users.Edit.class, new EntityEditorPanel<EditableUser>(EditableUser.class) {

            @Override
            protected IObject<?>[][] getFormMembers() {
                return new IObject[][] {

                { meta().enabled(), null },

                { meta().user().name(), meta().user().email() },

                };
            }

            @Override
            protected Class<? extends EntityServices.Save> getSaveService() {
                return ExamplesAdminServices.Save.class;
            }

            @Override
            protected void enhanceComponents(CEntityForm<EditableUser> form) {
                ((CComboBox<ExamplesBehavior>) get(meta().behavior())).setOptions(EnumSet.of(ExamplesBehavior.CRM_EMPLOYEE, ExamplesBehavior.CRM_ADMIN));
            }

        });
    }

    @Override
    protected EditableUser createNewEntity() {
        EditableUser u = EntityFactory.create(EditableUser.class);
        u.enabled().setValue(Boolean.TRUE);
        u.behavior().setValue(ExamplesBehavior.CRM_EMPLOYEE);
        return u;
    }

    @Override
    protected Class<? extends EntityServices.Retrieve> getRetrieveService() {
        return ExamplesAdminServices.Retrieve.class;
    }

}
