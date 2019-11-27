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

interface MessageHandler<T : Any> {

    fun handle(message: T)

    companion object {
        inline fun <T : Any> newHandler(crossinline handler: (T) -> Unit) =
            object : MessageHandler<T> {
                override fun handle(message: T) = handler.invoke(message)
            }
    }
}

interface CancellableMessageHandler<T : Any> : MessageHandler<T> {

    var cancel: () -> Unit

    companion object {
        inline fun <T : Any> newCancellableHandler(crossinline handler: (T) -> Boolean) =
            object : CancellableMessageHandler<T> {
                override lateinit var cancel: () -> Unit

                override fun handle(message: T) {
                    if (handler(message)) cancel()
                }
            }
    }
}