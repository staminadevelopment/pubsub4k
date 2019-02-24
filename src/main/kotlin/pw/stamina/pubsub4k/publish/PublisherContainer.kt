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
 * A delegating implementation of the [Publisher] interface. This
 * class is useful if
 */
class PublisherContainer<T>(
        val topic: Topic<T>
) : Publisher<T> {

    private var delegate = OptimizedPublisher.empty<T>()

    override val subscriptions: List<Subscription<T>>
        get() = delegate.subscriptions

    override fun publish(message: T) {
        delegate.publish(message)
    }

    fun updateSubscriptions(subscriptions: List<Subscription<T>>) {
        delegate = OptimizedPublisher.fromSubscriptions(subscriptions)
    }

    fun add(subscription: Subscription<T>) {
        delegate = delegate.added(subscription)
    }

    fun remove(subscription: Subscription<T>) {
        delegate = delegate.removed(subscription)
    }
}