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
import pw.stamina.pubsub4k.isSubtopicOf
import pw.stamina.pubsub4k.subscribe.Subscription

/**
 * The publisher provides an interface for publishing
 * events for its [topic] to its registered [subscriptions].
 * The publisher can publish messages of the same topic as
 * the publisher, or a [subtopic][isSubtopicOf].
 */
interface Publisher<T : Any> {

    /**
     * The topic of this publisher.
     */
    val topic: Topic<T>

    /**
     * The subscriptions registered for this publisher.
     */
    val subscriptions: Set<Subscription<T>>

    /**
     * Publishes the [message] to all its [subscriptions]. If
     * an exception is thrown it should always be caught, and
     * rethrown as a [PublicationException].
     */
    @Throws(PublicationException::class)
    fun publish(message: T)
}