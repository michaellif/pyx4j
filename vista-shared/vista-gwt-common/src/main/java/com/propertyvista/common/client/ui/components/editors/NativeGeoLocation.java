/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors;

import java.text.ParseException;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.Selector;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.ui.INativeFocusComponent;
import com.pyx4j.widgets.client.TextBox;

import com.propertyvista.domain.GeoLocation;
import com.propertyvista.domain.GeoLocation.LatitudeType;
import com.propertyvista.domain.GeoLocation.LongitudeType;

public class NativeGeoLocation extends SimplePanel implements INativeFocusComponent<GeoLocation> {

    public static String DEFAULT_STYLE_PREFIX = "pyx4j_GeoLocation";

    public static enum StyleDependent implements IStyleDependent {
        disabled, readOnly, invalid
    }

    private final TextBox latitude;

    private final ListBox latitudeType;

    private final TextBox longitude;

    private final ListBox longitudeType;

    private final CGeoLocation cComponent;

    private final HandlerManager focusHandlerManager;

    public NativeGeoLocation(CGeoLocation cComponent) {
        super();
        this.cComponent = cComponent;

        // ----------------------------------------------

        focusHandlerManager = new HandlerManager(this);
        FocusHandler groupFocusHandler = new FocusHandler() {
            @Override
            public void onFocus(FocusEvent e) {
                focusHandlerManager.fireEvent(e);
            }
        };
        BlurHandler groupBlurHandler = new BlurHandler() {
            @Override
            public void onBlur(final BlurEvent e) {
                focusHandlerManager.fireEvent(e);
            }
        };

        // ----------------------------------------------

        HorizontalPanel contentPanel = new HorizontalPanel();
        contentPanel.add(latitude = new TextBox());
        latitude.setWidth("100%");
        latitude.addFocusHandler(groupFocusHandler);
        latitude.addBlurHandler(groupBlurHandler);

        contentPanel.add(latitudeType = new ListBox());
        contentPanel.setCellWidth(latitudeType, "60px");
        DOM.getParent(latitudeType.getElement()).getStyle().setPaddingLeft(5, Unit.PX);

        for (LatitudeType item : LatitudeType.values()) {
            latitudeType.addItem(item.toString(), item.name());
        }
        latitudeType.setSelectedIndex(0);
        latitudeType.setWidth("100%");
        latitudeType.addFocusHandler(groupFocusHandler);
        latitudeType.addBlurHandler(groupBlurHandler);

        Widget gap;
        contentPanel.add(gap = new HTML("&"));
        contentPanel.setCellWidth(gap, "20px");
        DOM.getParent(gap.getElement()).getStyle().setPaddingLeft(10, Unit.PX);

        contentPanel.add(longitude = new TextBox());
        longitude.setWidth("100%");
        longitude.addFocusHandler(groupFocusHandler);
        longitude.addBlurHandler(groupBlurHandler);

        contentPanel.add(longitudeType = new ListBox());
        contentPanel.setCellWidth(longitudeType, "60px");
        DOM.getParent(longitudeType.getElement()).getStyle().setPaddingLeft(5, Unit.PX);

        for (LongitudeType item : LongitudeType.values()) {
            longitudeType.addItem(item.toString(), item.name());
        }
        longitudeType.setSelectedIndex(0);
        longitudeType.setWidth("100%");
        longitudeType.addFocusHandler(groupFocusHandler);
        longitudeType.addBlurHandler(groupBlurHandler);

        // ----------------------------------------------

        setWidget(contentPanel);
        contentPanel.setWidth("100%");
        setStyleName(DEFAULT_STYLE_PREFIX);
    }

    @Override
    public CComponent<?> getCComponent() {
        return cComponent;
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler focusHandler) {
        return focusHandlerManager.addHandler(FocusEvent.getType(), focusHandler);
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler blurHandler) {
        return focusHandlerManager.addHandler(BlurEvent.getType(), blurHandler);
    }

    @Override
    public void setEnabled(boolean enabled) {

        latitude.setEnabled(enabled);
        latitudeType.setEnabled(enabled);

        longitude.setEnabled(enabled);
        longitudeType.setEnabled(enabled);

        String dependentSuffix = Selector.getDependentName(StyleDependent.disabled);
        if (enabled) {
            removeStyleDependentName(dependentSuffix);
        } else {
            addStyleDependentName(dependentSuffix);
        }
    }

