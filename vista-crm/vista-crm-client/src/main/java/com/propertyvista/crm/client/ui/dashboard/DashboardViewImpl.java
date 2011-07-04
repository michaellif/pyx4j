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

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.widgets.client.dashboard.Dashboard;
import com.pyx4j.widgets.client.dashboard.Dashboard.Layout;
import com.pyx4j.widgets.client.dashboard.DashboardEvent;
import com.pyx4j.widgets.client.dashboard.IGadget;
import com.pyx4j.widgets.client.dashboard.IGadgetIterator;
import com.pyx4j.widgets.client.style.IStyleSuffix;

import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;
import com.propertyvista.crm.client.ui.gadgets.AddGadgetBox;
import com.propertyvista.crm.client.ui.gadgets.GadgetsFactory;
import com.propertyvista.crm.client.ui.gadgets.IGadgetBase;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.LayoutType;
import com.propertyvista.domain.dashboard.GadgetMetadata;

public class DashboardViewImpl extends DockLayoutPanel implements DashboardView {

    public static String DEFAULT_STYLE_PREFIX = "vista_DashboardView";

    public static enum StyleSuffix implements IStyleSuffix {
        actionsPanel
    }

    private static I18n i18n = I18nFactory.getI18n(DashboardViewImpl.class);

    private final CrmHeaderDecorator header;

    private final LayoutsSet layouts = new LayoutsSet();

    private final ScrollPanel scroll = new ScrollPanel();

    protected final HorizontalPanel actionsPanel;

    private Dashboard dashboard;

    private Presenter presenter;

    private DashboardMetadata dashboardMetadata;

    private Button btnSave;

    public DashboardViewImpl() {
        this(i18n.tr("Dashboard"));
    }

    public DashboardViewImpl(String caption) {
        super(Unit.EM);

        addNorth(header = new CrmHeaderDecorator(caption, layouts), VistaCrmTheme.defaultHeaderHeight);

        actionsPanel = new HorizontalPanel();
        actionsPanel.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.actionsPanel);
        actionsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        actionsPanel.setWidth("100%");
        actionsPanel.add(new HTML()); // just for %-tage cells alignment...
        addNorth(actionsPanel, VistaCrmTheme.defaultHeaderHeight);

