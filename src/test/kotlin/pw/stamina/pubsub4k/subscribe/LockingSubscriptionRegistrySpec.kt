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
import com.nhaarman.mockitokotlin2.verify
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import pw.stamina.pubsub4k.MessageSubscriber
import java.util.concurrent.locks.ReentrantReadWriteLock

object LockingSubscriptionRegistrySpec : Spek({
    describe("A locking subscription registry") {
        val lock = ReentrantReadWriteLock()

        val mockedRegistry by memoized { mock<SubscriptionRegistry>() }
        val lockingRegistry by memoized { LockingSubscriptionRegistry(mockedRegistry, lock) }

        val subscription by memoized { mock<Subscription<Any>>() }

        describe("registering subscription") {
            beforeEach {
                lockingRegistry.register(subscription)
            }

            it("should call register with subscription") {
                verify(mockedRegistry).register(subscription)
            }
        }

        describe("unregistering subscription") {
            beforeEach {
                lockingRegistry.unregister(subscription)
            }

            it("should call unregister with subscription") {
                verify(mockedRegistry).unregister(subscription)
            }
        }

        describe("registering all subscriptions") {
            val subscriptions by memoized { setOf(subscription) }

            beforeEach {
                lockingRegistry.registerAll(subscriptions)
            }

            it("should call registerAll with subscriptions") {
                verify(mockedRegistry).registerAll(subscriptions)
            }
        }

        describe("unregistering all subscriptions") {
            val subscriber by memoized { mock<MessageSubscriber>() }

            beforeEach {
                lockingRegistry.unregisterAll(subscriber)
            }

            it("should call unregisterAll with subscriber") {
                verify(mockedRegistry).unregisterAll(subscriber)
            }
        }

        describe("finding subscriptions by topic") {
            val topic = Any::class.java

            beforeEach {
                lockingRegistry.findSubscriptionsForTopic(topic)
            }

            it("should call findSubscriptionsForTopic with topic") {
                verify(mockedRegistry).findSubscriptionsForTopic(topic)
            }
        }
    }
})