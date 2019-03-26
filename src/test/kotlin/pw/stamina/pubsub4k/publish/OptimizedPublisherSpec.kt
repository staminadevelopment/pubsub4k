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

