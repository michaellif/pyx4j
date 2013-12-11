/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-11
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.legal.n4.forms;

import com.pyx4j.site.client.ui.visor.AbstractVisorEditor;

import com.propertyvista.crm.rpc.dto.legal.n4.N4AddressInputDTO;

public class N4AddressInputFormVisor extends AbstractVisorEditor<N4AddressInputDTO> {

    public N4AddressInputFormVisor(com.pyx4j.site.client.ui.visor.IVisorEditor.Controller controller) {
        super(controller);
        setForm(new N4AddressInputForm());
    }

}
