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

import pw.stamina.pubsub4k.Topic
import pw.stamina.pubsub4k.isSubtopicOf
import pw.stamina.pubsub4k.subscribe.Subscription

class StandardPublisherRegistry : PublisherRegistry {

    private val publisherLookupMap = mutableMapOf<Topic<*>, PublisherContainer<*>>()

    override fun <T> findPublisher(topic: Topic<T>): PublisherContainer<T>? {
        @Suppress("UNCHECKED_CAST")
        return publisherLookupMap[topic] as? PublisherContainer<T>
    }

    override fun <T> findOrCreatePublisher(
            topic: Topic<T>, subscriptions: (Topic<T>) -> Set<Subscription<T>>): PublisherContainer<T> {
        val containerCreator = {
            PublisherContainer(topic).apply { updateSubscriptions(subscriptions(topic)) }
        }

        @Suppress("UNCHECKED_CAST")
        return publisherLookupMap.getOrPut(topic, containerCreator) as PublisherContainer<T>
    }

    override fun <T> findPublishersFor(subscription: Subscription<T>): Set<PublisherContainer<T>> {
        val publishers = mutableSetOf<PublisherContainer<T>>()

        publisherLookupMap.values.forEach { publisher ->
            if (publisher.topic.isSubtopicOf(subscription.topic)) {
                @Suppress("UNCHECKED_CAST")
                val castedPublisher = publisher as PublisherContainer<T>

                if (subscription.topicFilter?.accepts(castedPublisher.topic) != false) {
                    publishers.add(castedPublisher)
                }
            }
        }

        return publishers
    }

    override fun <T> removePublisher(topic: Topic<T>) {
        publisherLookupMap.remove(topic)
    }
}