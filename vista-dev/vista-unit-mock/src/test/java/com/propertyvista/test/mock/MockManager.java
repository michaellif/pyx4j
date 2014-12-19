/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 20, 2013
 * @author michaellif
 */
package com.propertyvista.test.mock;

import java.util.LinkedHashMap;
import java.util.Map;

public class MockManager {

    private final Map<Class<? extends MockDataModel<?>>, MockDataModel<?>> models;

    private final MockConfig config;

    public MockManager(MockConfig config) {
        this.config = config;
        models = new LinkedHashMap<Class<? extends MockDataModel<?>>, MockDataModel<?>>();
    }

    public void addModel(Class<? extends MockDataModel<?>> modelType) {
        try {
            MockDataModel<?> model = modelType.newInstance();
            model.setMockManager(this);
            model.generate();
            models.put(modelType, model);
        } catch (Exception e) {
            throw new Error("Failed to instantiate Mock Model " + modelType.getSimpleName(), e);
        }
    }

    public MockConfig getConfig() {
        return config;
    }

    @SuppressWarnings("unchecked")
    public <E extends MockDataModel<?>> E getDataModel(Class<E> modelClass) {
        E model = (E) models.get(modelClass);
        if (model == null) {
            throw new Error("Failed to find Mock Model " + modelClass.getSimpleName() + ". Most likely required Model is not yet added to MockManager.");
        }
        return model;
    }
}
