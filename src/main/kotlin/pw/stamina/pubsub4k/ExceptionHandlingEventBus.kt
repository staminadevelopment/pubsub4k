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

import pw.stamina.pubsub4k.publish.PublicationException
import pw.stamina.pubsub4k.publish.Publisher

internal class ExceptionHandlingEventBus(
    private val bus: EventBus,
    private val exceptionHandler: ExceptionHandler
) : EventBus by bus {

    override fun <T : Any> getPublisher(topic: Topic<T>): Publisher<T> {
        val publisher = bus.getPublisher(topic)
        return ExceptionHandlingPublisher(publisher, exceptionHandler)
    }
}

internal class ExceptionHandlingPublisher<T : Any>(
    private val publisher: Publisher<T>,
    private val exceptionHandler: ExceptionHandler
) : Publisher<T> by publisher {

    override fun publish(message: T) = try {
        publisher.publish(message)
    } catch (e: PublicationException) {
        exceptionHandler(e)
    }
}

internal typealias ExceptionHandler = (exception: PublicationException) -> Unit

fun EventBus.withExceptionHandling(exceptionHandler: ExceptionHandler): EventBus {
    return ExceptionHandlingEventBus(this, exceptionHandler)
}