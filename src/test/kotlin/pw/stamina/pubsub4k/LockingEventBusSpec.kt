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

package pw.stamina.pubsub4k

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.verify
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBe
import org.mockito.Mockito
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import pw.stamina.pubsub4k.publish.Publisher
import pw.stamina.pubsub4k.subscribe.CancellableMessageHandler.Companion.newCancellableHandler
import pw.stamina.pubsub4k.subscribe.MessageHandler
import pw.stamina.pubsub4k.subscribe.Subscription

object LockingEventBusSpec : Spek({
    describe("A locking event bus") {
        val parentBus by memoized {
            mock<EventBus>(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
        }

        val lockingBus by memoized {
            parentBus.withLocking()
        }

        describe("adding subscription") {
            val subscription by memoized { mock<Subscription<Any>>() }

            it("should add to parent bus") {
                lockingBus.addSubscription(subscription)
                verify(parentBus).addSubscription(subscription)
            }
        }

        describe("removing subscription") {
            val subscription by memoized { mock<Subscription<Any>>() }

            it("should remove from parent bus") {
                lockingBus.removeSubscription(subscription)
                verify(parentBus).removeSubscription(subscription)
            }
        }

        describe("removing all subscriptions for subscriber") {
            val subscriber by memoized { mock<MessageSubscriber>() }

            it("should remove all subscriptions for subscriber from parent bus") {
                lockingBus.removeAllSubscriptions(subscriber)
                verify(parentBus).removeAllSubscriptions(subscriber)
            }
        }

        describe("adding subscription using on") {
            val topic = Any::class.java
            val handler = MessageHandler.newHandler<Any> {}
            val subscriber by memoized { mock<MessageSubscriber>() }

            it("should add subscription to parent bus using on") {
                lockingBus.on(topic, subscriber, handler)
                verify(parentBus).on(topic, subscriber, handler)
            }
        }

        describe("adding cancellable subscription") {
            val topic = Any::class.java
            val handler = newCancellableHandler<Any> { false }
            val subscriber by memoized { mock<MessageSubscriber>() }

            it("should add subscription to parent bus using once") {
                lockingBus.cancellableOn(topic, subscriber, handler)
                verify(parentBus).cancellableOn(topic, subscriber, handler)
            }
        }

        describe("getting publisher by topic") {
            val mockedPublisher by memoized { mock<Publisher<Any>>() }

            beforeEach {
                parentBus.stub {
                    on { parentBus.getPublisher<Any>() } doReturn mockedPublisher
                }
            }

            val lockingBusPublisher by memoized { lockingBus.getPublisher<Any>() }

            it("should return publisher from parent bus") {
                lockingBusPublisher shouldBe mockedPublisher

                verify(parentBus).getPublisher<Any>()
            }
        }
    }
})