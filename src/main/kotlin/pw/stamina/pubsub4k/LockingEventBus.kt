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

    override fun <T : Any> getPublisher(topic: Topic<T>): Publisher<T> = lock.write {
        bus.getPublisher(topic)
    }
}

internal class LockingSubscriptionRegistry(
    private val registry: SubscriptionRegistry,
    private val lock: ReentrantReadWriteLock
) : SubscriptionRegistry {

    override fun register(subscription: Subscription<Any>) = lock.write {
        registry.register(subscription)
    }

    override fun unregister(subscription: Subscription<Any>) = lock.write {
        registry.unregister(subscription)
    }

    override fun registerAll(subscriptions: Set<Subscription<Any>>) = lock.write {
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