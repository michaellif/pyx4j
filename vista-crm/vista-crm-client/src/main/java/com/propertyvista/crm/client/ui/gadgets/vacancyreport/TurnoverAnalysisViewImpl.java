/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-10-23
 * @author artyom
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.vacancyreport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.basic.SvgRoot;
import com.pyx4j.svg.chart.BarChart2D;
import com.pyx4j.svg.chart.ChartTheme;
import com.pyx4j.svg.chart.DataSource;
import com.pyx4j.svg.chart.GridBasedChartConfigurator;
import com.pyx4j.svg.chart.GridBasedChartConfigurator.GridType;
import com.pyx4j.svg.gwt.SvgFactoryForGwt;

import com.propertyvista.crm.client.ui.gadgets.vacancyreport.util.TimeRange;
import com.propertyvista.crm.client.ui.gadgets.vacancyreport.util.Tuple;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.UnitVacancyReportService;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportGadgetSettings;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportTurnoverAnalysisDTO;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportTurnoverAnalysisDTO.AnalysisResolution;

public class TurnoverAnalysisViewImpl implements TurnoverAnalysisView {
    private static final I18n i18n = I18n.get(TurnoverAnalysisViewImpl.class);

    private static final String MEASURE_SELECTOR_RADIO_GROUP_ID = "measureSelector";

    private static final String TURNOVER_ANALYSIS_CAPTION = "Turnover Analysis";

    private static final String RESOLUTION_SELECTOR_LABEL = "Scale";

    private static final boolean DEFAULT_IS_TURNOVER_MEASURED_BY_PERCENT = false;

    @SuppressWarnings("unchecked")
    private static final List<Tuple<Long, AnalysisResolution>> DEFAULT_TURNOVER_ANALYSIS_RESOLUTION_PER_RANGE = Arrays.asList(
    // keys must be in ascending order
            new Tuple<Long, AnalysisResolution>(TimeRange.WEEK, AnalysisResolution.Day), // 
            new Tuple<Long, AnalysisResolution>(2L * TimeRange.MONTH, AnalysisResolution.Week), //
            new Tuple<Long, AnalysisResolution>(TimeRange.YEAR, AnalysisResolution.Year));

    List<UnitVacancyReportTurnoverAnalysisDTO> data;

    private final SimplePanel graph;

    private final HorizontalPanel controls;

    private final VerticalPanel layoutPanel;

    ListBox resolutionSelector;

    RadioButton percent;

    RadioButton number;

    FlowPanel measureSelection;

    TurnoverAnalysisView.Presenter presenter;

    private UnitVacancyReportGadgetSettings settings;

    private TurnoverAnalysisFilteringCriteria filteringCriteria;

    private AnalysisResolution currentDefaultResolution;

    public TurnoverAnalysisViewImpl() {
        layoutPanel = new VerticalPanel();
        layoutPanel.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);
        layoutPanel.setWidth("100%");

        graph = new SimplePanel();

        controls = new HorizontalPanel();
        controls.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
        controls.getElement().getStyle().setPaddingLeft(2, Unit.EM);
        controls.setSpacing(10);

