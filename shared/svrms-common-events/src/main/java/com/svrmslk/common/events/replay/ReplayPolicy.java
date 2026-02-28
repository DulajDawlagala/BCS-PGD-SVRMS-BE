package com.svrmslk.common.events.replay;

import com.svrmslk.common.events.core.EventEnvelope;

import java.util.Set;
import java.util.function.Predicate;

/**
 * Policy that controls event replay behavior.Defines filtering, error handling, and rate limiting rules.
 *
 * @author Platform Team
 * @since 1.0.0
 * */


public class ReplayPolicy {
    private final boolean stopOnError;
    private final long delayBetweenEvents;
    private final Set<String> includedEventTypes;
    private final Set<String> excludedEventTypes;
    private final Set<String> includedTenants;
    private final Predicate<EventEnvelope<?>> customFilter;

    private ReplayPolicy(Builder builder) {
 this.stopOnError = builder.stopOnError;
 this.delayBetweenEvents = builder.delayBetweenEvents;
 this.includedEventTypes = builder.includedEventTypes;
 this.excludedEventTypes = builder.excludedEventTypes;
 this.includedTenants = builder.includedTenants;
 this.customFilter = builder.customFilter;
 }
  /**
  *
  * Determines if an event should be replayed based on policy rules.
  * */


    public boolean shouldReplay(EventEnvelope<?> envelope) {
 // Check event type inclusion
 if (includedEventTypes != null && !includedEventTypes.isEmpty()) {
 if (!includedEventTypes.contains(envelope.getEventType().value())) {
 return false;
 }
 }
  // Check event type exclusion
 if (excludedEventTypes != null && !excludedEventTypes.isEmpty()) {
 if (excludedEventTypes.contains(envelope.getEventType().value())) {
 return false;
 }
 }
  // Check tenant inclusion
 if (includedTenants != null && !includedTenants.isEmpty()) {
 if (envelope.getTenantId() == null || !includedTenants.contains(envelope.getTenantId())) {
 return false;
 }
 }
  // Apply custom filter
 if (customFilter != null) {
 return customFilter.test(envelope);
 }
 return true;
 }



    public boolean isStopOnError() {
 return stopOnError;
 }


    public long getDelayBetweenEvents() {
 return delayBetweenEvents;
 }


    public static Builder builder() {
 return new Builder();
 }
 /**
  *
  * Default policy: replay all events, don't stop on error, no delay.
  * */


    public static ReplayPolicy defaultPolicy() {
 return builder().build();
 }

 /**
  *
  * Builder for ReplayPolicy.
  * */


    public static class Builder {

        private boolean stopOnError = false;
        private long delayBetweenEvents = 0;
        private Set<String> includedEventTypes;
        private Set<String> excludedEventTypes;
        private Set<String> includedTenants;
        private Predicate<EventEnvelope<?>> customFilter;

        public Builder stopOnError(boolean stopOnError) {
 this.stopOnError = stopOnError;
 return this;
 }


        public Builder delayBetweenEvents(long delayMs) {
 this.delayBetweenEvents = delayMs;
 return this;
 }


        public Builder includeEventTypes(Set<String> eventTypes) {
 this.includedEventTypes = eventTypes;
 return this;
 }

        public Builder excludeEventTypes(Set<String> eventTypes) {
 this.excludedEventTypes = eventTypes;
 return this;
 }

        public Builder includeTenants(Set<String> tenantIds) {
 this.includedTenants = tenantIds;
 return this;
 }

        public Builder customFilter(Predicate<EventEnvelope<?>> filter) {
 this.customFilter = filter;
 return this;
 }

        public ReplayPolicy build() {
 return new ReplayPolicy(this);
 }

    }
}