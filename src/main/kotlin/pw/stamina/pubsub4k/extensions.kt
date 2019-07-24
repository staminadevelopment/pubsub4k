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

import pw.stamina.pubsub4k.publish.Publisher
import java.util.function.Consumer

/**
 * Returns the publisher associated with the [T] topic, if
 * a publisher does not exist a new one is created.
 */
inline fun <reified T : Any> EventBus.getPublisher(): Publisher<T> {
    return getPublisher(T::class.java)
}

inline fun <reified T : Any> EventBus.on(subscriber: MessageSubscriber, crossinline handler: (T) -> Unit) {
    on(T::class.java, subscriber, Consumer { handler(it) })
}

inline fun <reified T : Any> EventBus.once(subscriber: MessageSubscriber, crossinline handler: (T) -> Unit) {
    once(T::class.java, subscriber, Consumer { handler(it) })
}
