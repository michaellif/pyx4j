/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.dashboard;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Singleton;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;
import com.propertyvista.crm.client.ui.gadgets.GadgetsFactory;
import com.propertyvista.crm.rpc.domain.DashboardMetadata;
import com.propertyvista.crm.rpc.domain.DashboardMetadata.LayoutType;
import com.propertyvista.crm.rpc.domain.GadgetMetadata;
import com.propertyvista.crm.rpc.domain.GadgetMetadata.GadgetType;

import com.pyx4j.dashboard.client.DashboardPanel;
import com.pyx4j.dashboard.client.IGadget;
import com.pyx4j.dashboard.client.Layout;
import com.pyx4j.widgets.client.dialog.DialogPanel;

@Singleton
public class DashboardViewImpl extends SimplePanel implements DashboardView {

    private static I18n i18n = I18nFactory.getI18n(DashboardViewImpl.class);

    final DashboardPanel dashboard = new DashboardPanel();

    final LayoutsSet layouts = new LayoutsSet();

    public DashboardViewImpl() {
        VerticalPanel main = new VerticalPanel();
        main.add(new CrmHeaderDecorator("Dashboard Menu/Tools", layouts));
        main.add(dashboard);
        main.setWidth("100%");
        setWidget(main);
    }

    @Override
    public void fillDashboard(DashboardMetadata dashboardMetadata) {
        dashboard.removeAllGadgets();

        if (dashboardMetadata.isEmpty()) {
            return;
        }

        // set dashboard layout:

        if (dashboardMetadata.layoutType().getValue() == (LayoutType.One)) {
            layouts.setLayout1();
        } else if (dashboardMetadata.layoutType().getValue() == LayoutType.Two11) {
            layouts.setLayout22();
        } else if (dashboardMetadata.layoutType().getValue() == LayoutType.Two12) {
            layouts.setLayout12();
        } else if (dashboardMetadata.layoutType().getValue() == LayoutType.Two21) {
            layouts.setLayout21();
        } else if (dashboardMetadata.layoutType().getValue() == LayoutType.Three) {
            layouts.setLayout3();
        }

        // fill the dashboard with gadgets:
        for (GadgetMetadata gmd : dashboardMetadata.gadgets()) {
            IGadget gadget = GadgetsFactory.createGadget(gmd.type().getValue(), gmd);
            if (gadget != null) {
                dashboard.addGadget(gadget, gmd.column().getValue());
                gadget.start(); // allow gadget execution... 
            }
        }
    }

    private class LayoutsSet extends HorizontalPanel {

        final Image layout1 = new Image();

        final Image layout12 = new Image();

        final Image layout21 = new Image();

        final Image layout22 = new Image();

        final Image layout3 = new Image();

