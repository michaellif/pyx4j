/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 8, 2011
 * @author Misha
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.portal.client.ptapp;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.propertyvista.portal.domain.DemoData;
import com.propertyvista.portal.domain.VistaBehavior;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.UnitSelectionCriteria;
import com.propertyvista.portal.rpc.pt.PotentialTenantServices;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.ClientSecurityController;
import com.pyx4j.security.client.SecurityControllerEvent;
import com.pyx4j.security.client.SecurityControllerHandler;
import com.pyx4j.site.client.place.AppPlace;
import com.pyx4j.widgets.client.GlassPanel;
import com.pyx4j.widgets.client.GlassPanel.GlassStyle;

public class PtAppWizardManager implements SecurityControllerHandler {

    private Application application;

    private final EventBus eventBus;

    private SiteGinjector ginjector;

    private final PlaceController placeController;

    private UnitSelectionCriteria unitSelectionCriteria;

    private Message messageDialog;

    public static class WizardStep {

        enum StepStatus {

            Visited,

            Incomleate
        }

        AppPlace place;

    }

    public PtAppWizardManager(PlaceController placeController, EventBus eventBus) {
        this.eventBus = eventBus;
        this.placeController = placeController;
        eventBus.addHandler(SecurityControllerEvent.getType(), this);

    }

    public void initWizard(final SiteGinjector ginjector) {
        GlassPanel.show(GlassStyle.Transparent);

        this.ginjector = ginjector;

        unitSelectionCriteria = EntityFactory.create(UnitSelectionCriteria.class);
        unitSelectionCriteria.propertyCode().setValue(Window.Location.getParameter("b"));
        unitSelectionCriteria.floorplanName().setValue(Window.Location.getParameter("u"));

        if (ApplicationMode.isDevelopment()) {
            if (unitSelectionCriteria.floorplanName().isNull()) {
                unitSelectionCriteria.floorplanName().setValue(DemoData.REGISTRATION_DEFAULT_FLOORPLAN);
            }
            if (unitSelectionCriteria.propertyCode().isNull()) {
                unitSelectionCriteria.propertyCode().setValue(DemoData.REGISTRATION_DEFAULT_BUILDINGNAME);
            }
        }

        RPCManager.execute(PotentialTenantServices.UnitExists.class, unitSelectionCriteria, new DefaultAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                //TODO Vlad - this should return true
                if (!result) {
                    obtainAuthenticationData();
                } else {
                    ginjector.getPlaceHistoryHandler().handleCurrentHistory();
                    showMessageDialog("We can't find that building", "Error", "Back", new Command() {
                        @Override
                        public void execute() {
                            History.back();
                        }
                    });
                    GlassPanel.hide();
                }
            }
        });

    }

    private void obtainAuthenticationData() {
        ClientContext.obtainAuthenticationData(new DefaultAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                ginjector.getPlaceHistoryHandler().handleCurrentHistory();
                GlassPanel.hide();
            }

            @Override
            public void onFailure(Throwable caught) {
                ginjector.getPlaceHistoryHandler().handleCurrentHistory();
                super.onFailure(caught);
                GlassPanel.hide();
            }
        });
    }

    public void saveApplicationProgress() {

        RPCManager.execute(PotentialTenantServices.Save.class, application, new DefaultAsyncCallback<IEntity>() {
            @Override
            public void onSuccess(IEntity result) {
                application = (Application) result;
            }
        });
    }

    protected void goToNextStep() {
        Place current = placeController.getWhere();
        placeController.goTo(new SiteMap.Apartment());
    }

    @Override
    public void onSecurityContextChange(SecurityControllerEvent event) {
        if (ClientSecurityController.checkBehavior(VistaBehavior.POTENCIAL_TENANT)) {

            RPCManager.execute(PotentialTenantServices.GetCurrentApplication.class, unitSelectionCriteria, new DefaultAsyncCallback<Application>() {

                @Override
                public void onSuccess(Application result) {
                    application = result;
                    goToNextStep();
                }
            });
        } else {
            application = null;
            placeController.goTo(new SiteMap.CreateAccount());
        }
    }

    public Message getMessageDialog() {
        return messageDialog;
    }

    public void showMessageDialog(String message, String title, String buttonText, Command command) {
        messageDialog = new Message(message, title, buttonText, command);
        placeController.goTo(new SiteMap.GenericMessage());
    }

    public class Message {

        private final String message;

        private final String title;

        private final String buttonText;

        private final Command command;

        public Message(String message, String title, String buttonText, Command command) {
            super();
            this.message = message;
            this.title = title;
            this.buttonText = buttonText;
            this.command = command;
        }

        public String getMessage() {
            return message;
        }

        public String getTitle() {
            return title;
        }

        public String getButtonText() {
            return buttonText;
        }

        public Command getCommand() {
            return command;
        }

    }

}
