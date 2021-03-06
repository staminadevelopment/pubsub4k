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
import pw.stamina.pubsub4k.subscribe.CancellableMessageHandler
import pw.stamina.pubsub4k.subscribe.MessageHandler
import pw.stamina.pubsub4k.subscribe.Subscription
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal class LockingEventBus(
    private val bus: EventBus,
    private val lock: Lock
) : EventBus {

    override fun addSubscription(subscription: Subscription<*>) = lock.withLock {
        bus.addSubscription(subscription)
    }

    override fun removeSubscription(subscription: Subscription<*>) = lock.withLock {
        bus.removeSubscription(subscription)
    }

    override fun removeAllSubscriptions(subscriber: MessageSubscriber) = lock.withLock {
        bus.removeAllSubscriptions(subscriber)
    }

    override fun <T : Any> on(topic: Topic<T>, subscriber: MessageSubscriber, handler: MessageHandler<T>) = lock.withLock {
        bus.on(topic, subscriber, handler)
    }

    override fun <T : Any> cancellableOn(
        topic: Topic<T>,
        subscriber: MessageSubscriber,
        handler: CancellableMessageHandler<T>
    ) = lock.withLock {
        bus.cancellableOn(topic, subscriber, handler)
    }

    override fun <T : Any> getPublisher(topic: Topic<T>): Publisher<T> = lock.withLock {
        return bus.getPublisher(topic)
    }
}

fun EventBus.withLocking(lock: Lock = ReentrantLock()): EventBus {
    return LockingEventBus(this, lock)
}