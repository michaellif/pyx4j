package com.propertyvista.portal.client.ptapp.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;
import com.propertyvista.portal.client.ptapp.SiteMap;
import com.propertyvista.portal.client.ptapp.activity.TopRightActionsActivity.Theme;

import com.pyx4j.commons.StringDebugId;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.site.client.place.AppPlace;
import com.pyx4j.site.client.place.AppPlaceInfo;
import com.pyx4j.site.client.place.AppPlaceListing;

public class TopRightActionsViewImpl extends SimplePanel implements TopRightActionsView {

    private static I18n i18n = I18nFactory.getI18n(TopRightActionsViewImpl.class);

    private final HorizontalPanel linksPanel;

    private Presenter presenter;

    private final HTML greetings;

    private final CHyperlink logout;

    private final CHyperlink login;

    private final CComboBox<Theme> themes;

    @Inject
    public TopRightActionsViewImpl(AppPlaceListing appPlaceListing) {
        linksPanel = new HorizontalPanel();
        linksPanel.getElement().getStyle().setMargin(4, Unit.PX);
        linksPanel.getElement().getStyle().setFontSize(0.9, Unit.EM);
        setWidget(linksPanel);

        greetings = new HTML("");
        linksPanel.add(greetings);
        linksPanel.add(new HTML("&nbsp;"));

        NavigLink link = new NavigLink(appPlaceListing, new SiteMap.PrivacyPolicy());
        linksPanel.add(link);

        linksPanel.add(new HTML("&nbsp;-&nbsp;"));

        link = new NavigLink(appPlaceListing, new SiteMap.TermsAndConditions());
        linksPanel.add(link);

        linksPanel.add(new HTML("&nbsp;-&nbsp;"));

        logout = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                presenter.logout();
            }
        });
        logout.setDebugId(new StringDebugId("logout"));
        logout.setValue(i18n.tr("LogOut"));
        logout.setVisible(false);
        linksPanel.add(logout);

        login = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                presenter.login();
            }
        });
        login.setDebugId(new StringDebugId("login"));
        login.setValue(i18n.tr("LogIn"));
        linksPanel.add(login);

        linksPanel.add(new HTML("&nbsp;-&nbsp;"));

        themes = new CComboBox<Theme>("", true);
        themes.addValueChangeHandler(new ValueChangeHandler<Theme>() {
            @Override
            public void onValueChange(ValueChangeEvent<Theme> event) {
                presenter.setTheme(event.getValue());
            }
        });
        linksPanel.add(themes);
    }

    @Override
    public void setPresenter(final Presenter presenter) {
        this.presenter = presenter;
        themes.setOptions(presenter.getThemes());
        themes.setValue(presenter.getCurrentTheme());
    }

    private class NavigLink extends CHyperlink {

        public NavigLink(AppPlaceListing listing, final AppPlace place) {
            super(null, new Command() {
                @Override
                public void execute() {
                    presenter.getPlaceController().goTo(place);
                }
            });
            AppPlaceInfo info = listing.getPlaceInfo(place);
            setDebugId(new StringDebugId(info.getCaption()));
            setValue(i18n.tr(info.getCaption()));
        }

    }

    @Override
    public void onLogedOut() {
        logout.setVisible(false);
        login.setVisible(true);
        greetings.setHTML("");
    }

    @Override
    public void onLogedIn(String userName) {
        logout.setVisible(true);
        login.setVisible(false);
        greetings.setHTML(userName);
    }
}
