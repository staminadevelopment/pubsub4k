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

import pw.stamina.pubsub4k.ContentFilter
import pw.stamina.pubsub4k.MessageHandler
import pw.stamina.pubsub4k.MessageSubscriber
import pw.stamina.pubsub4k.Topic
import kotlin.properties.ReadOnlyProperty

class Subscription<T>(
        val topic: Topic<T>,

        val acceptSubtopics: Boolean,

        /**
         * Handler function for the messages received by this
         * subscription.
         */
        val messageHandler: MessageHandler<T>) {

    companion object {
        inline fun <reified T> newSubscription() = InitialSubscriptionBuilder(T::class.java)

        inline fun <reified T> newSubscription(
                acceptSubtopics: Boolean = false,
                noinline contentFilter: ContentFilter<T>? = null,
                noinline messageHandler: MessageHandler<T>
        ): ReadOnlyProperty<MessageSubscriber, Subscription<T>> {
            return newSubscription<T>()
                    .let { if (acceptSubtopics) it.acceptSubtopics() else it }
                    .let { contentFilter?.let(it::filterContent) ?: it }
                    .build(messageHandler)
        }
    }
}