/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 16, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports.directory;

import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.w3c.dom.Document;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.basic.SvgRoot;
import com.pyx4j.svg.chart.DataSource;
import com.pyx4j.svg.chart.GridBasedChartConfigurator;
import com.pyx4j.svg.chart.LineChart;
import com.pyx4j.svg.j2se.SvgFactoryForBatik;
import com.pyx4j.svg.j2se.SvgRootImpl;

import com.propertyvista.crm.rpc.services.dashboard.gadgets.AvailabilityReportService;
import com.propertyvista.crm.server.services.dashboard.gadgets.AvailabilityReportServiceImpl;
import com.propertyvista.crm.server.services.reports.AbstractGadgetReportModelCreator;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitTurnoversPerIntervalDTO;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.TurnoverAnalysisMetadata;

public class TurnoverAnalysisReportCreator extends AbstractGadgetReportModelCreator<TurnoverAnalysisMetadata> {

    private static final SimpleDateFormat MONTH_LABEL_FORMAT = new SimpleDateFormat("MMM-yy");

    private static final SimpleDateFormat REPORT_FORMAT = new SimpleDateFormat("yyyy-MMM-dd");

    private static final int HEIGHT = 250;

    private static final int WIDTH = 554;

    protected static final String GRAPH = "GRAPH";

    protected static final String AS_OF = "AS_OF";

    public TurnoverAnalysisReportCreator() {
        super(TurnoverAnalysisMetadata.class);
    }

    @Override
    protected void convert(
            final AsyncCallback<com.propertyvista.crm.server.services.reports.AbstractGadgetReportModelCreator.ConvertedGadgetMetadata> callback,
            GadgetMetadata gadgetMetadata, List<Key> selectedBuildings) {
        final TurnoverAnalysisMetadata turnoverAnalysisMetadata = (TurnoverAnalysisMetadata) gadgetMetadata;
        final LogicalDate asOf = turnoverAnalysisMetadata.customizeDate().isBooleanTrue() ? turnoverAnalysisMetadata.asOf().getValue() : new LogicalDate();

        AvailabilityReportService service = new AvailabilityReportServiceImpl();

        service.turnoverAnalysis(new AsyncCallback<Vector<UnitTurnoversPerIntervalDTO>>() {

            @Override
            public void onFailure(Throwable arg0) {
            }

            @Override
            public void onSuccess(Vector<UnitTurnoversPerIntervalDTO> data) {

                DataSource ds = new DataSource();
                for (UnitTurnoversPerIntervalDTO intervalData : data) {
                    ArrayList<Double> values = new ArrayList<Double>();
                    if (!turnoverAnalysisMetadata.isTurnoverMeasuredByPercent().isBooleanTrue()) {
                        values.add((double) intervalData.unitsTurnedOverAbs().getValue().intValue());
                    } else {
                        values.add(intervalData.unitsTurnedOverPct().getValue());
                    }

                    ds.addDataSet(ds.new Metric(MONTH_LABEL_FORMAT.format(intervalData.intervalValue().getValue())), values);
                }

                SvgFactory factory = new SvgFactoryForBatik();

                GridBasedChartConfigurator config = new GridBasedChartConfigurator(factory, ds, WIDTH, HEIGHT);
                BufferedImage graph = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

                SvgRoot svgroot = factory.getSvgRoot();
                ((SvgRootImpl) svgroot).setAttributeNS(null, "width", String.valueOf(WIDTH));
                ((SvgRootImpl) svgroot).setAttributeNS(null, "height", String.valueOf(HEIGHT));

                svgroot.add(new LineChart(config));

                Document doc = ((SvgRootImpl) svgroot).getDocument();

                BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
                transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, new Float(WIDTH));
                transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, new Float(HEIGHT));

                try {
                    transcoder.transcode(new TranscoderInput(doc), null);
                    graph = transcoder.getImage();
                } catch (TranscoderException exeption) {
                    throw new RuntimeException(exeption);
                }
                HashMap<String, Object> parameters = new HashMap<String, Object>();

                parameters.put(AS_OF, REPORT_FORMAT.format(asOf));
                parameters.put(GRAPH, graph);

                callback.onSuccess(new ConvertedGadgetMetadata(null, parameters));
            }
        }, new Vector<Key>(), asOf);
    }

    public class BufferedImageTranscoder extends ImageTranscoder {

        private BufferedImage image = null;

        @Override
        public BufferedImage createImage(int width, int height) {
            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
            return bi;
        }

        @Override
        public void writeImage(BufferedImage image, TranscoderOutput out) throws TranscoderException {
            this.image = image;
        }

        public BufferedImage getImage() {
            return this.image;
        }
    }
}
