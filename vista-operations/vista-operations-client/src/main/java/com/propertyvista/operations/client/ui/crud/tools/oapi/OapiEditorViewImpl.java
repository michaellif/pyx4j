/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2014
 * @author ernestog
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.tools.oapi;

import com.propertyvista.operations.client.ui.crud.OperationsEditorViewImplBase;
import com.propertyvista.operations.rpc.dto.OapiConversionDTO;

public class OapiEditorViewImpl extends OperationsEditorViewImplBase<OapiConversionDTO> implements OapiEditorView {

    public OapiEditorViewImpl() {
        setForm(new OapiForm(this));
    }

}
