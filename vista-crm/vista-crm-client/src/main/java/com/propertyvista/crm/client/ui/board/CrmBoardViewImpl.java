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
package com.propertyvista.crm.client.ui.board;

import java.util.Date;
import java.util.List;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;
import com.pyx4j.widgets.client.dashboard.IGadget;
import com.pyx4j.widgets.client.dashboard.IGadgetIterator;

import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.AnchorButton;
import com.propertyvista.crm.client.ui.crud.building.SelectedBuildingLister;
import com.propertyvista.crm.client.ui.decorations.CrmTitleBar;
import com.propertyvista.crm.client.ui.gadgets.building.IBuildingGadget;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;
import com.propertyvista.domain.property.asset.building.Building;

public class CrmBoardViewImpl extends BoardViewImpl implements CrmBoardView {

    protected static I18n i18n = I18n.get(CrmBoardViewImpl.class);

    protected final CrmTitleBar header = new CrmTitleBar("");

    protected final SimplePanel filtersPanel = new SimplePanel();

    private BuildingFilters filters;

    public CrmBoardViewImpl() {
        super();
    }

    public CrmBoardViewImpl(BoardBase board) {
        this();

        addNorth(header, VistaCrmTheme.defaultHeaderHeight);
        header.setHeight("100%"); // fill all that defaultHeaderHeight!..

        addNorth(filtersPanel, 0);
        filtersPanel.setStyleName(BoardBase.DEFAULT_STYLE_PREFIX + BoardBase.StyleSuffix.filtersPanel);

        setBoard(board);
    }

    @Override
    public void fill(DashboardMetadata dashboardMetadata) {
        super.fill(dashboardMetadata);

        filters = null;
        filtersPanel.setWidget(null);
        setWidgetSize(filtersPanel, 0);
        if (dashboardMetadata != null) {
            header.setCaption(dashboardMetadata.name().getStringView());

            if (dashboardMetadata.type().getValue() == DashboardType.building) {
                filters = new BuildingFilters();
                filtersPanel.setWidget(filters.getCompactVeiw());
                setWidgetSize(filtersPanel, VistaCrmTheme.defaultActionBarHeight);
            }
        }
    }

    @Override
    public IListerView<Building> getBuildingListerView() {
        return (filters != null ? filters.getBuildingListerView() : null);
    }

    private class BuildingFilters {

        private final IListerView<Building> buildingLister;

        CheckBox useDates = new CheckBox(i18n.tr("Use date interval"));

        private final CDatePicker fromDate = new CDatePicker();

        private final CDatePicker toDate = new CDatePicker();

        private final HTML filterDescription = new HTML();

        private final Widget compactView;

        private final Widget setupView;

        public BuildingFilters() {
            buildingLister = new ListerInternalViewImplBase<Building>(new SelectedBuildingLister());
            buildingLister.getLister().setSelectable(false);
            buildingLister.getLister().setHasCheckboxColumn(true);
            buildingLister.getLister().getListPanel().setPageSize(10);

            compactView = createCompactVeiw();
            setupView = createSetupVeiw();
        }

        public IListerView<Building> getBuildingListerView() {
            return buildingLister;
        }

        public Widget getCompactVeiw() {
            // update UI state:
            filterDescription.setHTML(getFilteringDescription());

            return compactView;
        }

        public Widget getSetupVeiw() {
            // reset UI state:
            buildingLister.restoreState();
            fromDate.setValue(new Date());
            toDate.setValue(new Date());
            useDates.setValue(false, true);

            return setupView;
        }

        public Widget createCompactVeiw() {
            HorizontalPanel main = new HorizontalPanel();

            filterDescription.setStyleName(BoardBase.DEFAULT_STYLE_PREFIX + BoardBase.StyleSuffix.filtersDescription);
            main.add(filterDescription);
            main.setCellVerticalAlignment(filterDescription, HasVerticalAlignment.ALIGN_MIDDLE);
            main.setCellHorizontalAlignment(filterDescription, HasHorizontalAlignment.ALIGN_CENTER);

            Button setup = new Button(i18n.tr("Setup"), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setWidgetSize(filtersPanel, 35);
                    filtersPanel.setWidget(filters.getSetupVeiw());
                }
            });
            main.add(setup);
            main.setCellWidth(setup, "1%"); // resize it to buttons width!..
            main.setCellVerticalAlignment(setup, HasVerticalAlignment.ALIGN_MIDDLE);
            main.setSpacing(4);

