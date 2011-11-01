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
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.basic.SvgRoot;
import com.pyx4j.svg.chart.ChartTheme;
import com.pyx4j.svg.chart.DataSource;
import com.pyx4j.svg.chart.GridBasedChartConfigurator;
import com.pyx4j.svg.chart.GridBasedChartConfigurator.GridType;
import com.pyx4j.svg.chart.LineChart;
import com.pyx4j.svg.gwt.SvgFactoryForGwt;

import com.propertyvista.crm.client.ui.gadgets.vacancyreport.util.TimeRange;
import com.propertyvista.crm.client.ui.gadgets.vacancyreport.util.Tuple;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.VacancyReportService;
import com.propertyvista.domain.dashboard.AbstractGadgetSettings;
import com.propertyvista.domain.dashboard.GadgetMetadata;
import com.propertyvista.domain.dashboard.GadgetMetadata.GadgetType;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.TurnoverAnalysisSettings;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitVacancyReportTurnoverAnalysisDTO;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitVacancyReportTurnoverAnalysisDTO.AnalysisResolution;

public class TurnoverAnalysisGraphGadget extends VacancyGadgetBase {
    private static final I18n i18n = I18n.get(TurnoverAnalysisGraphGadget.class);

    private static final String MEASURE_SELECTOR_RADIO_GROUP_ID = "measureSelector";

    private static final String RESOLUTION_SELECTOR_LABEL = "Scale";

    private static final boolean DEFAULT_IS_TURNOVER_MEASURED_BY_PERCENT = false;

    public static AnalysisResolution DEFAULT_TURNOVER_ANALYSIS_RESOLUTION_MAX = AnalysisResolution.Year;

    @SuppressWarnings("unchecked")
    private static final List<Tuple<Long, AnalysisResolution>> DEFAULT_TURNOVER_ANALYSIS_RESOLUTION_PER_RANGE = Arrays.asList(
    // keys must be in ascending order
            new Tuple<Long, AnalysisResolution>(TimeRange.WEEK, AnalysisResolution.Day), // 
            new Tuple<Long, AnalysisResolution>(2L * TimeRange.MONTH, AnalysisResolution.Week), //
            new Tuple<Long, AnalysisResolution>(TimeRange.YEAR, AnalysisResolution.Year));

    List<UnitVacancyReportTurnoverAnalysisDTO> data;

    private final SimplePanel graph;

    private final HorizontalPanel controls;

    private final FormFlexPanel layoutPanel;

    ListBox resolutionSelector;

    RadioButton percent;

    RadioButton number;

    FlowPanel measureSelection;

    private final VacancyReportService service;

    private final TurnoverAnalysisSettings settings;

    private AnalysisResolution currentDefaultResolution;

    public TurnoverAnalysisGraphGadget(GadgetMetadata gmd) {
        super(gmd);
        settings = gadgetMetadata.settings().cast();

        graph = new SimplePanel();

        controls = new HorizontalPanel();
        controls.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
        controls.getElement().getStyle().setPaddingLeft(2, Unit.EM);
        controls.setSpacing(10);

        resolutionSelector = new ListBox(false);
        resolutionSelector.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                populateTurnoverAnalysis();
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

        layoutPanel = new FormFlexPanel();
        int row = -1;
        layoutPanel.setWidth("100%");
        layoutPanel.setWidget(++row, 0, graph);
        layoutPanel.setWidget(++row, 0, controls);

        service = GWT.create(VacancyReportService.class);
    }

    @Override
    protected void selfInit(GadgetMetadata gmd) {
        gmd.type().setValue(GadgetType.TurnoverAnalysisGraph);
        gmd.name().setValue(GadgetType.TurnoverAnalysisGraph.toString());
    }

    @Override
    protected AbstractGadgetSettings createSettings() {
        TurnoverAnalysisSettings settings = EntityFactory.create(TurnoverAnalysisSettings.class);
        settings.isTurnoverMeasuredByPercent().setValue(DEFAULT_IS_TURNOVER_MEASURED_BY_PERCENT);
        settings.turnoverAnalysisResolution().setValue(DEFAULT_TURNOVER_ANALYSIS_RESOLUTION_MAX);

        return settings;
    }

