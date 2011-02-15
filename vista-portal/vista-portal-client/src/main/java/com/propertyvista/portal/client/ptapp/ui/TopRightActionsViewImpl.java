package com.propertyvista.portal.client.ptapp.ui;

import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.propertyvista.portal.client.ptapp.SiteMap;
import com.propertyvista.portal.client.ptapp.activity.TopRightActionsActivity.Theme;

import com.pyx4j.commons.StringDebugId;
import com.pyx4j.forms.client.events.OptionsChangeEvent;
import com.pyx4j.forms.client.events.OptionsChangeHandler;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.site.client.place.AppPlace;
import com.pyx4j.site.client.place.AppPlaceInfo;
import com.pyx4j.site.client.place.AppPlaceListing;

public class TopRightActionsViewImpl extends SimplePanel implements TopRightActionsView {

    private static I18n i18n = I18nFactory.getI18n(TopRightActionsViewImpl.class);

    private final HorizontalPanel linksPanel;

    private Presenter presenter;

    public TopRightActionsViewImpl() {
        linksPanel = new HorizontalPanel();
        linksPanel.getElement().getStyle().setMargin(4, Unit.PX);
        linksPanel.getElement().getStyle().setFontSize(0.9, Unit.EM);
        setWidget(linksPanel);
    }

    @Override
    public void setPresenter(final Presenter presenter) {
        this.presenter = presenter;

        linksPanel.clear();

        NavigLink link = new NavigLink(new SiteMap.PrivacyPolicy());
        linksPanel.add(link);

        linksPanel.add(new HTML("&nbsp;-&nbsp;"));

        link = new NavigLink(new SiteMap.TermsAndConditions());
        linksPanel.add(link);

        linksPanel.add(new HTML("&nbsp;-&nbsp;"));

        CHyperlink logout = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
            }
        });
        logout.setDebugId(new StringDebugId("logout"));
        logout.setValue(i18n.tr("LogOut"));
        linksPanel.add(logout);

        linksPanel.add(new HTML("&nbsp;-&nbsp;"));

        final CComboBox<Theme> themes = new CComboBox<Theme>("", true);
        themes.setOptions(presenter.getThemes());
        themes.setValue(presenter.getCurrentTheme());
        themes.addValueChangeHandler(new ValueChangeHandler<Theme>() {
            @Override
            public void onValueChange(ValueChangeEvent<Theme> event) {
                presenter.setTheme(event.getValue());
            }
        });
        linksPanel.add(themes);
    }

    private class NavigLink extends CHyperlink {

        public NavigLink(final AppPlace place) {
            super(null, new Command() {
                @Override
                public void execute() {
                    presenter.getPlaceController().goTo(place);
                }
            });
            AppPlaceListing listing = presenter.getAppPlaceListing();
            AppPlaceInfo info = listing.getPlaceInfo(place);
            setDebugId(new StringDebugId(info.getCaption()));
            setValue(i18n.tr(info.getCaption()));
        }

    }
}
