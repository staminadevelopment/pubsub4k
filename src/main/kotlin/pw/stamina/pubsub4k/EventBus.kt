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
import pw.stamina.pubsub4k.publish.StandardPublisherRegistry
import pw.stamina.pubsub4k.subscribe.StandardSubscriptionRegistry
import pw.stamina.pubsub4k.subscribe.SubscriptionRegistry

/**
 * The event bus is the central object
 */
interface EventBus {

    /**
     * The subscriptions registered for this event bus.
     */
    val subscriptions: SubscriptionRegistry

    /**
     * Returns the publisher associated with the [topic], if
     * a publisher does not already exist a new one is created.
     *
     * The [publisher][Publisher] allows users to publish their events
     * to the the registered subscriptions.
     */
    fun <T : Any> getPublisher(topic: Topic<T>): Publisher<T>

    companion object {

        /**
         * Returns a new event bus with
         *
         */
        @JvmStatic
        fun createDefaultBus(locking: Boolean = true): EventBus {
            val subscriptions = StandardSubscriptionRegistry()
            val publishers = StandardPublisherRegistry()

            val bus = StandardEventBus(subscriptions, publishers)

            return if (locking) bus.withLocking() else bus
        }
    }
}

/**
 * Returns the publisher associated with the [T] topic, if
 * a publisher does not exist a new one is created.
 */
inline fun <reified T : Any> EventBus.getPublisher(): Publisher<T> {
    return this.getPublisher(T::class.java)
}

typealias Topic<T> = Class<T>

/**
 * Returns `true` if the [other] topic is a subclass/subtopic
 * of this topic, otherwise returns `false`.
 *
 * A topic is a subtopic of another, if itself is
 * [Class.isAssignableFrom] from the other topic.
 */
fun Topic<*>.isSubtopicOf(other: Topic<*>) = other.isAssignableFrom(this)