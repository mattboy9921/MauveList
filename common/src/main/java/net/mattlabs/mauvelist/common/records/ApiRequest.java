package net.mattlabs.mauvelist.common.records;

public record ApiRequest<T>(String endpoint, T body) {

    public ApiRequest(String endpoint) {
        this(endpoint, null);
    }
}
