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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Vector;

import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.Document;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.basic.SvgRoot;
import com.pyx4j.svg.j2se.SvgFactoryForBatik;
import com.pyx4j.svg.j2se.SvgRootImpl;

import com.propertyvista.crm.rpc.services.dashboard.gadgets.AvailabilityReportService;
import com.propertyvista.crm.server.services.dashboard.gadgets.AvailabilityReportServiceImpl;
import com.propertyvista.crm.server.services.reports.AbstractGadgetReportModelCreator;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitTurnoversPerIntervalDTO;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.TurnoverAnalysisMetadata;

public class TurnoverAnalysisReportCreator extends AbstractGadgetReportModelCreator<TurnoverAnalysisMetadata> {

    public TurnoverAnalysisReportCreator() {
        super(TurnoverAnalysisMetadata.class);
    }

    @Override
    protected void convert(AsyncCallback<com.propertyvista.crm.server.services.reports.AbstractGadgetReportModelCreator.ConvertedGadgetMetadata> callback,
            GadgetMetadata gadgetMetadata) {
        // TODO Auto-generated method stub
        final TurnoverAnalysisMetadata turnoverAnalysisMetadata = (TurnoverAnalysisMetadata) gadgetMetadata;
        final int HEIGHT = 200;
        final int WIDTH = 555;
        AvailabilityReportService service = new AvailabilityReportServiceImpl();

        service.turnoverAnalysis(new AsyncCallback<Vector<UnitTurnoversPerIntervalDTO>>() {

            @Override
            public void onFailure(Throwable arg0) {
            }

            @Override
            public void onSuccess(Vector<UnitTurnoversPerIntervalDTO> arg0) {

                BufferedImage graph = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

                SvgFactory factory = new SvgFactoryForBatik();

                SvgRoot svgroot = factory.getSvgRoot();
                ((SvgRootImpl) svgroot).setAttributeNS(null, "width", String.valueOf(WIDTH));
                ((SvgRootImpl) svgroot).setAttributeNS(null, "height", String.valueOf(HEIGHT));
                Document doc = ((SvgRootImpl) svgroot).getDocument();
                SVGGraphics2D g = new SVGGraphics2D(doc);
                g.setSVGCanvasSize(new Dimension(WIDTH, HEIGHT));

                //JSVGCanvas canvas = new JSVGCanvas();
                //canvas.setSVGDocument((SVGDocument) doc);

            }
        }, new Vector<Key>(), new LogicalDate());
    }

}
