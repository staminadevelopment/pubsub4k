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

package pw.stamina.pubsub4k.subscribe

import pw.stamina.pubsub4k.MessageSubscriber
import pw.stamina.pubsub4k.Topic

interface SubscriptionRegistry {

    fun register(subscription: Subscription<*>): Boolean

    fun unregister(subscription: Subscription<*>): Boolean

    fun registerAll(subscriptions: Set<Subscription<*>>): Set<Subscription<*>>

    fun unregisterAll(subscriber: MessageSubscriber): Set<Subscription<*>>

    fun <T> findSubscriptionsForTopic(topic: Topic<T>): Set<Subscription<T>>
}

fun SubscriptionRegistry.registerAllReflectively(subscriber: MessageSubscriber) {
    val subscriptions = subscriber.javaClass.declaredFields.asSequence()
        .filter { Subscription::class.java.isAssignableFrom(it.type) }
        .onEach { it.isAccessible = true }
        .map { it.get(subscriber) }
        .filterIsInstance<Subscription<*>>()
        .toSet()

    registerAll(subscriptions)
}