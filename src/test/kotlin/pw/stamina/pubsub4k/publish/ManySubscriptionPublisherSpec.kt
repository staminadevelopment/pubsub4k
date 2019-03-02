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
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBe
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import pw.stamina.pubsub4k.subscribe.Subscription

object ManySubscriptionPublisherSpec : Spek({

    describe("Publisher with many subscriptions") {
        val subscription by memoized {
            mock<Subscription<Any>> { on { messageHandler } doReturn mock() }
        }
        val subscription2 by memoized {
            mock<Subscription<Any>> { on { messageHandler } doReturn mock() }
        }
        val subscription3 by memoized {
            mock<Subscription<Any>> { on { messageHandler } doReturn mock() }
        }

        describe("publisher with 2 subscriptions") {
            val subscriptions by memoized { setOf(subscription, subscription2) }
            val publisher by memoized { ManySubscriptionsPublisher(subscriptions) }

            it("subscriptions contain only specified subscriptions") {
                publisher.subscriptions shouldEqual subscriptions
            }

            describe("publishing message") {
                val message = Unit
                beforeEach {
                    publisher.publish(message)
                }

                it("should call message handler of subscriptions") {
                    subscriptions.forEach { subscription ->
                        verify(subscription.messageHandler).accept(message)
                    }
                }
            }

            describe("removed subscription") {
                describe("contained subscription") {
                    val result by memoized { publisher.removed(subscription) }

                    it("should return new single subscription publisher") {
                        result shouldBeInstanceOf SingleSubscriptionPublisher::class
                    }

                    it("should only contain subscription2") {
                        result.subscriptions shouldEqual setOf(subscription2)
                    }
                }

                describe("other subscription") {
                    val result by memoized { publisher.removed(subscription3) }

                    it("should return itself") {
                        result shouldBe publisher
                    }
                }
            }

            describe("added subscription") {
                describe("contained subscription") {
                    val result by memoized { publisher.added(subscription) }

                    it("should return itself") {
                        result shouldBe publisher
                    }

                    it("should not add subscription") {
                        result.subscriptions shouldEqual subscriptions
                    }
                }

                describe("other subscription") {
                    val result by memoized { publisher.added(subscription3) }

                    it("should return new publisher") {
                        result shouldNotBe publisher
                    }

                    it("should contain subscriptions from publisher and other subscription") {
                        result.subscriptions shouldEqual subscriptions + subscription3
                    }
                }
            }
        }

        describe("publisher with 3 subscriptions") {
            val subscriptions by memoized { setOf(subscription, subscription2, subscription3) }
            val publisher by memoized { ManySubscriptionsPublisher(subscriptions) }

            describe("removing subscription") {
                describe("contained subscription") {
                    val result by memoized { publisher.removed(subscription) }

                    it("should return new publisher") {
                        result shouldNotBe publisher
                    }

                    it("should only contain subscription2 and subscription3") {
                        result.subscriptions shouldEqual setOf(subscription2, subscription3)
                    }
                }
            }
        }
    }
})