/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-05
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.usermessage;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.dialog.images.DialogImages;

import com.propertyvista.common.client.site.UserMessage;
import com.propertyvista.common.client.site.UserMessage.UserMessageType;

public class UserMessageViewImpl extends Composite implements UserMessageView {

    public interface MessageTypeImages {

        ImageResource error();

        ImageResource info();

        ImageResource warning();
    }

    private static final I18n i18n = I18n.get(UserMessageView.class);

    private final SimplePanel messageTypeImageHolder;

    private final Label messageLabel;

    private final Label debugMessageLabel;

    private Presenter presenter;

    private final MessageTypeImages messageTypeImageResources;

    public UserMessageViewImpl(MessageTypeImages messageTypeImageResources) {
        this.messageTypeImageResources = messageTypeImageResources;
        FlowPanel viewPanel = new FlowPanel();
        viewPanel.getElement().getStyle().setTextAlign(TextAlign.CENTER);

        messageTypeImageHolder = new SimplePanel();
        viewPanel.add(messageTypeImageHolder);

        messageLabel = new Label();
        viewPanel.add(messageLabel);

        debugMessageLabel = new Label();
        debugMessageLabel.getElement().getStyle().setMarginTop(2, Unit.EM);
        viewPanel.add(debugMessageLabel);

        SimplePanel buttonHolder = new SimplePanel();
        buttonHolder.setWidth("100%");
        buttonHolder.getElement().getStyle().setMarginTop(2, Unit.EM);

        Button acceptMessageButton = new Button(i18n.tr("Ok"), new Command() {
            @Override
            public void execute() {
                presenter.acceptMessage();
            }
        });
        acceptMessageButton.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.NONE); // this is a must because there are people who write strage CSS

        buttonHolder.setWidget(acceptMessageButton);

        viewPanel.add(buttonHolder);

        initWidget(viewPanel);
    }

    public UserMessageViewImpl() {
        this(new MessageTypeImages() {

            private DialogImages di;
            {
                di = GWT.create(DialogImages.class);
            }

            @Override
            public ImageResource error() {
                return di.error();
            }

            @Override
            public ImageResource info() {
                return di.confirm();
            }

            @Override
            public ImageResource warning() {
                return di.info();
            }
        });
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate(UserMessage userMessage) {
        messageTypeImageHolder.setWidget(new Image(getUserMessageImageResource(userMessage.getMessageType())));
        messageLabel.setText(userMessage.getMessage());

        debugMessageLabel.setVisible(isDebugInfoRequired());
        debugMessageLabel.setText(isDebugInfoRequired() & userMessage.getDebugMessage() != null ? userMessage.getDebugMessage() : "");
    }

    private boolean isDebugInfoRequired() {
        return ApplicationMode.isDevelopment();
    }

    private ImageResource getUserMessageImageResource(UserMessageType messageType) {
        ImageResource messageImageResource = null;
        if (messageType == null) {
            messageImageResource = messageTypeImageResources.warning();
        } else {
            switch (messageType) {
            case ERROR:
                messageImageResource = messageTypeImageResources.error();
                break;
            case FAILURE:
                messageImageResource = messageTypeImageResources.error();
                break;
            case INFO:
                messageImageResource = messageTypeImageResources.info();
                break;
            case WARN:
                messageImageResource = messageTypeImageResources.warning();
                break;
            default:
                messageImageResource = messageTypeImageResources.warning();
                break;
            }
        }
        return messageImageResource;
    }
}
