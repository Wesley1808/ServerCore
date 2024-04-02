package me.wesley1808.servercore.common.config;

/**
 * Interface for copying configuration interfaces.
 * Dazzleconf's internals implement these interfaces through proxies and hashmap lookups.
 * Copying the configs allows us to skip those map lookups, which can be helpful when a config entry is accessed frequently.
 */
public interface Copyable {
    Copyable optimizedCopy();
}
