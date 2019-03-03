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

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.verify
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldEqual
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import pw.stamina.pubsub4k.Topic
import pw.stamina.pubsub4k.subscribe.Subscription
import java.util.function.Predicate

object StandardPublisherRegistrySpec : Spek({

    describe("A standard publisher registry") {
        val registry by memoized { StandardPublisherRegistry() }

        describe("no publishers created") {
            val topic: Topic<Any> = Any::class.java

            describe("finding publisher by topic") {
                val result by memoized { registry.findPublisher(topic) }

                it("should return null") {
                    result shouldBe null
                }
            }

            describe("removing publisher by topic") {
                val removed by memoized { registry.removePublisher(topic) }

                it("should return false") {
                    removed shouldBe false
                }
            }

            describe("find or create publisher") {
                val subscriptions by memoized { setOf<Subscription<Any>>(mock(), mock(), mock()) }

                // Do not use memoized here
                lateinit var publisher: Publisher<Any>
                beforeEach {
                    publisher = registry.findOrCreatePublisher(topic) { subscriptions }
                }

                it("publisher should contain specified subscriptions") {
                    publisher.subscriptions shouldEqual subscriptions
                }

                describe("find or create publisher for existing publisher") {
                    // Do not use memoized here
                    lateinit var result: Publisher<Any>
                    beforeEach {
                        result = registry.findOrCreatePublisher(topic) { emptySet() }
                    }

                    it("should return previously created publisher") {
                        result shouldBe publisher
                    }
                }

                describe("finding publisher by topic") {
                    val result by memoized { registry.findPublisher(topic) }

                    it("should return previously created publisher") {
                        result shouldBe publisher
                    }
                }

                describe("finding publisher by other topic") {
                    val otherTopic = String::class.java
                    val result by memoized { registry.findPublisher(otherTopic) }

                    it("should return null") {
                        result shouldBe null
                    }
                }

                describe("removing publisher by topic") {
                    // Do not use memoized here
                    var removed = false
                    beforeEach {
                        removed = registry.removePublisher(topic)
                    }


                    it("should return true") {
                        removed shouldBe true
                    }

                    describe("finding publisher by topic") {
                        val result by memoized { registry.findPublisher(topic) }

                        it("should return null") {
                            result shouldBe null
                        }
                    }

                    it("removed publisher should be cleared") {
                        publisher.subscriptions.shouldBeEmpty()
                    }
                }
            }
        }

        describe("with many publishers") {
            val topic: Topic<CharSequence> = CharSequence::class.java
            val supertopic: Topic<Any> = Any::class.java
            val subtopic: Topic<String> = String::class.java

            lateinit var publisher: Publisher<CharSequence>
            lateinit var publisherSub: Publisher<String>

            beforeEach {
                publisher = registry.findOrCreatePublisher(topic) { emptySet() }
                publisherSub = registry.findOrCreatePublisher(subtopic) { emptySet() }
                registry.findOrCreatePublisher(supertopic) { emptySet() }
            }

            val subscription by memoized {
                mock<Subscription<CharSequence>> {
                    on { this.topic } doReturn topic
                    on { messageHandler } doReturn mock()
                }
            }

            describe("find publishers for subscription") {
                describe("with no topic filter") {
                    val publishers by memoized { registry.findPublishersFor(subscription) }

                    it("publishers should contain just publisher and publisherSub") {
                        publishers shouldEqual setOf(publisher, publisherSub)
                    }
                }

                describe("with topic filter") {
                    val topicFilter by memoized { mock<Predicate<Class<out CharSequence>>>() }

                    beforeEach {
                        subscription.stub {
                            on { this.topicFilter } doReturn topicFilter
                        }
                    }

                    describe("any topic filter") {
                        beforeEach {
                            registry.findPublishersFor(subscription)
                        }

                        it("topic filter should be tested on topics") {
                            verify(topicFilter).test(topic)
                            verify(topicFilter).test(subtopic)
                        }
                    }

                    describe("topic filter rejects all") {
                        beforeEach {
                            topicFilter.stub {
                                on { test(any()) } doReturn false
                            }
                        }

                        val publishers by memoized { registry.findPublishersFor(subscription) }

                        it("publishers should be empty") {
                            publishers.shouldBeEmpty()
                        }
                    }

                    describe("topic filter accepts all") {
                        beforeEach {
                            topicFilter.stub {
                                on { test(any()) } doReturn true
                            }
                        }

                        val publishers by memoized { registry.findPublishersFor(subscription) }

                        it("publishers should contain just publisher and publisherSub") {
                            publishers shouldEqual setOf(publisher, publisherSub)
                        }
                    }
                }
            }

            describe("add subscription to publishers") {
                val publishers by memoized { registry.findPublishersFor(subscription) }

                beforeEach {
                    registry.addSubscriptionToPublishers(subscription)
                }

                it("should register subscription to publishers") {
                    publishers.forEach { publisher ->
                        publisher.subscriptions shouldEqual setOf(subscription)
                    }
                }

                describe("remove subscription from publishers") {
                    beforeEach {
                        registry.removeSubscriptionFromPublishers(subscription)
                    }

                    it("should register subscription to publishers") {
                        publishers.forEach { publisher ->
                            publisher.subscriptions.shouldBeEmpty()
                        }
                    }
                }
            }
        }
    }
})