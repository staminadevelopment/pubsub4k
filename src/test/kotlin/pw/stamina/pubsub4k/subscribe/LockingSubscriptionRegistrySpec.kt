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

package pw.stamina.pubsub4k.subscribe

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import pw.stamina.pubsub4k.LockingSubscriptionRegistry
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