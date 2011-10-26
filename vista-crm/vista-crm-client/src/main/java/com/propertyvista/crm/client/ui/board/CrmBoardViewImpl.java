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
import java.util.EnumSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.i18n.shared.I18nEnum;
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

    @com.pyx4j.i18n.annotations.I18n
    public enum DateIntervals {
        today,

        currentWeek,

        currentMonth,

        @Translate("Last 31 days")
        last31days,

        custom;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    private class BuildingFilters {

        private final Logger log = LoggerFactory.getLogger(BuildingFilters.class);

        private final IListerView<Building> buildingLister;

        private final CheckBox useDates = new CheckBox(i18n.tr("Use date interval"));

        private final ListBox dateIntervals = new ListBox();

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

            for (DateIntervals interval : EnumSet.allOf(DateIntervals.class)) {
                dateIntervals.addItem(interval.toString(), interval.name());
            }

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
            fromDate.setValue(TimeUtils.today());
            toDate.setValue(TimeUtils.today());
            dateIntervals.setItemSelected(0, true);
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

            final HorizontalPanel custom = new HorizontalPanel();

            Widget w;
            custom.add(w = new HTML(i18n.tr("From")));
            custom.setCellVerticalAlignment(w, HasVerticalAlignment.ALIGN_MIDDLE);
            custom.add(fromDate);
            custom.add(w = new HTML(i18n.tr("To")));
            custom.setCellVerticalAlignment(w, HasVerticalAlignment.ALIGN_MIDDLE);
            custom.add(toDate);

            custom.setVisible(false);
            dateIntervals.addChangeHandler(new ChangeHandler() {
                @Override
                public void onChange(ChangeEvent event) {
                    switch (DateIntervals.valueOf(dateIntervals.getValue(dateIntervals.getSelectedIndex()))) {
                    case custom:
                        custom.setVisible(true);
                        break;
                    default:
                        custom.setVisible(false);
                    }
                }
            });
            HorizontalPanel intervals = new HorizontalPanel();
            intervals.add(dateIntervals);

            final HorizontalPanel dates = new HorizontalPanel();

            dates.add(intervals);
            dates.add(custom);

            dates.setVisible(false);
            useDates.setValue(false);
            useDates.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    dates.setVisible(event.getValue());
                }
            });

            HorizontalPanel usage = new HorizontalPanel();
            usage.add(useDates);
            usage.setCellVerticalAlignment(useDates, HasVerticalAlignment.ALIGN_MIDDLE);

            HorizontalPanel dateInterval = new HorizontalPanel();
            dateInterval.add(usage);
            dateInterval.add(dates);
            main.add(dateInterval);

            // Alignment/styling:
            final int spacing = 7;
            final String height = "3em";

            custom.setSpacing(spacing);
            intervals.setSpacing(spacing);
            usage.setSpacing(spacing);

            useDates.getElement().getStyle().setMarginLeft(1, Unit.EM);
            useDates.getElement().getStyle().setMarginRight(2, Unit.EM);
            useDates.getElement().getStyle().setFontWeight(FontWeight.BOLDER);

            fromDate.setWidth("8.5em");
            toDate.setWidth("8.5em");

            dates.setHeight(height);
            usage.setHeight(height);
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
            String description = i18n.tr("Buildings") + ": ";

            List<Building> selectedBuildings = buildingLister.getLister().getCheckedItems();
            if (!selectedBuildings.isEmpty()) {
                String delimiter = ",";
                for (Building building : selectedBuildings) {
                    description += building.propertyCode().getStringView();
                    description += delimiter;
                }
                description = description.substring(0, description.lastIndexOf(delimiter));
            } else {
                description = i18n.tr("All Buildings");
            }

            if (useDates.getValue()) {
                description += " ";
                DateIntervals interval = DateIntervals.valueOf(dateIntervals.getValue(dateIntervals.getSelectedIndex()));
                switch (interval) {
                case today:
                case custom:
                    if (EqualsHelper.equals(fromDate.getValue(), toDate.getValue())) {
                        description += i18n.tr("of {0,date,short}", fromDate.getValue());
                    } else {
                        description += i18n.tr("from {0,date,short} to {1,date,short}", fromDate.getValue(), toDate.getValue());
                    }
                    break;
                default:
                    description += i18n.tr("of {0}", interval);
                }
            }

            return description;
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

                final long dayMlsecs = 24 * 60 * 60 * 1000;

                DateIntervals interval = DateIntervals.valueOf(dateIntervals.getValue(dateIntervals.getSelectedIndex()));
                switch (interval) {
                case custom:
                    filterData.fromDate = fromDate.getValue();
                    filterData.toDate = toDate.getValue();
                    break;
                case last31days:
                    filterData.fromDate = new Date();
                    filterData.toDate = TimeUtils.today();
                    filterData.fromDate.setTime(filterData.toDate.getTime() - 31 * dayMlsecs);
                    break;
                case currentMonth:
                    filterData.fromDate = new Date();
                    filterData.toDate = TimeUtils.today();
                    filterData.fromDate.setTime(filterData.toDate.getTime() - (filterData.toDate.getDate() - 1) * dayMlsecs); // note: days 1-based!..
                    break;
                case currentWeek:
                    filterData.fromDate = new Date();
                    filterData.toDate = TimeUtils.today();
                    filterData.fromDate.setTime(filterData.toDate.getTime() - filterData.toDate.getDay() * dayMlsecs); // note: weekdays 0-based (from Sunday)!..
                    break;
                case today:
                    filterData.fromDate = filterData.toDate = TimeUtils.today();
                    break;
                }

                // log date calculation:
                DateTimeFormat format = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT);
                String from = format.format(filterData.fromDate);
                String to = format.format(filterData.toDate);
                log.info("calculated dates: " + from + " - " + to);
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
