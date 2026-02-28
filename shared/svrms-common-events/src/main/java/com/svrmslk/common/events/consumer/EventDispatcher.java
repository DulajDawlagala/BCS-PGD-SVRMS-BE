package com.svrmslk.common.events.consumer;

import com.svrmslk.common.events.api.Event;
import com.svrmslk.common.events.api.EventHandler;
import com.svrmslk.common.events.core.EventEnvelope;
import com.svrmslk.common.events.exception.EventConsumeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Dispatches events to registered handlers based on event type.
 * Supports multiple handlers per event type for fan-out scenarios.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public class EventDispatcher {

    private static final Logger log = LoggerFactory.getLogger(EventDispatcher.class);

    private final Map<String, List<EventHandler<?>>> handlerRegistry;

    public EventDispatcher() {
        this.handlerRegistry = new ConcurrentHashMap<>();
    }

    /**
     * Registers a handler for a specific event type.
     *
     * @param eventType the event type to handle
     * @param handler the handler implementation
     */
    public <T extends Event> void registerHandler(String eventType, EventHandler<T> handler) {
        handlerRegistry
                .computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                .add(handler);

        log.info("Registered event handler: eventType={}, handler={}",
                eventType, handler.getClass().getSimpleName());
    }

    /**
     * Registers a handler using the event class to derive the type.
     *
     * @param eventClass the event class
     * @param handler the handler implementation
     */
    public <T extends Event> void registerHandler(Class<T> eventClass, EventHandler<T> handler) {
        String eventType = deriveEventType(eventClass);
        registerHandler(eventType, handler);
    }

    /**
     * Unregisters a handler for a specific event type.
     */
    public <T extends Event> void unregisterHandler(String eventType, EventHandler<T> handler) {
        List<EventHandler<?>> handlers = handlerRegistry.get(eventType);
        if (handlers != null) {
            handlers.remove(handler);
            log.info("Unregistered event handler: eventType={}, handler={}",
                    eventType, handler.getClass().getSimpleName());
        }
    }

    /**
     * Dispatches an event envelope to all registered handlers.
     *
     * @param envelope the event envelope
     * @throws EventConsumeException if all handlers fail
     */
    @SuppressWarnings("unchecked")
    public <T extends Event> void dispatch(EventEnvelope<T> envelope) {
        String eventType = envelope.getEventType().value();
        List<EventHandler<?>> handlers = handlerRegistry.get(eventType);

        if (handlers == null || handlers.isEmpty()) {
            log.warn("No handlers registered for event type: {}, eventId={}",
                    eventType, envelope.getEventId());
            return;
        }

        log.debug("Dispatching event to {} handler(s): eventType={}, eventId={}",
                handlers.size(), eventType, envelope.getEventId());

        boolean anySuccess = false;
        Exception lastException = null;

        for (EventHandler<?> handler : handlers) {
            try {
                // Cast is safe because handlers are registered with matching types
                EventHandler<T> typedHandler = (EventHandler<T>) handler;

                log.debug("Invoking handler: eventId={}, handler={}",
                        envelope.getEventId(), handler.getClass().getSimpleName());

                typedHandler.handle(envelope);
                anySuccess = true;

                log.debug("Handler completed successfully: eventId={}, handler={}",
                        envelope.getEventId(), handler.getClass().getSimpleName());

            } catch (Exception ex) {
                lastException = ex;
                log.error("Handler failed: eventId={}, handler={}",
                        envelope.getEventId(), handler.getClass().getSimpleName(), ex);
            }
        }

        // If all handlers failed, throw exception
        if (!anySuccess && lastException != null) {
            throw new EventConsumeException(
                    envelope.getEventId(),
                    eventType,
                    "All handlers failed for event",
                    lastException,
                    true
            );
        }
    }

    /**
     * Gets the count of registered handlers for an event type.
     */
    public int getHandlerCount(String eventType) {
        List<EventHandler<?>> handlers = handlerRegistry.get(eventType);
        return handlers != null ? handlers.size() : 0;
    }

    /**
     * Gets all registered event types.
     */
    public java.util.Set<String> getRegisteredEventTypes() {
        return handlerRegistry.keySet();
    }

    /**
     * Derives event type from event class name.
     * Example: UserRegisteredEvent -> user.registered
     */
    private String deriveEventType(Class<? extends Event> eventClass) {
        String className = eventClass.getSimpleName();

        if (className.endsWith("Event")) {
            className = className.substring(0, className.length() - 5);
        }

        return className
                .replaceAll("([a-z])([A-Z])", "$1.$2")
                .toLowerCase();
    }
}