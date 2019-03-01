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

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.amshove.kluent.shouldBeInstanceOf
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import pw.stamina.pubsub4k.publish.OptimizedPublisher.Companion.fromSubscriptions
import pw.stamina.pubsub4k.subscribe.Subscription

object OptimizedPublisherSpec : Spek({

    describe("fromSubscriptions") {
        describe("given empty set") {
            val result by memoized { fromSubscriptions<Any>(emptySet()) }

            it("should return empty publisher") {
                result shouldBeInstanceOf EmptyPublisher::class
            }
        }

        describe("given set with one subscription") {
            val subscription: Subscription<Any> = mock { on { messageHandler } doReturn mock() }

            val result by memoized { fromSubscriptions(setOf(subscription)) }

            it("should return publisher for a single subscription") {
                result shouldBeInstanceOf SingleSubscriptionPublisher::class
            }
        }

        describe("given set with multiple subscriptions") {
            val subscriptions = setOf<Subscription<Any>>(mock(), mock())
            val result by memoized { fromSubscriptions(subscriptions) }

            it("should return publisher for many subscriptions") {
                result shouldBeInstanceOf ManySubscriptionsPublisher::class
            }
        }
    }

    describe("empty") {
        val result by memoized { OptimizedPublisher.empty<Any>() }

        it("should return empty publisher") {
            result shouldBeInstanceOf EmptyPublisher::class
        }
    }
})

