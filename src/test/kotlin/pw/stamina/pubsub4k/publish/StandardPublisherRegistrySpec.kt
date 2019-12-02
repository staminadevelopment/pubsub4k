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
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldEqual
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import pw.stamina.pubsub4k.Topic
import pw.stamina.pubsub4k.subscribe.Subscription
import pw.stamina.pubsub4k.subscribe.TopicFilter

object StandardPublisherRegistrySpec : Spek({

    describe("A standard publisher registry") {
        //TODO: Mock publisher factory, and add test cases for it
        val publisherFactory = PublisherFactory.optimized()
        val registry by memoized { StandardPublisherRegistry(publisherFactory) }

        describe("no publishers created") {
            val topic: Topic<Any> = Any::class.java

            describe("finding publisher by topic") {
                val result by memoized { registry.findPublisher(topic) }

                it("should return null") {
                    result shouldBe null
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
                    val topicFilter by memoized { mock<TopicFilter<CharSequence>>() }

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
                            verify(topicFilter).testTopic(topic)
                            verify(topicFilter).testTopic(subtopic)
                        }
                    }

                    describe("topic filter rejects all") {
                        beforeEach {
                            topicFilter.stub {
                                on { testTopic(any()) } doReturn false
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
                                on { testTopic(any()) } doReturn true
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