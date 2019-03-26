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
import pw.stamina.pubsub4k.subscribe.Subscription

interface PublisherRegistry {

    /**
     * Finds the publisher associated with the specified
     * [topic], if none is found `null` is returned.
     */
    fun <T : Any> findPublisher(topic: Topic<T>): Publisher<T>?

    /**
     * Finds the publisher associated with the specified
     * [topic], if none is found a new publisher is created
     * with the [topic] and [subscriptions], and registered.
     */
    fun <T : Any> findOrCreatePublisher(
        topic: Topic<T>,
        subscriptions: ((Topic<T>) -> Set<Subscription<T>>)
    ): Publisher<T>

    /**
     * Finds all the publishers which topic is accepted by
     * the specified [subscription]. The [subscription]
     * accepts the topic if its own topic is a subtopic of
     * the publisher's topic and its [topic filter][Subscription.topicFilter]
     * is not null and accepts the topic.
     */
    fun <T : Any> findPublishersFor(subscription: Subscription<T>): Set<Publisher<T>>

    /**
     * Adds the subscription from all publishers
     */
    fun <T : Any> addSubscriptionToPublishers(subscription: Subscription<T>)

    fun <T : Any> removeSubscriptionFromPublishers(subscription: Subscription<T>)

    /**
     * Removes the publisher associated with the specified
     * [topic], and clears all its subscriptions.
     */
    fun <T> removePublisher(topic: Topic<T>): Boolean
}