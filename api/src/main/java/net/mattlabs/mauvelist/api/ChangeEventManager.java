package net.mattlabs.mauvelist.api;

import net.mattlabs.mauvelist.api.database.ChangeEventRepository;
import net.mattlabs.mauvelist.common.records.ChangeEventRecord;

import java.util.List;
import java.util.logging.Logger;

public class ChangeEventManager {

    private final ChangeEventRepository changeEventRepository;
    private final Logger logger;

    public ChangeEventManager() {
        changeEventRepository = new ChangeEventRepository();
        logger = MauveListAPI.getInstance().getLogger();
    }

    public List<ChangeEventRecord> getChangeEventsAfter(long eventId) {
        return changeEventRepository.getChangeEventsAfter(eventId);
    }
}
