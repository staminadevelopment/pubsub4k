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

import com.nhaarman.mockitokotlin2.mock
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.mockito.Mockito.RETURNS_DEEP_STUBS
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import pw.stamina.pubsub4k.subscribe.Subscription

object EmptyPublisherSpec : Spek({

    describe("An empty publisher") {
        val publisher by memoized { EmptyPublisher<Any>() }

        it("subscriptions should be empty") {
            publisher.subscriptions.shouldBeEmpty()
        }

        describe("publishing message") {
            val result by memoized { publisher.publish(Unit) }

            it("should do nothing and return unit") {
                result shouldBe Unit
            }
        }

        describe("removing subscription") {
            val result by memoized { publisher.removed(mock()) }

            it("should return itself") {
                result shouldBe publisher
            }
        }

        describe("adding subscription") {
            val subscription by memoized { mock<Subscription<Any>>(defaultAnswer = RETURNS_DEEP_STUBS) }

            val result by memoized { publisher.added(subscription) }

            it("should return single subscription publisher") {
                result shouldBeInstanceOf SingleSubscriptionPublisher::class
            }

            it("returned publisher should contain specified subscription") {
                result.subscriptions shouldEqual setOf(subscription)
            }
        }
    }
})