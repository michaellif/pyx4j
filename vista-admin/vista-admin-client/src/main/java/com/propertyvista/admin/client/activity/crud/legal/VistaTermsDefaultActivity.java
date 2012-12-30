/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 22, 2012
 * @author dev_vista
 * @version $Id$
 */
package com.propertyvista.admin.client.activity.crud.legal;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.admin.domain.legal.LegalDocument;
import com.propertyvista.admin.domain.legal.VistaTerms;
import com.propertyvista.admin.domain.legal.VistaTerms.Target;
import com.propertyvista.admin.rpc.services.VistaTermsCrudService;
import com.propertyvista.shared.i18n.CompiledLocale;

public class VistaTermsDefaultActivity extends AbstractActivity {

    private final CrudAppPlace place;

    private final Target target;

    public VistaTermsDefaultActivity(CrudAppPlace place, VistaTerms.Target target) {
        assert target != null;
        this.target = target;
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        VistaTermsCrudService srv = GWT.create(VistaTermsCrudService.class);
        srv.retrieveTerms(new DefaultAsyncCallback<Key>() {
            @Override
            public void onSuccess(Key result) {
                CrudAppPlace dst = AppSite.getHistoryMapper().createPlace(place.getClass());
                if (result != null) {
                    dst.formViewerPlace(result);
                } else {
                    LegalDocument doc = EntityFactory.create(LegalDocument.class);
                    doc.locale().setValue(CompiledLocale.en);
                    VistaTerms terms = EntityFactory.create(VistaTerms.class);
                    terms.target().setValue(target);
                    terms.version().document().add(doc);
                    dst.formNewItemPlace(terms);
                }
                AppSite.getPlaceController().goTo(dst);
            }
        }, target);
    }
}