        public LayoutsSet() {
            super();
            setDefaultImages();

            layout1.setTitle(i18n.tr("Switch layout"));
            layout1.getElement().getStyle().setCursor(Cursor.POINTER);
            layout1.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setLayout1();
                }
            });

            layout12.setTitle(i18n.tr("Switch layout"));
            layout12.getElement().getStyle().setCursor(Cursor.POINTER);
            layout12.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setLayout12();
                }
            });

            layout21.setTitle(i18n.tr("Switch layout"));
            layout21.getElement().getStyle().setCursor(Cursor.POINTER);
            layout21.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setLayout21();
                }
            });

            layout22.setTitle(i18n.tr("Switch layout"));
            layout22.getElement().getStyle().setCursor(Cursor.POINTER);
            layout22.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setLayout22();
                }
            });

            layout3.setTitle(i18n.tr("Switch layout"));
            layout3.getElement().getStyle().setCursor(Cursor.POINTER);
            layout3.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setLayout3();
                }
            });

            final Image addGadget = new Image(CrmImages.INSTANCE.dashboardAddGadget());
            addGadget.getElement().getStyle().setCursor(Cursor.POINTER);
            addGadget.addMouseOverHandler(new MouseOverHandler() {
                @Override
                public void onMouseOver(MouseOverEvent event) {
                    addGadget.setResource(CrmImages.INSTANCE.dashboardAddGadgetHover());
                }
            });
            addGadget.addMouseOutHandler(new MouseOutHandler() {
                @Override
                public void onMouseOut(MouseOutEvent event) {
                    addGadget.setResource(CrmImages.INSTANCE.dashboardAddGadget());
                }
            });
            addGadget.setTitle(i18n.tr("Add Gadget..."));
            addGadget.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    final AddGadgetBox agb = new AddGadgetBox();
                    agb.setPopupPositionAndShow(new PositionCallback() {
                        @Override
                        public void setPosition(int offsetWidth, int offsetHeight) {
                            agb.setPopupPosition((Window.getClientWidth() - offsetWidth) / 2, (Window.getClientHeight() - offsetHeight) / 2);
                        }
                    });
                    agb.show();
                }
            });

            this.add(layout1);
            this.add(layout12);
            this.add(layout21);
            this.add(layout22);
            this.add(layout3);
            this.add(new HTML("&nbsp;&nbsp;&nbsp;&nbsp;"));
            this.add(addGadget);
            this.setSpacing(4);
        }

        public void setLayout1() {
            if (dashboard.setLayout(new Layout(1, 1, 12))) {
                setDefaultImages();
                layout1.setResource(CrmImages.INSTANCE.dashboardLayout1_1());
            }
        }

        public void setLayout12() {
            Layout layout = new Layout(2, 1, 12);
            byte colWidths[] = { 33, 67 };
            layout.setColumnWidths(colWidths);
            if (dashboard.setLayout(layout)) {
                setDefaultImages();
                layout12.setResource(CrmImages.INSTANCE.dashboardLayout12_1());
            }
        }

        public void setLayout21() {
            Layout layout = new Layout(2, 1, 12);
            byte colWidths[] = { 67, 33 };
            layout.setColumnWidths(colWidths);
            if (dashboard.setLayout(layout)) {
                setDefaultImages();
                layout21.setResource(CrmImages.INSTANCE.dashboardLayout21_1());
            }
        }

        public void setLayout22() {
            if (dashboard.setLayout(new Layout(2, 1, 12))) {
                setDefaultImages();
                layout22.setResource(CrmImages.INSTANCE.dashboardLayout22_1());
            }
        }

        public void setLayout3() {
            if (dashboard.setLayout(new Layout(3, 1, 12))) {
                setDefaultImages();
                layout3.setResource(CrmImages.INSTANCE.dashboardLayout3_1());
            }
        }

        private void setDefaultImages() {
            layout1.setResource(CrmImages.INSTANCE.dashboardLayout1_0());
            layout12.setResource(CrmImages.INSTANCE.dashboardLayout12_0());
            layout21.setResource(CrmImages.INSTANCE.dashboardLayout21_0());
            layout22.setResource(CrmImages.INSTANCE.dashboardLayout22_0());
            layout3.setResource(CrmImages.INSTANCE.dashboardLayout3_0());
        }

        // add new gadget UI: 
        class AddGadgetBox extends DialogPanel {

            private final ListBox gadgetsList = new ListBox();

            private final Label gadgetDesc = new Label();

            public AddGadgetBox() {
                super(false, true);
                setCaption(i18n.tr("Gadget Directory"));

                listAvailableGadgets();

                HorizontalPanel gadgets = new HorizontalPanel();
                gadgets.add(gadgetsList);
                gadgets.add(gadgetDesc);
                gadgets.setSpacing(8);
                gadgets.setWidth("100%");

                gadgets.setCellWidth(gadgetsList, "35%");
                gadgetsList.setWidth("100%");

                // style right (description) cell:
                gadgetDesc.setText(i18n.tr("Select desired gadget in the list..."));
                Element cell = DOM.getParent(gadgetDesc.getElement());
                cell.getStyle().setPadding(3, Unit.PX);
                cell.getStyle().setBorderStyle(BorderStyle.SOLID);
                cell.getStyle().setBorderWidth(1, Unit.PX);
                cell.getStyle().setBorderColor("#bbb");

                HorizontalPanel buttons = new HorizontalPanel();
                buttons.add(new Button("Add", new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        hide();
                        addSelectedGadget();
                    }
                }));
                buttons.add(new Button("Cancel", new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        hide();
                    }
                }));
                buttons.setSpacing(8);

                VerticalPanel vPanel = new VerticalPanel();
                vPanel.add(gadgets);
                vPanel.add(buttons);
                vPanel.setCellHorizontalAlignment(buttons, HasHorizontalAlignment.ALIGN_CENTER);
                vPanel.setSpacing(8);
                vPanel.setSize("100%", "100%");

                setWidget(vPanel);
                setSize("400px", "150px");
//              getElement().getStyle().setProperty("minWidth", "400px");
//              getElement().getStyle().setProperty("minHeight", "150px");
            }

            private void listAvailableGadgets() {
                gadgetsList.clear();
                for (GadgetType gt : GadgetType.values()) {
                    gadgetsList.addItem(gt.name());
                }
                gadgetsList.setSelectedIndex(-1);
                gadgetsList.setVisibleItemCount(8);
                gadgetsList.addChangeHandler(new ChangeHandler() {
                    @Override
                    public void onChange(ChangeEvent event) {
                        if (gadgetsList.getSelectedIndex() >= 0) {
                            gadgetDesc.setText(GadgetsFactory.getGadgetTypeDescription(GadgetType.valueOf(gadgetsList.getItemText(gadgetsList
                                    .getSelectedIndex()))));
                        }
                    }
                });
            }

            private void addSelectedGadget() {
                IGadget gadget = null;
                if (gadgetsList.getSelectedIndex() >= 0) {
                    gadget = GadgetsFactory.createGadget(GadgetType.valueOf(gadgetsList.getItemText(gadgetsList.getSelectedIndex())), null);
                }

                if (gadget != null) {
                    dashboard.addGadget(gadget);
                    gadget.start();
                }
            }
        }
    }
}
