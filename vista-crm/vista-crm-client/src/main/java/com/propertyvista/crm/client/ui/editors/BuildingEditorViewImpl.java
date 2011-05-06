/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.editors;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.BuildingCrudService;
import com.propertyvista.portal.domain.Building;

public class BuildingEditorViewImpl extends DockLayoutPanel implements BuildingEditorView {

    private static I18n i18n = I18nFactory.getI18n(BuildingEditorViewImpl.class);

    private final BuildingEditorForm editor = new BuildingEditorForm();

    private final BuildingCrudService bcs = GWT.create(BuildingCrudService.class);

    public BuildingEditorViewImpl() {
        super(Unit.EM);
        setSize("100%", "100%");
        addNorth(new CrmHeaderDecorator(AppSite.getHistoryMapper().getPlaceInfo(new CrmSiteMap.Editors.Building()).getCaption()), 3);
        addSouth(createButtons(), 4);

        editor.initialize();
        add(new ScrollPanel(editor.asWidget()));
    }

    @Override
    public void setEditingEntityId(long entityId) {
        if (bcs != null) {
            bcs.retrieve(new AsyncCallback<Building>() {

                @Override
                public void onSuccess(Building result) {
                    editor.populate(result);
                }

                @Override
                public void onFailure(Throwable caught) {
                }
            }, entityId);
        }
    }

    private Widget createButtons() {
        HorizontalPanel buttons = new HorizontalPanel();
        buttons.add(new Button("Save", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                editor.setVisited(true);
                if (!editor.isValid()) {
                    throw new UserRuntimeException(editor.getValidationResults().getMessagesText(true));
                }

                if (bcs != null) {
                    bcs.save(new AsyncCallback<Building>() {

                        @Override
                        public void onSuccess(Building result) {
                            History.back();
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                        }
                    }, editor.getValue());
                }
            }
        }));
        buttons.add(new Button("Cancel", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                History.back();
            }
        }));
        buttons.setSpacing(10);
        SimplePanel wrap = new SimplePanel();
        wrap.getElement().getStyle().setProperty("borderTop", "1px solid #bbb");
        wrap.setWidget(buttons);
        wrap.setWidth("100%");
        return wrap;
    }
}
