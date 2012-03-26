/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 12, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.client.EntityDataSource;
import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.client.upload.UploadPanel;
import com.pyx4j.essentials.rpc.upload.UploadResponse;
import com.pyx4j.essentials.rpc.upload.UploadService;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;
import com.pyx4j.widgets.client.dialog.OkOptionText;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.validators.CanadianSinValidator;
import com.propertyvista.domain.media.ApplicationDocument;
import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType.Type;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.rpc.ptapp.dto.ApplicationDocumentUploadDTO;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationDocumentUploadService;

public abstract class ApplicationDocumentUploaderDialog extends VerticalPanel implements OkCancelOption, OkOptionText {

    private static final I18n i18n = I18n.get(ApplicationDocumentUploaderDialog.class);

    private final UploadPanel<ApplicationDocumentUploadDTO, ApplicationDocument> uploadPanel;

    private final Dialog dialog;

    private CEntityDecoratableEditor<ApplicationDocumentUploadDTO> documentUploadForm;

    @SuppressWarnings("unchecked")
    public ApplicationDocumentUploaderDialog(String title, final Key applicationId) {
        dialog = new Dialog(title, this, null);

        documentUploadForm = new CEntityDecoratableEditor<ApplicationDocumentUploadDTO>(ApplicationDocumentUploadDTO.class) {
            @Override
            public IsWidget createContent() {
                FlowPanel content = new FlowPanel();
                content.add(new DecoratorBuilder(inject(proto().identificationDocument(), new CEntityComboBox<IdentificationDocumentType>(
                        IdentificationDocumentType.class))).componentWidth(15).labelWidth(10).build());
                CEntityComboBox<IdentificationDocumentType> idCombo = (CEntityComboBox<IdentificationDocumentType>) get(proto().identificationDocument());
                idCombo.setOptionsDataSource(new EntityDataSource<IdentificationDocumentType>() {
                    @Override
                    public void obtain(EntityQueryCriteria<IdentificationDocumentType> criteria,
                            final AsyncCallback<EntitySearchResult<IdentificationDocumentType>> handlingCallback) {
                        ClientPolicyManager.obtainEffectivePolicy(ClientPolicyManager.getOrganizationPoliciesNode(), ApplicationDocumentationPolicy.class,
                                new DefaultAsyncCallback<ApplicationDocumentationPolicy>() {
                                    @Override
                                    public void onSuccess(ApplicationDocumentationPolicy result) {
                                        EntitySearchResult<IdentificationDocumentType> allowedIds = new EntitySearchResult<IdentificationDocumentType>();
                                        allowedIds.getData().addAll(result.allowedIDs());
                                        handlingCallback.onSuccess(allowedIds);
                                    }
                                });
                    }
                });

                content.add(new DecoratorBuilder(inject(proto().details())).componentWidth(15).labelWidth(10).build());
                get(proto().details()).addValueValidator(new EditableValueValidator<String>() {

                    private final EditableValueValidator<String> sinValidator = new CanadianSinValidator();

                    @Override
                    public ValidationFailure isValid(CComponent<String, ?> component, String value) {
                        if (value != null) {
                            if (getValue().identificationDocument().type().getValue() == Type.canadianSIN) {
                                return sinValidator.isValid(component, value);
                            } else {
                                return null;
                            }
                        } else {
                            return null;
                        }
                    }
                });
                return content;
            }
        };
        documentUploadForm.asWidget().setSize("400px", "100%");
        documentUploadForm.initContent();
        documentUploadForm.setMandatory(true);
        documentUploadForm.populateNew();

        uploadPanel = new UploadPanel<ApplicationDocumentUploadDTO, ApplicationDocument>(
                (UploadService<ApplicationDocumentUploadDTO, ApplicationDocument>) GWT.create(ApplicationDocumentUploadService.class)) {

            @Override
            protected void onUploadSubmit() {
                dialog.getOkButton().setEnabled(false);
            }

            @Override
            protected void onUploadError(UploadError error, String args) {
                super.onUploadError(error, args);
                dialog.getOkButton().setEnabled(true);
                uploadPanel.reset();
            }

            @Override
            protected void onUploadComplete(UploadResponse<ApplicationDocument> serverUploadResponse) {
                dialog.hide();
                ApplicationDocumentUploaderDialog.this.onUploadComplete(serverUploadResponse);
            }

            @Override
            protected ApplicationDocumentUploadDTO getUploadData() {
                return documentUploadForm.getValue();
            }

        };
        uploadPanel.setSupportedExtensions(ApplicationDocumentUploadService.supportedFormats);
        uploadPanel.setServletPath(GWT.getModuleBaseURL() + DeploymentConsts.uploadServletMapping);
        uploadPanel.setSize("400px", "100%");

        VerticalPanel dialogBody = new VerticalPanel();
        dialogBody.setSize("400px", "100%");
        dialogBody.add(documentUploadForm);
        dialogBody.add(uploadPanel);
        dialogBody.setCellHorizontalAlignment(uploadPanel, HasHorizontalAlignment.ALIGN_CENTER);

        dialog.setBody(dialogBody);
        dialog.setPixelSize(460, 200);
    }

    public void show() {
        dialog.show();
    }

    protected abstract void onUploadComplete(UploadResponse<ApplicationDocument> serverUploadResponse);

    @Override
    public boolean onClickOk() {
        if (documentUploadForm.isValid()) {
            uploadPanel.uploadSubmit();
            return false;
        } else {
            MessageDialog.error(i18n.tr("Validation Error"), documentUploadForm.getValidationResults().getMessagesText(true));
            return false;
        }
    }

    @Override
    public String optionTextOk() {
        return i18n.tr("Upload");
    }

    @Override
    public boolean onClickCancel() {
        uploadPanel.uploadCancel();
        return true;
    }

}
