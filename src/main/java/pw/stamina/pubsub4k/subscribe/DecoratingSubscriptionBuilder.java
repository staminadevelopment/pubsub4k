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

public final class DecoratingSubscriptionBuilder<T, U, R> extends SubscriptionBuilder<T, R> {

    @NotNull
    private final SubscriptionBuilder<T, U> parent;
    @NotNull
    private final HandlerDecorator<U, R> handlerDecorator;

    DecoratingSubscriptionBuilder(
            @NotNull SubscriptionBuilder<T, U> parent,
            @NotNull HandlerDecorator<U, R> handlerDecorator) {
        this.parent = parent;
        this.handlerDecorator = handlerDecorator;
    }

    @NotNull
    @Override
    public Subscription<T> build(@NotNull Consumer<R> messageHandler) {
        return parent.build(handlerDecorator.decorateHandler(messageHandler));
    }
}