        resolutionSelector = new ListBox(false);
        resolutionSelector.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                populate();
            }
        });

        controls.add(new Label(i18n.tr(RESOLUTION_SELECTOR_LABEL)));
        controls.add(resolutionSelector);

        ValueChangeHandler<Boolean> measureChangeHandler = new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if (settings != null) {
                    settings.isTurnoverMeasuredByPercent().setValue(percent.getValue());
                }
                redraw();
            }
        };

        percent = new RadioButton(MEASURE_SELECTOR_RADIO_GROUP_ID, i18n.tr("%"));
        percent.addValueChangeHandler(measureChangeHandler);
        percent.setValue(DEFAULT_IS_TURNOVER_MEASURED_BY_PERCENT, false);
        number = new RadioButton(MEASURE_SELECTOR_RADIO_GROUP_ID, i18n.tr("#"));
        number.addValueChangeHandler(measureChangeHandler);
        number.setValue(!percent.getValue(), false);

        measureSelection = new FlowPanel();
        measureSelection.getElement().getStyle().setPaddingLeft(2, Unit.EM);
        measureSelection.getElement().getStyle().setPaddingRight(2, Unit.EM);
        measureSelection.add(percent);
        measureSelection.add(number);
        controls.add(measureSelection);

        Label caption = new Label(i18n.tr(TURNOVER_ANALYSIS_CAPTION));

        HorizontalPanel captionPanel = new HorizontalPanel();
        caption.setHorizontalAlignment(Label.ALIGN_CENTER);
        captionPanel.add(caption);
        captionPanel.setWidth("100%");
        captionPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

        layoutPanel.add(captionPanel);
        layoutPanel.add(graph);
        layoutPanel.add(controls);
    }

    public void attachSettings(UnitVacancyReportGadgetSettings settings) {
        this.settings = settings;
        if (this.settings.isTurnoverMeasuredByPercent().isNull()) {
            this.settings.isTurnoverMeasuredByPercent().setValue(DEFAULT_IS_TURNOVER_MEASURED_BY_PERCENT);
        }
        percent.setValue(settings.isTurnoverMeasuredByPercent().getValue(), false);
        populate();
    }

    public void setFilteringCriteria(TurnoverAnalysisFilteringCriteria criteria) {
        this.filteringCriteria = criteria;
        refillResolutionSelector();
        AnalysisResolution selected = getSelectedResolution();
        AnalysisResolution defaultResolution = getDefaultResolution(filteringCriteria.getFrom(), filteringCriteria.getTo());
        if (selected == null || currentDefaultResolution != defaultResolution) {
            selectResolution(defaultResolution);
            currentDefaultResolution = defaultResolution;
        }
        populate();
    }

    private void refillResolutionSelector() {
        AnalysisResolution currentResolution = getSelectedResolution();
        resolutionSelector.clear();
        if (!isFilteringCriteriaAcceptable()) {
            return;
        }
        AnalysisResolution[] analysisValues = AnalysisResolution.values();
        int selectedResolutionIndex = -1;
        int addedCounter = 0;
        for (int i = 0; i < analysisValues.length; ++i) {
            AnalysisResolution resolution = analysisValues[i];
            if (isResolutionAcceptableForTheGivenDateRange(resolution, filteringCriteria.getFrom(), filteringCriteria.getTo())) {
                resolutionSelector.addItem(resolution.toString(), resolution.toString());
                if (resolution.equals(currentResolution)) {
                    selectedResolutionIndex = addedCounter;
                }
                ++addedCounter;
            }
        }
        if (selectedResolutionIndex >= 0) {
            resolutionSelector.setItemSelected(selectedResolutionIndex, false);
        }
    }

    private static AnalysisResolution getDefaultResolution(LogicalDate from, LogicalDate to) {
        AnalysisResolution defaultScale = DEFAULT_TURNOVER_ANALYSIS_RESOLUTION_MAX;
        if (from == null | to == null) {
            return defaultScale;
        }

        long range = to.getTime() - from.getTime();
        for (Tuple<Long, AnalysisResolution> defaultSetting : DEFAULT_TURNOVER_ANALYSIS_RESOLUTION_PER_RANGE) {
            if (range <= defaultSetting.car()) {
                defaultScale = defaultSetting.cdr();
                break;
            }
        }

        return defaultScale;
    }

    private void selectResolution(AnalysisResolution resolution) {
        if (resolution != null) {
            int itemCount = resolutionSelector.getItemCount();
            for (int i = 0; i < itemCount; ++i) {
                if (resolution.equals(AnalysisResolution.representationToValue(resolutionSelector.getValue(i)))) {
                    resolutionSelector.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private boolean isFilteringCriteriaAcceptable() {
        return filteringCriteria != null && (filteringCriteria.getFrom() != null & filteringCriteria.getTo() != null);
    }

    private static boolean isResolutionAcceptableForTheGivenDateRange(AnalysisResolution resolution, LogicalDate fromDate, LogicalDate toDate) {
        if (fromDate == null | toDate == null) {
            return false;
        }
        long fromTime = fromDate.getTime();
        long toTime = toDate.getTime();
        long interval = resolution.addTo(fromTime) - fromTime;

        return ((toTime - fromTime) / interval) <= UnitVacancyReportService.MAX_SUPPORTED_INTERVALS;
    }

    @Override
    public Widget asWidget() {
        return layoutPanel;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        populate();
    }

    @Override
    public AnalysisResolution getSelectedResolution() {
        int selected = resolutionSelector.getSelectedIndex();
        if (selected != -1) {
            return AnalysisResolution.representationToValue(resolutionSelector.getValue(selected));
        } else {
            return null;
        }
    }

    @Override
    public boolean isTunoverMeasuredByPercent() {
        return percent.getValue();
    }

    @Override
    public void setTurnoverAnalysisData(List<UnitVacancyReportTurnoverAnalysisDTO> data) {
        this.data = data;
        redraw();
    }

    @Override
    public boolean isEnabled() {
        return this.asWidget().isVisible();
    }

    @Override
    public void reportError(Throwable error) {
        // TODO Auto-generated method stub
    }

    private void redraw() {
        if (data == null || data.size() == 0 | filteringCriteria.getFrom() == null | filteringCriteria.getTo() == null) {
            // TODO maybe show something like "no data provided"?)
            graph.clear();
            return;
        }

        DataSource ds = new DataSource();

        for (UnitVacancyReportTurnoverAnalysisDTO intervalData : data) {
            ArrayList<Double> values = new ArrayList<Double>();
            if (!isTunoverMeasuredByPercent()) {
                values.add((double) intervalData.unitsTurnedOverAbs().getValue().intValue());
            } else {
                values.add(intervalData.unitsTurnedOverPct().getValue());
            }

            ds.addDataSet(ds.new Metric(getSelectedResolution().intervalLabelFormat(intervalData.fromDate().getValue(), intervalData.toDate().getValue())),
                    values);
        }

        SvgFactory factory = new SvgFactoryForGwt();

        GridBasedChartConfigurator config = new GridBasedChartConfigurator(factory, ds, 700, 200);
        config.setGridType(GridType.Both);
        config.setTheme(ChartTheme.Bright);
        config.setShowValueLabels(true);
        config.setZeroBased(false);

        SvgRoot svgroot = factory.getSvgRoot();
        svgroot.add(new BarChart2D(config));

        graph.clear();
        graph.add((Widget) svgroot);
        graph.setSize("700px", "200px");
        graph.getElement().getStyle().setOverflow(Overflow.HIDDEN);
    }

    private void populate() {
        if (presenter != null) {
            presenter.populateTurnoverAnalysis();
        }
    }

    @Override
    public TurnoverAnalysisFilteringCriteria getTurnoverAnalysisFilteringCriteria() {
        return this.filteringCriteria;
    }
}