        addActionButton(btnSave = new Button(i18n.tr("Save")));
        btnSave.addStyleName(btnSave.getStylePrimaryName() + VistaCrmTheme.StyleSuffixEx.SaveButton);
        btnSave.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.save();
            }
        });

        add(scroll);
        setSize("100%", "100%");
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void fill(DashboardMetadata dashboardMetadata) {
        this.dashboardMetadata = dashboardMetadata;

        dashboard = new Dashboard();
        dashboard.addEventHandler(new DashboardEvent() {
            @Override
            public void onEvent(Reason reason) {
                boolean save = true;
                switch (reason) {
                case addGadget:
                case removeGadget:
                    break;
                case repositionGadget:
                    break;
                case newLayout:
                    save = (DashboardViewImpl.this.dashboardMetadata.layoutType().getValue() != translateLayout(dashboard.getLayout()));
                    break;
                case updateGadget:
                    break;
                }

                if (save) {
                    // TODO just save immediately:
                    //presenter.save();
                    btnSave.setEnabled(true);
                }
            }
        });

        if (this.dashboardMetadata != null && !this.dashboardMetadata.isEmpty()) {
            header.setCaption(this.dashboardMetadata.name().getStringView());
            layouts.setLayout(this.dashboardMetadata.layoutType().getValue());
            // fill the dashboard with gadgets:
            for (final GadgetMetadata gmd : this.dashboardMetadata.gadgets()) {
                IGadgetBase gadget = GadgetsFactory.createGadget(gmd.type().getValue(), gmd);
                if (gadget != null) {
                    gadget.setPresenter(presenter);
                    dashboard.addGadget(gadget, gmd.column().getValue());
                    gadget.start(); // allow gadget execution... 
                }
            }
        }

        scroll.setWidget(dashboard);
        btnSave.setEnabled(false);
    }

    @Override
    public DashboardMetadata getData() {
        dashboardMetadata.layoutType().setValue(translateLayout(dashboard.getLayout()));
        dashboardMetadata.gadgets().clear();

        IGadgetIterator it = dashboard.getGadgetIterator();
        while (it.hasNext()) {
            IGadget gadget = it.next();
            if (gadget instanceof IGadgetBase) {
                GadgetMetadata gmd = ((IGadgetBase) gadget).getGadgetMetadata(); // gadget meta should be up to date!.. 
                gmd.column().setValue(it.getColumn()); // update current gadget column...
                dashboardMetadata.gadgets().add(gmd);
            }
        }

        return dashboardMetadata;
    }

    @Override
    public void onSaveSuccess() {
        btnSave.setEnabled(false);
    }

    @Override
    public boolean onSaveFail(Throwable caught) {
        // TODO Auto-generated method stub
        return false;
    }

    //
    // Internals:
    //

    protected void addActionButton(Button action) {
        actionsPanel.insert(action, 1);
        actionsPanel.setCellWidth(action, "1%");
        action.getElement().getStyle().setMarginRight(1, Unit.EM);
    }

    protected LayoutType translateLayout(Layout layout) {
        LayoutType layoutType = null;
        switch (layout) {
        case One:
            layoutType = LayoutType.One;
            break;
        case Two11:
            layoutType = LayoutType.Two11;
            break;
        case Two12:
            layoutType = LayoutType.Two12;
            break;
        case Two21:
            layoutType = LayoutType.Two21;
            break;
        case Three:
            layoutType = LayoutType.Three;
            break;
        }
        return layoutType;
    }

    private class LayoutsSet extends HorizontalPanel {

        final Image layout1 = new Image();

        final Image layout12 = new Image();

        final Image layout21 = new Image();

        final Image layout22 = new Image();

        final Image layout3 = new Image();

        public LayoutsSet() {
            setDefaultImages();

            layout1.setTitle(i18n.tr("Switch layout"));
            layout1.getElement().getStyle().setCursor(Cursor.POINTER);
            layout1.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setLayout(LayoutType.One);
                }
            });

            layout12.setTitle(i18n.tr("Switch layout"));
            layout12.getElement().getStyle().setCursor(Cursor.POINTER);
            layout12.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setLayout(LayoutType.Two12);
                }
            });

            layout21.setTitle(i18n.tr("Switch layout"));
            layout21.getElement().getStyle().setCursor(Cursor.POINTER);
            layout21.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setLayout(LayoutType.Two21);
                }
            });

            layout22.setTitle(i18n.tr("Switch layout"));
            layout22.getElement().getStyle().setCursor(Cursor.POINTER);
            layout22.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setLayout(LayoutType.Two11);
                }
            });

            layout3.setTitle(i18n.tr("Switch layout"));
            layout3.getElement().getStyle().setCursor(Cursor.POINTER);
            layout3.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setLayout(LayoutType.Three);
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
                    final AddGadgetBox agb = new AddGadgetBox(dashboardMetadata.type().getValue());
                    agb.setPopupPositionAndShow(new PositionCallback() {
                        @Override
                        public void setPosition(int offsetWidth, int offsetHeight) {
                            agb.setPopupPosition((Window.getClientWidth() - offsetWidth) / 2, (Window.getClientHeight() - offsetHeight) / 2);
                        }
                    });

                    agb.addCloseHandler(new CloseHandler<PopupPanel>() {
                        @Override
                        public void onClose(CloseEvent<PopupPanel> event) {
                            IGadget gadget = agb.getSelectedGadget();
                            if (gadget != null) {
                                dashboard.addGadget(gadget);
                                gadget.start();
                            }
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

        public void setLayout(LayoutType layoutType) {
            switch (layoutType) {
            case One:
                dashboard.setLayout(Layout.One);
                setDefaultImages();
                layout1.setResource(CrmImages.INSTANCE.dashboardLayout1_1());
                break;
            case Two11:
                dashboard.setLayout(Layout.Two11);
                setDefaultImages();
                layout22.setResource(CrmImages.INSTANCE.dashboardLayout22_1());
                break;
            case Two12:
                dashboard.setLayout(Layout.Two12);
                setDefaultImages();
                layout12.setResource(CrmImages.INSTANCE.dashboardLayout12_1());
                break;
            case Two21:
                dashboard.setLayout(Layout.Two21);
                setDefaultImages();
                layout21.setResource(CrmImages.INSTANCE.dashboardLayout21_1());
                break;
            case Three:
                dashboard.setLayout(Layout.Three);
                setDefaultImages();
                layout3.setResource(CrmImages.INSTANCE.dashboardLayout3_1());
                break;
            }
        }

        private void setDefaultImages() {
            layout1.setResource(CrmImages.INSTANCE.dashboardLayout1_0());
            layout12.setResource(CrmImages.INSTANCE.dashboardLayout12_0());
            layout21.setResource(CrmImages.INSTANCE.dashboardLayout21_0());
            layout22.setResource(CrmImages.INSTANCE.dashboardLayout22_0());
            layout3.setResource(CrmImages.INSTANCE.dashboardLayout3_0());
        }
    }
}
