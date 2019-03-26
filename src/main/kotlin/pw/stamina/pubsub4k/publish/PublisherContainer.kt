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

/**
 * The [PublisherContainer] implements the [Publisher]
 * interface, and provides function to add or remove
 * subscriptions.
 *
 * When subscriptions are added or removed from this class,
 * the [publisher] property is update with a new publisher,
 * effectively making this class copy-on-write, meaning it
 * is safe to publish to it from many threads, but only one
 * thread may update this class.
 */
class PublisherContainer<T : Any>(
    override val topic: Topic<T>,
    subscriptions: Set<Subscription<T>>
) : MutablePublisher<T> {

    private var publisher = OptimizedPublisher.fromSubscriptions(subscriptions)

    override val subscriptions: Set<Subscription<T>>
        get() = publisher.subscriptions

    override fun publish(message: T) {
        publisher.publish(message)
    }

    override fun add(subscription: Subscription<T>) {
        publisher = publisher.added(subscription)
    }

    override fun remove(subscription: Subscription<T>) {
        publisher = publisher.removed(subscription)
    }

    override fun clear() {
        publisher = OptimizedPublisher.empty()
    }
}