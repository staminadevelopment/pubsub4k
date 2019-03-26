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