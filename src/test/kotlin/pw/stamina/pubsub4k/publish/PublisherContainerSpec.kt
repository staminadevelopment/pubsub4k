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
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldEqual
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import pw.stamina.pubsub4k.subscribe.Subscription

object PublisherContainerSpec : Spek({

    val topic = Any::class.java

    describe("A publisher container") {
        val container by memoized { PublisherContainer(topic, emptySet()) }

        it("topic should be specified topic") {
            container.topic shouldBe topic
        }

        it("should have no subscriptions") {
            container.subscriptions.shouldBeEmpty()
        }

        describe("with added subscription") {
            val subscription by memoized { mock<Subscription<Any>> { on { messageHandler } doReturn mock() } }

            beforeEach {
                container.add(subscription)
            }

            it("subscriptions should contain only specified subscription") {
                container.subscriptions shouldEqual setOf(subscription)
            }

            describe("publishing message") {
                val message = Unit
                beforeEach {
                    container.publish(message)
                }

                it("should call message handler of subscription") {
                    verify(subscription.messageHandler).accept(message)
                }
            }

            describe("with removed subscription") {
                beforeEach {
                    container.remove(subscription)
                }

                it("subscriptions should be empty") {
                    container.subscriptions.shouldBeEmpty()
                }
            }

            describe("cleared subscriptions") {
                beforeEach {
                    container.clear()
                }

                it("subscriptions should be empty") {
                    container.subscriptions.shouldBeEmpty()
                }
            }
        }
    }
})