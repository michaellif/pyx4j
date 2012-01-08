/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 25, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents;

import java.util.Vector;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.ListBox;

import com.propertyvista.common.client.events.UserMessageEvent;
import com.propertyvista.portal.client.ui.decorations.UserMessagePanel;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.dto.MaintenanceRequestDTO;

public class NewMaintenanceRequestViewImpl extends FlowPanel implements NewMaintenanceRequestView {

    private static I18n i18n = I18n.get(NewMaintenanceRequestViewImpl.class);

    private final NewMaintenanceRequestForm form;

    private Presenter presenter;

    public NewMaintenanceRequestViewImpl() {
        add(new UserMessagePanel());

        form = new NewMaintenanceRequestForm();

        add(form);

        FlexTable buttonSet = new FlexTable();
        buttonSet.setWidth("100%");

        Button submitButton = new Button(i18n.tr("Submit"));
        submitButton.getElement().getStyle().setMargin(20, Unit.PX);
        submitButton.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
        submitButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                // TODO use presenter
                if (!form.isValid()) {
                    form.setVisited(true);
                    Window.scrollTo(0, 0);
                    AppSite.getEventBus().fireEvent(
                            new UserMessageEvent("The form was completed with errors outlined below. Please review and try again.", "",
                                    UserMessageEvent.UserMessageType.ERROR));
                } else {
                    presenter.submit();
                }
            }
        });
        buttonSet.setWidget(0, 0, submitButton);

        Button cancelButton = new Button(i18n.tr("Cancel"));
        cancelButton.getElement().getStyle().setMargin(20, Unit.PX);
        cancelButton.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // TODO use presenter
                AppSite.getPlaceController().goTo(new PortalSiteMap.Residents.Maintenance());
            }
        });
        buttonSet.setWidget(0, 1, cancelButton);

        add(buttonSet);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate(MaintenanceRequestDTO request) {
        form.populate(request);

    }

    class Selector<E extends IEntity> extends ListBox {

        private final Vector<E> values = new Vector<E>();

        Selector() {
            super(false);
            setWidth("100%");
            getElement().getStyle().setProperty("overflow", "auto");
            getElement().getStyle().setProperty("background", "white");
            clear();
        }

        @Override
        public void clear() {
            super.clear();
            values.clear();
        }

        public void clear(String defaultChoice) {
            clear();
            if (defaultChoice != null) {
                values.add(null);
                super.addItem(defaultChoice);
            }
        }

        void addItem(E entity, String label) {
            values.add(entity);
            super.addItem(label);
        }

        E getSelectedItem() {
            return values.get(getSelectedIndex());
        }
    }

}
