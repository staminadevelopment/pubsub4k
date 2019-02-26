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

package pw.stamina.pubsub4k.publish

import pw.stamina.pubsub4k.subscribe.Subscription

sealed class OptimizedPublisher<T> : Publisher<T> {

    abstract fun added(subscription: Subscription<T>): OptimizedPublisher<T>
    
    abstract fun removed(subscription: Subscription<T>): OptimizedPublisher<T>

    companion object {
        fun <T> fromSubscriptions(subscriptions: Set<Subscription<T>>) = when (subscriptions.size) {
            0 -> EmptyPublisher()
            1 -> SingleSubscriptionPublisher(subscriptions.single())
            else -> ManySubscriptionsPublisher(subscriptions)
        }

        fun <T> empty(): OptimizedPublisher<T> {
            return EmptyPublisher()
        }
    }
}

private class EmptyPublisher<T> : OptimizedPublisher<T>() {

    override val subscriptions = emptySet<Subscription<T>>()

    override fun publish(message: T) = Unit

    override fun added(subscription: Subscription<T>) = SingleSubscriptionPublisher(subscription)
    override fun removed(subscription: Subscription<T>) = this
}

private class SingleSubscriptionPublisher<T>(
        private val subscription: Subscription<T>
) : OptimizedPublisher<T>() {

    private val messageHandler = subscription.messageHandler

    override val subscriptions = setOf(subscription)

    override fun publish(message: T) = messageHandler.accept(message)

    override fun added(subscription: Subscription<T>) =
            ManySubscriptionsPublisher(subscriptions + subscription)

    override fun removed(subscription: Subscription<T>) =
            if (subscription == this.subscription) EmptyPublisher<T>() else this
}

private class ManySubscriptionsPublisher<T>(
        override val subscriptions: Set<Subscription<T>>
) : OptimizedPublisher<T>() {

    private val messageHandlers = subscriptions.map { it.messageHandler }

    override fun publish(message: T) = messageHandlers.forEach { handler -> handler.accept(message) }

    override fun added(subscription: Subscription<T>) =
            ManySubscriptionsPublisher(subscriptions + subscription)

    override fun removed(subscription: Subscription<T>): OptimizedPublisher<T> {
        val subscriptions = subscriptions - subscription
        return subscriptions.singleOrNull()?.let(::SingleSubscriptionPublisher)
                ?: ManySubscriptionsPublisher(subscriptions)
    }
}