/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.communication;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.communication.CommunicationGroupViewerView;
import com.propertyvista.crm.rpc.services.CommunicationGroupCrudService;
import com.propertyvista.domain.communication.CommunicationGroup;
import com.propertyvista.domain.security.VistaCrmBehavior;

public class CommunicationSettingsViewActivity extends CrmViewerActivity<CommunicationGroup> implements CommunicationGroupViewerView.Presenter {
    public CommunicationSettingsViewActivity(CrudAppPlace place) {

        super(place, CrmSite.getViewFactory().getView(CommunicationGroupViewerView.class), (AbstractCrudService<CommunicationGroup>) GWT
                .create(CommunicationGroupCrudService.class));
    }

    @Override
    public boolean canEdit() {
        return super.canEdit() & SecurityController.checkBehavior(VistaCrmBehavior.Maintenance);
    }
/*
 * public static CommunicationSettingsViewActivity create(CrudAppPlace crudPlace) {
 * if (crudPlace instanceof CommunicationSettings.Broadcasting) {
 * return new CommunicationSettingsViewActivity(crudPlace.formEditorPlace(new Key(EndpointGroup.Broadcasting.toString())));
 * } else if (crudPlace instanceof CommunicationSettings.Commandant) {
 * return new CommunicationSettingsViewActivity(crudPlace.formEditorPlace(new Key(EndpointGroup.Commandant.toString())));
 * } else if (crudPlace instanceof CommunicationSettings.Corporate) {
 * return new CommunicationSettingsViewActivity(crudPlace.formEditorPlace(new Key(EndpointGroup.Corporate.toString())));
 * }
 * return null;
 * }
 */

}
