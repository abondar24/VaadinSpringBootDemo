package org.abondar.experimental.vsb.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.WildcardParameter;

@Route("dynamic")
public class DynamicRouteView extends Div implements HasUrlParameter<String>, HasDynamicTitle {

    private String title = "";

    @Override
    public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
        if (parameter.isEmpty()){
            setText("HIII");
        } else {
            title=parameter;
            setText(String.format("Hello, %s!", parameter));
        }

    }
    @Override
    public String getPageTitle() {

        return title;
    }
}
