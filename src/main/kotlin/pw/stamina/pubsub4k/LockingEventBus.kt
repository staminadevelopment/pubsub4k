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
import pw.stamina.pubsub4k.subscribe.Subscription
import pw.stamina.pubsub4k.subscribe.SubscriptionRegistry
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

internal class LockingEventBus(
        private val bus: EventBus,
        private val lock: ReentrantReadWriteLock
) : EventBus {

    override val subscriptions = LockingSubscriptionRegistry(bus.subscriptions, lock)

    override fun <T> getPublisher(topic: Topic<T>): Publisher<T> = lock.write {
        bus.getPublisher(topic)
    }
}

internal class LockingSubscriptionRegistry(
        private val registry: SubscriptionRegistry,
        private val lock: ReentrantReadWriteLock
) : SubscriptionRegistry {

    override fun register(subscription: Subscription<*>) = lock.write {
        registry.register(subscription)
    }

    override fun unregister(subscription: Subscription<*>) = lock.write {
        registry.unregister(subscription)
    }

    override fun registerAll(subscriptions: Set<Subscription<*>>) = lock.write {
        registry.registerAll(subscriptions)
    }

    override fun unregisterAll(subscriber: MessageSubscriber) = lock.write {
        registry.unregisterAll(subscriber)
    }

    override fun <T> findSubscriptionsForTopic(topic: Topic<T>) = lock.read {
        registry.findSubscriptionsForTopic(topic)
    }
}

fun EventBus.withLocking(lock: ReentrantReadWriteLock = ReentrantReadWriteLock()): EventBus {
    return LockingEventBus(this, lock)
}