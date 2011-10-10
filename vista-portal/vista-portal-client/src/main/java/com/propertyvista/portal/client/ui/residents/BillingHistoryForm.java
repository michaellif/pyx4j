/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 11, 2011
 * @author dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.folder.IFolderDecorator;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.VistaEntityFolder;
import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.portal.client.ui.decorations.PortalHeaderBar;
import com.propertyvista.portal.domain.dto.BillDTO;
import com.propertyvista.portal.domain.dto.BillListDTO;
import com.propertyvista.portal.domain.dto.BillListDTO.SearchType;

public class BillingHistoryForm extends CEntityEditor<BillListDTO> implements BillingHistoryView {

    protected static I18n i18n = I18n.get(BillingHistoryForm.class);

    private Presenter presenter;

    public BillingHistoryForm() {
        super(BillListDTO.class, new VistaViewersComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        FlowPanel container = new FlowPanel();
        PortalHeaderBar header = new PortalHeaderBar(i18n.tr("Billing History"), "100%");
        header.addToTheRight(inject(proto().searchType()));

        get(proto().searchType()).addValueChangeHandler(new ValueChangeHandler<SearchType>() {
            @Override
            public void onValueChange(ValueChangeEvent<SearchType> event) {
                presenter.search(event.getValue());
            }
        });

        container.add(header);
        container.add(inject(proto().bills(), createBillingHistoryViewer()));
        return container;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

    private CEntityFolder<BillDTO> createBillingHistoryViewer() {
        return new VistaEntityFolder<BillDTO>(BillDTO.class, false) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "10em"));
                columns.add(new EntityFolderColumnDescriptor(proto().paidOn(), "7em"));
                columns.add(new EntityFolderColumnDescriptor(proto().total(), "10em"));
                columns.add(new EntityFolderColumnDescriptor(proto().transactionID(), "10em"));
                return columns;
            }

            @Override
            protected IFolderDecorator<BillDTO> createDecorator() {
                return new VistaTableFolderDecorator<BillDTO>(columns(), this);
            }
        };
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    class TableFolderViewer<E extends IEntity> extends TableFolderDecorator<BillDTO> {
//
//        private final FlowPanel content;
//
//        TableFolderViewer() {
//            content = new FlowPanel();
//            content.setWidth("100%");
//            HorizontalPanel header = new HorizontalPanel();
//            header.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Header);
//            header.setWidth("100%");
//            formatHeader("Type", "20%", header);
//            formatHeader("Date", "30%", header);
//            formatHeader("Amount", "30%", header);
//            formatHeader("Transaction ID", "20%", header);
//            content.add(header);
//        }
//
//        @Override
//        public void setComponent(CEntityFolder<BillDTO> viewer) {
//            content.add(viewer.getContainer());
//            setWidget(content);
//        }
//
//        private void formatHeader(String title, String width, CellPanel parent) {
//            Label item = new Label(i18n.tr(title));
//            parent.add(item);
//            parent.setCellWidth(item, width);
//        }
//    }
//
//    private CEntityFolder<BillDTO> createBillingHistoryViewer2() {
//        return new CEntityFolder<BillDTO>(BillDTO.class) {
//
//            @Override
//            protected IFolderDecorator<BillDTO> createDecorator() {
//                return new TableFolderViewer<BillDTO>();
//            }
//
//            @Override
//            protected CEntityFolderItemEditor<BillDTO> createItem() {
//                return createBillLineViewer();
//            }
//        };
//
//    }
//
//    private CEntityFolderItemEditor<BillDTO> createBillLineViewer() {
//
//        return new CEntityFolderItemEditor<BillDTO>() {
//
//            @Override
//            public IFolderItemViewerDecorator<BillDTO> createFolderItemDecorator() {
//                return new TableItemDecorator<BillDTO>();
//            }
//
//            @Override
//            public IsWidget createContent(BillDTO value) {
//                return createBillLine(value);
//            }
//
//            private IsWidget createBillLine(final BillDTO bill) {
//                HorizontalPanel container = new HorizontalPanel();
//                //   container.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Row);
//                container.setWidth("100%");
//                formatValue(bill.type().getStringView(), "20%", container);
//                formatValue(bill.paidOn().getStringView(), "30%", container);
//                formatValue(bill.total().getStringView(), "30%", container);
//                formatValue(bill.transactionID().getStringView(), "20%", container);
//
//                return container;
//            }
//
//            private void formatValue(String value, String width, CellPanel parent) {
//                Label item = new Label(i18n.tr(value));
//                parent.add(item);
//                parent.setCellWidth(item, width);
//
//            }
//        };
//    }

}