    @Override
    public boolean isEnabled() {
        return latitude.isEnabled();
    }

    @Override
    public void setEditable(boolean editable) {

        latitude.setReadOnly(!editable);
        latitudeType.setEnabled(editable);

        longitude.setReadOnly(!editable);
        longitudeType.setEnabled(editable);

        String dependentSuffix = Selector.getDependentName(StyleDependent.readOnly);
        if (editable) {
            removeStyleDependentName(dependentSuffix);
        } else {
            addStyleDependentName(dependentSuffix);
        }
    }

    @Override
    public boolean isEditable() {
        return !latitude.isReadOnly();
    }

    @Override
    public void setValid(boolean valid) {
        String dependentSuffix = Selector.getDependentName(StyleDependent.invalid);
        if (valid) {
            removeStyleDependentName(dependentSuffix);
        } else {
            addStyleDependentName(dependentSuffix);
        }
    }

    @Override
    public void setFocus(boolean focused) {
        latitude.setFocus(focused);
    }

    @Override
    public void setTabIndex(int index) {
        latitude.setTabIndex(index);
    }

    @Override
    public void setNativeValue(GeoLocation value) {
        if (value != null) {
            latitude.setText(new GeoNumberFormat(GeoNumberFormat.Type.latitude).format(value.latitude().getValue()));

            latitudeType.setSelectedIndex(0);
            if (latitudeType != null && !value.latitudeType().isNull()) {
                for (int i = 0; i < latitudeType.getItemCount(); ++i) {
                    if (value.latitudeType().getValue().name().compareTo(latitudeType.getValue(i)) == 0) {
                        latitudeType.setSelectedIndex(i);
                        break;
                    }
                }
            }

            // --------------------------------------------------------------------------------------------

            longitude.setText(new GeoNumberFormat(GeoNumberFormat.Type.longitute).format(value.longitude().getValue()));

            longitudeType.setSelectedIndex(1);
            if (longitudeType != null && !value.longitudeType().isNull()) {
                for (int i = 0; i < longitudeType.getItemCount(); ++i) {
                    if (value.longitudeType().getValue().name().compareTo(longitudeType.getValue(i)) == 0) {
                        longitudeType.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public GeoLocation getNativeValue() throws ParseException {

        GeoLocation value = EntityFactory.create(GeoLocation.class);

        value.latitude().setValue(new GeoNumberFormat(GeoNumberFormat.Type.latitude).parse(latitude.getText()));
        if (latitudeType.getSelectedIndex() >= 0) {
            value.latitudeType().setValue(LatitudeType.valueOf(latitudeType.getValue(latitudeType.getSelectedIndex())));
        }

        value.longitude().setValue(new GeoNumberFormat(GeoNumberFormat.Type.longitute).parse(longitude.getText()));
        if (longitudeType.getSelectedIndex() >= 0) {
            value.longitudeType().setValue(LongitudeType.valueOf(longitudeType.getValue(longitudeType.getSelectedIndex())));
        }

        return value;
    }

    @Override
    public void installStyles(String stylePrefix) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        latitude.ensureDebugId(baseID);
        longitude.ensureDebugId(baseID);
    }

    // ================================================================

    public static class GeoNumberFormat implements IFormat<Double> {

        public enum Type {
            latitude, longitute
        }

        private final Type type;

        private NumberFormat nf = NumberFormat.getFormat("#0.000000");;

        public GeoNumberFormat(Type type) {
            this.type = type;
        }

        public GeoNumberFormat(Type type, String pattern) {
            this(type);
            nf = NumberFormat.getFormat(pattern);
        }

        @Override
        public String format(Double value) {
            if (value != null) {
                return nf.format(value);
            } else {
                return "";
            }
        }

        @Override
        public Double parse(String string) throws ParseException {
            if (CommonsStringUtils.isEmpty(string)) {
                return null; // empty value case
            }
            try {
                return Math.abs(Double.valueOf(string));

            } catch (NumberFormatException e) {
                throw new ParseException("GeoNumber Format error", 0);
            }
        }
    }
}
