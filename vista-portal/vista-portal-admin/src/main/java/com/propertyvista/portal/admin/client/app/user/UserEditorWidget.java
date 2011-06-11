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

import com.pyx4j.entity.client.ui.CEntityForm;
import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.essentials.client.crud.EntityEditorPanel;
import com.pyx4j.essentials.client.crud.EntityEditorWidget;

import com.propertyvista.portal.admin.client.app.VistaAdminAppSiteMap;
import com.propertyvista.portal.admin.rpc.EditableUser;
import com.propertyvista.portal.admin.rpc.VistaAdminServices;

public class UserEditorWidget extends EntityEditorWidget<EditableUser> {

    public UserEditorWidget() {
        super(EditableUser.class, VistaAdminAppSiteMap.App.Users.Edit.class, new EntityEditorPanel<EditableUser>(EditableUser.class) {

            @Override
            protected IObject<?>[][] getFormMembers() {
                return new IObject[][] {

                { proto().enabled(), null },

                { proto().user().name(), proto().user().email() },

                { proto().behavior(), null },

                };
            }

            @Override
            protected Class<? extends EntityServices.Save> getSaveService() {
                return VistaAdminServices.Save.class;
            }

            @Override
            protected void enhanceComponents(final CEntityForm<EditableUser> form) {
            }

        });
    }

    @Override
    protected EditableUser createNewEntity() {
        EditableUser u = EntityFactory.create(EditableUser.class);
        u.enabled().setValue(Boolean.TRUE);
        return u;
    }

    @Override
    protected void populateForm(EditableUser entity) {
        super.populateForm(entity);
    }

    @Override
    protected Class<? extends EntityServices.Retrieve> getRetrieveService() {
        return VistaAdminServices.Retrieve.class;
    }

}
