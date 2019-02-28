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

import com.nhaarman.mockitokotlin2.mock
import org.amshove.kluent.shouldBeInstanceOf
import org.mockito.Mockito.RETURNS_DEEP_STUBS
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import pw.stamina.pubsub4k.publish.OptimizedPublisher.Companion.fromSubscriptions
import pw.stamina.pubsub4k.subscribe.Subscription

object OptimizedPublisherSpec : Spek({

    val subscription = mock<Subscription<Any>>(stubOnly = true, defaultAnswer = RETURNS_DEEP_STUBS)
    val subscription2 = mock<Subscription<Any>>(stubOnly = true, defaultAnswer = RETURNS_DEEP_STUBS)

    describe("fromSubscriptions") {
        describe("given empty set") {
            it("should return empty publisher") {
                fromSubscriptions<Any>(emptySet()) shouldBeInstanceOf EmptyPublisher::class
            }
        }

        describe("given set with one subscription") {
            it("should return publisher for a single subscription") {
                val subscriptions = setOf(subscription)
                fromSubscriptions(subscriptions) shouldBeInstanceOf SingleSubscriptionPublisher::class
            }
        }

        describe("given set with multiple subscriptions") {
            it("should return publisher for many subscriptions") {
                val subscriptions = setOf(subscription, subscription2)
                fromSubscriptions(subscriptions) shouldBeInstanceOf ManySubscriptionsPublisher::class
            }
        }
    }
})

