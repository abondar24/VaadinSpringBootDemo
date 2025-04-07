package org.abondar.experimental.articlemanager;


import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AppInfoContributor implements InfoContributor {
    @Override
    public void contribute(Info.Builder builder) {
        var customInfo = Map.of(
                "app_name", "Article Manager",
                "app_version", "1.0.0",
                "description", "Tiny article manager app",
                "additional_info", "Part of experimental development program"
        );

        builder.withDetail("app_info",customInfo);
    }
}