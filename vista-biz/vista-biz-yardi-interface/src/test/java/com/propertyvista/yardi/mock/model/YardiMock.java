/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 15, 2014
 * @author stanp
 */
package com.propertyvista.yardi.mock.model;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import com.propertyvista.yardi.YardiInterface;
import com.propertyvista.yardi.mock.model.manager.YardiMockManager;
import com.propertyvista.yardi.stubs.YardiStubFactory;

public class YardiMock {

    static final ThreadLocal<YardiMock> threadLocalInstance = new ThreadLocal<YardiMock>() {
        @Override
        protected YardiMock initialValue() {
            return new YardiMock();
        }
    };

    public static YardiMock server() {
        return threadLocalInstance.get();
    }

    private final YardiMockModel mockModel = new YardiMockModel();

    private final Map<Class<? extends YardiMockManager>, YardiMockManager> managers = new HashMap<>();

    private boolean simulateError;

    public void reset() {
        mockModel.reset();
        managers.clear();
        simulateError = false;
    }

    public <M extends YardiMockManager> void addManager(Class<M> managerType) {
        addManager(managerType, null);
    }

    @SuppressWarnings("unchecked")
    public <M extends YardiMockManager> void addManager(Class<M> managerType, Class<? extends M> implClass) {
        try {
            if (implClass == null) {
                String implClassName = managerType.getName();
                int lastDot = implClassName.lastIndexOf(".");
                implClassName = implClassName.subSequence(0, lastDot) + ".impl" + implClassName.substring(lastDot) + "Impl";
                implClass = (Class<? extends M>) Class.forName(implClassName);
            }
            managers.put(managerType, implClass.newInstance());
        } catch (Exception e) {
            throw new Error("Failed to instantiate Yardi Mock Model " + implClass.getSimpleName(), e);
        }
    }

    public void simulateException() {
        this.simulateError = true;
    }

    @SuppressWarnings("unchecked")
    public <M extends YardiMockManager> M getManager(Class<M> managerType) {
        try {
            return (M) managers.get(managerType);
        } catch (NullPointerException e) {
            throw new Error("Manager not found: " + managerType.getSimpleName());
        }
    }

    public static <M extends YardiInterface> void addStub(Class<M> stubType, Class<? extends M> stubClass) {
        try {
            YardiStubFactory.register(stubType, stubClass);
        } catch (Exception e) {
            throw new Error("Failed to instantiate Yardi Stub Mock " + stubClass.getSimpleName(), e);
        }
    }

    public YardiMockModel getModel() {
        return mockModel;
    }

    public void validate() throws RemoteException {
        if (simulateError) {
            simulateError = false;
            throw new RemoteException("Simulated RemoteException");
        }
    }
}
