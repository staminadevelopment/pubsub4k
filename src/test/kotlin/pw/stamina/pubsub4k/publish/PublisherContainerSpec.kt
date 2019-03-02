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