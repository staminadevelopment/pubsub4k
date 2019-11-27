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

import com.nhaarman.mockitokotlin2.*
import org.amshove.kluent.mock
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object ExceptionHandlingPublisherFactorySpec : Spek({

    describe("An exception handling publisher factory") {

        val expectedException by memoized { mock<PublicationException>() }
        val mockedPublisher by memoized { mock<Publisher<Any>>() }
        val exceptionHandler by memoized { mock<ExceptionHandler>() }
        val mockedFactory by memoized {
            mock<PublisherFactory> {
                on { createPublisher<Any>(any(), any()) } doReturn mockedPublisher
            }
        }
        val factory by memoized { ExceptionHandlingPublisherFactory(mockedFactory, exceptionHandler) }

        describe("getting a publisher") {

            val publisher by memoized { factory.createPublisher(Any::class.java, emptySet()) }

            describe("publishing message") {
                val message by memoized { Any() }

                describe("with non-mocking mocked publisher") {
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