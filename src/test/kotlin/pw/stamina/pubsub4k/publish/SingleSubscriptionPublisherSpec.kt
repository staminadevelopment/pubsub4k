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
import com.nhaarman.mockitokotlin2.verify
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import pw.stamina.pubsub4k.subscribe.Subscription

object SingleSubscriptionPublisherSpec : Spek({

    describe("A publisher with a single subscription") {
        val subscription by memoized {
            mock<Subscription<Any>> { on { messageHandler } doReturn mock() }
        }

        val publisher by memoized { SingleSubscriptionPublisher(subscription) }

        it("subscriptions should contain only specified subscription") {
            publisher.subscriptions shouldEqual setOf(subscription)
        }

        describe("publishing message") {
            val message = Unit

            beforeEach {
                publisher.publish(message)
            }

            it("should call message handler of subscription") {
                verify(subscription.messageHandler).handle(message)
            }
        }

        describe("removing subscription") {
            describe("own subscription") {
                val result by memoized { publisher.removed(subscription) }

                it("should return empty publisher") {
                    result shouldBeInstanceOf EmptyPublisher::class
                }
            }

            describe("other subscription") {
                val dummySubscription: Subscription<Any> = mock()
                val result by memoized { publisher.removed(dummySubscription) }

                it("should return itself") {
                    result shouldBe publisher
                }
            }
        }

        describe("adding subscription") {
            describe("own subscription") {
                val result by memoized { publisher.added(subscription) }

                it("should return itself") {
                    result shouldBe publisher
                }
            }

            describe("other subscription") {
                val dummySubscription: Subscription<Any> = mock()
                val result by memoized { publisher.added(dummySubscription) }

                it("should return many subscriptions publisher") {
                    result shouldBeInstanceOf ManySubscriptionsPublisher::class
                }
            }
        }
    }
})