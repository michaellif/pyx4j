/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 29, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.payments;

import static com.propertyvista.crm.client.ui.gadgets.util.ColumnDescriptorConverter.asColumnDesciptorEntityList;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.common.client.ui.components.SubsetSelector.Layout;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.c.CEnumSubsetSelector;
import com.propertyvista.crm.client.ui.board.BoardView;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.common.Directory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.common.ListerGadgetInstanceBase;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.PaymentReportService;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentRecordForReportDTO;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.PaymentRecordsGadgetMetadata;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.property.asset.building.Building;

public class PaymentRecordsGadgetFactory extends AbstractGadget<PaymentRecordsGadgetMetadata> {

    private final static I18n i18n = I18n.get(PaymentRecordsGadgetFactory.class);

    private static class PaymentRecordsGadget extends ListerGadgetInstanceBase<PaymentRecordForReportDTO, PaymentRecordsGadgetMetadata> {

        private final PaymentReportService service;

        private HTML titlePanel;

        public PaymentRecordsGadget(GadgetMetadata gadgetMetadata) {
            super(PaymentRecordsGadgetMetadata.class, gadgetMetadata, PaymentRecordForReportDTO.class, false);
            this.service = GWT.<PaymentReportService> create(PaymentReportService.class);
        }

        @Override
        public boolean isSetupable() {
            return true;
        }

        @Override
        public com.pyx4j.widgets.client.dashboard.IGadget.ISetup getSetup() {
            return new SetupForm(new CEntityDecoratableForm<PaymentRecordsGadgetMetadata>(PaymentRecordsGadgetMetadata.class) {

                @Override
                public IsWidget createContent() {
                    FormFlexPanel p = new FormFlexPanel();
                    int row = -1;

                    p.setWidget(++row, 0, new DecoratorBuilder(inject(proto().refreshInterval())).build());
                    p.setWidget(++row, 0, new DecoratorBuilder(inject(proto().pageSize())).build());
                    p.setWidget(++row, 0, new HTML("&nbsp"));
                    p.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customizeTargetDate())).build());
                    p.setWidget(++row, 0, new DecoratorBuilder(inject(proto().targetDate())).build());
                    get(proto().targetDate()).setVisible(false);
                    get(proto().customizeTargetDate()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                        @Override
                        public void onValueChange(ValueChangeEvent<Boolean> event) {
                            if (event.getValue() != null) {
                                get(proto().targetDate()).setVisible(event.getValue());
                            }
                        }
                    });
                    p.setWidget(++row, 0, new HTML("&nbsp"));
                    p.setWidget(++row, 0, new DecoratorBuilder(inject(proto().paymentType())).build());
                    CComponent<Set<PaymentRecord.PaymentStatus>, ?> subsetSelector = new CEnumSubsetSelector<PaymentRecord.PaymentStatus>(
                            PaymentRecord.PaymentStatus.class, Layout.Horizontal);
                    subsetSelector.addValueValidator(new EditableValueValidator<Set<PaymentRecord.PaymentStatus>>() {
                        @Override
                        public ValidationFailure isValid(CComponent<Set<PaymentStatus>, ?> component, Set<PaymentStatus> value) {
                            if (value != null && value.isEmpty()) {
                                return new ValidationFailure(i18n.tr("Please select at lease one payment status option"));
                            } else {
                                return null;
                            }
                        }
                    });
                    p.setWidget(++row, 0, new DecoratorBuilder(inject(proto().paymentStatus(), subsetSelector), 50).build());

                    return p;
                }

