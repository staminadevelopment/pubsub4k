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

import pw.stamina.pubsub4k.Topic
import pw.stamina.pubsub4k.isSubtopicOf
import pw.stamina.pubsub4k.subscribe.Subscription

class StandardPublisherRegistry(private val publisherFactory: PublisherFactory) : PublisherRegistry {

    private val publisherLookupMap = mutableMapOf<Topic<*>, Publisher<*>>()

    override fun <T : Any> findPublisher(topic: Topic<T>): Publisher<T>? {
        @Suppress("UNCHECKED_CAST")
        return publisherLookupMap[topic] as? Publisher<T>
    }

    override fun <T : Any> findOrCreatePublisher(
        topic: Topic<T>, subscriptions: (Topic<T>) -> Set<Subscription<T>>
    ): Publisher<T> {
        @Suppress("UNCHECKED_CAST")
        return publisherLookupMap.getOrPut(topic, {
            publisherFactory.createPublisher(topic, subscriptions(topic))
        }) as Publisher<T>
    }

    override fun <T : Any> findPublishersFor(subscription: Subscription<T>): Set<Publisher<T>> {
        val matchingPublishers = mutableSetOf<Publisher<T>>()

        publisherLookupMap.values.forEach { publisher ->
            if (publisher.topic.isSubtopicOf(subscription.topic)) {
                @Suppress("UNCHECKED_CAST")
                val castedPublisher = publisher as Publisher<T>
                matchingPublishers.add(castedPublisher)
            }
        }

        return matchingPublishers
    }

    override fun <T : Any> addSubscriptionToPublishers(subscription: Subscription<T>) =
        findPublishersFor(subscription).forEach { it.add(subscription) }

    override fun <T : Any> removeSubscriptionFromPublishers(subscription: Subscription<T>) =
        findPublishersFor(subscription).forEach { it.remove(subscription) }
}