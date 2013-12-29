/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 5, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.administration.website.content;

import java.util.EnumSet;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmCrudAppPlace;
import com.propertyvista.crm.rpc.CrmSiteMap.Administration.ContentManagement;
import com.propertyvista.crm.rpc.services.HomePageGadgetCrudService;
import com.propertyvista.domain.site.gadgets.HomePageGadget;
import com.propertyvista.dto.SiteDescriptorDTO;

public class SiteViewerImpl extends CrmViewerViewImplBase<SiteDescriptorDTO> implements SiteViewer {

    private static final I18n i18n = I18n.get(SiteViewerImpl.class);

    public SiteViewerImpl() {
        setForm(new SiteForm(this));

        // Add actions:
        addAction(new MenuItem(i18n.tr("Add Child Page"), new Command() {
            @Override
            public void execute() {
                Key valueKey = getForm().getValue().getPrimaryKey();
                if (valueKey != null) { // shouldn't be new unsaved value!..
                    newChild(valueKey);
                }
            }
        }));
        addAction(new MenuItem(i18n.tr("Add Home Page Gadget"), new Command() {
            @Override
            public void execute() {
                new GadgetSelectorDialog().show();
            }
        }));
        addAction(new MenuItem(i18n.tr("Add City Page"), new Command() {
            @Override
            public void execute() {
                Key parentId = getForm().getValue().getPrimaryKey();
                if (parentId != null) {
                    ((SiteViewer.Presenter) getPresenter()).editNew(parentId, ContentManagement.Website.CityIntroPage.class);
                }
            }
        }));
    }

    @Override
    public void viewChild(Key id) {
        ((SiteViewer.Presenter) getPresenter()).viewChild(id);
    }

    @Override
    public void viewChild(Key id, Class<? extends CrmCrudAppPlace> openPlaceClass) {
        ((SiteViewer.Presenter) getPresenter()).viewChild(id, openPlaceClass);
    }

    @Override
    public void newChild(Key parentid) {
        ((SiteViewer.Presenter) getPresenter()).editNew(parentid);
    }

    class GadgetSelectorDialog extends SelectEnumDialog<HomePageGadget.GadgetType> {
        public GadgetSelectorDialog() {
            super(i18n.tr("Select Gadget Type"), EnumSet.allOf(HomePageGadget.GadgetType.class));
        }

        @Override
        public boolean onClickOk() {
            final HomePageGadget.GadgetType type = getSelectedType();
            if (type != null) {
                HomePageGadgetCrudService.HomePageGadgetInitializationData id = EntityFactory
                        .create(HomePageGadgetCrudService.HomePageGadgetInitializationData.class);
                id.type().setValue(type);
                AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(HomePageGadget.class).formNewItemPlace(id));
                return true;
            }
            return false;
        }
    }
}