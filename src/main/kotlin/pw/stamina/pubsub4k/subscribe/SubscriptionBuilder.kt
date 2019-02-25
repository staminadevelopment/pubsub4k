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

package pw.stamina.pubsub4k.subscribe

import pw.stamina.pubsub4k.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

sealed class SubscriptionBuilder<T, U> {

    fun filterContent(filter: ContentFilter<U>): SubscriptionBuilder<T, U> {
        return DecoratedSubscriptionBuilder(this) { handler ->
            { message -> if (filter(message)) handler(message) }
        }
    }

    fun <R> mapped(mapper: ContentMapper<U, R>): SubscriptionBuilder<T, R> {
        return DecoratedSubscriptionBuilder(this) { handler ->
            { message -> handler(mapper(message)) }
        }
    }

    fun <R> filterMapped(filter: ContentFilter<U>, mapper: ContentMapper<U, R>): SubscriptionBuilder<T, R> {
        return DecoratedSubscriptionBuilder(this) { handler ->
            { message -> if (filter(message)) handler(mapper(message)) }
        }
    }

    fun build(messageHandler: MessageHandler<U>): ReadOnlyProperty<MessageSubscriber, Subscription<T>> {
        val subscription = buildSubscription(messageHandler)

        return object : ReadOnlyProperty<MessageSubscriber, Subscription<T>> {
            override fun getValue(thisRef: MessageSubscriber, property: KProperty<*>) = subscription
        }
    }

    internal abstract fun buildSubscription(messageHandler: MessageHandler<U>): Subscription<T>

    operator fun invoke(messageHandler: MessageHandler<U>) = build(messageHandler)
}

class InitialSubscriptionBuilder<T>(private val topic: Topic<T>) : SubscriptionBuilder<T, T>() {

    private var acceptSubtopics = false

    override fun buildSubscription(messageHandler: MessageHandler<T>): Subscription<T> {
        return Subscription(topic, acceptSubtopics, messageHandler)
    }

    fun acceptSubtopics(): SubscriptionBuilder<T, T> {
        acceptSubtopics = true
        return this
    }
}

private class DecoratedSubscriptionBuilder<T, U, R>(
        private val parent: SubscriptionBuilder<T, U>,
        private val handlerDecorator: (handler: MessageHandler<R>) -> MessageHandler<U>
) : SubscriptionBuilder<T, R>() {

    override fun buildSubscription(messageHandler: MessageHandler<R>): Subscription<T> {
        val handler = handlerDecorator(messageHandler)
        return parent.buildSubscription(handler)
    }
}