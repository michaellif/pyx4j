/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 6, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.admin.server;

import java.util.List;
import java.util.Locale;

import org.xnap.commons.i18n.I18n;

import com.propertyvista.portal.admin.rpc.EditableUser;
import com.propertyvista.portal.admin.rpc.VistaAdminServices;
import com.propertyvista.portal.domain.User;
import com.propertyvista.server.domain.UserCredential;

import com.pyx4j.entity.rpc.EntityCriteriaByPK;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18nFactory;

public class VistaAdminServicesImpl implements VistaAdminServices {

    private static I18n i18n = I18nFactory.getI18n();

    public static class SaveImpl extends EntityServicesImpl.MergeSaveImpl implements VistaAdminServices.Save {

        @Override
        public IEntity execute(IEntity request) {
            EditableUser requestEditableUser = (EditableUser) request;
            User user = requestEditableUser.user();
            user.email().setValue(user.email().getValue().toLowerCase(Locale.ENGLISH));

            EntityQueryCriteria<User> criteria = EntityQueryCriteria.create(User.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().email(), user.email().getValue()));
            List<User> users = PersistenceServicesFactory.getPersistenceService().query(criteria);
            for (User existsUser : users) {
                if (!existsUser.getPrimaryKey().equals(user.getPrimaryKey())) {
                    throw new RuntimeException(i18n.tr("Email already registered"));
                }
            }

            user = (User) super.execute(user);

            UserCredential crs = PersistenceServicesFactory.getPersistenceService().retrieve(UserCredential.class, user.getPrimaryKey());
            if (crs == null) {
                crs = EntityFactory.create(UserCredential.class);
                crs.setPrimaryKey(user.getPrimaryKey());
                crs.user().set(user);
                crs.credential().set(user.email());
            }
            crs.enabled().set(requestEditableUser.enabled());
            crs.behavior().set(requestEditableUser.behavior());

            super.execute(crs);

            EditableUser editableUser = EntityFactory.create(EditableUser.class);
            editableUser.setPrimaryKey(user.getPrimaryKey());
            editableUser.user().set(user);

            editableUser.enabled().set(crs.enabled());
            editableUser.behavior().set(crs.behavior());

            return editableUser;
        }
    }

    public static class RetrieveImpl extends EntityServicesImpl.RetrieveImpl implements VistaAdminServices.Retrieve {

        @Override
        public IEntity execute(EntityQueryCriteria request) {
            EntityQueryCriteria<User> criteria;
            if (request instanceof EntityCriteriaByPK) {
                criteria = EntityCriteriaByPK.create(User.class, ((EntityCriteriaByPK<?>) request).getPrimaryKey());
            } else {
                criteria = new EntityQueryCriteria<User>(User.class);
                for (Criterion c : ((EntityQueryCriteria<?>) request).getFilters()) {
                    criteria.add(c);
                }
            }
            User user = (User) super.execute(criteria);

            EditableUser editableUser = EntityFactory.create(EditableUser.class);
            editableUser.setPrimaryKey(user.getPrimaryKey());
            editableUser.user().set(user);

            // -- copy Authentication data but not credentials.
            UserCredential crs = PersistenceServicesFactory.getPersistenceService().retrieve(UserCredential.class, user.getPrimaryKey());
            editableUser.enabled().set(crs.enabled());
            editableUser.behavior().set(crs.behavior());

            return editableUser;
        }
    }

    public static class SearchImpl extends EntityServicesImpl.SearchImpl implements VistaAdminServices.Search {

    }
}
