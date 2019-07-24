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
import pw.stamina.pubsub4k.publish.PublisherRegistry
import pw.stamina.pubsub4k.subscribe.PublisherUpdatingSubscriptionRegistry
import pw.stamina.pubsub4k.subscribe.Subscription
import pw.stamina.pubsub4k.subscribe.SubscriptionRegistry
import java.util.function.Consumer

class StandardEventBus(
    subscriptions: SubscriptionRegistry,
    private val publishers: PublisherRegistry
) : EventBus {

    private val subscriptions = PublisherUpdatingSubscriptionRegistry(subscriptions, publishers)

    override fun addSubscription(subscription: Subscription<*>) {
        subscriptions.register(subscription)
    }

    override fun removeSubscription(subscription: Subscription<*>) {
        subscriptions.unregister(subscription)
    }

    override fun removeAllSubscriptions(subscriber: MessageSubscriber) {
        subscriptions.unregisterAll(subscriber)
    }

    override fun <T : Any> on(topic: Topic<T>, subscriber: MessageSubscriber, handler: Consumer<T>) {
        addSubscription(Subscription(topic, subscriber, null, handler))
    }

    override fun <T : Any> once(topic: Topic<T>, subscriber: MessageSubscriber, handler: Consumer<T>) {
        val subscriptionRemovingConsumer = SubscriptionRemovingConsumer(this, handler)
        val subscription = Subscription(topic, subscriber, null, subscriptionRemovingConsumer)

        subscriptionRemovingConsumer.subscription = subscription

        addSubscription(subscription)
    }

    override fun <T : Any> getPublisher(topic: Topic<T>): Publisher<T> {
        return publishers.findOrCreatePublisher(topic, subscriptions::findSubscriptionsForTopic)
    }

    override fun disposePublisher(topic: Topic<*>) {
        publishers.removePublisher(topic)
    }

    class SubscriptionRemovingConsumer<T : Any>(
        private val bus: EventBus,
        private val handler: Consumer<T>
    ) : Consumer<T> {
        internal lateinit var subscription: Subscription<T>

        override fun accept(message: T) {
            bus.removeSubscription(subscription)
            handler.accept(message)
        }
    }
}