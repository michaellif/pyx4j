/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.fundstransfer.directdebitrecords;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.forms.client.ui.CTextArea;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.operations.client.activity.crud.fundstransfer.directdebitrecord.DirectDebitRecordViewerActivity;
import com.propertyvista.operations.client.ui.crud.OperationsViewerViewImplBase;
import com.propertyvista.operations.domain.eft.dbp.DirectDebitRecord;
import com.propertyvista.operations.domain.eft.dbp.DirectDebitRecordProcessingStatus;

public class DirectDebitRecordViewerViewImpl extends OperationsViewerViewImplBase<DirectDebitRecord> implements DirectDebitRecordViewerView {

    private final static I18n i18n = I18n.get(DirectDebitRecordViewerViewImpl.class);

    private final Button btnMarkRefunded;

    public DirectDebitRecordViewerViewImpl() {
        super(true);
        setForm(new DirectDebitRecordForm(this));

        // Add actions:
        btnMarkRefunded = new Button(i18n.tr("Mark 'Refunded'"), new Command() {
            @Override
            public void execute() {
                new OperationNotesBox() {
                    @Override
                    public boolean onClickOk() {
                        if (CommonsStringUtils.isEmpty(getNotes())) {
                            MessageDialog.error(i18n.tr("Error"), i18n.tr("Please fill the notes"));
                            return false;
                        }
                        ((DirectDebitRecordViewerActivity) getPresenter()).markRefunded(getNotes());
                        return true;
                    }
                }.show();
            }
        });
        addHeaderToolbarItem(btnMarkRefunded.asWidget());
    }

    @Override
    public void populate(DirectDebitRecord value) {
        super.populate(value);

        btnMarkRefunded.setVisible(DirectDebitRecordProcessingStatus.Invalid.equals(value.processingStatus().getValue()));
    }

    private abstract class OperationNotesBox extends OkCancelDialog {

        private final CTextArea reason = new CTextArea();

        public OperationNotesBox() {
            super(i18n.tr("Operation Notes"));
            setBody(createBody());
            setDialogPixelWidth(350);
        }

        protected Widget createBody() {
            VerticalPanel content = new VerticalPanel();

            content.add(new HTML(i18n.tr("Please fill the notes") + ":"));
            content.add(reason);

            reason.asWidget().setWidth("100%");
            content.setWidth("100%");
            return content.asWidget();
        }

        public String getNotes() {
            return reason.getValue();
        }
    }
}
