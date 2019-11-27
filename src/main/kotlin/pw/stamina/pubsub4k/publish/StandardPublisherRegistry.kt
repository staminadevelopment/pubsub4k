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

class StandardPublisherRegistry : PublisherRegistry {

    private val publisherLookupMap = mutableMapOf<Topic<*>, MutablePublisher<*>>()

    override fun <T : Any> findPublisher(topic: Topic<T>): Publisher<T>? {
        @Suppress("UNCHECKED_CAST")
        return publisherLookupMap[topic] as? Publisher<T>
    }

    override fun <T : Any> findOrCreatePublisher(
        topic: Topic<T>, subscriptions: (Topic<T>) -> Set<Subscription<T>>
    ): Publisher<T> {
        val publisherCreator = { PublisherContainer(topic, subscriptions(topic)) }

        @Suppress("UNCHECKED_CAST")
        return publisherLookupMap.getOrPut(topic, publisherCreator) as Publisher<T>
    }

    override fun <T : Any> findPublishersFor(subscription: Subscription<T>): Set<Publisher<T>> {
        return mutableSetOf<Publisher<T>>().also { publishers ->
            forEachPublisherFor(subscription) { subscription -> publishers.add(subscription) }
        }
    }

    override fun <T : Any> addSubscriptionToPublishers(subscription: Subscription<T>) =
        forEachPublisherFor(subscription) { it.add(subscription) }

    override fun <T : Any> removeSubscriptionFromPublishers(subscription: Subscription<T>) =
        forEachPublisherFor(subscription) { it.remove(subscription) }

    private fun <T : Any> forEachPublisherFor(
        subscription: Subscription<T>,
        action: (MutablePublisher<T>) -> Unit
    ) {
        publisherLookupMap.values.forEach { publisher ->
            if (publisher.topic.isSubtopicOf(subscription.topic)) {
                @Suppress("UNCHECKED_CAST")
                val castedPublisher = publisher as MutablePublisher<T>

                if (subscription.topicFilter?.testTopic(castedPublisher.topic) != false) {
                    action(castedPublisher)
                }
            }
        }
    }

    override fun removePublisher(topic: Topic<*>) =
        publisherLookupMap.remove(topic)?.apply { clear() } != null
}