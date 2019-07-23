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

package pw.stamina.pubsub4k.subscribe

import pw.stamina.pubsub4k.MessageSubscriber
import pw.stamina.pubsub4k.publish.PublisherRegistry

class PublisherUpdatingSubscriptionRegistry(
    private val registry: SubscriptionRegistry,
    private val publishers: PublisherRegistry
) : SubscriptionRegistry by registry {

    override fun register(subscription: Subscription<*>): Boolean {
        val registered = registry.register(subscription)
        if (registered) publishers.addSubscriptionToPublishers(subscription)
        return registered
    }

    override fun unregister(subscription: Subscription<*>): Boolean {
        val unregistered = registry.unregister(subscription)
        if (unregistered) publishers.removeSubscriptionFromPublishers(subscription)
        return unregistered
    }

    override fun registerAll(subscriptions: Set<Subscription<*>>): Set<Subscription<*>> {
        val registeredSubscriptions = registry.registerAll(subscriptions)
        registeredSubscriptions.forEach { publishers.addSubscriptionToPublishers(it) }
        return registeredSubscriptions
    }

    override fun unregisterAll(subscriber: MessageSubscriber): Set<Subscription<*>> {
        val unregisteredSubscriptions = registry.unregisterAll(subscriber)
        unregisteredSubscriptions.forEach { publishers.removeSubscriptionFromPublishers(it) }
        return unregisteredSubscriptions
    }
}