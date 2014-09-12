/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 9, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.crm.client.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.config.shared.ApplicationBackend;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.BehaviorChangeEvent;
import com.pyx4j.security.client.BehaviorChangeHandler;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.ContextChangeEvent;
import com.pyx4j.security.client.ContextChangeHandler;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.shared.domain.Notification;
import com.pyx4j.site.shared.domain.Notification.NotificationType;

import com.propertyvista.common.client.config.VistaFeaturesCustomizationClient;
import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.NotificationsView;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.shared.config.VistaDemo;

public class NotificationsActivity extends AbstractActivity implements NotificationsView.NotificationsPresenter {

    private static final I18n i18n = I18n.get(NotificationsActivity.class);

    private final NotificationsView view;

    private final List<Notification> notifications = new ArrayList<>();

    private final Notification productionNotification = new Notification(i18n.tr("This is PRODUCTION Environment!"), i18n.tr("PRODUCTION SUPPORT!"),
            NotificationType.WARNING);

    public NotificationsActivity() {
        view = CrmSite.getViewFactory().getView(NotificationsView.class);
        if (VistaFeaturesCustomizationClient.enviromentTitleVisible && VistaDemo.isDemo()) {
            notifications.add(new Notification(i18n.tr("This is Demo Environment"), i18n.tr("Demo Environment"), NotificationType.INFO));
        }
    }

    @Override
    public void start(AcceptsOneWidget container, EventBus eventBus) {
        view.setPresenter(this);
        container.setWidget(view);

        updateAuthenticatedView();
        view.showNotifications(notifications);

        eventBus.addHandler(BehaviorChangeEvent.getType(), new BehaviorChangeHandler() {
            @Override
            public void onBehaviorChange(BehaviorChangeEvent event) {
                updateAuthenticatedView();
                view.showNotifications(notifications);
            }
        });

        eventBus.addHandler(ContextChangeEvent.getType(), new ContextChangeHandler() {

            @Override
            public void onContextChange(ContextChangeEvent event) {
                updateAuthenticatedView();
                view.showNotifications(notifications);
            }
        });

    }

    public NotificationsActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void acceptMessage(Notification notification) {
        notifications.remove(notification);
        view.showNotifications(notifications);
    }

    private void updateAuthenticatedView() {
        if (ClientContext.isAuthenticated() && SecurityController.check(VistaBasicBehavior.PropertyVistaSupport) && ApplicationBackend.isProductionBackend()) {
            notifications.add(productionNotification);
        } else {
            notifications.remove(productionNotification);
        }
    }

}
