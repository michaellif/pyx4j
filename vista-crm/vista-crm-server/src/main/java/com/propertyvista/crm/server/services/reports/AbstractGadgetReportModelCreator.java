/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.crm.server.services.reports.directory.BuildingListerReportCreator;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;

public abstract class AbstractGadgetReportModelCreator<G extends GadgetMetadata> implements GadgetReportModelCreator {

    private static final String REPORT_DESIGN_NAME_PREFIX = "reports";

    private final Class<G> gadgetMetadataClass;

    public AbstractGadgetReportModelCreator(Class<G> gadgetMetadataClass) {
        this.gadgetMetadataClass = gadgetMetadataClass;
    }

    /**
     * @param callback
     *            not <code>null</code>
     * @param gadgetMetadata
     *            not <code>null</code>
     */
    @Override
    public void createReportModel(final AsyncCallback<JasperReportModel> callback, GadgetMetadata gadgetMetadata, Vector<Key> selectedBuildings) {
        if (canHandle(gadgetMetadata.getInstanceValueClass())) {
            convert(new AsyncCallback<AbstractGadgetReportModelCreator.ConvertedGadgetMetadata>() {

                @Override
                public void onFailure(Throwable arg0) {

                }

                @Override
                public void onSuccess(ConvertedGadgetMetadata reportData) {
                    callback.onSuccess(new JasperReportModel(designName(gadgetMetadataClass), reportData.data, reportData.parameters));
                }
            }, gadgetMetadata);
        } else {
            callback.onFailure(new Error(BuildingListerReportCreator.class.getSimpleName() + " can't handle a gadget metadata class "
                    + gadgetMetadata.getInstanceValueClass().getSimpleName()));
        }
    }

    protected String designName(Class<? extends GadgetMetadata> gadgetMetadataClass) {
        return REPORT_DESIGN_NAME_PREFIX + "." + gadgetMetadataClass.getSimpleName();
    }

    public boolean canHandle(Class<? extends IEntity> class1) {
        return class1.equals(this.gadgetMetadataClass);
    }

    protected abstract void convert(AsyncCallback<ConvertedGadgetMetadata> callback, GadgetMetadata gadgetMetadata);

    public static class ConvertedGadgetMetadata {

        public List<? extends IEntity> data;

        public Map<String, Object> parameters;

        public ConvertedGadgetMetadata(List<? extends IEntity> data, Map<String, Object> parameters) {
            this.data = data;
            this.parameters = parameters;
        }
    }
}
