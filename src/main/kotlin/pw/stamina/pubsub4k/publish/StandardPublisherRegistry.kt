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
import pw.stamina.pubsub4k.isSupertopicOf
import pw.stamina.pubsub4k.subscribe.Subscription

@Suppress("UNCHECKED_CAST")
class StandardPublisherRegistry(private val publisherFactory: PublisherFactory) : PublisherRegistry {

    private val publisherLookup = mutableMapOf<Topic<*>, Publisher<*>>()
    private val topicToPublishersLookup = mutableMapOf<Topic<*>, MutableSet<Publisher<*>>>()

    override fun <T : Any> findPublisher(topic: Topic<T>): Publisher<T>? {
        return publisherLookup[topic] as? Publisher<T>
    }

    override fun <T : Any> findOrCreatePublisher(
        topic: Topic<T>, subscriptions: (Topic<T>) -> Set<Subscription<T>>
    ): Publisher<T> {
        return publisherLookup.getOrPut(topic, {
            createAndIndexPublisher(topic, subscriptions(topic))
        }) as Publisher<T>
    }

    private fun <T : Any> createAndIndexPublisher(
        topic: Topic<T>,
        subscriptions: Set<Subscription<T>>
    ): Publisher<T> {
        val publisher = publisherFactory.createPublisher(topic, subscriptions)

        val matchingPublishers = publisherLookup.values
            .filterTo(mutableSetOf()) { it.topic.isSubtopicOf(topic) }

        topicToPublishersLookup[topic] = matchingPublishers

        topicToPublishersLookup
            .filterKeys { it.isSupertopicOf(topic) }
            .values.forEach { it.add(publisher) }

        return publisher
    }

    override fun <T : Any> findPublishersByTopic(topic: Topic<T>): Set<Publisher<T>> {
        return topicToPublishersLookup[topic] as? Set<Publisher<T>> ?: emptySet()
    }

    override fun <T : Any> addSubscriptionToPublishers(subscription: Subscription<T>) =
        findPublishersByTopic(subscription.topic).forEach { it.add(subscription) }

    override fun <T : Any> removeSubscriptionFromPublishers(subscription: Subscription<T>) =
        findPublishersByTopic(subscription.topic).forEach { it.remove(subscription) }
}