    @Override
    public void start() {
        super.start();
        populateTurnoverAnalysis();
    }

    @Override
    public boolean isSetupable() {
        return false;
    }

    @Override
    public Widget asWidget() {
        return layoutPanel;
    }

    @Override
    protected void setFilteringCriteria(FilterDataDemoAdapter criteria) {
        this.filter = criteria;
        if (criteria != null) {
            fillResolutionSelector();
            AnalysisResolution selected = getSelectedResolution();
            AnalysisResolution defaultResolution = getDefaultResolution(filter.getFrom(), filter.getTo());
            if (selected == null || currentDefaultResolution != defaultResolution) {
                setResolution(defaultResolution);
                currentDefaultResolution = defaultResolution;
            }
        }
        populateTurnoverAnalysis();
    }

    /**
     * This method is supposed to be used to adjust the available scale (resolution) combo box for the selected filtering criteria.
     */
    private void fillResolutionSelector() {
        AnalysisResolution currentResolution = getSelectedResolution();
        resolutionSelector.clear();
        if (!isFilteringCriteriaAcceptable()) {
            return;
        }
        AnalysisResolution[] analysisValues = AnalysisResolution.values();
        int selectedResolutionIndex = -1;
        for (int i = 0; i < analysisValues.length; ++i) {
            AnalysisResolution resolution = analysisValues[i];
            resolutionSelector.addItem(resolution.toString(), resolution.toString());
            if (resolution.equals(currentResolution)) {
                selectedResolutionIndex = i;
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

    private void setResolution(AnalysisResolution resolution) {
        if (resolution != null) {
            int resolutionsCount = resolutionSelector.getItemCount();
            for (int i = 0; i < resolutionsCount; ++i) {
                if (resolution.equals(AnalysisResolution.representationToValue(resolutionSelector.getValue(i)))) {
                    resolutionSelector.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private boolean isFilteringCriteriaAcceptable() {
        return filter != null && (filter.getFrom() != null & filter.getTo() != null);
    }

    public AnalysisResolution getSelectedResolution() {
        int selected = resolutionSelector.getSelectedIndex();
        if (selected != -1) {
            return AnalysisResolution.representationToValue(resolutionSelector.getValue(selected));
        } else {
            return null;
        }
    }

    public boolean isTunoverMeasuredByPercent() {
        return percent.getValue();
    }

    public void setTurnoverAnalysisData(List<UnitVacancyReportTurnoverAnalysisDTO> data) {
        this.data = data;
        redraw();
    }

    private void redraw() {
        graph.clear();
        if (data == null || data.size() == 0 | (filter == null || (filter.getFrom() == null | filter.getTo() == null))) {
            // TODO maybe show something like "no data provided"?)
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
        svgroot.add(new LineChart(config));

        graph.add((Widget) svgroot);
        graph.setSize("700px", "200px");
        graph.getElement().getStyle().setOverflow(Overflow.HIDDEN);
    }

    private boolean isEnabled() {
        return this.asWidget().isVisible();
    }

    private void reportError(Throwable error) {
        // TODO Auto-generated method stub
    }

    // Presenter
    private void populateTurnoverAnalysis() {
        if (isEnabled()) {
            AnalysisResolution scale = getSelectedResolution();
            if (filter == null | scale == null) {
                setTurnoverAnalysisData(null);
                return;
            }
            service.turnoverAnalysis(new AsyncCallback<Vector<UnitVacancyReportTurnoverAnalysisDTO>>() {

                @Override
                public void onFailure(Throwable caught) {
                    reportError(caught);
                }

                @Override
                public void onSuccess(Vector<UnitVacancyReportTurnoverAnalysisDTO> result) {
                    setTurnoverAnalysisData(result);
                }
            }, new Vector<String>(filter.getBuildingsFilteringCriteria()), filter.getFrom(), filter.getTo(), scale);
        }
    }

}
