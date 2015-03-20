/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 18, 2015
 * @author vlads
 */
package com.propertyvista.config.deployment;

import java.io.Serializable;

import com.pyx4j.config.server.NamespaceData;

import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.security.common.VistaApplication;

public class VistaApplicationContext extends NamespaceData implements Serializable {

    private static final long serialVersionUID = 1L;

    private VistaApplication application;

    private Pmc currentPmc;

    private VistaApplicationContext() {
        super(null);
    }

    public VistaApplicationContext(String namespace, VistaApplication application, Pmc currentPmc) {
        super(namespace);
        this.application = application;
        this.currentPmc = currentPmc;
    }

    public VistaApplication getApplication() {
        return application;
    }

    public Pmc getCurrentPmc() {
        return currentPmc;
    }

}
