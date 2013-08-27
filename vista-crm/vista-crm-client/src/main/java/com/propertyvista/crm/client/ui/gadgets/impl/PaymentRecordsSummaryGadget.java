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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTable.SortChangeHandler;
import com.pyx4j.forms.client.ui.datatable.DataTablePanel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.client.ui.gadgets.forms.PaymentsSummaryGadgetMetadataForm;
import com.propertyvista.crm.client.ui.gadgets.util.ListerUtils;
import com.propertyvista.crm.client.ui.gadgets.util.Provider;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.PaymentRecordsSummaryGadgetService;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentFeesDTO;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentFeesHolderDTO;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentsSummary;
import com.propertyvista.domain.dashboard.gadgets.type.PaymentsSummaryGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.property.asset.building.Building;

public class PaymentRecordsSummaryGadget extends GadgetInstanceBase<PaymentsSummaryGadgetMetadata> {

    private final static I18n i18n = I18n.get(PaymentRecordsSummaryGadget.class);

    private final static List<ColumnDescriptor> DEFAULT_PAYMENTS_SUMMARY_COLUMN_DESCRIPTORS;
    static {
        PaymentsSummary proto = EntityFactory.getEntityPrototype(PaymentsSummary.class);
        DEFAULT_PAYMENTS_SUMMARY_COLUMN_DESCRIPTORS = Arrays.asList(//@formatter:off
                (PaymentsSummary.summaryByBuilding)?
                        new MemberColumnDescriptor.Builder(proto.building()).build():
                        new MemberColumnDescriptor.Builder(proto.merchantAccount().accountNumber()).title(i18n.ntr("Merchant Account")).build(),
                        new MemberColumnDescriptor.Builder(proto.status()).build(),
                        new MemberColumnDescriptor.Builder(proto.cash()).build(),
                        new MemberColumnDescriptor.Builder(proto.check()).build(),
                        new MemberColumnDescriptor.Builder(proto.eCheck()).build(),
                        new MemberColumnDescriptor.Builder(proto.eft()).build(),
                        new MemberColumnDescriptor.Builder(proto.visa()).build(),
                        new MemberColumnDescriptor.Builder(proto.visaDebit()).build(),
                        new MemberColumnDescriptor.Builder(proto.masterCard()).build(),
                        new MemberColumnDescriptor.Builder(proto.cc()).build(),
                        new MemberColumnDescriptor.Builder(proto.interac()).build()
                
        );//@formatter:on
    }

    private final PaymentRecordsSummaryGadgetService service;

    private CEntityForm<PaymentFeesHolderDTO> paymentFeesForm;

    private HTML summaryTitlePanel;

    private DataTablePanel<PaymentsSummary> paymentsSummaryTablePanel;

    private int pageNumber;

