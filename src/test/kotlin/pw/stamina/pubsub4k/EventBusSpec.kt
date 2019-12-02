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

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object EventBusSpec : Spek({
    describe("A locking default event bus instance") {
        val bus by memoized { EventBus.createDefaultBus() }

        it("should be instance of LockingEventBus") {
            bus shouldBeInstanceOf LockingEventBus::class
        }
    }

    describe("A non-locking default event bus instance") {
        val bus by memoized { EventBus.createDefaultBus(locking = false) }

        it("should be instance of StandardEventBus") {
            bus shouldBeInstanceOf StandardEventBus::class
        }
    }

    describe("An event bus") {
        val bus by memoized { mock<EventBus>() }

        describe("getting publisher by topic with reified function") {
            beforeEach {
                bus.getPublisher<Any>()
            }

            it("should call normal getPublisher function") {
                verify(bus).getPublisher(Any::class.java)
            }
        }

        describe("withLocking") {
            val busWithLocking by memoized { bus.withLocking() }

            it("should return locking bus") {
                busWithLocking.shouldBeInstanceOf<LockingEventBus>()
            }
        }
    }
})

object TopicSpec : Spek({
    describe("A topic") {
        val topic: Topic<CharSequence> = CharSequence::class.java

        val supertopic: Topic<Any> = Any::class.java
        val subtopic: Topic<String> = String::class.java

        describe("isSubtopicOf") {
            it("should return true for same topic") {
                topic.isSubtopicOf(topic) shouldBe true
            }

            it("should return true for supertopic") {
                topic.isSubtopicOf(supertopic) shouldBe true
            }

            it("should return false for subtopic") {
                topic.isSubtopicOf(subtopic) shouldBe false
            }
        }

        describe("isSupertopicOf") {
            it("should return true for same topic") {
                topic.isSupertopicOf(topic) shouldBe true
            }

            it("should return false for supertopic") {
                topic.isSupertopicOf(supertopic) shouldBe false
            }

            it("should return true for subtopic") {
                topic.isSupertopicOf(subtopic) shouldBe true
            }
        }
    }
})