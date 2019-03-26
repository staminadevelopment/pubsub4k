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

public final class InitialSubscriptionBuilder<T> extends SubscriptionBuilder<T, T> {

    @NotNull
    private final Class<T> topic;
    @NotNull
    private final MessageSubscriber subscriber;
    @Nullable
    private Predicate<Class<? extends T>> topicFilter;

    InitialSubscriptionBuilder(@NotNull Class<T> topic, @NotNull MessageSubscriber subscriber) {
        this.topic = topic;
        this.subscriber = subscriber;
    }

    @NotNull
    public SubscriptionBuilder<T, T> filterTopic(@NotNull Predicate<Class<? extends T>> filter) {
        topicFilter = filter;
        return this;
    }

    @NotNull
    public SubscriptionBuilder<T, T> rejectSubtopics() {
        return filterTopic((topic) -> this.topic == topic);
    }

    @NotNull
    @Override
    public Subscription<T> build(@NotNull Consumer<T> messageHandler) {
        return new Subscription<>(topic, subscriber, topicFilter, messageHandler);
    }
}