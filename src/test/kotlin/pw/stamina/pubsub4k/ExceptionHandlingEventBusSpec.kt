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
import pw.stamina.pubsub4k.publish.Publisher

object ExceptionHandlingEventBusSpec : Spek({

    describe("An exception handling event bus") {

        val expectedException by memoized { RuntimeException("expected") }
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