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

package pw.stamina.pubsub4k.publish

import pw.stamina.pubsub4k.subscribe.Subscription
import java.util.function.Consumer

/**
 * An optimized set of [Publisher] implementations. This
 * class does not implement the [Publisher] interface to
 * avoid implementing the topic property.
 */
sealed class OptimizedPublisher<T> {

    // From the Publisher interface
    abstract val subscriptions: Set<Subscription<T>>

    abstract fun publish(message: T)

    /** Returns an optimized publisher with the specified [subscription] added to it. */
    abstract fun added(subscription: Subscription<T>): OptimizedPublisher<T>

    /** Returns an optimized publisher with the specified [subscription] remove from it. */
    abstract fun removed(subscription: Subscription<T>): OptimizedPublisher<T>

    companion object {

        /** Returns a new publisher optimized for the specified [subscriptions]. */
        fun <T> fromSubscriptions(subscriptions: Set<Subscription<T>>) = when (subscriptions.size) {
            0 -> empty()
            1 -> SingleSubscriptionPublisher(subscriptions.single())
            else -> ManySubscriptionsPublisher(subscriptions)
        }

        /** Returns a new publisher optimized for no registered subscriptions. */
        fun <T> empty(): OptimizedPublisher<T> {
            return EmptyPublisher()
        }
    }
}

internal class EmptyPublisher<T> : OptimizedPublisher<T>() {

    override val subscriptions = emptySet<Subscription<T>>()

    override fun publish(message: T) = Unit

    override fun added(subscription: Subscription<T>) = SingleSubscriptionPublisher(subscription)
    override fun removed(subscription: Subscription<T>) = this
}

internal class SingleSubscriptionPublisher<T>(
    private val subscription: Subscription<T>
) : OptimizedPublisher<T>() {

    private val messageHandler = subscription.messageHandler

    override val subscriptions = setOf(subscription)

    override fun publish(message: T) {
        try {
            messageHandler.accept(message)
        } catch (e: Exception) {
            throw PublicationException(subscription, e)
        }
    }

    override fun added(subscription: Subscription<T>) =
        if (subscription == this.subscription) this else
            ManySubscriptionsPublisher(setOf(this.subscription, subscription))

    override fun removed(subscription: Subscription<T>) =
        if (subscription == this.subscription) EmptyPublisher<T>() else this
}

internal class ManySubscriptionsPublisher<T>(
    override val subscriptions: Set<Subscription<T>>
) : OptimizedPublisher<T>() {

    override fun publish(message: T) {
        /*
         * We pass a Consumer instance to use the Iterable#forEach
         * method from Java instead of the Kotlin version, because
         * ArrayList provides an optimized version that internally
         * iterates its array of elements.
        */
        subscriptions.forEach(Consumer { subscription ->
            try {
                subscription.messageHandler.accept(message)
            } catch (e: Exception) {
                throw PublicationException(subscription, e)
            }
        })
    }

    override fun added(subscription: Subscription<T>): OptimizedPublisher<T> {
        if (subscriptions.contains(subscription)) return this

        return ManySubscriptionsPublisher(subscriptions + subscription)
    }

    override fun removed(subscription: Subscription<T>): OptimizedPublisher<T> {
        if (!subscriptions.contains(subscription)) return this

        val subscriptions = subscriptions - subscription
        return subscriptions.singleOrNull()?.let(::SingleSubscriptionPublisher)
            ?: ManySubscriptionsPublisher(subscriptions)
    }
}