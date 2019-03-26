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