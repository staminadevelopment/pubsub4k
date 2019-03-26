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

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class SubscriptionBuilder<T, U> {

    @NotNull
    public final SubscriptionBuilder<T, U> filterMessage(@NotNull Predicate<U> filter) {
        return new DecoratingSubscriptionBuilder<>(this, (handler) -> (message) -> {
            if (filter.test(message)) handler.accept(message);
        });
    }

    @NotNull
    public final <R> SubscriptionBuilder<T, R> mapped(@NotNull Function<U, R> mapper) {
        return new DecoratingSubscriptionBuilder<>(this,
                (handler) -> (message) -> handler.accept(mapper.apply(message)));
    }

    @NotNull
    public final <R> SubscriptionBuilder<T, R> filterMapped(@NotNull ContentFilterMapper<U, R> filterMapper) {
        return filterMessage(filterMapper::filter).mapped(filterMapper::map);
    }

    @NotNull
    public abstract Subscription<T> build(@NotNull Consumer<U> messageHandler);
}