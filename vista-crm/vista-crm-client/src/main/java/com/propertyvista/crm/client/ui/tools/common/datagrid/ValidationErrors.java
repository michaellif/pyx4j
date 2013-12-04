/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-04
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.common.datagrid;

import java.util.Collections;
import java.util.List;

public class ValidationErrors {

    private final List<String> validationErrors;

    private final boolean pending;

    /** Creates validation errors in pending state */
    public ValidationErrors() {
        this.pending = true;
        this.validationErrors = Collections.emptyList();
    }

    public ValidationErrors(List<String> validationErrors) {
        this.pending = false;
        this.validationErrors = validationErrors;
    }

    public boolean isPending() {
        return this.pending;
    }

    public List<String> getValidationErrorMessages() {
        return Collections.unmodifiableList(validationErrors);
    }

}