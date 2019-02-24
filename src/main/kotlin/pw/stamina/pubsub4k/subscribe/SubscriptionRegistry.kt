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

package pw.stamina.pubsub4k.subscribe

import pw.stamina.pubsub4k.MessageSubscriber
import pw.stamina.pubsub4k.Topic
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.isAccessible

interface SubscriptionRegistry {

    fun register(subscriber: MessageSubscriber, subscription: Subscription<*>)

    fun registerAll(subscriber: MessageSubscriber, subscriptions: Set<Subscription<*>>)

    fun unregister(subscriber: MessageSubscriber, subscription: Subscription<*>)

    fun unregisterAll(subscriber: MessageSubscriber)

    fun <T> findSubscriptionsForTopic(topic: Topic<T>): List<Subscription<T>>
}

fun SubscriptionRegistry.registerAllReflectively(subscriber: MessageSubscriber) {
    val subscriberProperties = subscriber.javaClass.kotlin.declaredMemberProperties

    val subscriptions = subscriberProperties.asSequence()
            .filter { it.returnType.isSubtypeOf(Subscription::class.starProjectedType) }
            .onEach { it.isAccessible = true }
            .map { it.get(subscriber) }
            .filterIsInstance<Subscription<*>>()
            .toSet()

    registerAll(subscriber, subscriptions)
}