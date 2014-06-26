/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-22
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.simulator.dbp;

import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.operations.client.ui.crud.OperationsListerViewImplBase;
import com.propertyvista.operations.domain.eft.dbp.simulator.DirectDebitSimFile;

public class DirectDebitSimFileListerViewImpl extends OperationsListerViewImplBase<DirectDebitSimFile> implements DirectDebitSimFileListerView {

    public DirectDebitSimFileListerViewImpl() {
        setLister(new DirectDebitSimFileLister());
    }

    public static class DirectDebitSimFileLister extends AbstractLister<DirectDebitSimFile> {

        public DirectDebitSimFileLister() {
            super(DirectDebitSimFile.class, true);
            setDataTableModel(new DataTableModel<DirectDebitSimFile>(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().serialNumber()).build(),
                    new MemberColumnDescriptor.Builder(proto().creatationDate()).build(),
                    new MemberColumnDescriptor.Builder(proto().sentDate()).build(),
                    new MemberColumnDescriptor.Builder(proto().status()).build()
            ));//@formatter:on
        }
    }
}
