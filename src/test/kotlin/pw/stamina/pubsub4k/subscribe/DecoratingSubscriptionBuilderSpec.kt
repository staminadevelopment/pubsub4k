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

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.verify
import org.amshove.kluent.shouldBe
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.util.function.Consumer

object DecoratingSubscriptionBuilderSpec : Spek({

    describe("A decorating subscription builder") {
        val parent by memoized { mock<SubscriptionBuilder<Any, Any>>() }
        val decorator by memoized { mock<HandlerDecorator<Any, Any>>() }

        val builder by memoized { DecoratingSubscriptionBuilder(parent, decorator) }

        describe("building subscription") {
            val messageHandler by memoized { mock<Consumer<Any>>() }
            val decoratedMessageHandler by memoized { mock<Consumer<Any>>() }

            val subscription by memoized { mock<Subscription<Any>>() }

            lateinit var result: Subscription<Any>
            beforeEach {
                decorator.stub {
                    on { decorateHandler(messageHandler) } doReturn decoratedMessageHandler
                }

                parent.stub {
                    on { build(any()) } doReturn subscription
                }

                result = builder.build(messageHandler)
            }

            it("should call decorator with messageHandler") {
                verify(decorator).decorateHandler(messageHandler)
            }

            it("should call parent build with decoratedMessageHandler") {
                verify(parent).build(decoratedMessageHandler)
            }

            it("should return subscription from parent") {
                result shouldBe subscription
            }
        }
    }
})