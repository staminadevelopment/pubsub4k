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

package pw.stamina.pubsub4k

import pw.stamina.pubsub4k.publish.Publisher
import pw.stamina.pubsub4k.publish.StandardPublisherRegistry
import pw.stamina.pubsub4k.subscribe.StandardSubscriptionRegistry
import pw.stamina.pubsub4k.subscribe.SubscriptionRegistry

interface EventBus {

    /**
     * Returns the publisher associated with the [topic], if
     * a publisher does not exist a new one is created.
     */
    fun <T> getPublisher(topic: Topic<T>): Publisher<T>

    val subscriptions: SubscriptionRegistry

    companion object {

        @JvmStatic
        fun createStandardBus(): EventBus {
            val publishers = StandardPublisherRegistry()
            val subscriptions = StandardSubscriptionRegistry()

            return StandardEventBus(subscriptions, publishers)
        }
    }
}

/**
 * Returns the publisher associated with the [T] topic, if
 * a publisher does not exist a new one is created.
 */
inline fun <reified T> EventBus.getPublisher(): Publisher<T> {
    return this.getPublisher(T::class.java)
}

typealias Topic<T> = Class<T>

fun Topic<*>.isSubtopicOf(other: Topic<*>) = other.isAssignableFrom(this)