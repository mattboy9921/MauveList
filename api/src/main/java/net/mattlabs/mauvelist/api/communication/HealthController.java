package net.mattlabs.mauvelist.api.communication;

import io.javalin.http.Context;

public class HealthController {

    public void getHealth(Context context) {
        context.result("MauveList API is running!");
    }
}
