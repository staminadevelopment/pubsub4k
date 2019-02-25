/*
 * MIT License
 *
 * Copyright (c) 2019 Stamina Development
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package pw.stamina.pubsub4k.subscribe;

import org.jetbrains.annotations.NotNull;

public abstract class SubscriptionBuilder<T, U> {

    @NotNull
    public final SubscriptionBuilder<T, U> filterContent(@NotNull ContentFilter<U> filter) {
        return new DecoratedSubscriptionBuilder<>(this, (handler) -> (message) -> {
            if (filter.accepts(message)) handler.accept(message);
        });
    }

    @NotNull
    public final <R> SubscriptionBuilder<T, R> mapped(@NotNull ContentMapper<U, R> mapper) {
        return new DecoratedSubscriptionBuilder<>(this, (handler) -> (message) -> {
            handler.accept(mapper.apply(message));
        });
    }

    @NotNull
    public final <R> SubscriptionBuilder<T, R> filterMapped(@NotNull ContentFilterMapper<U, R> filterMapper) {
        return new DecoratedSubscriptionBuilder<>(this, (handler) -> (message) -> {
            if (filterMapper.accepts(message)) handler.accept(filterMapper.apply(message));
        });
    }

    @NotNull
    public abstract Subscription<T> build(MessageHandler<U> messageHandler);
}
