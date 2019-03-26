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

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.verify
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBeInstanceOf
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import pw.stamina.pubsub4k.publish.PublicationException
import pw.stamina.pubsub4k.publish.Publisher

object ExceptionHandlingEventBusSpec : Spek({

    describe("An exception handling event bus") {

        val expectedException by memoized { mock<PublicationException>() }
        val mockedPublisher by memoized { mock<Publisher<Any>>() }
        val mockedBus by memoized {
            mock<EventBus> {
                on { getPublisher<Any>() } doReturn mockedPublisher
            }
        }

        val exceptionHandler by memoized { mock<ExceptionHandler>() }
        val bus by memoized { ExceptionHandlingEventBus(mockedBus, exceptionHandler) }

        describe("getting a publisher") {

            val publisher by memoized { bus.getPublisher<Any>() }

            it("should be an exception handling publisher") {
                publisher.shouldBeInstanceOf<ExceptionHandlingPublisher<Any>>()
            }

            describe("publishing message") {
                val message by memoized { Any() }

                describe("with normal mocked publisher") {
                    beforeEach {
                        publisher.publish(message)
                    }

                    it("should publish message to mocked publisher") {
                        verify(mockedPublisher).publish(message)
                    }
                }

                describe("with exception throwing mocked publisher") {
                    beforeEach {
                        mockedPublisher.stub {
                            on { it.publish(any()) } doThrow expectedException
                        }

                        publisher.publish(message)
                    }

                    it("should catch exception and pass it to exception handler") {
                        verify(exceptionHandler).invoke(expectedException)
                    }
                }
            }
        }
    }
})