                @Override
                protected void onPopulate() {
                    super.onPopulate();
                    get(proto().targetDate()).setVisible(getValue().customizeTargetDate().isBooleanTrue());
                }
            });
        };

        @Override
        public void setContainerBoard(final BoardView board) {
            super.setContainerBoard(board);
            board.addBuildingSelectionChangedEventHandler(new BuildingSelectionChangedEventHandler() {
                @Override
                public void onBuildingSelectionChanged(BuildingSelectionChangedEvent event) {
                    populate();
                }
            });
        }

        @Override
        protected PaymentRecordsGadgetMetadata createDefaultSettings(Class<PaymentRecordsGadgetMetadata> metadataClass) {
            PaymentRecordsGadgetMetadata settings = super.createDefaultSettings(metadataClass);

            // all payment types
            settings.paymentType().setValue(null);

            // all statuses
            settings.paymentStatus().addAll(EnumSet.allOf(PaymentRecord.PaymentStatus.class));

            PaymentRecordForReportDTO proto = EntityFactory.create(PaymentRecordForReportDTO.class);
            settings.columnDescriptors().addAll(asColumnDesciptorEntityList(Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto.billingAccount().accountNumber()).title(i18n.tr("Merchant Account")).build(),
                    new MemberColumnDescriptor.Builder(proto.billingAccount().lease().unit().belongsTo().propertyCode()).title(i18n.tr("Building")).build(),
                    new MemberColumnDescriptor.Builder(proto.billingAccount().lease().leaseId()).title(i18n.tr("Lease")).build(),
                    new MemberColumnDescriptor.Builder(proto.primaryTenant()).title(i18n.tr("Tenant")).build(),                    
                    new MemberColumnDescriptor.Builder(proto.paymentMethod().type()).title(i18n.tr("Method")).build(),
                    new MemberColumnDescriptor.Builder(proto.paymentStatus()).title(i18n.tr("Status")).build(),
                    new MemberColumnDescriptor.Builder(proto.createdDate()).title(i18n.tr("Created")).build(),
                    new MemberColumnDescriptor.Builder(proto.receivedDate()).title(i18n.tr("Received")).build(),
                    new MemberColumnDescriptor.Builder(proto.finalizeDate()).title(i18n.tr("Finalized")).build(),
                    new MemberColumnDescriptor.Builder(proto.targetDate()).title(i18n.tr("Target")).build(),
                    new MemberColumnDescriptor.Builder(proto.amount()).title(i18n.tr("Amount")).build()                    
            )));//@formatter:on
            return settings;
        }

        @Override
        protected Widget initContentPanel() {
            VerticalPanel contentPanel = new VerticalPanel();
            contentPanel.setWidth("100%");

            contentPanel.add(initTitleWidget());
            contentPanel.add(initListerWidget());
            return contentPanel;
        }

        @Override
        protected void populatePage(final int pageNumber) {
            service.paymentRecords(new AsyncCallback<EntitySearchResult<PaymentRecordForReportDTO>>() {

                @Override
                public void onSuccess(EntitySearchResult<PaymentRecordForReportDTO> result) {
                    redrawTitle();
                    setPageData(result.getData(), pageNumber, result.getTotalRows(), result.hasMoreData());
                    populateSucceded();
                }

                @Override
                public void onFailure(Throwable caught) {
                    populateFailed(caught);
                }

            }, new Vector<Building>(containerBoard.getSelectedBuildingsStubs()), getTargetDate(), getMetadata().paymentType().getValue(),
                    new Vector<PaymentRecord.PaymentStatus>(getMetadata().paymentStatus()), pageNumber, getPageSize(), new Vector<Sort>(
                            getListerSortingCriteria()));
        }

        private LogicalDate getTargetDate() {
            return getMetadata().customizeTargetDate().isBooleanTrue() ? getMetadata().targetDate().getValue() : new LogicalDate();
        }

        private Widget initTitleWidget() {
            titlePanel = new HTML("");
            return titlePanel;
        }

        @Override
        protected void onItemSelect(PaymentRecordForReportDTO item) {
            AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(PaymentRecord.class, item.getPrimaryKey()));
        }

        private void redrawTitle() {
            String statusFilterView = i18n.tr("none"); // actually this never supposed to happen thanks to automatic setup form validation
            Iterator<PaymentRecord.PaymentStatus> i = getMetadata().paymentStatus().iterator();
            if (i.hasNext()) {
                statusFilterView = i.next().toString();
            }
            while (i.hasNext()) {
                statusFilterView += ", " + i.next().toString();
            }
            String separator = "&nbsp&nbsp&nbsp&nbsp&nbsp";
            titlePanel.setHTML(new SafeHtmlBuilder()//@formatter:off                     
                    .appendHtmlConstant("<b>")                   
                    .appendEscaped(i18n.tr("Target Date: {0}", getTargetDate())).appendHtmlConstant(separator)
                    .appendEscaped(i18n.tr("Payment Method: {0}", getMetadata().paymentType().getValue())).appendHtmlConstant(separator)                    
                    .appendEscaped(i18n.tr("Payment Statuses: {0}", statusFilterView))
                    .appendHtmlConstant("</b>")
                    .toSafeHtml());//@formatter:off
            titlePanel.getElement().getStyle().setProperty("textAlign", "center");
                    
        }
    }

    public PaymentRecordsGadgetFactory() {
        super(PaymentRecordsGadgetMetadata.class);
    }

    @Override
    public List<String> getCategories() {
        return Arrays.asList(Directory.Categories.Payments.toString());
    }

    @Override
    public boolean isBuildingGadget() {
        return true;
    }

    @Override
    protected GadgetInstanceBase<PaymentRecordsGadgetMetadata> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new PaymentRecordsGadget(gadgetMetadata);
    }
       

}
