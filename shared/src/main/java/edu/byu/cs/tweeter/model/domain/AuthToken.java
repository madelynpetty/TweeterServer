package edu.byu.cs.tweeter.model.domain;

import java.io.Serializable;
import java.util.UUID;

/**
 * Represents an auth token in the system.
 */
public class AuthToken implements Serializable {
    public String identifier;

    public AuthToken() {
        this.identifier = UUID.randomUUID().toString();
    }

    public String getIdentifier() {
        return identifier;
    }
}
