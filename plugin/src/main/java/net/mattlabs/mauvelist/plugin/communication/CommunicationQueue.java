package net.mattlabs.mauvelist.plugin.communication;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Represents MauveList's communication queue file, saved to the plugin data folder as {@code communication_queue.json}
 * with JSON formatting.
 *
 * <p>This class is serialized into the communication queue file and deserialized into a {@code CommunicationQueue}
 * object via Configurate. On load, either the object is created using the default field values of this class, or they
 * are set using the values in the existing file.</p>
 *
 * <p>The public methods of this class provide the config values once loaded.</p>
 */
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
@ConfigSerializable
public class CommunicationQueue {

    @SuppressWarnings("unused")
    @Setting(value = "_mattIsAwesome")
    @Comment("""
            MauveList Communication Queue
            By Mattboy9921
            https://github.com/mattboy9921/MauveList
            
            ** Do not touch the contents of this file unless you know what you are doing! **""")
    private boolean _mattIsAwesome = true;

    private ConcurrentLinkedQueue<QueuedRequest> queuedRequests = new ConcurrentLinkedQueue<>();

    public ConcurrentLinkedQueue<QueuedRequest> getQueuedRequests() {
        return queuedRequests;
    }
}
