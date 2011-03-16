/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-16
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui.components;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dev.Link;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
import com.propertyvista.portal.domain.pt.ApplicationDocument;
import com.propertyvista.portal.domain.pt.ApplicationDocument.DocumentType;
import com.propertyvista.portal.rpc.pt.ApplicationDocumentsList;
import com.propertyvista.portal.rpc.pt.services.ApplicationDocumentsService;

import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.forms.client.ui.CHyperlink;

public class FileUpload extends HorizontalPanel {

    private static I18n i18n = I18nFactory.getI18n(FileUpload.class);

    VerticalPanel docList;

    public FileUpload() {

        HTML side = new HTML("&nbsp;&nbsp;&nbsp;");
        add(side);

        Element td = DOM.getParent(side.getElement());
        if (td != null) {
            td.getStyle().setBackgroundColor("#bbb");
        }

        add(new HTML("&nbsp;&nbsp;&nbsp;"));
        add(new Image(SiteImages.INSTANCE.exclamation()));

        final FlowPanel fp = new FlowPanel();
        fp.getElement().getStyle().setPaddingLeft(1, Unit.EM);
        fp.add(new HTML(HtmlUtils.h4(i18n.tr("Attach Files"))));
        fp.add(docList = new VerticalPanel());
        docList.getElement().getStyle().setPaddingLeft(1, Unit.EM);
        docList.getElement().getStyle().setPaddingBottom(1, Unit.EM);
        fp.add(new Button(i18n.tr("Browse"), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                new FileUploadDialog(/* getEditorPanel().getEntity() */) {
                    @Override
                    public void onComplete() {
                    }
                }.show();
            }
        }));

        add(fp);
        setCellVerticalAlignment(fp, HorizontalPanel.ALIGN_TOP);
        setCellWidth(fp, "100%");

    }

    public void populate(Long tenantId, DocumentType documentType) {

        docList.clear();

        ApplicationDocumentsService ads = (ApplicationDocumentsService) GWT.create(ApplicationDocumentsService.class);
        if (ads != null) {
            ads.retrieveAttachments(new AsyncCallback<ApplicationDocumentsList>() {

                @Override
                public void onSuccess(ApplicationDocumentsList result) {
                    for (ApplicationDocument doc : result.documents()) {

                        CHyperlink link = new CHyperlink(doc.getStringView(), new Command() {
                            @Override
                            public void execute() {
                                //TODO: show file here... 
                            }
                        });

                        Button remove = new Button(i18n.tr("x"), new ClickHandler() {

                            @Override
                            public void onClick(ClickEvent event) {
                                // TODO remove file here
                            }
                        });

                        HorizontalPanel item = new HorizontalPanel();
                        item.add(link);
                        item.add(remove);

                        docList.add(item);
                    }
                }

                @Override
                public void onFailure(Throwable caught) {
                    // TODO Auto-generated method stub

                }
            }, tenantId, documentType);
        }

    }
}