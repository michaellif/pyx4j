/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 22, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.tabpanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.UIObject;

import com.pyx4j.widgets.client.ImageFactory;
import com.pyx4j.widgets.client.Tooltip;
import com.pyx4j.widgets.client.WidgetsImageBundle;

/**
 * @author michaellif
 * 
 */
public class TabBarItem extends HorizontalPanel {

    private final Label label;

    private boolean isModifyed = false;

    private final Label modifyedLabel;

    private boolean isEnabled = true;

    private boolean isSelected = false;

    private Image closeImage;

    private final WidgetsImageBundle images = ImageFactory.getImages();

    public TabBarItem(final TabBar parent, String labelString, ImageResource imageResource, boolean closable) {
        super();

        sinkEvents(Event.ONCLICK);
        addClickHandler(parent);

        HTML leftSubpanel = new HTML("&nbsp;");
        leftSubpanel.setHeight("25px");
        leftSubpanel.setWidth("8px");
        add(leftSubpanel);
        setStyleName("gwt-TabBarItem");
        leftSubpanel.setStyleName("gwt-TabBarItemLeft");

        final HorizontalPanel rightSubpanel = new HorizontalPanel();
        rightSubpanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
        rightSubpanel.setStyleName("gwt-TabBarItemRight");
        DOM.setElementProperty(rightSubpanel.getElement(), "cellSpacing", "4");

        if (imageResource != null) {
            Image image = new Image(imageResource);
            image.setStyleName("gwt-TabBarItemImage");
            rightSubpanel.add(image);
            rightSubpanel.setCellVerticalAlignment(image, HasVerticalAlignment.ALIGN_MIDDLE);
        }

        modifyedLabel = new Label("*");
        modifyedLabel.setVisible(isModifyed);
        rightSubpanel.add(modifyedLabel);

        label = new Label(labelString);
        label.setStyleName("gwt-TabBarItemLabel");
        label.setWordWrap(false);
        rightSubpanel.add(label);

        if (closable) {

            closeImage = new Image(images.closeTab());

            DOM.setStyleAttribute(closeImage.getElement(), "cursor", "pointer");
            DOM.setStyleAttribute(closeImage.getElement(), "cursor", "hand");
            closeImage.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    for (int i = 0; i < parent.getTabBarPanel().getWidgetCount(); ++i) {
                        if (parent.getTabBarPanel().getWidget(i) == closeImage.getParent().getParent()) {
                            parent.getTabPanelModel().remove(i, false);
                        }
                    }
                }
            });
            closeImage.addMouseOverHandler(new MouseOverHandler() {

                @Override
                public void onMouseOver(MouseOverEvent event) {
                    closeImage.setResource(images.closeTabFocused());
                }
            });
            closeImage.addMouseOutHandler(new MouseOutHandler() {

                @Override
                public void onMouseOut(MouseOutEvent event) {
                    closeImage.setResource(images.closeTab());
                }
            });

            Tooltip.tooltip(closeImage, "Close");

            rightSubpanel.add(closeImage);
            rightSubpanel.setCellHorizontalAlignment(closeImage, HasHorizontalAlignment.ALIGN_RIGHT);
            rightSubpanel.setCellVerticalAlignment(closeImage, HasVerticalAlignment.ALIGN_MIDDLE);
        }

        rightSubpanel.setCellWidth(label, "100%");
        rightSubpanel.setCellHeight(label, "100%");

        add(rightSubpanel);
        setCellHeight(leftSubpanel, "25px");
        setCellWidth(leftSubpanel, "8px");
        setCellHeight(rightSubpanel, "100%");
    }

    /**
     * @param label
     */
    public void setLabel(String labelString) {
        label.setText(labelString);
    }

    boolean isSelected() {
        return isSelected;
    }

    void setSelected(boolean selected) {
        this.isSelected = selected;
        if (selected) {
            addStyleName("gwt-TabBarItem-selected");
            getWidget(0).addStyleName("gwt-TabBarItemLeft-selected");
            getWidget(1).addStyleName("gwt-TabBarItemRight-selected");
        } else {
            removeStyleName("gwt-TabBarItem-selected");
            getWidget(0).removeStyleName("gwt-TabBarItemLeft-selected");
            getWidget(1).removeStyleName("gwt-TabBarItemRight-selected");
        }
    }

    boolean isEnabled() {
        return isEnabled;
    }

    void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
        if (!enabled) {
            addStyleName("gwt-TabBarItem-disabled");
            getWidget(0).addStyleName("gwt-TabBarItemLeft-disabled");
            getWidget(1).addStyleName("gwt-TabBarItemRight-disabled");
        } else {
            removeStyleName("gwt-TabBarItem-disabled");
            getWidget(0).removeStyleName("gwt-TabBarItemLeft-disabled");
            getWidget(1).removeStyleName("gwt-TabBarItemRight-disabled");
        }
    }

    //    @Override
    //    public void onBrowserEvent(Event event) {
    //
    //        if (closeImage != null && DOM.eventGetTarget(event).equals(closeImage.getElement())) {
    //            return;
    //        }
    //        switch (DOM.eventGetType(event)) {
    //        case Event.ONCLICK:
    //            //            if (clickListeners != null) {
    //            //                clickListeners.fireClick(this);
    //            //            }
    //            break;
    //        }
    //    }

    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }

    public boolean isModifyed() {
        return isModifyed;
    }

    public void setModifyed(boolean isModifyed) {
        this.isModifyed = isModifyed;
        modifyedLabel.setVisible(isModifyed);
    }

}