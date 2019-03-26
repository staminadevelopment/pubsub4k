/*
 * Copyright 2019 Stamina Development
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pw.stamina.pubsub4k.subscribe;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pw.stamina.pubsub4k.MessageSubscriber;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class Subscription<T> {

    @NotNull
    private final Class<T> topic;
    @NotNull
    private final MessageSubscriber subscriber;
    @Nullable
    private final Predicate<Class<? extends T>> topicFilter;

    /**
     * Handler function for the messages received by this
     * subscription.
     */
    @NotNull
    private final Consumer<T> messageHandler;

    Subscription(
            @NotNull Class<T> topic,
            @NotNull MessageSubscriber subscriber,
            @Nullable Predicate<Class<? extends T>> topicFilter,
            @NotNull Consumer<T> messageHandler) {
        this.topic = topic;
        this.subscriber = subscriber;
        this.topicFilter = topicFilter;
        this.messageHandler = messageHandler;
    }

    @NotNull
    public Class<T> getTopic() {
        return topic;
    }

    @NotNull
    public MessageSubscriber getSubscriber() {
        return subscriber;
    }

    @Nullable
    public Predicate<Class<? extends T>> getTopicFilter() {
        return topicFilter;
    }

    @NotNull
    public Consumer<T> getMessageHandler() {
        return messageHandler;
    }

    @NotNull
    public static <T> InitialSubscriptionBuilder<T> newSubscription(
            @NotNull Class<T> topic, @NotNull MessageSubscriber subscriber) {
        return new InitialSubscriptionBuilder<>(topic, subscriber);
    }
}