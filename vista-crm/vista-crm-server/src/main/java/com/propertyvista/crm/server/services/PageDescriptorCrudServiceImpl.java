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
package com.propertyvista.crm.server.services;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.services.PageDescriptorCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceImpl;
import com.propertyvista.domain.site.Locale;
import com.propertyvista.domain.site.PageContent;
import com.propertyvista.domain.site.PageDescriptor;

public class PageDescriptorCrudServiceImpl extends GenericCrudServiceImpl<PageDescriptor> implements PageDescriptorCrudService {

    private Locale.Lang lang = Locale.Lang.english;

    public PageDescriptorCrudServiceImpl() {
        super(PageDescriptor.class);
    }

    @Override
    public void setLang(AsyncCallback<Boolean> callback, Locale.Lang lang) {
        this.lang = lang;
        callback.onSuccess(true);
    }

    @Override
    public void retrieveLandingPage(AsyncCallback<Key> callback) {
        EntityQueryCriteria<PageDescriptor> criteria = EntityQueryCriteria.create(PageDescriptor.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().lang(), lang));
        criteria.add(PropertyCriterion.eq(criteria.proto().type(), PageDescriptor.Type.landing));
        List<Key> list = PersistenceServicesFactory.getPersistenceService().queryKeys(criteria);
        if (list.isEmpty()) {
            throw new Error("Landing page not found");
        } else {
            callback.onSuccess(list.get(0));
        }
    }

    @Override
    public void retrieve(AsyncCallback<PageDescriptor> callback, Key entityId) {
        PageDescriptor page = PersistenceServicesFactory.getPersistenceService().retrieve(dboClass, entityId);
        PersistenceServicesFactory.getPersistenceService().retrieve(page.content());
        EntityQueryCriteria<PageDescriptor> childPagesCriteria = EntityQueryCriteria.create(PageDescriptor.class);
        childPagesCriteria.add(PropertyCriterion.eq(childPagesCriteria.proto().parent(), page));
        page.childPages().addAll(PersistenceServicesFactory.getPersistenceService().query(childPagesCriteria));
        callback.onSuccess(page);
    }

    private void buildPath(PageDescriptor page) {
        List<String> parents = new Vector<String>();
        PageDescriptor c = (PageDescriptor) page.cloneEntity();
        do {
            parents.add(c.caption().getStringView());
            PersistenceServicesFactory.getPersistenceService().retrieve(c.parent());
            c = c.parent();
        } while (!c.parent().isNull());

        Collections.reverse(parents);
        StringBuilder path = new StringBuilder();
        for (String pe : parents) {
            path.append(PageContent.PATH_SEPARATOR);
            path.append(pe);
        }
        page.content().path().setValue(path.toString());
    }

    @Override
    public void create(AsyncCallback<PageDescriptor> callback, PageDescriptor entity) {
        buildPath(entity);
        entity.lang().setValue(lang);
        PersistenceServicesFactory.getPersistenceService().persist(entity);
        callback.onSuccess(entity);
    }

    @Override
    public void save(AsyncCallback<PageDescriptor> callback, PageDescriptor entity) {
        buildPath(entity);
        PersistenceServicesFactory.getPersistenceService().merge(entity);
        callback.onSuccess(entity);
    }

    @Override
    public void deleteChildPage(AsyncCallback<Boolean> callback, PageDescriptor page) {
        PersistenceServicesFactory.getPersistenceService().delete(page);
        callback.onSuccess(true);
    }
}
