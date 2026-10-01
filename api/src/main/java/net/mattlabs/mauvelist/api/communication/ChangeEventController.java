package net.mattlabs.mauvelist.api.communication;

import io.javalin.http.Context;
import net.mattlabs.mauvelist.api.ChangeEventManager;
import net.mattlabs.mauvelist.api.MauveListAPI;

import java.util.logging.Logger;

public class ChangeEventController {

    private final ChangeEventManager changeEventManager;
    private final Logger logger;

    public ChangeEventController() {
        changeEventManager = new ChangeEventManager();
        logger = MauveListAPI.getInstance().getLogger();
    }

    public void getEvents(Context context) {
        String after = context.pathParam("after");

        if (after != null) {
            context.status(400).result("Missing required query parameter: after");
        }
        else {
            long eventID;

            try {
                eventID = Long.parseLong(after);

                logger.info("Received change event request for events after: " + eventID);

                context.json(changeEventManager.getChangeEventsAfter(eventID));
            }
            catch (NumberFormatException e) {
                context.status(400).result("Invalid query parameter: after");
            }
        }
    }
}
