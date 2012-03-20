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
import java.util.Map;
import java.util.Vector;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;

import com.propertyvista.crm.rpc.services.dashboard.gadgets.AvailabilityReportService;
import com.propertyvista.crm.server.services.dashboard.gadgets.AvailabilityReportServiceImpl;
import com.propertyvista.crm.server.services.reports.AbstractGadgetReportModelCreator;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityReportSummaryDTO;
import com.propertyvista.domain.dashboard.gadgets.type.AvailabilitySummary;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;

public class UnitAvailabilitySummaryReportCreator extends AbstractGadgetReportModelCreator<AvailabilitySummary> {

    private static final SimpleDateFormat MONTH_LABEL_FORMAT = new SimpleDateFormat("MMM-yy");

    private static final SimpleDateFormat REPORT_FORMAT = new SimpleDateFormat("yyyy-MMM-dd");

    private static final int HEIGHT = 250;

    private static final int WIDTH = 554;

    protected static final String GRAPH = "GRAPH";

    protected static final String AS_OF = "AS_OF";

    public UnitAvailabilitySummaryReportCreator() {
        super(AvailabilitySummary.class);
    }

    @Override
    protected void convert(
            final AsyncCallback<com.propertyvista.crm.server.services.reports.AbstractGadgetReportModelCreator.ConvertedGadgetMetadata> callback,
            GadgetMetadata gadgetMetadata, List<Key> selectedBuildings) {
        final AvailabilitySummary availabilitySummaryMetadata = (AvailabilitySummary) gadgetMetadata;
        final LogicalDate asOf = availabilitySummaryMetadata.customizeDate().isBooleanTrue() ? availabilitySummaryMetadata.asOf().getValue()
                : new LogicalDate();

        AvailabilityReportService service = new AvailabilityReportServiceImpl();

        service.summary(new AsyncCallback<UnitAvailabilityReportSummaryDTO>() {

            @Override
            public void onFailure(Throwable arg0) {

            }

            @Override
            public void onSuccess(UnitAvailabilityReportSummaryDTO summary) {
                List<UnitAvailabilityReportSummaryDTO> details = new ArrayList<UnitAvailabilityReportSummaryDTO>();
                details.add(summary);
                Map<String, Object> parameters = new HashMap<String, Object>();

                parameters.put(AS_OF, REPORT_FORMAT.format(asOf));
                callback.onSuccess(new ConvertedGadgetMetadata(details, parameters));
            }
        }, new Vector<Key>(selectedBuildings), asOf);
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
