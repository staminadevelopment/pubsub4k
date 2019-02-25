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

public final class Subscription<T> {

    @NotNull private final Class<T> topic;
    private final boolean acceptSubtopics;

    /**
     * Handler function for the messages received by this
     * subscription.
     */
    @NotNull private final MessageHandler<T> messageHandler;

    public Subscription(
            @NotNull Class<T> topic,
            boolean acceptSubtopics,
            @NotNull MessageHandler<T> messageHandler) {
        this.topic = topic;
        this.acceptSubtopics = acceptSubtopics;
        this.messageHandler = messageHandler;
    }

    @NotNull
    public Class<T> getTopic() {
        return topic;
    }

    public boolean isAcceptSubtopics() {
        return acceptSubtopics;
    }

    @NotNull
    public MessageHandler<T> getMessageHandler() {
        return messageHandler;
    }

    @NotNull
    public static <T> InitialSubscriptionBuilder<T> newSubscription(@NotNull Class<T> topic) {
        return new InitialSubscriptionBuilder<>(topic);
    }
}
