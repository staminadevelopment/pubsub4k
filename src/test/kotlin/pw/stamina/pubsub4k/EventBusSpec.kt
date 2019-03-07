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
    }
})