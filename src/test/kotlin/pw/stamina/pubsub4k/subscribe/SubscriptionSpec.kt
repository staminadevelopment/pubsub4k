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

import com.nhaarman.mockitokotlin2.mock
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import pw.stamina.pubsub4k.MessageSubscriber
import pw.stamina.pubsub4k.Topic
import pw.stamina.pubsub4k.subscribe.Subscription.newSubscription
import java.util.function.Consumer
import java.util.function.Predicate

object SubscriptionSpec : Spek({
    describe("A subscription") {
        val topic = Any::class.java

        val subscriber by memoized { mock<MessageSubscriber>() }
        val topicFilter by memoized { mock<Predicate<Topic<out Any>>>() }
        val messageHandler by memoized { mock<Consumer<Any>>() }

        val subscription by memoized { Subscription(topic, subscriber, topicFilter, messageHandler) }

        it("topic should be specified topic") {
            subscription.topic shouldBe topic
        }

        it("subscriber should be specified subscriber") {
            subscription.subscriber shouldBe subscriber
        }

        it("topicFilter should be specified topicFilter") {
            subscription.topicFilter shouldBe topicFilter
        }

        it("messageHandler should be specified messageHandler") {
            subscription.messageHandler shouldBe messageHandler
        }
    }

    describe("newSubscription") {
        val topic = Any::class.java
        val subscriber by memoized { mock<MessageSubscriber>() }

        val builder by memoized { newSubscription(topic, subscriber) }

        it("should return new InitialSubscriptionBuilder") {
            builder shouldBeInstanceOf InitialSubscriptionBuilder::class
        }
    }
})