    public PaymentRecordsSummaryGadget(PaymentsSummaryGadgetMetadata gadgetMetadata) {
        super(gadgetMetadata, PaymentsSummaryGadgetMetadata.class, new PaymentsSummaryGadgetMetadataForm());
        service = GWT.<PaymentRecordsSummaryGadgetService> create(PaymentRecordsSummaryGadgetService.class);
        pageNumber = 0;
        setDefaultPopulator(new Populator() {
            @Override
            public void populate() {
                populatePage(pageNumber);
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

    private void populatePage(final int pageNumber) {
        service.paymentRecordsSummary(//@formatter:off
                new AsyncCallback<EntitySearchResult<PaymentsSummary>>() {
                    
                    @Override
                    public void onSuccess(EntitySearchResult<PaymentsSummary> result) {
                        paymentsSummaryTablePanel.setPageSize(getMetadata().paymentsSummaryListerSettings().pageSize().getValue());
                        paymentsSummaryTablePanel.populateData(result.getData(), pageNumber, result.hasMoreData(), result.getTotalRows());
                        populateFeesPanel();
                    }
                    
                    @Override
                    public void onFailure(Throwable caught) {
                        populateFailed(caught);
                    }
                    
                }, 
                new Vector<Building>(containerBoard.getSelectedBuildingsStubs()),
                getStatusDate(),
                new Vector<PaymentRecord.PaymentStatus>(getMetadata().paymentStatus()),
                pageNumber,
                getMetadata().paymentsSummaryListerSettings().pageSize().getValue(),
                new Vector<Sort>(paymentsSummaryTablePanel.getDataTable().getDataTableModel().getSortCriteria())
        );//@formatter:on
    }

    @Override
    protected Widget initContentPanel() {

        VerticalPanel contentPanel = new VerticalPanel();
        contentPanel.setWidth("100%");

        contentPanel.add(initSummaryPanel());
        contentPanel.add(initFeesPanel());

        return contentPanel;
    }

    private Widget initSummaryPanel() {
        VerticalPanel summaryPanel = new VerticalPanel();
        summaryPanel.setWidth("100%");

        summaryTitlePanel = new HTML();
        summaryTitlePanel.setWidth("100%");
        summaryTitlePanel.getElement().getStyle().setProperty("textAlign", "center");
        summaryPanel.add(summaryTitlePanel);

        summaryPanel.add(initPaymentSummaryTableWidget());

        return summaryPanel;
    }

    private Widget initPaymentSummaryTableWidget() {
        paymentsSummaryTablePanel = new DataTablePanel<PaymentsSummary>(PaymentsSummary.class);
        ListerUtils.bind(paymentsSummaryTablePanel)//@formatter:off
        .columnDescriptors(DEFAULT_PAYMENTS_SUMMARY_COLUMN_DESCRIPTORS)
        .setupable(ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(getMetadata().ownerUser().getPrimaryKey()))
        .userSettingsProvider(new Provider<ListerUserSettings>() {
            @Override
            public ListerUserSettings get() {
                return getMetadata().paymentsSummaryListerSettings();
            }
         })
        .onColumnSelectionChanged(new Command() {
            @Override
            public void execute() {
                saveMetadata();
            }
        })
        .init();
        //@formatter:on

        paymentsSummaryTablePanel.setFilterApplyCommand(new Command() {
            @Override
            public void execute() {
                populate(false);
            }
        });
        paymentsSummaryTablePanel.setFirstActionHandler(new Command() {
            @Override
            public void execute() {
                pageNumber = 0;
                populate(false);
            }
        });
        paymentsSummaryTablePanel.setPrevActionHandler(new Command() {
            @Override
            public void execute() {
                if (pageNumber != 0) {
                    --pageNumber;
                    populate(false);
                }
            }
        });
        paymentsSummaryTablePanel.setNextActionHandler(new Command() {
            @Override
            public void execute() {
                pageNumber += 1;
                populate(false);
            }
        });
        paymentsSummaryTablePanel.setLastActionHandler(new Command() {
            @Override
            public void execute() {
                pageNumber = (paymentsSummaryTablePanel.getDataTableModel().getTotalRows() - 1)
                        / getMetadata().paymentsSummaryListerSettings().pageSize().getValue();
                populate(false);
            }
        });
        paymentsSummaryTablePanel.getDataTable().addSortChangeHandler(new SortChangeHandler<PaymentsSummary>() {
            @Override
            public void onChange(ColumnDescriptor column) {
                populate(false);
            }
        });

        return paymentsSummaryTablePanel;
    }

    private Widget initFeesPanel() {
        VerticalPanel feesPanel = new VerticalPanel();
        feesPanel.setWidth("100%");

        HTML feesCaption = new HTML(new SafeHtmlBuilder().appendEscaped(i18n.tr("Note: The following fees are applied (as of today)")).toSafeHtml());
        feesCaption.setWidth("100%");
        feesCaption.getElement().getStyle().setPaddingTop(2, Unit.EM);
        feesCaption.getElement().getStyle().setProperty("textAlign", "center");
        feesPanel.add(feesCaption);

        paymentFeesForm = new PaymentFeesForm();
        paymentFeesForm.initContent();

        feesPanel.add(paymentFeesForm);
        feesPanel.setCellHorizontalAlignment(paymentFeesForm, HasHorizontalAlignment.ALIGN_CENTER);

        return feesPanel;
    }

    private void redrawSummaryCaption() {
        String statusFilterView = !getMetadata().paymentStatus().isEmpty() ? makeListView(getMetadata().paymentStatus()) : i18n.tr("none");

        summaryTitlePanel.setHTML(new SafeHtmlBuilder()//@formatter:off                     
                .appendHtmlConstant("<div>")
                    .appendEscaped(i18n.tr("Target Date: {0,date,short}", getStatusDate()))
                .appendHtmlConstant("</div>")
                                    
                .appendHtmlConstant("<div>")                    
                    .appendEscaped(i18n.tr("Payment Statuses: {0}", statusFilterView))
                .appendHtmlConstant("</div>")
                .toSafeHtml());//@formatter:on

    }

    private String makeListView(Iterable<?> col) {
        StringBuilder viewBuilder = new StringBuilder();
        Iterator<?> i = col.iterator();
        if (i.hasNext()) {
            viewBuilder.append(i.next().toString());
        }
        while (i.hasNext()) {
            viewBuilder.append(", ").append(i.next().toString());
        }
        return viewBuilder.toString();

    }

    private void populateFeesPanel() {
        service.fundsTransferFees(new AsyncCallback<PaymentFeesHolderDTO>() {

            @Override
            public void onFailure(Throwable caught) {
                populateFailed(caught);
            }

            @Override
            public void onSuccess(PaymentFeesHolderDTO result) {
                paymentFeesForm.populate(result);
                redrawSummaryCaption();
                populateSucceded();
            }
        });

    }

    private LogicalDate getStatusDate() {
        return getMetadata().customizeDate().isBooleanTrue() ? getMetadata().asOf().getValue() : new LogicalDate(ClientContext.getServerDate());
    }

    private static final class PaymentFeesForm extends CEntityDecoratableForm<PaymentFeesHolderDTO> {

        public PaymentFeesForm() {
            super(PaymentFeesHolderDTO.class);
            setViewable(true);
            setEditable(false);
        }

        @Override
        public IsWidget createContent() {
            FlowPanel panel = new FlowPanel();
            panel.add(inject(proto().paymentFees(), new PaymentFeesFolder()));
            return panel;
        }

        private static final class PaymentFeesFolder extends VistaTableFolder<PaymentFeesDTO> {

            private final List<EntityFolderColumnDescriptor> columns;

            public PaymentFeesFolder() {
                super(PaymentFeesDTO.class);
                setAddable(false);
                setRemovable(false);
                columns = Arrays.asList(//@formatter:off
                        new EntityFolderColumnDescriptor(proto().paymentFeePolicy(), "10em"),
                        new EntityFolderColumnDescriptor(proto().visa(), "8em"),
                        new EntityFolderColumnDescriptor(proto().visaDebit(), "8em"),
                        new EntityFolderColumnDescriptor(proto().masterCard(), "8em"),
                        new EntityFolderColumnDescriptor(proto().eCheck(), "8em"),
                        new EntityFolderColumnDescriptor(proto().directBanking(), "8em")
                );//@formatter:on
            }

            @Override
            public List<EntityFolderColumnDescriptor> columns() {
                return columns;
            }

        }
    }
}