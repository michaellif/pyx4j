/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 27, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.impl;

import static com.propertyvista.crm.svg.gadgets.util.LabelHelper.makeListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.prime.lister.EntityDataTablePanel;
import com.pyx4j.site.client.ui.prime.lister.ListerDataSource;

import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.client.ui.gadgets.forms.PaymentRecordsGadgetMetadataForm;
import com.propertyvista.crm.client.ui.gadgets.util.ListerUtils;
import com.propertyvista.crm.client.ui.gadgets.util.ListerUtils.ItemSelectCommand;
import com.propertyvista.crm.client.ui.gadgets.util.Provider;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.PaymentRecordsGadgetListService;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentRecordForReportDTO;
import com.propertyvista.domain.dashboard.gadgets.type.PaymentRecordsGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.PaymentType;

public class PaymentRecordsGadget extends GadgetInstanceBase<PaymentRecordsGadgetMetadata> {

    private final static I18n i18n = I18n.get(PaymentRecordsGadget.class);

    private final static List<ColumnDescriptor> DEFAULT_COLUMN_DESCRIPTORS;
    static {
        PaymentRecordForReportDTO proto = EntityFactory.getEntityPrototype(PaymentRecordForReportDTO.class);
        DEFAULT_COLUMN_DESCRIPTORS = Arrays.asList(//@formatter:off
                new MemberColumnDescriptor.Builder(proto.merchantAccount()).build(),
                new MemberColumnDescriptor.Builder(proto.building()).build(),
                new MemberColumnDescriptor.Builder(proto.lease()).build(),
                new MemberColumnDescriptor.Builder(proto.tenant()).build(),                    
                new MemberColumnDescriptor.Builder(proto.method()).build(),
                new MemberColumnDescriptor.Builder(proto.status()).build(),
                new MemberColumnDescriptor.Builder(proto.created()).build(),
                new MemberColumnDescriptor.Builder(proto.received()).build(),
                new MemberColumnDescriptor.Builder(proto.finalized()).build(),
                new MemberColumnDescriptor.Builder(proto.target()).build(),
                new MemberColumnDescriptor.Builder(proto.amount()).build()                    
        );//@formatter:on
    }

    private HTML titlePanel;

    private EntityDataTablePanel<PaymentRecordForReportDTO> lister;

    public PaymentRecordsGadget(PaymentRecordsGadgetMetadata gadgetMetadata) {
        super(gadgetMetadata, PaymentRecordsGadgetMetadata.class, new PaymentRecordsGadgetMetadataForm());
        setDefaultPopulator(new Populator() {
            @Override
            public void populate() {
                lister.getDataTablePanel().setPageSize(getMetadata().paymentRecordsListerSettings().pageSize().getValue());

                lister.getDataSource().clearPreDefinedFilters();
                List<Criterion> criteria = new ArrayList<Criterion>();
                criteria.add(PropertyCriterion.eq(lister.proto().lastStatusChangeDate(), getTargetDate()));

                if (!containerBoard.getSelectedBuildingsStubs().isEmpty()) {
                    criteria.add(PropertyCriterion.in(lister.proto().buildingFilterAnchor(), containerBoard.getSelectedBuildingsStubs()));
                }
                criteria.add(PropertyCriterion.in(lister.proto().method(), new Vector<PaymentType>(getMetadata().paymentMethodFilter().getValue())));
                criteria.add(PropertyCriterion.in(lister.proto().status(), new Vector<PaymentRecord.PaymentStatus>(getMetadata().paymentStatusFilter()
                        .getValue())));
                lister.getDataSource().setPreDefinedFilters(criteria);

                lister.obtain(0);
                redrawTitle();
                populateSucceded();
            }
        });
    }

    @Override
    public void setContainerBoard(IBuildingFilterContainer board) {
        super.setContainerBoard(board);
        board.addBuildingSelectionChangedEventHandler(new BuildingSelectionChangedEventHandler() {
            @Override
            public void onBuildingSelectionChanged(BuildingSelectionChangedEvent event) {
                populate();
            }
        });
    }

    @Override
    protected Widget initContentPanel() {
        VerticalPanel contentPanel = new VerticalPanel();
        contentPanel.setWidth("100%");

        contentPanel.add(initTitleWidget());

        lister = new EntityDataTablePanel<PaymentRecordForReportDTO>(PaymentRecordForReportDTO.class);
        lister.setDataSource(new ListerDataSource<PaymentRecordForReportDTO>(PaymentRecordForReportDTO.class, GWT
                .<PaymentRecordsGadgetListService> create(PaymentRecordsGadgetListService.class)));
        ListerUtils.bind(lister.getDataTablePanel())//@formatter:off
        .columnDescriptors(DEFAULT_COLUMN_DESCRIPTORS)
        .setupable(ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(getMetadata().ownerUser().getPrimaryKey()))
        .userSettingsProvider(new Provider<ListerUserSettings>() {
            @Override
            public ListerUserSettings get() {
                return getMetadata().paymentRecordsListerSettings();
            }
         })
        .onColumnSelectionChanged(new Command() {
            @Override
            public void execute() {
                saveMetadata();
            }
        })
        .onItemSelectedCommand(new ItemSelectCommand<PaymentRecordForReportDTO>() {                
            @Override
            public void execute(PaymentRecordForReportDTO item) {
                AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(PaymentRecord.class).formViewerPlace(item.getPrimaryKey()));
            }
        })
        .init();
        //@formatter:on

        contentPanel.add(lister);

        return contentPanel;
    }

    private LogicalDate getTargetDate() {
        return getMetadata().customizeTargetDate().isBooleanTrue() ? getMetadata().targetDate().getValue() : new LogicalDate(ClientContext.getServerDate());
    }

    private Widget initTitleWidget() {
        titlePanel = new HTML("");
        return titlePanel;
    }

    private void redrawTitle() {
        // actually "none" is never supposed to happen thanks to automatic setup form validation
        String paymentTypeFilterView = !getMetadata().paymentMethodFilter().isEmpty() ? makeListView(getMetadata().paymentMethodFilter()) : i18n.tr("none");
        String statusFilterView = !getMetadata().paymentStatusFilter().isEmpty() ? makeListView(getMetadata().paymentStatusFilter()) : i18n.tr("none");

        titlePanel.setHTML(new SafeHtmlBuilder()//@formatter:off                     
                .appendHtmlConstant("<div>")
                    .appendEscaped(i18n.tr("Target Date: {0,date,short}", getTargetDate()))
                .appendHtmlConstant("</div>")
                
                .appendHtmlConstant("<div>")                    
                    .appendEscaped(i18n.tr("Payment Method: {0}", paymentTypeFilterView))
                .appendHtmlConstant("</div>")
                
                .appendHtmlConstant("<div>")                    
                    .appendEscaped(i18n.tr("Payment Statuses: {0}", statusFilterView))
                .appendHtmlConstant("</div>")
                
                .toSafeHtml());//@formatter:on

        titlePanel.getElement().getStyle().setProperty("textAlign", "center");
    }

}