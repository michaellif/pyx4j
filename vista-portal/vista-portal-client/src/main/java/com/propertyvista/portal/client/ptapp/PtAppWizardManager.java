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

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.propertyvista.portal.domain.DemoData;
import com.propertyvista.portal.domain.VistaBehavior;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.UnitSelectionCriteria;
import com.propertyvista.portal.rpc.pt.PotentialTenantServices;
import com.propertyvista.portal.rpc.pt.SiteMap;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.ClientSecurityController;
import com.pyx4j.security.client.SecurityControllerEvent;
import com.pyx4j.security.client.SecurityControllerHandler;
import com.pyx4j.site.rpc.AppPlace;

public class PtAppWizardManager implements SecurityControllerHandler {

    private static I18n i18n = I18nFactory.getI18n(PtAppWizardManager.class);

    private static PtAppWizardManager instance;

    private Application application;

    private final SiteGinjector ginjector;

    private UnitSelectionCriteria unitSelectionCriteria;

    private Message messageDialog;

    public static class WizardStep {

        enum StepStatus {

            Visited,

            Incomleate
        }

        AppPlace place;

    }

    private PtAppWizardManager(SiteGinjector ginjector) {
        this.ginjector = ginjector;
        ginjector.getEventBus().addHandler(SecurityControllerEvent.getType(), this);
        //TODO implement initial application message
        //showMessageDialog(i18n.tr("Application is looking for building availability..."), i18n.tr("Loading..."), null, null);
        obtainAuthenticationData();
    }

    public static void initWizard(final SiteGinjector ginjector) {
        if (instance == null) {
            instance = new PtAppWizardManager(ginjector);
        } else {
            throw new RuntimeException("PtAppWizardManager is already initialized");
        }
    }

    public static PtAppWizardManager instance() {
        if (instance == null) {
            throw new RuntimeException("PtAppWizardManager is not yet initialized");
        }
        return instance;
    }

    private void obtainAuthenticationData() {
        ClientContext.obtainAuthenticationData(new DefaultAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                obtainUnitSelection();
            }

            //TODO remove this when initial application message is implemented
            @Override
            public void onFailure(Throwable caught) {
                ginjector.getPlaceHistoryHandler().handleCurrentHistory();
                super.onFailure(caught);
            }
        });
    }

    private void obtainUnitSelection() {
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
                ginjector.getPlaceHistoryHandler().handleCurrentHistory();
                if (!result) {
                    showMessageDialog("We can't find that building", "Error", "Back", new Command() {
                        @Override
                        public void execute() {
                            History.back();
                        }
                    });
                }
            }

            //TODO remove this when initial application message is implemented
            @Override
            public void onFailure(Throwable caught) {
                ginjector.getPlaceHistoryHandler().handleCurrentHistory();
                super.onFailure(caught);
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
        Place current = ginjector.getPlaceController().getWhere();
        ginjector.getPlaceController().goTo(new SiteMap.Apartment());
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
            ginjector.getPlaceController().goTo(new SiteMap.CreateAccount());
        }
    }

    public static EventBus getEventBus() {
        return instance().ginjector.getEventBus();
    }

    public static Message getMessageDialog() {
        return instance().messageDialog;
    }

    public static void showMessageDialog(String message, String title, String buttonText, Command command) {
        instance().messageDialog = new Message(message, title, buttonText, command);
        instance().ginjector.getPlaceController().goTo(new SiteMap.GenericMessage());
    }

    public static class Message {

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
