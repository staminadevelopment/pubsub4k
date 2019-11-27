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

/**
 * The publisher provides an interface for publishing
 * events for its [topic] to its registered [subscriptions].
 * The publisher can publish messages of the same topic as
 * the publisher, or a [subtopic][isSubtopicOf].
 */
interface Publisher<T : Any> {

    /** The topic of this publisher. */
    val topic: Topic<T>

    /** The subscriptions registered for this publisher. */
    val subscriptions: Set<Subscription<T>>

    /** Adds the [subscription] to this publisher. */
    fun add(subscription: Subscription<T>)

    /** Removes the [subscription] from this publisher. */
    fun remove(subscription: Subscription<T>)

    /** Clears all the [subscriptions] from this publisher. */
    fun clear()

    /**
     * Publishes the [message] to all its [subscriptions]. If
     * an exception is thrown it should always be caught, and
     * rethrown as a [PublicationException].
     */
    @Throws(PublicationException::class)
    fun publish(message: T)
}