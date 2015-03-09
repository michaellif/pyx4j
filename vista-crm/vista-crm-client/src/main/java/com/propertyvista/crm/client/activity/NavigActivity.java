/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 28, 2014
 * @author michaellif
 */
package com.propertyvista.crm.client.activity;

import java.util.Vector;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.ClientLocaleUtils;
import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.login.GetSatisfaction;
import com.propertyvista.crm.client.event.BoardUpdateEvent;
import com.propertyvista.crm.client.ui.NavigView;
import com.propertyvista.crm.client.ui.NavigView.NavigPresenter;
import com.propertyvista.crm.rpc.services.MessageCategoryCrudService;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.EvictionFlowPolicyCrudService;
import com.propertyvista.crm.rpc.services.reports.CrmAvailableReportService;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.policy.dto.EvictionFlowPolicyDTO;
import com.propertyvista.domain.policy.policies.EvictionFlowPolicy;
import com.propertyvista.domain.policy.policies.domain.EvictionFlowStep.EvictionStepType;
import com.propertyvista.domain.reports.AvailableCrmReport;
import com.propertyvista.domain.reports.AvailableCrmReport.CrmReportType;
import com.propertyvista.shared.i18n.CompiledLocale;

public class NavigActivity extends AbstractActivity implements NavigPresenter {

    private final NavigView view;

    public NavigActivity() {
        view = CrmSite.getViewFactory().getView(NavigView.class);
        view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        obtainAvailableLocales();
        updateDashboardItems();

        // Do not load hidden menus immediately to let main view load first
        Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                updateMessageCategoryItems();
                updateAvailableReportItems();
                return false;
            }
        }, 1300);
    }

    public void withPlace(Place place) {
        view.updateUserName(ClientContext.getUserVisit().getName());

        if (place instanceof AppPlace) {
            view.select((AppPlace) place);
        }
    }

    @Override
    public void getSatisfaction() {
        GetSatisfaction.open();
    };

    @Override
    public void logout() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                ClientContext.logout(new DefaultAsyncCallback<AuthenticationResponse>() {
                    @Override
                    public void onSuccess(AuthenticationResponse result) {
                        AppSite.getPlaceController().goTo(AppPlace.NOWHERE);
                    }
                });
            }
        });
    }

    public void onBoardUpdate(BoardUpdateEvent event) {
        if (DashboardMetadata.class.equals(event.getTargetEntityType())) {
            updateDashboardItems();
        } else if (MessageCategory.class.equals(event.getTargetEntityType())) {
            updateMessageCategoryItems();
        } else if (AvailableCrmReport.class.equals(event.getTargetEntityType())) {
            updateAvailableReportItems();
        } else if (EvictionFlowPolicy.class.equals(event.getTargetEntityType())) {
            updateN4BatchItems();
        }
    }

    private void updateDashboardItems() {
        if (SecurityController.check(DataModelPermission.permissionRead(DashboardMetadata.class))) {
            GWT.<DashboardMetadataCrudService> create(DashboardMetadataCrudService.class).list(
                    new DefaultAsyncCallback<EntitySearchResult<DashboardMetadata>>() {

                        @Override
                        public void onSuccess(EntitySearchResult<DashboardMetadata> result) {
                            view.updateDashboards(result.getData());
                        }

                    }, EntityListCriteria.create(DashboardMetadata.class));
        }
    }

    private void updateAvailableReportItems() {
        GWT.<CrmAvailableReportService> create(CrmAvailableReportService.class).obtainAvailableReportTypes(new DefaultAsyncCallback<Vector<CrmReportType>>() {
            @Override
            public void onSuccess(Vector<CrmReportType> result) {
                view.updateAvailableReports(result);
            }

        });
    }

    private void updateMessageCategoryItems() {
        if (SecurityController.check(DataModelPermission.permissionRead(MessageCategory.class))) {
            GWT.<MessageCategoryCrudService> create(MessageCategoryCrudService.class).list(new DefaultAsyncCallback<EntitySearchResult<MessageCategory>>() {

                @Override
                public void onSuccess(EntitySearchResult<MessageCategory> result) {
                    view.updateCommunicationGroups(result.getData());
                }

            }, EntityListCriteria.create(MessageCategory.class));
        }
    }

    private void updateN4BatchItems() {
        EntityListCriteria<EvictionFlowPolicyDTO> criteria = EntityListCriteria.create(EvictionFlowPolicyDTO.class);
        criteria.eq(criteria.proto().evictionFlow().$().stepType(), EvictionStepType.N4);
        GWT.<EvictionFlowPolicyCrudService> create(EvictionFlowPolicyCrudService.class).list(
                new DefaultAsyncCallback<EntitySearchResult<EvictionFlowPolicyDTO>>() {

                    @Override
                    public void onSuccess(EntitySearchResult<EvictionFlowPolicyDTO> result) {
                        view.setN4BatchesVisible(!result.getData().isEmpty());
                    }
                }, criteria);
    }

    private void obtainAvailableLocales() {
        view.setAvailableLocales(ClientLocaleUtils.obtainAvailableLocales());
    }

    @Override
    public void setLocale(CompiledLocale locale) {
        ClientLocaleUtils.changeApplicationLocale(locale);
    }

}
