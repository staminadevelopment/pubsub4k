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

package pw.stamina.pubsub4k

import pw.stamina.pubsub4k.publish.Publisher
import pw.stamina.pubsub4k.publish.StandardPublisherRegistry
import pw.stamina.pubsub4k.subscribe.CancellableMessageHandler
import pw.stamina.pubsub4k.subscribe.MessageHandler
import pw.stamina.pubsub4k.subscribe.StandardSubscriptionRegistry
import pw.stamina.pubsub4k.subscribe.Subscription

/**
 * The event bus is the central object
 */
interface EventBus {

    fun addSubscription(subscription: Subscription<*>)

    fun removeSubscription(subscription: Subscription<*>)

    fun removeAllSubscriptions(subscriber: MessageSubscriber)

    fun <T : Any> on(topic: Topic<T>, subscriber: MessageSubscriber, handler: MessageHandler<T>)

    fun <T : Any> cancellableOn(topic: Topic<T>, subscriber: MessageSubscriber, handler: CancellableMessageHandler<T>)

    fun <T : Any> once(topic: Topic<T>, subscriber: MessageSubscriber, handler: MessageHandler<T>)

    /**
     * Returns the publisher associated with the [topic], if
     * a publisher does not already exist a new one is created.
     *
     * The [publisher][Publisher] allows users to publish their events
     * to the the registered subscriptions.
     */
    fun <T : Any> getPublisher(topic: Topic<T>): Publisher<T>

    fun disposePublisher(topic: Topic<*>)

    companion object {

        /**
         * Returns a new event bus with
         *
         */
        @JvmStatic
        fun createDefaultBus(locking: Boolean = true): EventBus {
            val subscriptions = StandardSubscriptionRegistry()
            val publishers = StandardPublisherRegistry()

            val bus = StandardEventBus(subscriptions, publishers)

            return if (locking) bus.withLocking() else bus
        }
    }
}