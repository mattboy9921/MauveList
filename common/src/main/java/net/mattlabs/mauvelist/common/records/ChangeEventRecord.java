package net.mattlabs.mauvelist.common.records;

public record ChangeEventRecord(long id, long userID, Long membershipID, Long banID, Long applicationId, EventType eventType) {
}
