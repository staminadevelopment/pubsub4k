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
import pw.stamina.pubsub4k.Topic
import pw.stamina.pubsub4k.isSubtopicOf

class StandardSubscriptionRegistry : SubscriptionRegistry {

    private val subscriberToSubscriptionMap = mutableMapOf<MessageSubscriber, MutableSet<Subscription<*>>>()

    override fun register(subscription: Subscription<*>) =
        subscriberToSubscriptionMap.getOrPut(subscription.subscriber, ::mutableSetOf).add(subscription)

    override fun unregister(subscription: Subscription<*>): Boolean {
        return subscriberToSubscriptionMap[subscription.subscriber]?.remove(subscription) ?: false
    }

    override fun unregisterAll(subscriber: MessageSubscriber): Set<Subscription<*>> {
        return subscriberToSubscriptionMap.remove(subscriber) ?: emptySet()
    }

    override fun <T : Any> findSubscriptionsForTopic(topic: Topic<T>): Set<Subscription<T>> {
        return subscriberToSubscriptionMap.values.asSequence().flatten()
            .filter { topic.isSubtopicOf(it.topic) }
            .filterIsInstance<Subscription<T>>()
            .toSet()
    }
}