/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 21, 2012
 * @author dev_vista
 * @version $Id$
 */
package com.propertyvista.admin.server.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.AbstractVersionedCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.VersionedCriteria;

import com.propertyvista.admin.domain.legal.VistaTerms;
import com.propertyvista.admin.rpc.services.VistaTermsCrudService;

public class VistaTermsCrudServiceImpl extends AbstractVersionedCrudServiceImpl<VistaTerms> implements VistaTermsCrudService {

    public VistaTermsCrudServiceImpl() {
        super(VistaTerms.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    public void retrieveTerms(AsyncCallback<Key> callback) {
        EntityQueryCriteria<VistaTerms> criteria = EntityQueryCriteria.create(VistaTerms.class);
        criteria.setVersionedCriteria(VersionedCriteria.onlyFinalized);
        List<Key> list = Persistence.service().queryKeys(criteria);
        if (!list.isEmpty()) {
            callback.onSuccess(list.get(0));
        } else {
            criteria.setVersionedCriteria(VersionedCriteria.onlyDraft);
            list = Persistence.service().queryKeys(criteria);
            callback.onSuccess(list.isEmpty() ? null : list.get(0));
        }
    }
}
