/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 25, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents;

import java.util.Vector;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.maintenance.IssueClassification;
import com.propertyvista.domain.maintenance.IssueElement;
import com.propertyvista.domain.maintenance.IssueRepairSubject;
import com.propertyvista.domain.maintenance.IssueSubjectDetails;
import com.propertyvista.portal.rpc.portal.dto.MaintenanceRequestDTO;

public interface NewMaintenanceRequestView extends IsWidget {

    void setPresenter(Presenter presenter);

    void populate(MaintenanceRequestDTO requests);

    void updateIssueElementSelector(Vector<IssueElement> rooms);

    void updateIssueRepairSubjectSelector(IList<IssueRepairSubject> subjects);

    void updateIssueSubjectDetailsSelector(IList<IssueSubjectDetails> details);

    void updateIssueClassificationSelector(IList<IssueClassification> classifications);

    interface Presenter {

        public void submit();

        public void onIssueElementSelection(IssueElement selectedItem);

        public void onIssueRepairSubjectSelection(IssueRepairSubject selectedItem);

        public void onSubjectDetailsSelection(IssueSubjectDetails selectedItem);

    }

}
