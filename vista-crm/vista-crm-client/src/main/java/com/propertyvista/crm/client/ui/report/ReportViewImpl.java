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
package com.propertyvista.crm.client.ui.report;

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
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.widgets.client.dashboard.DashboardEvent;
import com.pyx4j.widgets.client.dashboard.IGadget;
import com.pyx4j.widgets.client.dashboard.IGadgetIterator;
import com.pyx4j.widgets.client.dashboard.Report;
import com.pyx4j.widgets.client.dashboard.Report.Location;
import com.pyx4j.widgets.client.style.IStyleSuffix;

import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;
import com.propertyvista.crm.client.ui.gadgets.AddGadgetBox;
import com.propertyvista.crm.client.ui.gadgets.GadgetsFactory;
import com.propertyvista.crm.client.ui.gadgets.IGadgetBase;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.GadgetMetadata;

public class ReportViewImpl extends DockLayoutPanel implements ReportView {

    public static String DEFAULT_STYLE_PREFIX = "vista_DashboardView";

    public static enum StyleSuffix implements IStyleSuffix {
        actionsPanel
    }

    private static I18n i18n = I18nFactory.getI18n(ReportViewImpl.class);

    private final CrmHeaderDecorator header;

    private final ScrollPanel scroll = new ScrollPanel();

    protected final HorizontalPanel actionsPanel;

    private Report report;

    private Presenter presenter;

    private DashboardMetadata dashboardMetadata;

    private Button btnSave;

    public ReportViewImpl() {
        this(i18n.tr("Report"));
    }

    public ReportViewImpl(String caption) {
        super(Unit.EM);

        addNorth(header = new CrmHeaderDecorator(caption, new AddWidgetButton()), VistaCrmTheme.defaultHeaderHeight);

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

        report = new Report();
        report.addEventHandler(new DashboardEvent() {
            @Override
            public void onEvent(Reason reason) {
                boolean save = true;
                switch (reason) {
                case addGadget:
                case removeGadget:
                    break;
                case repositionGadget:
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

        if (!dashboardMetadata.isEmpty()) {
            header.setCaption(dashboardMetadata.name().getStringView());
            // fill the dashboard with gadgets:
            for (GadgetMetadata gmd : dashboardMetadata.gadgets()) {
                IGadget gadget = GadgetsFactory.createGadget(gmd.type().getValue(), gmd);
                if (gadget != null) {
                    Report.Location location;
                    // decode columns:
                    switch (gmd.column().getValue()) {
                    case 0:
                        location = Location.Left;
                        break;
                    case 1:
                        location = Location.Right;
                        break;
                    default:
                        location = Location.Full;
                    }

                    report.addGadget(gadget, location);
                    gadget.start(); // allow gadget execution... 
                }
            }
        }

        scroll.setWidget(report);
        btnSave.setEnabled(false);
    }

    @Override
    public DashboardMetadata getData() {
        dashboardMetadata.gadgets().clear();

        IGadgetIterator it = report.getGadgetIterator();
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

    private class AddWidgetButton extends SimplePanel {

        public AddWidgetButton() {
            super();

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

                    agb.addCloseHandler(new CloseHandler<PopupPanel>() {
                        @Override
                        public void onClose(CloseEvent<PopupPanel> event) {
                            IGadget gadget = agb.getSelectedGadget();
                            if (gadget != null) {
                                report.insertGadget(gadget, (gadget.isFullWidth() ? Report.Location.Full : Report.Location.Left), 0);
                                gadget.start();
                            }
                        }
                    });

                    agb.show();
                }
            });

            setWidget(addGadget);
        }
    }
}
