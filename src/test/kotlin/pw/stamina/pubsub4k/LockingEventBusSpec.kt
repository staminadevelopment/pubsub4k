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
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.verify
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBe
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import pw.stamina.pubsub4k.publish.Publisher
import java.util.concurrent.locks.ReentrantReadWriteLock

object LockingEventBusSpec : Spek({
    // TODO: Rewrite tests
    describe("A locking event bus") {
        val parentBus by memoized { mock<EventBus>() }

        lateinit var lockingBus: EventBus
        beforeEach {
            val lock = ReentrantReadWriteLock()
            lockingBus = LockingEventBus(parentBus, lock)
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