            main.setSize("100%", "100%");
            return main;
        }

        private Widget createSetupVeiw() {
            VerticalPanel main = new VerticalPanel();

//            LayoutPanel wrap = new LayoutPanel();
//            wrap.add(new ScrollPanel(buildingLister.asWidget()));
//            wrap.setSize("100%", "32em");
//            main.add(wrap);

            main.add(new ScrollPanel(buildingLister.asWidget()));

            // ------------------------------------------------------------------------------------

            final HorizontalPanel dates = new HorizontalPanel();

            Widget w;
            dates.add(w = new HTML(i18n.tr("From")));
            dates.setCellVerticalAlignment(w, HasVerticalAlignment.ALIGN_MIDDLE);
            dates.add(fromDate);
            dates.add(w = new HTML(i18n.tr("To")));
            dates.setCellVerticalAlignment(w, HasVerticalAlignment.ALIGN_MIDDLE);
            dates.add(toDate);

            dates.setVisible(false);
            useDates.setValue(false);
            useDates.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    dates.setVisible(event.getValue());
                }
            });

            HorizontalPanel dateUsage = new HorizontalPanel();
            dateUsage.add(useDates);
            dateUsage.setCellVerticalAlignment(useDates, HasVerticalAlignment.ALIGN_MIDDLE);

            HorizontalPanel dateInterval = new HorizontalPanel();
            dateInterval.add(dateUsage);
            dateInterval.add(dates);
            main.add(dateInterval);

            // Alignment/styling:
            final int spacing = 7;
            final String height = "3em";

            dates.setSpacing(spacing);
            dateUsage.setSpacing(spacing);

            useDates.getElement().getStyle().setMarginLeft(1, Unit.EM);
            useDates.getElement().getStyle().setMarginRight(2, Unit.EM);
            useDates.getElement().getStyle().setFontWeight(FontWeight.BOLDER);

            fromDate.setWidth("8.5em");
            toDate.setWidth("8.5em");

            dates.setHeight(height);
            dateUsage.setHeight(height);
            dateInterval.setHeight(height);

            // ------------------------------------------------------------------------------------

            HorizontalPanel buttons = new HorizontalPanel();

            Button apply = new Button(i18n.tr("Apply"), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setWidgetSize(filtersPanel, VistaCrmTheme.defaultActionBarHeight);
                    filtersPanel.setWidget(filters.getCompactVeiw());

                    applyFiltering();
                }
            });
            buttons.add(apply);

            AnchorButton cancel = new AnchorButton(i18n.tr("Cancel"), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setWidgetSize(filtersPanel, VistaCrmTheme.defaultActionBarHeight);
                    filtersPanel.setWidget(filters.getCompactVeiw());
                }
            });
            buttons.add(cancel);
            buttons.setCellWidth(cancel, "60px");
            buttons.setCellHorizontalAlignment(cancel, HasHorizontalAlignment.ALIGN_CENTER);
            buttons.setCellVerticalAlignment(cancel, HasVerticalAlignment.ALIGN_MIDDLE);
            buttons.setSpacing(5);

            main.add(buttons);
            main.setCellHeight(buttons, "1%"); // resize it to buttons height!..
            buttons.setCellVerticalAlignment(buttons, HasVerticalAlignment.ALIGN_MIDDLE);
            main.setCellHorizontalAlignment(buttons, HasHorizontalAlignment.ALIGN_RIGHT);
            main.setWidth("100%");

            // ------------------------------------------------------------------------------------

            LayoutPanel wrap = new LayoutPanel();
            wrap.add(new ScrollPanel(main));
            wrap.setSize("100%", "100%");
            return wrap;
        }

        private String getFilteringDescription() {
            String filterDescription = i18n.tr("Data for Buildings : ");

            List<Building> selectedBuildings = buildingLister.getLister().getCheckedItems();
            if (!selectedBuildings.isEmpty()) {
                for (Building building : selectedBuildings) {
                    filterDescription += building.propertyCode().getStringView();
                    filterDescription += "; ";
                }
            } else {
                filterDescription = i18n.tr("Data for all Buildings");
            }

            if (useDates.getValue()) {
                String format = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT).toString();
                filterDescription += i18n.tr(", from: ") + TimeUtils.simpleFormat(fromDate.getValue(), format);
                filterDescription += i18n.tr(" to: ") + TimeUtils.simpleFormat(toDate.getValue(), format);
            }

            return filterDescription;
        }

        private void applyFiltering() {
            IBuildingGadget.FilterData filterData = new IBuildingGadget.FilterData();

            List<Building> selectedBuildings = buildingLister.getLister().getCheckedItems();
            if (!selectedBuildings.isEmpty()) {
                for (Building building : selectedBuildings) {
                    filterData.buildings.add(building.getPrimaryKey());
                }
            }

            if (useDates.getValue()) {
                filterData.fromDate = fromDate.getValue();
                filterData.toDate = toDate.getValue();
            }

            // notify gadgets:
            IGadgetIterator it = board.getBoard().getGadgetIterator();
            if (it.hasNext()) {
                IGadget gadget = it.next();
                if (gadget instanceof IBuildingGadget) {
                    ((IBuildingGadget) gadget).setFiltering(filterData);
                }
            }
        }
    }
}
